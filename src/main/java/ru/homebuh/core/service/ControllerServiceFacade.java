package ru.homebuh.core.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.homebuh.core.controller.dto.*;
import ru.homebuh.core.domain.*;
import ru.homebuh.core.domain.enums.OperationTypeEnum;
import ru.homebuh.core.mapper.ExchangeMapper;
import ru.homebuh.core.mapper.OperationMapper;
import ru.homebuh.core.mapper.TransferMapper;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

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
    private final OperationMapper operationMapper;
    private final UserInfoService userInfoService;
    private final TransferService transferService;
    private final TransferMapper transferMapper;
    private final ExchangeService exchangeService;
    private final ExchangeMapper exchangeMapper;

    public Collection<AccountEntity> findAllFamilyAccountsByUserId(String userId) {
        return accountService.findAllFamilyAccountsByUserId(userId);
    }

    public AccountSummary createAccount(String userId, AccountCreate accountCreate) {
        UserInfoEntity userInfo = userInfoService.getUserInfo(userId);
        return accountService.create(userInfo, accountCreate);
    }

    public AccountSummary updateAccount(String userId, AccountUpdate accountUpdate) {
        //проверяем, что обновляемые счета принадлежат пользователю или семье
        accountUpdate.getInitialBalance()
                .forEach(b -> authorizationService.account(userId, b.getAccountId()));
        return accountService.update(userId, accountUpdate);
    }

    public Collection<AccountSummary> findAllFamilyAccountsSummaries(String userid) {
        return accountService.findAllFamilyAccountsSummaries(userid);
    }

    public AccountSummary findUserAccountSummaryByAccountId(String userId, Long accountId) {
        //проверяем, что счет принадлежит пользователю или семье
        authorizationService.account(userId, accountId);
        return accountService.findAccountSummaryByAccountId(accountId);
    }

    public Collection<CategoryEntity> findAllFamilyCategoriesByUserId(String userId) {
        return categoryService.findAllFamilyCategoriesByUserId(userId);
    }

    public CategoryEntity findUserCategoryById(String userId, Long categoryId) {
        //проверяем, что категория принадлежит пользователю или семье
        authorizationService.category(userId, categoryId);
        return categoryService.getCategory(categoryId);
    }

    public CategoryEntity createCategory(String userId, CategoryCreate categoryCreate) {
        UserInfoEntity userInfo = userInfoService.getUserInfo(userId);
        return categoryService.create(userInfo, categoryCreate);
    }

    public CategoryEntity updateCategory(String userId, Long categoryId, CategoryUpdate categoryUpdate) {
        //проверяем, что категория принадлежит пользователю или семье
        authorizationService.category(userId, categoryId);
        return categoryService.update(categoryId, categoryUpdate);
    }

    public Collection<CurrencyEntity> findAllCurrencies() {
        return currencyService.findAll();
    }

    public CurrencyEntity getCurrencyByCode(String currencyCode) {
        return currencyService.getByCode(currencyCode);
    }

    public Collection<CurrencyEntity> findAllFamilyCurrenciesByUserId(String userId) {
        return currencyService.findAllFamilyCurrenciesByUserId(userId);
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

    public OperationDto createExpense(String userId, OperationCreate operationCreate) {
        UserInfoEntity userInfo = userInfoService.getUserInfo(userId);
        OperationEntity expense = operationService.createExpense(userInfo, operationCreate);
        return operationMapper.mapToDto(expense);
    }

    public OperationDto createIncome(String userId, OperationCreate operationCreate) {
        UserInfoEntity userInfo = userInfoService.getUserInfo(userId);
        OperationEntity income = operationService.createIncome(userInfo, operationCreate);
        return operationMapper.mapToDto(income);
    }

    public Collection<OperationDto> findOperationsByPredicate(String userId, Predicate predicate) {
        List<OperationEntity> operations = operationService.findByPredicate(userId, predicate);
        return operationMapper.mapToDto(operations);
    }

    public Collection<OperationDto> dailyOperations(String userId, OperationTypeEnum operationType, OffsetDateTime date) {
        List<OperationEntity> operations = operationService.familyDailyOperations(userId, operationType, date);
        return operationMapper.mapToDto(operations);
    }

    public SettingDto findUserSettingByName(String userId, String name) {
        return settingService.findUserSettingByName(userId, name);
    }

    public SettingDto saveSetting(String userId, SettingCreate settingCreate) {
        return settingService.save(userId, settingCreate);
    }

    public TransferDto createTransfer(String userId, TransferCreate transferCreate) {
        UserInfoEntity userInfo = userInfoService.getUserInfo(userId);
        TransferEntity transferEntity = transferService.transferCreate(userInfo, transferCreate);
        return transferMapper.mapToDto(transferEntity);
    }

    public Collection<TransferDto> findTransfersByPredicate(String userId, Predicate predicate) {
        Collection<TransferEntity> transfers = transferService.findByPredicate(userId, predicate);
        return transferMapper.mapToDto(transfers);
    }

    public Collection<TransferDto> dailyTransfers(String userId, OffsetDateTime date) {
        Collection<TransferEntity> transfers = transferService.findDailyTransfers(userId, date);
        return transferMapper.mapToDto(transfers);
    }

    public ExchangeDto createExchange(String userId, ExchangeCreate exchangeCreate) {
        UserInfoEntity userInfo = userInfoService.getUserInfo(userId);
        ExchangeEntity exchange = exchangeService.exchangeCreate(userInfo, exchangeCreate);
        return exchangeMapper.mapToDto(exchange);
    }

    public Collection<ExchangeDto> findExchangesByPredicate(String userId, Predicate predicate) {
        Collection<ExchangeEntity> transfers = exchangeService.findByPredicate(userId, predicate);
        return exchangeMapper.mapToDto(transfers);
    }

    public Collection<ExchangeDto> dailyExchanges(String userId, OffsetDateTime date) {
        Collection<ExchangeEntity> exchanges = exchangeService.findDailyExchanges(userId, date);
        return exchangeMapper.mapToDto(exchanges);
    }
}
