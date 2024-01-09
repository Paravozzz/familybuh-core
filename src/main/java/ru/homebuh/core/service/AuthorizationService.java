package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.UserInfoEntity;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserInfoService userInfoService;
    private final AccountService accountService;
    @Lazy
    @Autowired
    private AuthorizationService self;

    /**
     * Проверка что у пользователя или семьи есть права
     *
     * @param userId
     * @param accountId
     * @throws ResponseStatusException если нет прав
     */
    public void isAuthorized(String userId, Long accountId) {
        if (userId == null || accountId == null || userId.isBlank() || accountId < 1)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        AccountEntity account = accountService.getAccount(accountId);
        UserInfoEntity accountOwner = account.getUserInfo();
        String accountOwnerId;
        if (accountOwner == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        accountOwnerId = accountOwner.getId();

        List<UserInfoEntity> familyMembers = userInfoService.findAllFamilyMembers(userId);

        if (familyMembers.stream().noneMatch(member -> accountOwnerId.equalsIgnoreCase(member.getId())))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    /**
     * @param userId
     * @param account
     * @throws ResponseStatusException если нет прав
     */
    public void isAuthorized(String userId, AccountEntity account) {
        if (account == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        self.isAuthorized(userId, account.getId());
    }

    /**
     * @param userInfo
     * @param account
     * @throws ResponseStatusException если нет прав
     */
    public void isAuthorized(UserInfoEntity userInfo, AccountEntity account) {
        if (userInfo == null || account == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        self.isAuthorized(userInfo.getId(), account.getId());
    }
}
