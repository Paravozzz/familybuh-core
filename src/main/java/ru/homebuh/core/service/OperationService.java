package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.OperationCreate;
import ru.homebuh.core.controller.dto.OperationDto;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CategoryEntity;
import ru.homebuh.core.domain.OperationEntity;
import ru.homebuh.core.domain.enums.OperationType;
import ru.homebuh.core.mapper.OperationMapper;
import ru.homebuh.core.repository.OperationRepository;

import java.math.BigDecimal;

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
        final String currencyCode = operationCreate.getCurrencyCode();
        currencyService.isCurrencyExists(currencyCode);
        AccountEntity userAccount = accountService.getUserAccount(userId, operationCreate.getAccountId(), currencyCode);
        final Long categoryId = operationCreate.getCategoryId();
        CategoryEntity category = categoryService.findUserCategoryById(userId, categoryId);
        if (category.isIncome()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Category with id(" + categoryId + ") is not expense type.");
        }
        OperationEntity expenseOperation = new OperationEntity();
        expenseOperation.setCategory(category);
        expenseOperation.setAmount(new BigDecimal(operationCreate.getAmount()).abs().negate());
        expenseOperation.setAccount(userAccount);
        expenseOperation.setOperationType(OperationType.EXPENSE);
        expenseOperation.setDescription(operationCreate.getDescription());
        expenseOperation.setDate(operationCreate.getDate());

        expenseOperation = operationRepository.save(expenseOperation);

        return operationMapper.mapExpense(expenseOperation);
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
        final String currencyCode = operationCreate.getCurrencyCode();
        currencyService.isCurrencyExists(currencyCode);
        AccountEntity userAccount = accountService.getUserAccount(userId, operationCreate.getAccountId(), currencyCode);
        final Long categoryId = operationCreate.getCategoryId();
        CategoryEntity category = categoryService.findUserCategoryById(userId, categoryId);
        if (!category.isIncome()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Category with id(" + categoryId + ") is not income type.");
        }
        OperationEntity incomeOperation = new OperationEntity();
        incomeOperation.setCategory(category);
        incomeOperation.setAmount(new BigDecimal(operationCreate.getAmount()).abs());
        incomeOperation.setAccount(userAccount);
        incomeOperation.setOperationType(OperationType.INCOME);
        incomeOperation.setDescription(operationCreate.getDescription());
        incomeOperation.setDate(operationCreate.getDate());

        incomeOperation = operationRepository.save(incomeOperation);

        return operationMapper.mapIncome(incomeOperation);
    }
}
