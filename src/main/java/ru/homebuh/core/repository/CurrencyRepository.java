package ru.homebuh.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.homebuh.core.domain.CurrencyEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, String> {
    @Query("select distinct user.currencies from UserInfoEntity user where user.id = ?1")
    List<CurrencyEntity> findAllByUserId(String id);

    @Query("select distinct user.currencies from UserInfoEntity user where user.id in ?1")
    List<CurrencyEntity> findAllByUserIdIn(Collection<String> ids);

    Optional<CurrencyEntity> findByCodeIgnoreCase(String code);
}
