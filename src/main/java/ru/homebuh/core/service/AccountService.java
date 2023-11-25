package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.AccountCreate;
import ru.homebuh.core.controller.dto.MasterAccountCreate;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.mapper.AccountMapper;
import ru.homebuh.core.repository.AccountRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Transactional
    public void createMasterAccount(MasterAccountCreate masterAccountCreate) {
        accountRepository.save(accountMapper.map(masterAccountCreate));
    }

    @Transactional
    public void createAccount(AccountCreate accountCreate) {
        accountRepository.save(accountMapper.map(accountCreate));
    }

    /**
     * Найти все обычные счета пользователя
     *
     * @param id идентификатор пользователя
     * @return список обычных счетов
     */
    public List<AccountEntity> findAllByUserIdIgnoreCase(String id) {
        return accountRepository.findAllByUserIdIgnoreCase(id);
    }

    /**
     * Найти все мастер-счета пользователя
     *
     * @param id идентификатор пользователя
     * @return список мастер-счетов
     */
    public List<AccountEntity> findAllMasterByUserIdIgnoreCase(String id) {
        return accountRepository.findAllMasterByUserIdIgnoreCase(id);
    }

    /**
     * Найти мастер-счет пользователя
     *
     * @param userId       идентификатор пользователя
     * @param currencyCode буквенный код валюты
     * @return мастер-счет
     */
    public Optional<AccountEntity> findMasterByUserIdIgnoreCaseAndCurrencyCode(String userId, String currencyCode) {
        return accountRepository.findMasterByUserIdIgnoreCaseAndCurrencyCode(userId, currencyCode);
    }

    /**
     * Получить мастер-счет пользователя
     *
     * @param userId       идентификатор пользователя
     * @param currencyCode буквенный код валюты
     * @return мастер-счет
     * @throws ResponseStatusException если мастер-счёт не найден
     */
    public AccountEntity getUserMasterAccount(String userId, String currencyCode) {
        return accountRepository.findMasterByUserIdIgnoreCaseAndCurrencyCode(userId, currencyCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Master account not found by currencyCode(" + currencyCode + ")"));
    }

    /**
     * Получить обычный счет пользователя
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
}
