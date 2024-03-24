package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CategoryEntity;
import ru.homebuh.core.domain.UserInfoEntity;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserInfoService userInfoService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final OperationService operationService;


    /**
     * Проверка что у пользователя или семьи есть права
     *
     * @param userId
     * @param accountId
     * @throws ResponseStatusException если нет прав
     */
    public void account(String userId, Long accountId) {
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
     * Проверка что у пользователя или семьи есть права
     *
     * @param userId
     * @param account
     * @throws ResponseStatusException если нет прав
     */
    public void account(String userId, AccountEntity account) {
        if (userId == null || account == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        account(userId, account.getId());
    }

    /**
     * Проверка что у пользователя или семьи есть права
     *
     * @param userInfo
     * @param account
     * @throws ResponseStatusException если нет прав
     */
    public void account(UserInfoEntity userInfo, AccountEntity account) {
        if (userInfo == null || account == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        account(userInfo.getId(), account.getId());
    }

    /**
     * Проверка что у пользователя или семьи есть права
     *
     * @param userId
     * @param categoryId
     * @throws ResponseStatusException если нет прав
     */
    public void category(String userId, Long categoryId) {
        if (userId == null || categoryId == null || userId.isBlank() || categoryId < 1)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        List<CategoryEntity> familyCategories = categoryService.findAllFamilyCategoriesByUserId(userId);
        if (familyCategories.stream().anyMatch(category -> category.getId().equals(categoryId))) {
            return;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Проверка что у пользователя или семьи есть права
     *
     * @param userId
     * @param category
     * @throws ResponseStatusException если нет прав
     */
    public void category(String userId, CategoryEntity category) {
        if (userId == null || category == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        category(userId, category.getId());
    }

    /**
     * Проверка что у пользователя или семьи есть права
     *
     * @param userInfo
     * @param category
     * @throws ResponseStatusException если нет прав
     */
    public void category(UserInfoEntity userInfo, CategoryEntity category) {
        if (userInfo == null || category == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        category(userInfo.getId(), category.getId());
    }

    public void operation(String userId, Long operationId) {
        if (userId == null || operationId == null || userId.isBlank() || operationId < 1)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        operationService.findFamilyOperationById(userId, operationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    }
}
