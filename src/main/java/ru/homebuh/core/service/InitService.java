package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.InitCreate;
import ru.homebuh.core.controller.dto.MasterAccountCreate;
import ru.homebuh.core.controller.dto.UserInfoCreate;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.repository.CurrencyRepository;
import ru.homebuh.core.repository.UserInfoRepository;
import ru.homebuh.core.util.Constatnts;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InitService {

    private final UserInfoService userInfoService;
    private final AccountService accountService;
    private final UserInfoRepository userInfoRepository;
    private final CurrencyRepository currencyRepository;

    private static Set<CurrencyEntity> findCurrenciesWithoutMasterAccount(Set<CurrencyEntity> userCurrencies, List<AccountEntity> allMasterAccounts) {
        Set<CurrencyEntity> currenciesWithoutMasterAccounts = new HashSet<>(userCurrencies);
        Set<CurrencyEntity> masterCurrencies = allMasterAccounts.stream().map(AccountEntity::getCurrency).collect(Collectors.toSet());
        currenciesWithoutMasterAccounts.removeAll(masterCurrencies);
        return currenciesWithoutMasterAccounts;
    }

    @Transactional
    public void initUser(String userId, InitCreate initCreate) {
        final UserInfoEntity userInfoEntity;
        Optional<UserInfoEntity> optionalUserInfo = userInfoRepository.findByIdIgnoreCase(userId);
        userInfoEntity = optionalUserInfo.orElseGet(() -> userInfoService.create(new UserInfoCreate(userId)));

        final String initCurrencyId = initCreate.getCurrencyId();
        CurrencyEntity initCurrency = currencyRepository.findById(initCurrencyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constatnts.NOT_FOUND_BY_ID_TEMPLATE, "Currency", "id", initCurrencyId)));

        Set<CurrencyEntity> userCurrencies = userInfoEntity.getCurrencies();
        if (userCurrencies.isEmpty()) {
            userCurrencies.add(initCurrency);
            createMaserByCurrency(userId, initCurrency);
        } else {
            List<AccountEntity> allMasterAccounts = accountService.findAllMasterByUserIdIgnoreCase(userId);
            Set<CurrencyEntity> currenciesWithoutMasterAccount = findCurrenciesWithoutMasterAccount(userCurrencies, allMasterAccounts);
            for (CurrencyEntity currency : currenciesWithoutMasterAccount) {
                createMaserByCurrency(userId, currency);
            }
        }

    }

    private void createMaserByCurrency(String userId, CurrencyEntity initCurrency) {
        final Optional<AccountEntity> optionalMasterAccount = accountService.findMasterByUserIdIgnoreCaseAndCurrencyId(userId, initCurrency.getId());
        if (optionalMasterAccount.isEmpty()) {
            accountService.createMasterAccount(new MasterAccountCreate(initCurrency.getCode(), userId));
        }
    }

    private boolean checkInit(String userId) {
        //1. Пользователь должен существовать
        final Optional<UserInfoEntity> userInfoEntityOptional = userInfoRepository.findByIdIgnoreCase(userId);
        if (userInfoEntityOptional.isEmpty())
            return false;

        //2. У пользователя должна быть задана хотя бы одна валюта
        final UserInfoEntity userInfoEntity = userInfoEntityOptional.get();
        Set<CurrencyEntity> userCurrencies = userInfoEntity.getCurrencies();
        if (userCurrencies.isEmpty())
            return false;

        //3. У пользователя должны быть созданы мастер-счёта
        final List<AccountEntity> allMasterAccounts = accountService.findAllMasterByUserIdIgnoreCase(userId);
        if (allMasterAccounts.isEmpty())
            return false;

        //3. У каждой валюты, которая есть у пользователя, должен быть создан мастер-счёт
        return findCurrenciesWithoutMasterAccount(userCurrencies, allMasterAccounts).isEmpty();
    }

    /**
     * Проверяет выполнена ли первоначальная настройка приложения для текущего пользователя
     *
     * @param userId идентификатор пользователя
     * @return true, если начальная настройка приложения для текущего пользователя выполнена
     */
    @Transactional
    public ResponseEntity<Boolean> isUserInitResponse(String userId) {
        if (checkInit(userId)) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }


}
