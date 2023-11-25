package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.homebuh.core.controller.dto.OperationCreate;
import ru.homebuh.core.controller.dto.OperationDto;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.repository.OperationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final UserInfoService userInfoService;
    private final OperationRepository operationRepository;

    /**
     * Расходная операция
     *
     * @param userId          идентификатор пользователя
     * @param operationCreate данные
     * @return список операций за день
     */
    public List<OperationDto> expenseCreate(String userId, OperationCreate operationCreate) {
        UserInfoEntity userInfo = userInfoService.findByIdIgnoreCase(userId);
        return new ArrayList<>(0);
    }

    /**
     * Доходная операция
     *
     * @param userId          идентификатор пользователя
     * @param operationCreate данные
     * @return список операций за день
     */
    public List<OperationDto> incomeCreate(String userId, OperationCreate operationCreate) {
        UserInfoEntity userInfo = userInfoService.findByIdIgnoreCase(userId);
        return new ArrayList<>(0);
    }
}
