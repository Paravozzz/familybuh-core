package ru.homebuh.core.controller;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ru.homebuh.core.controller.dto.OperationCreate;
import ru.homebuh.core.controller.dto.OperationDto;
import ru.homebuh.core.domain.OperationEntity;
import ru.homebuh.core.domain.enums.OperationTypeEnum;
import ru.homebuh.core.service.OperationService;

import java.time.OffsetDateTime;
import java.util.Collection;

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

    @GetMapping("user/operations")
    Collection<OperationDto> findByPredicate(
            final JwtAuthenticationToken token,
            @QuerydslPredicate(root = OperationEntity.class)
            Predicate predicate
    ) {
        return operationService.findByPredicate(token.getName(), predicate);
    }

    @GetMapping("user/operations/daily")
    Collection<OperationDto> dailyOperation(
            final JwtAuthenticationToken token,
            @RequestParam(required = true) Integer operationType,
            @RequestParam(required = true) OffsetDateTime date
            ) {
        return operationService.dailyOperation(token.getName(), OperationTypeEnum.values()[operationType], date);
    }
}
