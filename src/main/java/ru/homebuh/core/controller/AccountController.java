package ru.homebuh.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.service.AccountService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("user/accounts")
    Collection<AccountEntity> getAll(final JwtAuthenticationToken token) {
        return accountService.findAllByUserIdIgnoreCase(token.getName());
    }

}
