package ru.homebuh.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.homebuh.core.domain.OperationEntity;

public interface OperationRepository extends
        JpaRepository<OperationEntity, Long>,
        QuerydslPredicateExecutor<OperationEntity> {


}
