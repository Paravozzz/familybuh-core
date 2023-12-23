package ru.homebuh.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.homebuh.core.domain.AccountEntity;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    /**
     * Найти все счета пользователя
     *
     * @param userId идентификатор пользователя
     * @return список обычных счетов
     */
    @Query("select account from AccountEntity account where lower(account.userInfo.id) = lower(?1)")
    List<AccountEntity> findAllByUserIdIgnoreCase(String userId);

    /**
     * Найти счет пользователя
     * @param userId       идентификатор пользователя
     * @param accountId    идентификатор счёта
     * @param currencyCode буквенный код валюты счёта
     * @return обычный счет
     */
    @Query("select account from AccountEntity account where lower(account.userInfo.id) = lower(?1) " +
            "and account.id = ?2 " +
            "and account.currency.code = upper(?3)")
    Optional<AccountEntity> findAccount(String userId, Long accountId, String currencyCode);
}
