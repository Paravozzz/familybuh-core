package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.AccountCreate;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.mapper.AccountMapper;
import ru.homebuh.core.repository.AccountRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Transactional
    public void createAccount(AccountCreate accountCreate) {
        accountRepository.save(accountMapper.map(accountCreate));
    }

    /**
     * Найти все счета пользователя
     *
     * @param id идентификатор пользователя
     * @return список обычных счетов
     */
    public List<AccountEntity> findAllByUserIdIgnoreCase(String id) {
        return accountRepository.findAllByUserIdIgnoreCase(id);
    }


    /**
     * Получить счет пользователя
     *
     * @param userId       идентификатор пользователя
     * @param accountId    идентификатор счёта
     * @param currencyCode буквенный код валюты счёта
     * @return обычный счет
     * @throws ResponseStatusException если счёт не найден
     */
    public AccountEntity getUserAccount(String userId, Long accountId, String currencyCode) {
        return accountRepository.findAccount(userId, accountId, currencyCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Account not found by accountId(" + accountId + ") and currencyCode(" + currencyCode + ")"));
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
}
