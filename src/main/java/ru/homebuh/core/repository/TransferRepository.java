package ru.homebuh.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.homebuh.core.domain.TransferEntity;

import java.util.Collection;

@Repository
public interface TransferRepository extends JpaRepository<TransferEntity, Long>,
        QuerydslPredicateExecutor<TransferEntity> {
    @Modifying
    @Query("delete TransferEntity tr where tr.userInfo.id in ?1")
    void deleteAllByUserIdIn(Collection<String> userIds);

}
