package ru.homebuh.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.homebuh.core.domain.ExchangeEntity;

import java.util.Collection;

@Repository
public interface ExchangeRepository extends JpaRepository<ExchangeEntity, Long>,
        QuerydslPredicateExecutor<ExchangeEntity> {
    @Modifying
    @Query("delete ExchangeEntity ex where ex.userInfo.id in ?1")
    void deleteAllByUserIdIn(Collection<String> userIds);
}
