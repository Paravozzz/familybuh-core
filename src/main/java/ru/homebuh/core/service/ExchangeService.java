package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.ExchangeCreate;
import ru.homebuh.core.controller.dto.OperationCreate;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.ExchangeEntity;
import ru.homebuh.core.domain.OperationEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.domain.enums.OperationTypeEnum;
import ru.homebuh.core.repository.ExchangeRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final OperationService operationService;
    private final ExchangeRepository exchangeRepository;
    private final AccountService accountService;

    /**
     * Операция обмен валюты
     *
     * @param userInfo       пользователь
     * @param exchangeCreate данные
     * @return Операция обмен валюты
     */
    @Transactional
    public ExchangeEntity exchangeCreate(UserInfoEntity userInfo, ExchangeCreate exchangeCreate) {
        AccountEntity expenseAccount = accountService.getAccount(exchangeCreate.getExpenseAccountId());
        AccountEntity incomeAccount = accountService.getAccount(exchangeCreate.getIncomeAccountId());

        String expenseCurrencyCode = expenseAccount.getCurrency().getCode();
        String incomeCurrencyCode = incomeAccount.getCurrency().getCode();

        if (expenseCurrencyCode.equalsIgnoreCase(incomeCurrencyCode)) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Income and expense currencies must have the different currency code when exchange creates!"
            );
        }

        BigDecimal expenseAmount = new BigDecimal(exchangeCreate.getExpenseAmount());
        BigDecimal incomeAmount = new BigDecimal(exchangeCreate.getIncomeAmount());
        String description = exchangeCreate.getDescription() == null ? "" : exchangeCreate.getDescription();
        OffsetDateTime date = exchangeCreate.getDate() == null ? OffsetDateTime.now() : exchangeCreate.getDate();

        OperationCreate expenseCreate = new OperationCreate(
                expenseAmount.abs().negate().toString(),
                expenseCurrencyCode,
                expenseAccount.getId(),
                null,
                description,
                date
        );

        OperationEntity expenseOperation = operationService.createWithoutCategory(userInfo, expenseCreate, OperationTypeEnum.EXCHANGE);

        OperationCreate incomeCreate = new OperationCreate(
                incomeAmount.abs().toString(),
                incomeCurrencyCode,
                incomeAccount.getId(),
                null,
                description,
                date
        );

        OperationEntity incomeOperation = operationService.createWithoutCategory(userInfo, incomeCreate, OperationTypeEnum.EXCHANGE);

        ExchangeEntity newExchange = new ExchangeEntity(null, expenseOperation, incomeOperation, description, date, userInfo);

        return exchangeRepository.save(newExchange);
    }

    @Transactional
    public void deleteAllFamilyExchanges(Collection<String> familyIds) {
        exchangeRepository.deleteAllByUserIdIn(familyIds);
    }

}
