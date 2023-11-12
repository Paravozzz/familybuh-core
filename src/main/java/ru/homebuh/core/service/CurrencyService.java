package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.MasterAccountCreate;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.mapper.AccountMapper;
import ru.homebuh.core.repository.AccountRepository;
import ru.homebuh.core.repository.CurrencyRepository;
import ru.homebuh.core.repository.UserInfoRepository;
import ru.homebuh.core.util.Constatnts;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final UserInfoRepository userInfoRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public List<CurrencyEntity> findAllByUserId(String id) {
        return currencyRepository.findAllByUserId(id);
    }

    public List<CurrencyEntity> findAll() {
        return currencyRepository.findAll();
    }

    @Transactional
    public CurrencyEntity attachCurrencyToUser(String userId, String currencyCode) {
        Optional<UserInfoEntity> optionalUserInfo = userInfoRepository.findByIdIgnoreCase(userId);
        UserInfoEntity userInfoEntity = optionalUserInfo
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constatnts.NOT_FOUND_BY_ID_TEMPLATE, "User", "id", userId)));

        CurrencyEntity currencyEntity = currencyRepository.findByCodeIgnoreCase(currencyCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constatnts.NOT_FOUND_BY_ID_TEMPLATE, "Currency", "code", currencyCode)));

        if (!userInfoEntity.getCurrencies().contains(currencyEntity)) {
            userInfoEntity.getCurrencies().add(currencyEntity);
            userInfoRepository.save(userInfoEntity);

            AccountEntity masterAccountEntity = accountMapper.map(new MasterAccountCreate(currencyEntity.getCode(), userInfoEntity.getId()));
            accountRepository.save(masterAccountEntity);
        }

        return currencyEntity;
    }

    @Transactional
    public CurrencyEntity detachCurrencyToUser(String userId, String currencyCode) {
        Optional<UserInfoEntity> optionalUserInfo = userInfoRepository.findByIdIgnoreCase(userId);
        optionalUserInfo
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constatnts.NOT_FOUND_BY_ID_TEMPLATE, "User", "id", userId)));

        CurrencyEntity currencyEntity = currencyRepository.findByCodeIgnoreCase(currencyCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constatnts.NOT_FOUND_BY_ID_TEMPLATE, "Currency", "code", currencyCode)));

        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, currencyEntity.toString());
    }

}
