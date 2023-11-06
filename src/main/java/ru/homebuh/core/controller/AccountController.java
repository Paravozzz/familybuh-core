package ru.homebuh.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.aspect.UserInitAnnotation;
import ru.homebuh.core.controller.dto.AccountCreate;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.service.AccountService;
import ru.homebuh.core.util.Constatnts;

import java.text.MessageFormat;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class AccountController {

    private final AccountService accountService;

    @UserInitAnnotation
    @GetMapping("accounts")
    Collection<AccountEntity> getAll(final JwtAuthenticationToken token) {
        return accountService.findAllByUserId(token.getName());
    }

    @GetMapping("account/{accountId}")
    AccountEntity findById(final JwtAuthenticationToken token, @PathVariable(name = "accountId") Long accountId) {
        return accountService
                .findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constatnts.NOT_FOUND_BY_ID_TEMPLATE, "Account", "id" ,accountId)));
    }

    @UserInitAnnotation
    @PostMapping("account")
    AccountEntity create(final JwtAuthenticationToken token, @RequestBody(required = true) AccountCreate accountCreate) {
        return accountService.create(accountCreate);
    }
}
