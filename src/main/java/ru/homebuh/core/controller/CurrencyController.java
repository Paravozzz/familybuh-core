package ru.homebuh.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.service.CurrencyService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("currencies")
    Collection<CurrencyEntity> getAll() {
        return currencyService.findAll();
    }

    @GetMapping("currency/{currencyCode}")
    CurrencyEntity findByCode(@PathVariable String currencyCode) {
        return currencyService.findByCode(currencyCode);
    }

    @GetMapping("user/currencies")
    Collection<CurrencyEntity> getAll(final JwtAuthenticationToken token) {
        return currencyService.findAllByUserId(token.getName());
    }

    @PostMapping("user/currency/{currencyCode}")
    @ResponseStatus(HttpStatus.CREATED)
    CurrencyEntity attachCurrencyToUser(final JwtAuthenticationToken token, @PathVariable String currencyCode) {
        return currencyService.attachCurrencyToUser(token.getName(), currencyCode);
    }

    @DeleteMapping("user/currency/{currencyCode}")
    CurrencyEntity detachCurrencyToUser(final JwtAuthenticationToken token, @PathVariable String currencyCode) {
        return currencyService.detachCurrencyToUser(token.getName(), currencyCode);
    }
}
