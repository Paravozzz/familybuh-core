package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.homebuh.core.controller.dto.OperationCreate;
import ru.homebuh.core.domain.OperationEntity;
import ru.homebuh.core.repository.OperationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationRepository operationRepository;

    /**
     * Расходная операция
     *
     * @param userId          идентификатор пользователя
     * @param operationCreate данные
     * @return список операций за день
     */
    public List<OperationEntity> expenseCreate(String userId, OperationCreate operationCreate) {
        return new ArrayList<>(0);
    }

    /**
     * Доходная операция
     *
     * @param userId          идентификатор пользователя
     * @param operationCreate данные
     * @return список операций за день
     */
    public List<OperationEntity> incomeCreate(String userId, OperationCreate operationCreate) {
        return new ArrayList<>(0);
    }
}
