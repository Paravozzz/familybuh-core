package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.repository.AccountRepository;
import ru.homebuh.core.repository.CurrencyRepository;
import ru.homebuh.core.util.Constants;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final UserInfoService userInfoService;
    private final AccountRepository accountRepository;

    /**
     * Все валюты пользователя и его семьи
     *
     * @param userId идентификатор пользователя
     * @return
     */
    public List<CurrencyEntity> findAllFamilyCurrenciesByUserId(String userId) {
        List<UserInfoEntity> familyMembers = userInfoService.findAllFamilyMembers(userId);
        Set<String> membersIds = familyMembers.stream().map(UserInfoEntity::getId).collect(Collectors.toSet());
        return currencyRepository.findAllByUserIdIn(membersIds).stream().distinct().toList();
    }

    public List<CurrencyEntity> findAll() {
        return currencyRepository.findAll();
    }

    /**
     * Добавить валюту в пользовательский список валют
     *
     * @param userId
     * @param currencyCode
     * @return
     */
    @Transactional
    public CurrencyEntity attachCurrencyToUser(String userId, String currencyCode) {
        UserInfoEntity userInfo = userInfoService.getUserInfo(userId);

        CurrencyEntity currencyEntity = currencyRepository.findByCodeIgnoreCase(currencyCode)
                .orElseThrow(notFoundByCodeExceptionSupplier(currencyCode));

        if (!userInfo.getCurrencies().contains(currencyEntity)) {
            //Добавляем валюту пользователю
            userInfo.getCurrencies().add(currencyEntity);
            userInfoService.save(userInfo);

            //При добавлении новой валюты пользователю необходимо создать счета для этой валюты
            List<AccountEntity> userAccounts = accountRepository.findAllByUserIdIgnoreCase(userInfo.getId());
            Map<String, String> accountNames = userAccounts.stream()
                    .collect(Collectors.toMap(AccountEntity::getName, AccountEntity::getDescription, (first, second) -> first));
            List<AccountEntity> newAccounts = new ArrayList<>(accountNames.size());
            accountNames.forEach((name, description) -> {
                AccountEntity account = new AccountEntity();
                account.setCurrency(currencyEntity);
                account.setName(name);
                account.setUserInfo(userInfo);
                account.setDescription(description == null ? "" : description);
                account.setInitialBalance(new BigDecimal(0));
                newAccounts.add(account);
            });
            accountRepository.saveAll(newAccounts);

        }

        return currencyEntity;
    }

    /**
     * Удалить валюту из пользовательского спискв валют
     *
     * @param userId
     * @param currencyCode
     * @return
     */
    @Transactional
    public CurrencyEntity detachCurrencyToUser(String userId, String currencyCode) {
        userInfoService.isUserExists(userId);

        CurrencyEntity currencyEntity = currencyRepository.findByCodeIgnoreCase(currencyCode)
                .orElseThrow(notFoundByCodeExceptionSupplier(currencyCode));

        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, currencyEntity.toString());
    }

    /**
     * Поиск валюты по буквенному коду
     *
     * @param currencyCode буквенный код
     * @return валюта
     * @throws ResponseStatusException если валюта не найдена в справочнике валют
     */
    public CurrencyEntity getByCode(String currencyCode) {
        return currencyRepository.findByCodeIgnoreCase(currencyCode)
                .orElseThrow(notFoundByCodeExceptionSupplier(currencyCode));
    }

    public static Supplier<ResponseStatusException> notFoundByCodeExceptionSupplier(String currencyCode) {
        return () -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                MessageFormat.format(Constants.NOT_FOUND_BY_PARAM_TEMPLATE, Constants.CURRENCY, "code", currencyCode));
    }
}
