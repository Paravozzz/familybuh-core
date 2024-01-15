package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.OperationCreate;
import ru.homebuh.core.controller.dto.TransferCreate;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.OperationEntity;
import ru.homebuh.core.domain.TransferEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.domain.enums.OperationTypeEnum;
import ru.homebuh.core.repository.TransferRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final OperationService operationService;
    private final TransferRepository transferRepository;
    private final AccountService accountService;

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
                expenseCurrencyCode,
                expenseAccount.getId(),
                null,
                description,
                date
        );

        OperationEntity expenseOperation = operationService.createWithoutCategory(userInfo, expenseCreate, OperationTypeEnum.TRANSFER);

        OperationCreate incomeCreate = new OperationCreate(
                amount.abs().toString(),
                incomeCurrencyCode,
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

}
