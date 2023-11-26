package ru.homebuh.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.homebuh.core.controller.dto.OperationCreate;
import ru.homebuh.core.controller.dto.OperationDto;
import ru.homebuh.core.service.OperationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class OperationController {
    private final OperationService operationService;

    @PostMapping("user/operation/expense")
    OperationDto expenseCreate(
            final JwtAuthenticationToken token,
            @RequestBody OperationCreate operationCreate) {
        return operationService.expenseCreate(token.getName(), operationCreate);
    }

    @PostMapping("user/operation/income")
    OperationDto incomeCreate(
            final JwtAuthenticationToken token,
            @RequestBody OperationCreate operationCreate) {
        return operationService.incomeCreate(token.getName(), operationCreate);
    }
}
