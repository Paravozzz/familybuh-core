package ru.homebuh.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ru.homebuh.core.aspect.UserInitAnnotation;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.service.CurrencyService;
import ru.homebuh.core.service.UserInfoService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class UserInfoController {
    private final CurrencyService currencyService;
    private final UserInfoService userInfoService;

    @UserInitAnnotation
    @GetMapping("user/currencies")
    Collection<CurrencyEntity> getAll(final JwtAuthenticationToken token) {
        return currencyService.findAllByUserId(token.getName());
    }

    @UserInitAnnotation
    @PostMapping("user/currency/{currencyCode}")
    @ResponseStatus(HttpStatus.CREATED)
    CurrencyEntity attachCurrencyToUser(final JwtAuthenticationToken token, @PathVariable String currencyCode) {
        return userInfoService.attachCurrencyToUser(token.getName(), currencyCode);
    }

    @UserInitAnnotation
    @DeleteMapping("user/currency/{currencyCode}")
    CurrencyEntity detachCurrencyToUser(final JwtAuthenticationToken token, @PathVariable String currencyCode) {
        return userInfoService.detachCurrencyToUser(token.getName(), currencyCode);
    }
}
