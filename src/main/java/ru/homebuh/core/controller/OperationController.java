package ru.homebuh.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.homebuh.core.controller.dto.OperationCreate;
import ru.homebuh.core.domain.OperationEntity;
import ru.homebuh.core.service.OperationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class OperationController {
    private final OperationService operationService;

    @PutMapping("user/operation/expense")
    List<OperationEntity> expenseCreate(
            final JwtAuthenticationToken token,
            @RequestBody OperationCreate operationCreate) {
        return operationService.expenseCreate(token.getName(), operationCreate);
    }

    @PutMapping("user/operation/income")
    List<OperationEntity> incomeCreate(
            final JwtAuthenticationToken token,
            @RequestBody OperationCreate operationCreate) {
        return operationService.incomeCreate(token.getName(), operationCreate);
    }
}
