package ru.homebuh.core.service;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.homebuh.core.controller.dto.AccountSummary;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.domain.FamilyEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.repository.AccountRepository;
import ru.homebuh.core.repository.CurrencyRepository;
import ru.homebuh.core.repository.FamilyRepository;
import ru.homebuh.core.repository.UserInfoRepository;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureEmbeddedDatabase
@ActiveProfiles("test")
@SpringBootTest
//@DataJpaTest
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @DirtiesContext
    @Test
    @DisplayName("Поиск счетов. Два пользователя в семье.")
    void findAllByUserIdIgnoreCaseTest1() {

        //Arrange
        FamilyEntity family = createFamily();

        String userId1 = "user1";
        UserInfoEntity user1 = createUser(family, userId1);

        String userId2 = "user2";
        UserInfoEntity user2 = createUser(family, userId2);

        String userId3 = "user3";
        UserInfoEntity user3 = createUser(null, userId3);

        CurrencyEntity currency = currencyRepository.getReferenceById("643");

        String accName1 = "account1";
        AccountEntity account1 = createAccount(user1, accName1, currency);

        String accName2 = "account2";
        AccountEntity account2 = createAccount(user2, accName2, currency);

        String accName3 = "account3";
        AccountEntity account3 = createAccount(user3, accName3, currency);

        //Act
        List<AccountEntity> accounts = accountService.findAllFamilyAccountsByUserId(userId1);

        //Assert
        assertNotNull(family);
        assertNotNull(user1);
        assertNotNull(user2);
        assertNotNull(user3);
        assertNotNull(account1);
        assertNotNull(account2);
        assertNotNull(account3);

        assertNotNull(accounts);
        assertEquals(2, accounts.size(), "Должно быть найдено 2 счёта");
        assertTrue(accounts.stream().anyMatch(acc -> acc.getName().equals(accName1)));
        assertTrue(accounts.stream().anyMatch(acc -> acc.getName().equals(accName2)));
        assertFalse(accounts.stream().anyMatch(acc -> acc.getName().equals(accName3)));
    }

    @DirtiesContext
    @Test
    @DisplayName("Поиск счетов. Один пользователь.")
    void findAllByUserIdIgnoreCaseTest2() {

        //Arrange
        String userId1 = "user1";
        UserInfoEntity user1 = createUser(null, userId1);

        CurrencyEntity currency = currencyRepository.getReferenceById("643");

        Long accId1 = 201L;
        String accName1 = "account1";
        AccountEntity account1 = createAccount(user1, accName1, currency);

        Long accId2 = 202L;
        String accName2 = "account2";
        AccountEntity account2 = createAccount(user1, accName2, currency);


        //Act
        List<AccountEntity> accounts = accountService.findAllAccountsByUserId(userId1);

        //Assert
        assertNotNull(user1);
        assertNotNull(account1);
        assertNotNull(account2);

        assertNotNull(accounts);
        assertEquals(2, accounts.size(), "Должно быть найдено 2 счёта");
        assertTrue(accounts.stream().anyMatch(acc -> acc.getName().equals(accName1)));
        assertTrue(accounts.stream().anyMatch(acc -> acc.getName().equals(accName2)));
    }

    @DirtiesContext
    @Test
    @DisplayName("Поиск счетов. Один пользователь. Нет счетов")
    void findAllByUserIdIgnoreCaseTest3() {

        //Arrange
        String userId1 = "user1";
        UserInfoEntity user1 = createUser(null, userId1);

        String userId2 = "user2";
        UserInfoEntity user2 = createUser(null, userId2);

        CurrencyEntity currency = currencyRepository.getReferenceById("643");

        Long accId1 = 201L;
        String accName1 = "account1";
        AccountEntity account1 = createAccount(user2, accName1, currency);

        Long accId2 = 202L;
        String accName2 = "account2";
        AccountEntity account2 = createAccount(user2, accName2, currency);


        //Act
        List<AccountEntity> accounts = accountService.findAllAccountsByUserId(userId1);

        //Assert
        assertNotNull(user1);
        assertNotNull(user2);
        assertNotNull(account1);
        assertNotNull(account2);

        assertNotNull(accounts);
        assertEquals(0, accounts.size(), "Должно быть найдено 0 счетов");
    }

    @DirtiesContext
    @Test
    @DisplayName("Обобщённая информация о счетах.")
    void findAllSummariesTest1() {
        //Arrange
        String userId1 = "user1";
        UserInfoEntity user1 = createUser(null, userId1);

        CurrencyEntity cur1 = currencyRepository.getReferenceById("643");
        CurrencyEntity cur2 = currencyRepository.getReferenceById("840");

        String accName = "account1";
        AccountEntity account1 = createAccount(user1, accName, cur1);

        AccountEntity account2 = createAccount(user1, accName, cur2);

        //Act
        Collection<AccountSummary> summaries = accountService.findAllFamilyAccountsSummaries(userId1);

        //Assert
        assertNotNull(summaries);
        assertEquals(1, summaries.size());
        assertTrue(summaries.stream().anyMatch(s -> s.getName().equals(accName)));
        assertTrue(summaries.stream().flatMap(s -> s.getInitialBalance().stream()).anyMatch(b -> b.getCurrencyCode().equals("RUB")));
        assertTrue(summaries.stream().flatMap(s -> s.getInitialBalance().stream()).anyMatch(b -> b.getCurrencyCode().equals("USD")));
    }

    @DirtiesContext
    @Test
    @DisplayName("Поиск счёта.")
    void getUserAccount() {
        //Arrange
        String userId1 = "user1";
        UserInfoEntity user1 = createUser(null, userId1);

        CurrencyEntity cur1 = currencyRepository.getReferenceById("643");

        String accName = "account1";
        AccountEntity account = createAccount(user1, accName, cur1);
        Long accId1 = account.getId();

        //Act
        account = accountService.getAccount(accId1);

        //Assert
        assertNotNull(account);
        assertEquals(accName, account.getName());
        assertEquals(accId1, account.getId());
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