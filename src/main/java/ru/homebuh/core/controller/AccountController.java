package ru.homebuh.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ru.homebuh.core.controller.dto.AccountCreate;
import ru.homebuh.core.controller.dto.AccountSummary;
import ru.homebuh.core.controller.dto.AccountUpdate;
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

    @PostMapping("user/account")
    AccountSummary create(
            final JwtAuthenticationToken token,
            @RequestBody AccountCreate accountCreate) {
        return accountService.create(token.getName(), accountCreate);
    }

    @PutMapping("user/account")
    AccountSummary update(
            final JwtAuthenticationToken token,
            @RequestBody AccountUpdate accountUpdate) {
        return accountService.update(token.getName(), accountUpdate);
    }

    /**
     * Получение обобщённой информации о счётах пользователя
     *
     * @param token
     * @return
     */
    @GetMapping("user/account-summaries")
    Collection<AccountSummary> getAllSummaries(final JwtAuthenticationToken token) {
        return accountService.findAllSummaries(token.getName());
    }

    /**
     * Получение обобщенной информации о счёте по идентификатору одного из счетов
     *
     * @param token
     * @param accountId
     * @return
     */
    @GetMapping("user/account-summary/{accountId}")
    AccountSummary findUserAccountSummaryByAccountId(final JwtAuthenticationToken token, @PathVariable Long accountId) {
        return accountService.findUserAccountSummaryByAccountId(token.getName(), accountId);
    }

}
