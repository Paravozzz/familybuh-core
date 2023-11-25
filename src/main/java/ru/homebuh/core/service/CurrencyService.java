package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.MasterAccountCreate;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.repository.CurrencyRepository;
import ru.homebuh.core.util.Constants;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final UserInfoService userInfoService;
    private final AccountService accountService;

    public List<CurrencyEntity> findAllByUserId(String id) {
        return currencyRepository.findAllByUserId(id);
    }

    public List<CurrencyEntity> findAll() {
        return currencyRepository.findAll();
    }

    @Transactional
    public CurrencyEntity attachCurrencyToUser(String userId, String currencyCode) {
        UserInfoEntity userInfoEntity = userInfoService.findByIdIgnoreCase(userId);

        CurrencyEntity currencyEntity = currencyRepository.findByCodeIgnoreCase(currencyCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constants.NOT_FOUND_BY_PARAM_TEMPLATE, Constants.CURRENCY, "code", currencyCode)));

        if (!userInfoEntity.getCurrencies().contains(currencyEntity)) {
            userInfoEntity.getCurrencies().add(currencyEntity);
            userInfoService.save(userInfoEntity);

            accountService.createMasterAccount(new MasterAccountCreate(currencyEntity.getCode(), userInfoEntity.getId()));
        }

        return currencyEntity;
    }

    @Transactional
    public CurrencyEntity detachCurrencyToUser(String userId, String currencyCode) {
        userInfoService.isUserExists(userId);

        CurrencyEntity currencyEntity = currencyRepository.findByCodeIgnoreCase(currencyCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constants.NOT_FOUND_BY_PARAM_TEMPLATE, Constants.CURRENCY, "code", currencyCode)));

        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, currencyEntity.toString());
    }

    /**
     * Поиск валюты по буквенному коду
     *
     * @param currencyCode буквенный код
     * @return валюта
     * @throws ResponseStatusException если валюта не найдена в справочнике валют
     */
    public CurrencyEntity findByCode(String currencyCode) {
        return currencyRepository.findByCodeIgnoreCase(currencyCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constants.NOT_FOUND_BY_PARAM_TEMPLATE, Constants.CURRENCY, "code", currencyCode)));
    }

    /**
     * Проверяет наличие валюты с кодом в справочнике валют
     *
     * @param currencyCode буквенный код
     * @throws ResponseStatusException если валюта не найдена в справочнике валют
     */
    public void isCurrencyExists(String currencyCode) {
        findByCode(currencyCode);
    }
}
