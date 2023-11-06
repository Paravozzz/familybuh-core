package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.UserInfoCreate;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.mapper.UserInfoMapper;
import ru.homebuh.core.repository.CurrencyRepository;
import ru.homebuh.core.repository.UserInfoRepository;
import ru.homebuh.core.util.Constatnts;

import java.text.MessageFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserInfoService {
    private final UserInfoRepository userInfoRepository;
    private final CurrencyRepository currencyRepository;
    private final UserInfoMapper mapper;

    @Transactional
    public UserInfoEntity create(UserInfoCreate userInfoCreate) {
        return userInfoRepository.save(mapper.map(userInfoCreate));
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
