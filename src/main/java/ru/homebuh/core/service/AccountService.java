package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.AccountBalanceUpdate;
import ru.homebuh.core.controller.dto.AccountCreate;
import ru.homebuh.core.controller.dto.AccountSummary;
import ru.homebuh.core.controller.dto.AccountUpdate;
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

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    /**
     * Найти все счета пользователя
     *
     * @param userId идентификатор пользователя
     * @return список счетов
     */
    public List<AccountEntity> findAllByUserIdIgnoreCase(String userId) {
        return accountRepository.findAllByUserIdIgnoreCase(userId);
    }

    /**
     * Найти все счёта пользователя и обобщить данные по ним
     *
     * @param userId идентификатор пользователя
     * @return список счетов
     */
    public Collection<AccountSummary> findAllSummaries(String userId) {
        return accountRepository.findAllByUserIdIgnoreCase(userId).stream()
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
     * @param userId    идентификатор пользователя
     * @param accountId идентификатор счёта
     * @return счет
     * @throws ResponseStatusException если счёт не найден
     */
    public AccountEntity getUserAccount(String userId, Long accountId) {
        return accountRepository.findAccount(userId, accountId)
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
     * @param userId    идентификатор пользователя
     * @param accountId идентификатор счёта
     * @return обобщённая информация о счёте
     */
    public AccountSummary findUserAccountSummaryByAccountId(String userId, Long accountId) {
        AccountEntity account = this.getUserAccount(userId, accountId);
        String accountName = account.getName();
        Collection<AccountEntity> accounts = accountRepository.findAccounts(userId, accountName);
        return accountMapper.mapToSummary(accounts);
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
        //Проверить, что у пользователя еще не существует счетов с таким именем
        String name = accountCreate.getName() == null ? "" : accountCreate.getName();
        Collection<AccountEntity> existentAccounts = accountRepository.findAccounts(userId, name);
        if (!existentAccounts.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Account with name \"" + name + "\" already exists.");

        Collection<AccountEntity> newAccounts = accountMapper.map(userId, accountCreate);
        newAccounts = accountRepository.saveAll(newAccounts);
        return accountMapper.mapToSummary(newAccounts);
    }

    @Transactional
    public AccountSummary update(String userId, AccountUpdate accountUpdate) {
        Collection<AccountBalanceUpdate> initialBalance =
                accountUpdate.getInitialBalance() == null ? Collections.emptyList() : accountUpdate.getInitialBalance();
        Map<Long, AccountBalanceUpdate> initialBalanceMap = initialBalance.stream().collect(Collectors.toMap(AccountBalanceUpdate::getAccountId, a -> a, (a, b) -> a));
        Set<Long> accountIds = initialBalanceMap.keySet();
        Collection<AccountEntity> accountsForUpdate = accountRepository.findAccounts(userId, accountIds);

        //Проверить, что у счетов одно имя
        Set<String> accountNames = accountsForUpdate.stream().map(AccountEntity::getName).collect(Collectors.toSet());
        if (accountNames.size() > 1)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Account IDs do not belong to the same group.");

        //Проверить, что у пользователя еще не существует счетов с таким именем
        String name = accountUpdate.getName() == null ? "" : accountUpdate.getName();
        Collection<AccountEntity> existentAccounts = accountRepository.findAccounts(userId, name);
        if (!existentAccounts.isEmpty()) {
            Set<Long> existentAccountIds = existentAccounts.stream().map(AccountEntity::getId).collect(Collectors.toSet());
            existentAccountIds.removeAll(accountIds);
            if (!existentAccountIds.isEmpty())
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Account with name \"" + name + "\" already exists.");
        }

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
}
