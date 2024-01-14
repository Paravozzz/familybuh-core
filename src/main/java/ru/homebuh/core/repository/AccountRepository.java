package ru.homebuh.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.homebuh.core.domain.AccountEntity;

import java.util.Collection;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    /**
     * Найти все счета пользователя
     *
     * @param userId идентификатор пользователя
     * @return список счетов
     */
    @Query("select account from AccountEntity account where lower(account.userInfo.id) = lower(?1)")
    List<AccountEntity> findAllByUserIdIgnoreCase(String userId);

    /**
     * Найти все счета пользователей
     *
     * @param userIds идентификаторы пользователя
     * @return список счетов
     */
    @Query("select account from AccountEntity account where lower(account.userInfo.id) in ?1")
    List<AccountEntity> findAllByUserIdIgnoreCase(Collection<String> userIds);

    /**
     * Найти счета пользователя по имени счёта
     *
     * @param userId      идентификатор пользователя
     * @param accountName имя счёта
     * @return счёт
     */
    @Query("select account from AccountEntity account where lower(account.userInfo.id) = ?1 " +
            "and account.name = ?2")
    List<AccountEntity> findAccountsByName(String userId, String accountName);

    /**
     * Найти счета пользователей по имени счёта
     *
     * @param userIds      идентификаторы пользователей
     * @param accountName имя счёта
     * @return счёт
     */
    @Query("select account from AccountEntity account where lower(account.userInfo.id) in ?1 " +
            "and account.name = ?2")
    List<AccountEntity> findAccountsByName(Collection<String> userIds, String accountName);

    /**
     * Найти счета по их идентификаторам
     *
     * @param accountIds идентификаторы счетов
     * @return
     */
    @Query("select account from AccountEntity account where account.id in ?1")
    List<AccountEntity> findAccounts(Collection<Long> accountIds);

    @Modifying
    @Query("delete AccountEntity account where account.userInfo.id in ?1")
    void deleteAllFamilyAccounts(Collection<String> familyIds);
}
