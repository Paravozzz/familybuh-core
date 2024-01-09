package ru.homebuh.core.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.homebuh.core.controller.dto.*;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CategoryEntity;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.domain.enums.OperationTypeEnum;

import java.time.OffsetDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ControllerServiceFacade {

    private final AccountService accountService;
    private final CategoryService categoryService;
    private final CurrencyService currencyService;
    private final InitService initService;
    private final OperationService operationService;
    private final SettingService settingService;
    private final AuthorizationService authorizationService;
    private final UserInfoService userInfoService;

    public Collection<AccountEntity> findAllFamilyAccountsByUserId(String userId) {
        return accountService.findAllFamilyAccountsByUserId(userId);
    }

    public AccountSummary createAccount(String userId, AccountCreate accountCreate) {
        return accountService.create(userId, accountCreate);
    }

    public AccountSummary updateAccount(String userId, AccountUpdate accountUpdate) {
        //проверяем, что обновляемые счета принадлежат пользователю или семье
        accountUpdate.getInitialBalance()
                .forEach(b -> authorizationService.isAuthorized(userId, b.getAccountId()));
        return accountService.update(userId, accountUpdate);
    }

    public Collection<AccountSummary> findAllAccountsSummaries(String userid) {
        return accountService.findAllAccountsSummaries(userid);
    }

    public AccountSummary findUserAccountSummaryByAccountId(String userId, Long accountId) {
        //TODO: проверить что запрашиваемый счёт принадлежит пользователю или семье
        return accountService.findAccountSummaryByAccountId(accountId);
    }

    public Collection<CategoryEntity> findAllUserCategoriesByUserId(String userId) {
        return categoryService.findAllByUserId(userId);
    }

    public CategoryEntity findUserCategoryById(String userId, Long categoryId) {
        //TODO: проверить что категория принадлежит пользователю или семье
        return categoryService.getCategory(categoryId);
    }

    public CategoryEntity createCategory(String userId, CategoryCreate categoryCreate) {
        return categoryService.create(userId, categoryCreate);
    }

    public CategoryEntity updateCategory(String userId, Long categoryId, CategoryUpdate categoryUpdate) {
        //TODO: проверить что категория принадлежит пользователю или семье
        return categoryService.update(categoryId, categoryUpdate);
    }

    public Collection<CurrencyEntity> findAllCurrencies() {
        return currencyService.findAll();
    }

    public CurrencyEntity getCurrencyByCode(String currencyCode) {
        return currencyService.getByCode(currencyCode);
    }

    public Collection<CurrencyEntity> findAllCurrenciesByUserId(String userId) {
        return currencyService.findAllByUserId(userId);
    }

    public CurrencyEntity attachCurrencyToUser(String userId, String currencyCode) {
        return currencyService.attachCurrencyToUser(userId, currencyCode);
    }

    public CurrencyEntity detachCurrencyToUser(String userId, String currencyCode) {
        return currencyService.detachCurrencyToUser(userId, currencyCode);
    }

    public boolean isUserInit(String userId) {
        return initService.isUserInit(userId);
    }

    public void initUser(String userId, InitCreate initCreate) {
        initService.initUser(userId, initCreate);
    }

    public OperationDto expenseCreate(String userId, OperationCreate operationCreate) {
        return operationService.expenseCreate(userId, operationCreate);
    }

    public OperationDto incomeCreate(String userId, OperationCreate operationCreate) {
        return operationService.incomeCreate(userId, operationCreate);
    }

    public Collection<OperationDto> findOperationsByPredicate(String userId, Predicate predicate) {
        return operationService.findByPredicate(userId, predicate);
    }

    public Collection<OperationDto> dailyOperation(String userId, OperationTypeEnum operationType, OffsetDateTime date) {
        return operationService.dailyOperation(userId, operationType, date);
    }

    public SettingDto findUserSettingByName(String userId, String name) {
        return settingService.findUserSettingByName(userId, name);
    }

    public SettingDto saveSetting(String userId, SettingCreate settingCreate) {
        return settingService.save(userId, settingCreate);
    }
}
