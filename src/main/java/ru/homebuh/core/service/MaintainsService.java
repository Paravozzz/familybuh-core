package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.homebuh.core.domain.CategoryEntity;
import ru.homebuh.core.domain.UserInfoEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintainsService {

    private final AccountService accountService;
    private final UserInfoService userInfoService;
    private final CategoryService categoryService;
    private final OperationService operationService;
    private final TransferService transferService;
    private final ExchangeService exchangeService;

    @Transactional
    public void cleanUserData(String userId) {
        List<UserInfoEntity> family = userInfoService.findAllFamilyMembers(userId);
        Set<String> familyIds = family.stream().map(UserInfoEntity::getId).collect(Collectors.toSet());
        exchangeService.deleteAllFamilyExchanges(familyIds);
        transferService.deleteAllFamilyTransfers(familyIds);
        operationService.deleteAllFamilyOperations(familyIds);
        accountService.deleteAllFamilyAccounts(familyIds);
        family.forEach(userInfoEntity -> userInfoEntity.getCurrencies().clear());
        Set<Long> familyCategotiesIds = new HashSet<>();
        family.forEach(user -> {
            List<CategoryEntity> categories = user.getCategories();
            Set<Long> categotiesIds = categories.stream().map(CategoryEntity::getId).collect(Collectors.toSet());
            familyCategotiesIds.addAll(categotiesIds);
            user.getCategories().clear();
        });
        categoryService.deleteAllCategoriesByIdIn(familyCategotiesIds);
        userInfoService.saveAll(family);
    }
}
