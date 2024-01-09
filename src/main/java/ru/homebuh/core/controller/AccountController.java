package ru.homebuh.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ru.homebuh.core.controller.dto.AccountCreate;
import ru.homebuh.core.controller.dto.AccountSummary;
import ru.homebuh.core.controller.dto.AccountUpdate;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.service.ControllerServiceFacade;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class AccountController {

    private final ControllerServiceFacade controllerServiceFacade;
    @GetMapping("user/accounts")
    Collection<AccountEntity> getAll(final JwtAuthenticationToken token) {
        return controllerServiceFacade.findAllFamilyAccountsByUserId(token.getName());
    }

    @PostMapping("user/account")
    AccountSummary create(
            final JwtAuthenticationToken token,
            @RequestBody AccountCreate accountCreate) {
        return controllerServiceFacade.createAccount(token.getName(), accountCreate);
    }

    @PutMapping("user/account")
    AccountSummary update(
            final JwtAuthenticationToken token,
            @RequestBody AccountUpdate accountUpdate) {
        return controllerServiceFacade.updateAccount(token.getName(), accountUpdate);
    }

    /**
     * Получение обобщённой информации о счётах пользователя
     *
     * @param token
     * @return
     */
    @GetMapping("user/account-summaries")
    Collection<AccountSummary> getAllSummaries(final JwtAuthenticationToken token) {
        return controllerServiceFacade.findAllAccountsSummaries(token.getName());
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
        return controllerServiceFacade.findUserAccountSummaryByAccountId(token.getName(), accountId);
    }

}
