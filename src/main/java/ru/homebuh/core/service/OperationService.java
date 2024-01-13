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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final UserInfoService userInfoService;
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
        UserInfoEntity userInfo = userInfoService.getUserInfo(userId);
        AccountEntity userAccount = accountService.getAccount(operationCreate.getAccountId());
        final Long categoryId = operationCreate.getCategoryId();
        CategoryEntity category = categoryService.getCategory(categoryId);
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
        expenseOperation.setUserInfo(userInfo);

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
        UserInfoEntity userInfo = userInfoService.getUserInfo(userId);
        AccountEntity userAccount = accountService.getAccount(operationCreate.getAccountId());
        final Long categoryId = operationCreate.getCategoryId();
        CategoryEntity category = categoryService.getCategory(categoryId);
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
        incomeOperation.setUserInfo(userInfo);

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
        if (predicate == null || predicate.equals(new BooleanBuilder()))
            return new ArrayList<>();

        BooleanBuilder booleanBuilder = securePredicate(userId, predicate);

        Iterable<OperationEntity> result = operationRepository.findAll(booleanBuilder);

        return operationMapper.mapToDto(result);
    }

    /**
     * Получить операции за день для пользователя и его семьи
     *
     * @param userId        идентификатор пользователя
     * @param operationType тип операции
     * @param date          дата
     * @return
     */
    @Transactional
    public Collection<OperationDto> familyDailyOperations(String userId, OperationTypeEnum operationType, OffsetDateTime date) {

        BooleanBuilder booleanBuilder = securePredicate(userId, new BooleanBuilder());

        booleanBuilder.and(QOperationEntity.operationEntity.operationType.eq(operationType));

        OffsetDateTime from = OffsetDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0, 0, 0, date.getOffset());
        OffsetDateTime to = OffsetDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 23, 59, 59, 999999999, date.getOffset());
        booleanBuilder.and(QOperationEntity.operationEntity.date.between(from, to));

        Iterable<OperationEntity> result = operationRepository.findAll(booleanBuilder);

        return operationMapper.mapToDto(result);
    }

    @Transactional
    public void deleteAllFamilyOperations(Collection<String> familyIds) {
        operationRepository.deleteAllByUserIdIn(familyIds);
    }

    /**
     * Модифицирует предикат для поиска только по счетам и категориям принадлежащим пользователю или его семье
     *
     * @param userId
     * @param predicate
     * @return
     */
    private BooleanBuilder securePredicate(String userId, Predicate predicate) {
        BooleanBuilder booleanBuilder = new BooleanBuilder(predicate);

        //Только принадлежащие пользователю и его семье категории
        Set<Long> userCategoriesIds = categoryService.findAllFamilyCategoriesByUserId(userId).stream().map(CategoryEntity::getId).collect(Collectors.toSet());
        booleanBuilder.and(QOperationEntity.operationEntity.category.id.in(userCategoriesIds));

        //Только принадлежащие пользователю и его семье счета
        Set<Long> userAccountsIds = accountService.findAllFamilyAccountsByUserId(userId).stream().map(AccountEntity::getId).collect(Collectors.toSet());
        booleanBuilder.and(QOperationEntity.operationEntity.account.id.in(userAccountsIds));
        return booleanBuilder;
    }
}
