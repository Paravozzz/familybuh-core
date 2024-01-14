package ru.homebuh.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.homebuh.core.domain.OperationEntity;

import java.util.Collection;

@Repository
public interface OperationRepository extends
        JpaRepository<OperationEntity, Long>,
        QuerydslPredicateExecutor<OperationEntity> {

    @Modifying
    @Query("delete OperationEntity op where op.userInfo.id in ?1")
    void deleteAllByUserIdIn(Collection<String> userIds);
}
