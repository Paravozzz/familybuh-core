package ru.homebuh.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.homebuh.core.domain.AccountEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
     * Найти счет пользователя
     *
     * @param userId    идентификатор пользователя
     * @param accountId идентификатор счёта
     * @return счет
     */
    @Query("select account from AccountEntity account where lower(account.userInfo.id) = lower(?1) " +
            "and account.id = ?2")
    Optional<AccountEntity> findAccount(String userId, Long accountId);

    /**
     * Найти счета пользователя по имени счёта
     *
     * @param userId      идентификатор пользователя
     * @param accountName имя счёта
     * @return счёт
     */
    @Query("select account from AccountEntity account where lower(account.userInfo.id) = lower(?1) " +
            "and account.name = ?2")
    Collection<AccountEntity> findAccounts(String userId, String accountName);

    /**
     * Найти счета по их идентификаторам
     *
     * @param userId     идентификатор пользователя
     * @param accountIds идентификаторы счетов
     * @return
     */
    @Query("select account from AccountEntity account where lower(account.userInfo.id) = lower(?1) " +
            "and account.id in ?2")
    Collection<AccountEntity> findAccounts(String userId, Collection<Long> accountIds);
}
