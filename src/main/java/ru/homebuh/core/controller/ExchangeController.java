package ru.homebuh.core.controller;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ru.homebuh.core.controller.dto.ExchangeCreate;
import ru.homebuh.core.controller.dto.ExchangeDto;
import ru.homebuh.core.domain.ExchangeEntity;
import ru.homebuh.core.service.ControllerServiceFacade;

import java.time.OffsetDateTime;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class ExchangeController {
    private final ControllerServiceFacade controllerServiceFacade;

    @PostMapping("user/exchange")
    ExchangeDto exchangeCreate(
            final JwtAuthenticationToken token,
            @RequestBody ExchangeCreate exchangeCreate) {
        return controllerServiceFacade.createExchange(token.getName(), exchangeCreate);
    }

    @GetMapping("user/exchanges")
    Collection<ExchangeDto> findByPredicate(
            final JwtAuthenticationToken token,
            @QuerydslPredicate(root = ExchangeEntity.class)
            Predicate predicate
    ) {
        return controllerServiceFacade.findExchangesByPredicate(token.getName(), predicate);
    }

    @GetMapping("user/exchanges/daily")
    Collection<ExchangeDto> dailyExchanges(
            final JwtAuthenticationToken token,
            @RequestParam(required = true) OffsetDateTime date
    ) {
        return controllerServiceFacade.dailyExchanges(token.getName(), date);
    }
}
