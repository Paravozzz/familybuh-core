package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.homebuh.core.controller.dto.AccountCreate;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.mapper.AccountMapper;
import ru.homebuh.core.repository.AccountRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Transactional
    public AccountEntity create(AccountCreate accountCreate) {
        return accountRepository.save(accountMapper.map(accountCreate));
    }
    @Transactional
    public Collection<AccountEntity> findAllByUserId(String id) {

        return accountRepository.findAllByUserId(id);
    }

    public Optional<AccountEntity> findById(Long accountId) {
        return accountRepository.findById(accountId);
    }
}
