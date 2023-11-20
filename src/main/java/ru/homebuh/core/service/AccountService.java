package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public AccountEntity createMasterAccount(MasterAccountCreate masterAccountCreate) {
        return accountRepository.save(accountMapper.map(masterAccountCreate));
    }

    /**
     * Найти все обычные счета пользователя
     *
     * @param id идентификатор пользователя
     * @return список обычных счетов
     */
    @Transactional
    public List<AccountEntity> findAllByUserIdIgnoreCase(String id) {
        return accountRepository.findAllByUserIdIgnoreCase(id);
    }

    /**
     * Найти все мастер-счета пользователя
     *
     * @param id идентификатор пользователя
     * @return список мастер-счетов
     */
    @Transactional
    public List<AccountEntity> findAllMasterByUserIdIgnoreCase(String id) {
        return accountRepository.findAllMasterByUserIdIgnoreCase(id);
    }

    /**
     * Найти мастер-счет пользователя
     *
     * @param userId     идентификатор пользователя
     * @param currencyId цифровой код валюты
     * @return мастер-счет
     */
    @Transactional
    public Optional<AccountEntity> findMasterByUserIdIgnoreCaseAndCurrencyId(String userId, String currencyId) {
        return accountRepository.findMasterByUserIdIgnoreCaseAndCurrencyId(userId, currencyId);
    }
}
