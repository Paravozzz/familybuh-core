package ru.homebuh.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.homebuh.core.domain.OperationEntity;

public interface OperationRepository extends JpaRepository<OperationEntity, Long> {


}
