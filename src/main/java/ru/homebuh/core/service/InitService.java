package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.*;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CategoryEntity;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.util.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InitService {

    private final UserInfoService userInfoService;
    private final AccountService accountService;
    private final CurrencyService currencyService;
    private final CategoryService categoryService;

    /**
     * Проверяет выполнена ли первоначальная настройка приложения для текущего пользователя
     *
     * @param userId идентификатор пользователя
     * @return true, если начальная настройка приложения для текущего пользователя выполнена
     */
    @Transactional
    public boolean isUserInit(String userId) {
        //1. Пользователь должен существовать
        final UserInfoEntity userInfoEntity;
        try {
            userInfoEntity = userInfoService.getUserInfo(userId);
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()))
                return false;
            else
                throw e;
        }

        //2. У пользователя должна быть задана хотя бы одна валюта
        Collection<CurrencyEntity> userCurrencies = userInfoEntity.getCurrencies();
        if (userCurrencies.isEmpty())
            return false;

        //3. У пользователя должен быть создан хотя бы один счёт
        if (accountService.findAllAccountsByUserId(userId).isEmpty())
            return false;

        //4. У пользователя должна быть хотя бы одна расходная категория
        if (categoryService.findAllExpenseByUserId(userId).isEmpty())
            return false;

        //5. У пользователя должна быть хотя бы одна доходная категория
        return !categoryService.findAllIncomeByUserId(userId).isEmpty();
    }

    @Transactional
    public void initUser(String userId, InitCreate initCreate) {
        //1. Пользователь должен существовать
        final UserInfoEntity userInfoEntity;
        UserInfoEntity userInfoEntityTemp;
        try {
            userInfoEntityTemp = userInfoService.getUserInfo(userId);
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()))
                userInfoEntityTemp = userInfoService.create(new UserInfoCreate(userId, null));
            else
                throw e;
        }
        userInfoEntity = userInfoEntityTemp;

        //2. У пользователя должна быть задана хотя бы одна валюта
        final String initCurrencyCode = initCreate.getCurrencyCode();
        CurrencyEntity initCurrency = currencyService.getByCode(initCurrencyCode);

        List<CurrencyEntity> userCurrencies = userInfoEntity.getCurrencies();
        if (userCurrencies.isEmpty() || !userCurrencies.contains(initCurrency)) {
            userCurrencies.add(initCurrency);
            userInfoService.save(userInfoEntity);
        }

        //3. У пользователя должен быть создан хотя бы один счёт
        List<AccountEntity> userAccounts = accountService.findAllAccountsByUserId(userId);
        if (userAccounts.isEmpty()) {
            Constants.INITIAL_ACCOUNTS.forEach(accountName -> {
                Collection<AccountBalanceCreate> balances = new ArrayList<>(1);
                AccountBalanceCreate balance = new AccountBalanceCreate("0", initCurrencyCode);
                balances.add(balance);
                AccountCreate accountCreate = new AccountCreate(accountName, "", balances);
                accountService.create(userInfoEntity, accountCreate);
            });
        }

        //4. У пользователя должна быть хотя бы одна расходная категория
        List<CategoryEntity> expCategories = categoryService.findAllExpenseByUserId(userId);
        if (expCategories.isEmpty()) {
            Constants.INITIAL_EXPENSE_CATEGORIES.forEach(categoryName -> {
                CategoryCreate categoryCreate = new CategoryCreate(categoryName, false);
                categoryService.create(userInfoEntity, categoryCreate);
            });
        }

        //5. У пользователя должна быть хотя бы одна доходная категория
        List<CategoryEntity> incCategories = categoryService.findAllIncomeByUserId(userId);
        if (incCategories.isEmpty()) {
            Constants.INITIAL_INCOME_CATEGORIES.forEach(categoryName -> {
                CategoryCreate categoryCreate = new CategoryCreate(categoryName, true);
                categoryService.create(userInfoEntity, categoryCreate);
            });
        }
    }
}
