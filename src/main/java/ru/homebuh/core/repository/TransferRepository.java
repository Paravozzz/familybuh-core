package ru.homebuh.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.homebuh.core.domain.TransferEntity;

@Repository
public interface TransferRepository extends JpaRepository<TransferEntity, Long> {
}
