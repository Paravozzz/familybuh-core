package ru.homebuh.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.homebuh.core.domain.AccountEntity;

import java.util.List;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    @Query("select account from AccountEntity account where account.userInfo.id = ?1")
    List<AccountEntity> findAllByUserId(String id);
}
