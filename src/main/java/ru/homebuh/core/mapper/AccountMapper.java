package ru.homebuh.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.AccountBalance;
import ru.homebuh.core.controller.dto.AccountCreate;
import ru.homebuh.core.controller.dto.AccountSummary;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.repository.CurrencyRepository;
import ru.homebuh.core.service.UserInfoService;
import ru.homebuh.core.util.Constants;

import java.text.MessageFormat;
import java.util.Collection;

@Mapper(componentModel = "spring")
public abstract class AccountMapper {

    @Autowired
    protected UserInfoService userInfoRepository;

    @Autowired
    protected CurrencyRepository currencyRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userInfo", expression = "java(fetchUserInfo(source.getUserInfoId().toLowerCase()))")
    @Mapping(target = "currency", expression = "java(fetchCurrency(source.getCurrencyCode()))")
    public abstract AccountEntity map(AccountCreate source);

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
}
