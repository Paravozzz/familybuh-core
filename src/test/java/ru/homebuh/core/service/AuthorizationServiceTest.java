package ru.homebuh.core.service;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.domain.FamilyEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.repository.AccountRepository;
import ru.homebuh.core.repository.CurrencyRepository;
import ru.homebuh.core.repository.FamilyRepository;
import ru.homebuh.core.repository.UserInfoRepository;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureEmbeddedDatabase
@ActiveProfiles("test")
@SpringBootTest
class AuthorizationServiceTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Test
    @DirtiesContext
    @DisplayName("Счёт принадлежит пользователю.")
    void isAuthorizedAccountTest1() {
        //Arrange
        String userId1 = "user1";
        UserInfoEntity user1 = createUser(null, userId1);

        CurrencyEntity currency = currencyRepository.getReferenceById("643");

        String accName1 = "account1";
        AccountEntity account1 = createAccount(user1, accName1, currency);

        //Act
        //Assert
        assertNotNull(user1);
        assertNotNull(account1);
        assertDoesNotThrow(() -> {
            authorizationService.account(userId1, account1.getId());
        });
    }

    @Test
    @DirtiesContext
    @DisplayName("Счёт принадлежит семье.")
    void isAuthorizedAccountTest2() {
        //Arrange
        FamilyEntity family = createFamily();

        String userId1 = "user1";
        UserInfoEntity user1 = createUser(family, userId1);

        String userId2 = "user2";
        UserInfoEntity user2 = createUser(family, userId2);

        CurrencyEntity currency = currencyRepository.getReferenceById("643");

        String accName1 = "account1";
        AccountEntity account1 = createAccount(user1, accName1, currency);

        String accName2 = "account2";
        AccountEntity account2 = createAccount(user2, accName2, currency);


        //Act
        //Assert
        assertNotNull(user1);
        assertNotNull(user2);
        assertNotNull(account1);
        assertNotNull(account2);
        assertDoesNotThrow(() -> {
            authorizationService.account(userId1, account2.getId());
        });
    }

    @Test
    @DirtiesContext
    @DisplayName("Счёт не принадлежит пользователю.")
    void isAuthorizedAccountTest3() {
        //Arrange

        String userId1 = "user1";
        UserInfoEntity user1 = createUser(null, userId1);

        String userId2 = "user2";
        UserInfoEntity user2 = createUser(null, userId2);

        CurrencyEntity currency = currencyRepository.getReferenceById("643");

        String accName1 = "account1";
        AccountEntity account1 = createAccount(user1, accName1, currency);

        String accName2 = "account2";
        AccountEntity account2 = createAccount(user2, accName2, currency);


        //Act
        //Assert
        assertNotNull(user1);
        assertNotNull(user2);
        assertNotNull(account1);
        assertNotNull(account2);
        assertThrows(ResponseStatusException.class, () -> {
            authorizationService.account(userId1, account2.getId());
        });
    }

    @Test
    @DirtiesContext
    @DisplayName("Невалидные данные.")
    void isAuthorizedAccountTest4() {
        //Arrange
        //Act
        //Assert
        assertThrows(ResponseStatusException.class, () -> {
            authorizationService.account("", 0L);
        });
    }

    @Test
    @DirtiesContext
    @DisplayName("Пользователь не найден.")
    void isAuthorizedAccountTest5() {
        //Arrange
        UserInfoEntity user1 = createUser(null, "user1");
        //Act
        //Assert
        assertThrows(ResponseStatusException.class, () -> {
            authorizationService.account(user1.getId(), 100L);
        });
    }

    @Test
    @DirtiesContext
    @DisplayName("Пользователь найден. Счёт не найден.")
    void isAuthorizedAccountTest6() {
        //Arrange
        //Act
        //Assert
        assertThrows(ResponseStatusException.class, () -> {
            authorizationService.account("user1", 1L);
        });
    }

    private AccountEntity createAccount(UserInfoEntity userInfo, String accountName, CurrencyEntity currency) {
        AccountEntity account = new AccountEntity();
        account.setName(accountName);
        account.setUserInfo(userInfo);
        account.setCurrency(currency);
        return accountRepository.save(account);
    }

    private UserInfoEntity createUser(FamilyEntity family, String userId) {
        UserInfoEntity user = new UserInfoEntity();
        user.setId(userId);
        user.setFamily(family);
        user = userInfoRepository.save(user);
        return user;
    }

    private FamilyEntity createFamily() {
        FamilyEntity family = new FamilyEntity();
        family = familyRepository.save(family);
        return family;
    }
}