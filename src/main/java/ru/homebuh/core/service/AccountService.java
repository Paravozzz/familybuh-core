package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.*;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.mapper.AccountMapper;
import ru.homebuh.core.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserInfoService userInfoService;
    private final AccountRepository accountRepository;
    @Lazy
    @Autowired
    private CurrencyService currencyService;
    private final AccountMapper accountMapper;
    @Lazy
    @Autowired
    private AccountService self;

    /**
     * Найти все счета пользователя
     *
     * @param userId идентификатор пользователя
     * @return список счетов
     */
    public List<AccountEntity> findAllAccountsByUserId(String userId) {
        return accountRepository.findAllByUserIdIgnoreCase(userId);
    }

    /**
     * Найти все счета семьи
     *
     * @param userId идентификатор пользователя
     * @return список счетов
     */
    public List<AccountEntity> findAllFamilyAccountsByUserId(String userId) {
        List<UserInfoEntity> familyMembers = userInfoService.findAllFamilyMembers(userId);
        Set<String> familyMembersId = familyMembers.stream().map(UserInfoEntity::getId).collect(Collectors.toSet());
        return accountRepository.findAllByUserIdIgnoreCase(familyMembersId);
    }

    /**
     * Найти все счета пользователя и обобщить данные по ним
     *
     * @param userId идентификатор пользователя
     * @return список счетов
     */
    public Collection<AccountSummary> findAllAccountsSummaries(String userId) {
        return self.findAllAccountsByUserId(userId).stream()
                .collect(Collectors.groupingBy(AccountEntity::getName))
                .values().stream()
                .filter(Objects::nonNull)
                .map(accountMapper::mapToSummary)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Получить счет пользователя
     *
     * @param accountId идентификатор счёта
     * @return счет
     * @throws ResponseStatusException если счёт не найден
     */
    public AccountEntity getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account not found by accountId(" + accountId + ")"));
    }

    /**
     * Создать все счёта для определённой валюты
     *
     * @param userInfo пользователь
     * @param currency валюта счёта
     */
    @Transactional
    public void createUserAccountsWithCurrency(UserInfoEntity userInfo, CurrencyEntity currency) {
        List<AccountEntity> userAccounts = accountRepository.findAllByUserIdIgnoreCase(userInfo.getId());
        Map<String, String> accountNames = userAccounts.stream()
                .collect(Collectors.toMap(AccountEntity::getName, AccountEntity::getDescription, (first, second) -> first));
        List<AccountEntity> newAccounts = new ArrayList<>(accountNames.size());
        accountNames.forEach((name, description) -> {
            AccountEntity account = new AccountEntity();
            account.setCurrency(currency);
            account.setName(name);
            account.setUserInfo(userInfo);
            account.setDescription(description == null ? "" : description);
            newAccounts.add(account);
        });
        accountRepository.saveAll(newAccounts);
    }

    /**
     * Получить обобщённую информацию о счёте
     *
     * @param accountId идентификатор счёта
     * @return обобщённая информация о счёте
     */
    public AccountSummary findAccountSummaryByAccountId(Long accountId) {
        AccountEntity account = self.getAccount(accountId);
        UserInfoEntity userInfo = account.getUserInfo();
        String userId = userInfo.getId();
        String name = account.getName();
        Collection<AccountEntity> accountsByName = accountRepository.findAccountsByName(userId, name);
        return accountMapper.mapToSummary(accountsByName);
    }

    /**
     * Создать новый счёт
     *
     * @param userId        идентификатор пользователя
     * @param accountCreate данные для создания счёта
     * @return обобщённая информация о счёте
     */
    @Transactional
    public AccountSummary create(String userId, AccountCreate accountCreate) {
        UserInfoEntity userInfo = userInfoService.findByIdIgnoreCase(userId);

        //Проверить, что у семьи не существует счетов с таким именем
        List<UserInfoEntity> allFamilyMembers = userInfoService.findAllFamilyMembers(userId);
        Set<String> familyMembersIds = allFamilyMembers.stream().map(UserInfoEntity::getId).collect(Collectors.toSet());
        String name = accountCreate.getName() == null ? "" : accountCreate.getName();
        List<AccountEntity> existentAccounts = accountRepository.findAccountsByName(familyMembersIds, name);
        if (!existentAccounts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Account with name \"" + name + "\" already exists.");
        }

        Collection<AccountEntity> newAccounts = self.createNewAccounts(userInfo, accountCreate);
        newAccounts = accountRepository.saveAll(newAccounts);
        return accountMapper.mapToSummary(newAccounts);
    }

    @Transactional
    public AccountSummary update(String userId, AccountUpdate accountUpdate) {
        Collection<AccountBalanceUpdate> initialBalance =
                accountUpdate.getInitialBalance() == null ? Collections.emptyList() : accountUpdate.getInitialBalance();
        Map<Long, AccountBalanceUpdate> initialBalanceMap = initialBalance.stream()
                .collect(Collectors.toMap(AccountBalanceUpdate::getAccountId, a -> a, (a, b) -> a));
        Set<Long> accountIds = initialBalanceMap.keySet();
        List<AccountEntity> accountsForUpdate = accountRepository.findAccounts(accountIds);

        //Проверить, что у счетов одно имя
        Set<String> accountNames = accountsForUpdate.stream().map(AccountEntity::getName).collect(Collectors.toSet());
        if (accountNames.size() > 1)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Account IDs do not belong to the same group.");

        //Проверить, что у семьи не существует счетов с таким именем, кроме тех, которые хотим обновить
        List<UserInfoEntity> allFamilyMembers = userInfoService.findAllFamilyMembers(userId);
        Set<String> familyMembersIds = allFamilyMembers.stream().map(UserInfoEntity::getId).collect(Collectors.toSet());
        String name = accountUpdate.getName() == null ? "" : accountUpdate.getName();
        List<AccountEntity> existentAccounts = accountRepository.findAccountsByName(familyMembersIds, name).stream()
                .filter(account -> !accountIds.contains(account.getId())) //Кроме счетов, которые хотим обновить.
                .toList();
        if (!existentAccounts.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Account with name \"" + name + "\" already exists.");

        String description = accountUpdate.getDescription() == null ? "" : accountUpdate.getDescription();
        accountsForUpdate.forEach(accountEntity -> {
            accountEntity.setName(name);
            accountEntity.setDescription(description);
            AccountBalanceUpdate balance = initialBalanceMap.get(accountEntity.getId());
            if (balance != null) {
                accountEntity.setInitialBalance(new BigDecimal(balance.getAmount()));
            }
        });
        accountsForUpdate = accountRepository.saveAll(accountsForUpdate);
        return accountMapper.mapToSummary(accountsForUpdate);
    }

    private Collection<AccountEntity> createNewAccounts(UserInfoEntity userInfo, AccountCreate source) {
        if (source == null)
            return Collections.emptyList();

        Collection<AccountBalanceCreate> balances = source.getInitialBalance();
        if (balances == null || balances.isEmpty())
            return Collections.emptyList();

        String name = source.getName() == null ? "" : source.getName();
        String description = source.getDescription() == null ? "" : source.getDescription();


        return balances.stream().map(item -> {
            AccountEntity account = new AccountEntity();
            account.setName(name);
            account.setDescription(description);
            account.setUserInfo(userInfo);
            account.setInitialBalance(new BigDecimal(item.getAmount()));
            CurrencyEntity currency = currencyService.getByCode(item.getCurrencyCode());
            account.setCurrency(currency);
            return account;
        }).toList();
    }
}
