package ru.homebuh.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.homebuh.core.domain.AccountEntity;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    /**
     * Найти все обычные счета пользователя
     * @param userId идентификатор пользователя
     * @return список обычных счетов
     */
    @Query("select account from AccountEntity account where lower(account.userInfo.id) = lower(?1) " +
            "and lower(account.userInfo.id) <> lower(account.name)")
    List<AccountEntity> findAllByUserIdIgnoreCase(String userId);

    /**
     * Найти все мастер-счета пользователя
     * @param userId идентификатор пользователя
     * @return список мастер-счетов
     */
    @Query("select account from AccountEntity account where lower(account.userInfo.id) = lower(?1) " +
            "and lower(account.userInfo.id) = lower(account.name)")
    List<AccountEntity> findAllMasterByUserIdIgnoreCase(String userId);

    /**
     * Найти мастер-счет пользователя
     * @param userId идентификатор пользователя
     * @param currencyId цифровой код валюты
     * @return мастер-счет
     */
    @Query("select account from AccountEntity account where lower(account.userInfo.id) = lower(?1) " +
            "and lower(account.userInfo.id) = lower(account.name)" +
            "and account.currency.id = upper(?2)")
    Optional<AccountEntity> findMasterByUserIdIgnoreCaseAndCurrencyId(String userId, String currencyId);
}
