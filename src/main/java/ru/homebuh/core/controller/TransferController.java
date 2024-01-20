package ru.homebuh.core.controller;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ru.homebuh.core.controller.dto.TransferCreate;
import ru.homebuh.core.controller.dto.TransferDto;
import ru.homebuh.core.domain.TransferEntity;
import ru.homebuh.core.service.ControllerServiceFacade;

import java.time.OffsetDateTime;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class TransferController {
    private final ControllerServiceFacade controllerServiceFacade;

    @PostMapping("user/transfer")
    TransferDto expenseCreate(
            final JwtAuthenticationToken token,
            @RequestBody TransferCreate transferCreate) {
        return controllerServiceFacade.createTransfer(token.getName(), transferCreate);
    }

    @GetMapping("user/transfers")
    Collection<TransferDto> findByPredicate(
            final JwtAuthenticationToken token,
            @QuerydslPredicate(root = TransferEntity.class)
            Predicate predicate
    ) {
        return controllerServiceFacade.findTransfersByPredicate(token.getName(), predicate);
    }

    @GetMapping("user/transfers/daily")
    Collection<TransferDto> dailyTransfers(
            final JwtAuthenticationToken token,
            @RequestParam(required = true) OffsetDateTime date
    ) {
        return controllerServiceFacade.dailyTransfers(token.getName(), date);
    }
}
