package ru.homebuh.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.homebuh.core.domain.FamilyEntity;

@Repository
public interface FamilyRepository extends JpaRepository<FamilyEntity, Long> {

}
