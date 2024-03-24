package ru.homebuh.core.controller;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ru.homebuh.core.controller.dto.OperationCreate;
import ru.homebuh.core.controller.dto.OperationDto;
import ru.homebuh.core.controller.dto.OperationUpdate;
import ru.homebuh.core.domain.OperationEntity;
import ru.homebuh.core.domain.enums.OperationTypeEnum;
import ru.homebuh.core.service.ControllerServiceFacade;

import java.time.OffsetDateTime;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class OperationController {
    private final ControllerServiceFacade controllerServiceFacade;

    @GetMapping("user/operation/{operationId}")
    OperationDto findById(final JwtAuthenticationToken token, @PathVariable("operationId") Long operationId) {
        return controllerServiceFacade.findOperationById(token.getName(), operationId);
    }

    @PutMapping("user/operation/{operationId}")
    OperationDto update(final JwtAuthenticationToken token,
                        @PathVariable("operationId") Long operationId,
                        @RequestBody OperationUpdate operationUpdate) {
        return controllerServiceFacade.updateOperation(token.getName(), operationId, operationUpdate);
    }

    @PostMapping("user/operation/expense")
    OperationDto expenseCreate(
            final JwtAuthenticationToken token,
            @RequestBody OperationCreate operationCreate) {
        return controllerServiceFacade.createExpense(token.getName(), operationCreate);
    }

    @PostMapping("user/operation/income")
    OperationDto incomeCreate(
            final JwtAuthenticationToken token,
            @RequestBody OperationCreate operationCreate) {
        return controllerServiceFacade.createIncome(token.getName(), operationCreate);
    }

    @GetMapping("user/operations")
    Collection<OperationDto> findByPredicate(
            final JwtAuthenticationToken token,
            @QuerydslPredicate(root = OperationEntity.class)
            Predicate predicate
    ) {
        return controllerServiceFacade.findOperationsByPredicate(token.getName(), predicate);
    }

    @GetMapping("user/operations/daily")
    Collection<OperationDto> dailyOperations(
            final JwtAuthenticationToken token,
            @RequestParam(required = true) Integer operationType,
            @RequestParam(required = true) OffsetDateTime date
            ) {
        return controllerServiceFacade.dailyOperations(token.getName(), OperationTypeEnum.values()[operationType], date);
    }
}
