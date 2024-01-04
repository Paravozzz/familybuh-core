package ru.homebuh.core.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.OperationCreate;
import ru.homebuh.core.controller.dto.OperationDto;
import ru.homebuh.core.domain.*;
import ru.homebuh.core.domain.enums.OperationTypeEnum;
import ru.homebuh.core.mapper.OperationMapper;
import ru.homebuh.core.repository.OperationRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final UserInfoService userInfoService;
    private final CurrencyService currencyService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final OperationRepository operationRepository;
    private final OperationMapper operationMapper;

    /**
     * Доходная операция
     *
     * @param userId          идентификатор пользователя
     * @param operationCreate данные
     * @return доходная операция
     */
    @Transactional
    public OperationDto expenseCreate(String userId, OperationCreate operationCreate) {
        userInfoService.isUserExists(userId);
        AccountEntity userAccount = accountService.getUserAccount(userId, operationCreate.getAccountId());
        final Long categoryId = operationCreate.getCategoryId();
        CategoryEntity category = categoryService.findUserCategoryById(userId, categoryId);
        if (category.isIncome()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Category with id(" + categoryId + ") is not expense type.");
        }
        OperationEntity expenseOperation = new OperationEntity();
        expenseOperation.setCategory(category);
        expenseOperation.setAmount(new BigDecimal(operationCreate.getAmount()).abs().negate());
        expenseOperation.setAccount(userAccount);
        expenseOperation.setOperationType(OperationTypeEnum.EXPENSE);
        expenseOperation.setDescription(operationCreate.getDescription());
        expenseOperation.setDate(operationCreate.getDate());

        expenseOperation = operationRepository.save(expenseOperation);

        return operationMapper.mapToDto(expenseOperation);
    }

    /**
     * Доходная операция
     *
     * @param userId          идентификатор пользователя
     * @param operationCreate данные
     * @return доходная операция
     */
    @Transactional
    public OperationDto incomeCreate(String userId, OperationCreate operationCreate) {
        userInfoService.isUserExists(userId);
        AccountEntity userAccount = accountService.getUserAccount(userId, operationCreate.getAccountId());
        final Long categoryId = operationCreate.getCategoryId();
        CategoryEntity category = categoryService.findUserCategoryById(userId, categoryId);
        if (!category.isIncome()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Category with id(" + categoryId + ") is not income type.");
        }
        OperationEntity incomeOperation = new OperationEntity();
        incomeOperation.setCategory(category);
        incomeOperation.setAmount(new BigDecimal(operationCreate.getAmount()).abs());
        incomeOperation.setAccount(userAccount);
        incomeOperation.setOperationType(OperationTypeEnum.INCOME);
        incomeOperation.setDescription(operationCreate.getDescription());
        incomeOperation.setDate(operationCreate.getDate());

        incomeOperation = operationRepository.save(incomeOperation);

        return operationMapper.mapToDto(incomeOperation);
    }

    /**
     * Поиск принадлежащих пользователю операций по заданным параметрам
     *
     * @param userId    идентификатор пользователя
     * @param predicate предикат
     * @return
     */
    @Transactional
    public Collection<OperationDto> findByPredicate(String userId, Predicate predicate) {
        if (predicate == null)
            return new ArrayList<>();
        UserInfoEntity userInfo = userInfoService.findByIdIgnoreCase(userId);

        BooleanBuilder booleanBuilder = new BooleanBuilder(predicate);

        //Только принадлежащие пользователю категории
        List<CategoryEntity> userCategories = userInfo.getCategories();
        booleanBuilder.and(QOperationEntity.operationEntity.category.in(userCategories));

        //Только принадлежащие пользователю счета
        List<AccountEntity> userAccounts = accountService.findAllByUserIdIgnoreCase(userId);
        booleanBuilder.and(QOperationEntity.operationEntity.account.in(userAccounts));

        Iterable<OperationEntity> result = operationRepository.findAll(booleanBuilder);

        return operationMapper.mapToDto(result);
    }

    /**
     * Получить операции за день
     *
     * @param userId        идентификатор пользователя
     * @param operationType тип операции
     * @param date          дата
     * @return
     */
    @Transactional
    public Collection<OperationDto> dailyOperation(String userId, OperationTypeEnum operationType, OffsetDateTime date) {
        UserInfoEntity userInfo = userInfoService.findByIdIgnoreCase(userId);

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        //Только принадлежащие пользователю категории
        List<CategoryEntity> userCategories = userInfo.getCategories();
        booleanBuilder.and(QOperationEntity.operationEntity.category.in(userCategories));

        //Только принадлежащие пользователю счета
        List<AccountEntity> userAccounts = accountService.findAllByUserIdIgnoreCase(userId);
        booleanBuilder.and(QOperationEntity.operationEntity.account.in(userAccounts));

        booleanBuilder.and(QOperationEntity.operationEntity.operationType.eq(operationType));

        OffsetDateTime from = OffsetDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0, 0, 0, date.getOffset());
        OffsetDateTime to = OffsetDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 23, 59, 59, 999999999, date.getOffset());
        booleanBuilder.and(QOperationEntity.operationEntity.date.between(from, to));

        Iterable<OperationEntity> result = operationRepository.findAll(booleanBuilder);

        return operationMapper.mapToDto(result);
    }
}
