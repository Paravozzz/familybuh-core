package ru.homebuh.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import ru.homebuh.core.controller.dto.AccountBalance;
import ru.homebuh.core.controller.dto.AccountSummary;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.service.CurrencyService;
import ru.homebuh.core.service.UserInfoService;

import java.util.Collection;

@Mapper(componentModel = "spring")
public abstract class AccountMapper {

    @Autowired
    protected UserInfoService userInfoRepository;

    @Lazy
    @Autowired
    protected CurrencyService currencyService;

    protected UserInfoEntity fetchUserInfo(String id) {
        return userInfoRepository.getUserInfo(id);
    }

    protected CurrencyEntity fetchCurrency(String code) {
        return currencyService.getByCode(code);
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
}
