package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
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
import ru.homebuh.core.repository.CurrencyRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static ru.homebuh.core.service.CurrencyService.notFoundByCodeExceptionSupplier;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserInfoService userInfoService;
    private final AccountRepository accountRepository;
    private final CurrencyRepository currencyRepository;
    private final AccountMapper accountMapper;

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
     * Найти все счета пользователя и его семьи и обобщить данные по ним
     *
     * @param userId идентификатор пользователя
     * @return список счетов
     */
    public Collection<AccountSummary> findAllFamilyAccountsSummaries(String userId) {
        List<UserInfoEntity> familyMembers = userInfoService.findAllFamilyMembers(userId);
        Set<String> familyMembersId = familyMembers.stream().map(UserInfoEntity::getId).collect(Collectors.toSet());
        List<AccountEntity> familyAccounts = accountRepository.findAllByUserIdIgnoreCase(familyMembersId);
        return familyAccounts.stream()
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
                .orElseThrow(notFoundByIdExceptionSupplier(accountId));
    }

    public static Supplier<ResponseStatusException> notFoundByIdExceptionSupplier(Long accountId) {
        return () -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Account not found by accountId(" + accountId + ")");
    }


    /**
     * Получить обобщённую информацию о счёте
     *
     * @param accountId идентификатор счёта
     * @return обобщённая информация о счёте
     */
    public AccountSummary findAccountSummaryByAccountId(Long accountId) {
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(notFoundByIdExceptionSupplier(accountId));
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
        UserInfoEntity userInfo = userInfoService.getUserInfo(userId);

        //Проверить, что у семьи не существует счетов с таким именем
        List<UserInfoEntity> allFamilyMembers = userInfoService.findAllFamilyMembers(userId);
        Set<String> familyMembersIds = allFamilyMembers.stream().map(UserInfoEntity::getId).collect(Collectors.toSet());
        String name = accountCreate.getName() == null ? "" : accountCreate.getName();
        List<AccountEntity> existentAccounts = accountRepository.findAccountsByName(familyMembersIds, name);
        if (!existentAccounts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Account with name \"" + name + "\" already exists.");
        }

        Collection<AccountBalanceCreate> balances = accountCreate.getInitialBalance();
        if (balances == null || balances.isEmpty()) {
            Set<String> userCurrencies = userInfo.getCurrencies().stream().map(currency -> currency.getCode()).collect(Collectors.toSet());
            balances = userCurrencies.stream()
                    .map(currencyCode -> AccountBalanceCreate.builder()
                            .currencyCode(currencyCode)
                            .amount("0")
                            .build())
                    .toList();
        }

        String description = accountCreate.getDescription() == null ? "" : accountCreate.getDescription();


        Collection<AccountEntity> newAccounts = balances.stream().map(item -> {
            AccountEntity account = new AccountEntity();
            account.setName(name);
            account.setDescription(description);
            account.setUserInfo(userInfo);
            account.setInitialBalance(new BigDecimal(item.getAmount()));
            String currencyCode = item.getCurrencyCode();
            CurrencyEntity currency = currencyRepository.findByCodeIgnoreCase(currencyCode).orElseThrow(notFoundByCodeExceptionSupplier(currencyCode));
            account.setCurrency(currency);
            return account;
        }).toList();

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

}
