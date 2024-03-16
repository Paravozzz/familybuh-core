package ru.homebuh.core.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.ExchangeCreate;
import ru.homebuh.core.controller.dto.OperationCreate;
import ru.homebuh.core.domain.*;
import ru.homebuh.core.domain.enums.OperationTypeEnum;
import ru.homebuh.core.repository.ExchangeRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final OperationService operationService;
    private final ExchangeRepository exchangeRepository;
    private final AccountService accountService;
    private final UserInfoService userInfoService;

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

    /**
     * Получить операции обменов валют за день для пользователя и его семьи
     *
     * @param userId идентификатор пользователя
     * @param date   дата
     * @return
     */
    @Transactional
    public Collection<ExchangeEntity> findDailyExchanges(String userId, OffsetDateTime date) {
        BooleanBuilder booleanBuilder = securePredicate(userId, new BooleanBuilder());

        OffsetDateTime from = OffsetDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0, 0, 0, date.getOffset());
        OffsetDateTime to = OffsetDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 23, 59, 59, 999999999, date.getOffset());
        booleanBuilder.and(QExchangeEntity.exchangeEntity.date.between(from, to));

        Iterable<ExchangeEntity> result = exchangeRepository.findAll(booleanBuilder);

        return StreamSupport.stream(result.spliterator(), false).toList();
    }

    /**
     * Поиск принадлежащих пользователю операций по обмену валют по заданным параметрам
     *
     * @param userId    идентификатор пользователя
     * @param predicate предикат
     * @return
     */
    @Transactional
    public List<ExchangeEntity> findByPredicate(String userId, Predicate predicate) {
        if (predicate == null || predicate.equals(new BooleanBuilder()))
            return new ArrayList<>();

        BooleanBuilder booleanBuilder = securePredicate(userId, predicate);

        Iterable<ExchangeEntity> result = exchangeRepository.findAll(booleanBuilder);

        return StreamSupport.stream(result.spliterator(), false).toList();
    }

    /**
     * Модифицирует предикат для поиска только по обменам валют принадлежащим пользователю или его семье
     *
     * @param userId
     * @param predicate
     * @return
     */
    private BooleanBuilder securePredicate(String userId, Predicate predicate) {
        BooleanBuilder booleanBuilder = new BooleanBuilder(predicate);

        //Только принадлежащие пользователю и его семье обмены валют
        Set<String> familyIds = userInfoService.findAllFamilyMembers(userId).stream().map(UserInfoEntity::getId).collect(Collectors.toSet());
        booleanBuilder.and(QExchangeEntity.exchangeEntity.userInfo.id.in(familyIds));

        return booleanBuilder;
    }
}
