package ru.homebuh.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.AccountBalance;
import ru.homebuh.core.controller.dto.AccountSummary;
import ru.homebuh.core.controller.dto.AccountUpdate;
import ru.homebuh.core.domain.AccountEntity;

import java.util.Collection;
import java.util.function.Supplier;

@Mapper(componentModel = "spring")
public abstract class AccountMapper {

    @Mapping(target = "accountId", source = "id")
    @Mapping(target = "amount", source = "initialBalance")
    @Mapping(target = "currencyCode", source = "currency.code")
    public abstract AccountBalance map(AccountEntity source);

    public abstract AccountUpdate mapSummaryToUpdate(AccountSummary source);

    public AccountSummary mapToSummary(Collection<AccountEntity> accounts) {
        if (accounts == null || accounts.isEmpty())
            return null;
        AccountEntity account = accounts.stream().findFirst().orElseThrow(notFoundByIdExceptionSupplier());

        AccountSummary summary = new AccountSummary();
        summary.setDescription(account.getDescription()); //NOSONAR
        summary.setName(account.getName()); //NOSONAR

        summary.setInitialBalance(accounts.stream().map(this::map).toList());

        return summary;
    }

    public static Supplier<ResponseStatusException> notFoundByIdExceptionSupplier() {
        return () -> new ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "AccountMapper.mapToSummary(Collection<AccountEntity> accounts) empty collection not allowed!");
    }
}
