package ru.homebuh.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ru.homebuh.core.controller.dto.AccountCreate;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.service.AccountService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("accounts")
    Collection<AccountEntity> getAll(final JwtAuthenticationToken token) {
        return accountService.findAllByUserIdIgnoreCase(token.getName());
    }

    @PostMapping("account")
    AccountEntity create(final JwtAuthenticationToken token, @RequestBody(required = true) AccountCreate accountCreate) {
        return accountService.createAccount(accountCreate);
    }
}
