package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.homebuh.core.controller.dto.AccountCreate;
import ru.homebuh.core.controller.dto.CategoryCreate;
import ru.homebuh.core.controller.dto.InitCreate;
import ru.homebuh.core.controller.dto.UserInfoCreate;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CategoryEntity;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.repository.UserInfoRepository;
import ru.homebuh.core.util.Constants;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InitService {

    private final UserInfoService userInfoService;
    private final AccountService accountService;
    private final CurrencyService currencyService;
    private final CategoryService categoryService;
    private final UserInfoRepository userInfoRepository;

    /**
     * Проверяет выполнена ли первоначальная настройка приложения для текущего пользователя
     *
     * @param userId идентификатор пользователя
     * @return true, если начальная настройка приложения для текущего пользователя выполнена
     */
    @Transactional
    public boolean isUserInit(String userId) {
        //1. Пользователь должен существовать
        final Optional<UserInfoEntity> userInfoEntityOptional = userInfoRepository.findByIdIgnoreCase(userId);
        if (userInfoEntityOptional.isEmpty())
            return false;

        //2. У пользователя должна быть задана хотя бы одна валюта
        final UserInfoEntity userInfoEntity = userInfoEntityOptional.get();
        Collection<CurrencyEntity> userCurrencies = userInfoEntity.getCurrencies();
        if (userCurrencies.isEmpty())
            return false;

        //3. У пользователя должен быть создан хотя бы один счёт
        if (accountService.findAllByUserIdIgnoreCase(userId).isEmpty())
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
        final Optional<UserInfoEntity> optionalUserInfo = userInfoRepository.findByIdIgnoreCase(userId);
        final UserInfoEntity userInfoEntity = optionalUserInfo.orElseGet(() -> userInfoService.create(new UserInfoCreate(userId)));

        //2. У пользователя должна быть задана хотя бы одна валюта
        final String initCurrencyCode = initCreate.getCurrencyCode();
        CurrencyEntity initCurrency = currencyService.findByCode(initCurrencyCode);

        List<CurrencyEntity> userCurrencies = userInfoEntity.getCurrencies();
        if (userCurrencies.isEmpty() || !userCurrencies.contains(initCurrency)) {
            userCurrencies.add(initCurrency);
            userInfoService.save(userInfoEntity);
        }

        //3. У пользователя должен быть создан хотя бы один счёт
        List<AccountEntity> userAccounts = accountService.findAllByUserIdIgnoreCase(userId);
        if (userAccounts.isEmpty()) {
            Constants.INITIAL_ACCOUNTS.forEach(accountName -> {
                AccountCreate accountCreate = AccountCreate.builder()
                        .userInfoId(userId)
                        .currencyCode(initCurrencyCode)
                        .name(accountName)
                        .description("")
                        .build();

                accountService.createAccount(accountCreate);
            });
        }

        //4. У пользователя должна быть хотя бы одна расходная категория
        List<CategoryEntity> expCategories = categoryService.findAllExpenseByUserId(userId);
        if (expCategories.isEmpty()) {
            Constants.INITIAL_EXPENSE_CATEGORIES.forEach(categoryName -> {
                CategoryCreate categoryCreate = new CategoryCreate(categoryName, false);
                categoryService.create(userId, categoryCreate);
            });
        }

        //5. У пользователя должна быть хотя бы одна доходная категория
        List<CategoryEntity> incCategories = categoryService.findAllIncomeByUserId(userId);
        if (incCategories.isEmpty()) {
            Constants.INITIAL_INCOME_CATEGORIES.forEach(categoryName -> {
                CategoryCreate categoryCreate = new CategoryCreate(categoryName, true);
                categoryService.create(userId, categoryCreate);
            });
        }
    }
}
