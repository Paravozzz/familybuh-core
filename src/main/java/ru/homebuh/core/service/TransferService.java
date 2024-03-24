package ru.homebuh.core.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.OperationCreate;
import ru.homebuh.core.controller.dto.TransferCreate;
import ru.homebuh.core.domain.*;
import ru.homebuh.core.domain.enums.OperationTypeEnum;
import ru.homebuh.core.repository.TransferRepository;

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
public class TransferService {

    private final OperationService operationService;
    private final TransferRepository transferRepository;
    private final AccountService accountService;
    private final UserInfoService userInfoService;

    /**
     * Операция перемещение
     *
     * @param userInfo       идентификатор пользователя
     * @param transferCreate данные
     * @return Операция перемещение
     */
    @Transactional
    public TransferEntity transferCreate(UserInfoEntity userInfo, TransferCreate transferCreate) {

        AccountEntity expenseAccount = accountService.getAccount(transferCreate.getExpenseAccountId());
        AccountEntity incomeAccount = accountService.getAccount(transferCreate.getIncomeAccountId());

        String expenseCurrencyCode = expenseAccount.getCurrency().getCode();
        String incomeCurrencyCode = incomeAccount.getCurrency().getCode();

        if (!expenseCurrencyCode.equalsIgnoreCase(incomeCurrencyCode)) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Income and expense currencies must have the same currency code when transfer create!"
            );
        }

        BigDecimal amount = new BigDecimal(transferCreate.getAmount());
        String description = transferCreate.getDescription() == null ? "" : transferCreate.getDescription();
        OffsetDateTime date = transferCreate.getDate() == null ? OffsetDateTime.now() : transferCreate.getDate();

        OperationCreate expenseCreate = new OperationCreate(
                amount.abs().negate().toString(),
                expenseAccount.getId(),
                null,
                description,
                date
        );

        OperationEntity expenseOperation = operationService.createWithoutCategory(userInfo, expenseCreate, OperationTypeEnum.TRANSFER);

        OperationCreate incomeCreate = new OperationCreate(
                amount.abs().toString(),
                incomeAccount.getId(),
                null,
                description,
                date
        );

        OperationEntity incomeOperation = operationService.createWithoutCategory(userInfo, incomeCreate, OperationTypeEnum.TRANSFER);

        TransferEntity newTransfer = new TransferEntity(null, expenseOperation, incomeOperation, description, date, userInfo);

        return transferRepository.save(newTransfer);
    }

    @Transactional
    public void deleteAllFamilyTransfers(Collection<String> familyIds) {
        transferRepository.deleteAllByUserIdIn(familyIds);
    }

    /**
     * Поиск принадлежащих пользователю переводов по заданным параметрам
     *
     * @param userId    идентификатор пользователя
     * @param predicate предикат
     * @return
     */
    @Transactional
    public List<TransferEntity> findByPredicate(String userId, Predicate predicate) {
        if (predicate == null || predicate.equals(new BooleanBuilder()))
            return new ArrayList<>();

        BooleanBuilder booleanBuilder = securePredicate(userId, predicate);

        Iterable<TransferEntity> result = transferRepository.findAll(booleanBuilder);

        return StreamSupport.stream(result.spliterator(), false).toList();
    }

    /**
     * Получить переводы за день для пользователя и его семьи
     *
     * @param userId идентификатор пользователя
     * @param date   дата
     * @return
     */
    @Transactional
    public List<TransferEntity> findDailyTransfers(String userId, OffsetDateTime date) {
        BooleanBuilder booleanBuilder = securePredicate(userId, new BooleanBuilder());

        OffsetDateTime from = OffsetDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0, 0, 0, date.getOffset());
        OffsetDateTime to = OffsetDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 23, 59, 59, 999999999, date.getOffset());
        booleanBuilder.and(QTransferEntity.transferEntity.date.between(from, to));

        Iterable<TransferEntity> result = transferRepository.findAll(booleanBuilder);

        return StreamSupport.stream(result.spliterator(), false).toList();
    }

    /**
     * Модифицирует предикат для поиска только по переводам принадлежащим пользователю или его семье
     *
     * @param userId
     * @param predicate
     * @return
     */
    private BooleanBuilder securePredicate(String userId, Predicate predicate) {
        BooleanBuilder booleanBuilder = new BooleanBuilder(predicate);

        //Только принадлежащие пользователю и его семье переводы
        Set<String> familyIds = userInfoService.findAllFamilyMembers(userId).stream().map(UserInfoEntity::getId).collect(Collectors.toSet());
        booleanBuilder.and(QTransferEntity.transferEntity.userInfo.id.in(familyIds));

        return booleanBuilder;
    }


}
