package ru.homebuh.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.AccountBalance;
import ru.homebuh.core.controller.dto.AccountBalanceCreate;
import ru.homebuh.core.controller.dto.AccountCreate;
import ru.homebuh.core.controller.dto.AccountSummary;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.repository.CurrencyRepository;
import ru.homebuh.core.service.UserInfoService;
import ru.homebuh.core.util.Constants;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;

@Mapper(componentModel = "spring")
public abstract class AccountMapper {

    @Autowired
    protected UserInfoService userInfoRepository;

    @Autowired
    protected CurrencyRepository currencyRepository;

    protected UserInfoEntity fetchUserInfo(String id) {
        return userInfoRepository.findByIdIgnoreCase(id);
    }

    protected CurrencyEntity fetchCurrency(String code) {
        return currencyRepository.
                findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constants.NOT_FOUND_BY_PARAM_TEMPLATE, Constants.CURRENCY, "code", code)
                ));
    }

    @Mapping(target = "accountId", source = "id")
    @Mapping(target = "amount", source = "initialBalance")
    @Mapping(target = "currencyCode", source = "currency.code")
    public abstract AccountBalance map(AccountEntity source);

    public AccountSummary mapToSummary(Collection<AccountEntity> accounts) {
        if (accounts == null || accounts.isEmpty())
            return null;
        AccountEntity first = accounts.stream().findFirst().orElse(null);

        AccountSummary summary = new AccountSummary();
        summary.setDescription(first == null ? "" : first.getDescription()); //NOSONAR
        summary.setName(first == null ? "" : first.getName()); //NOSONAR

        summary.setInitialBalance(accounts.stream().map(this::map).toList());

        return summary;
    }

    public Collection<AccountEntity> map(String userId, AccountCreate source) {
        if (source == null)
            return Collections.emptyList();

        Collection<AccountBalanceCreate> balances = source.getInitialBalance();
        if (balances == null || balances.isEmpty())
            return Collections.emptyList();

        String name = source.getName() == null ? "" : source.getName();
        String description = source.getDescription() == null ? "" : source.getDescription();

        UserInfoEntity userInfo = fetchUserInfo(userId);

        return balances.stream().map(item -> {
            AccountEntity account = new AccountEntity();
            account.setName(name);
            account.setDescription(description);
            account.setUserInfo(userInfo);
            account.setInitialBalance(new BigDecimal(item.getAmount()));
            CurrencyEntity currency = fetchCurrency(item.getCurrencyCode());
            account.setCurrency(currency);
            return account;
        }).toList();
    }
}
