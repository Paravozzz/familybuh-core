package ru.homebuh.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.homebuh.core.domain.FamilyEntity;
import ru.homebuh.core.domain.UserInfoEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfoEntity, String> {
    Optional<UserInfoEntity> findByIdIgnoreCase(String id);
    @Query("select u from UserInfoEntity u where u.family = ?1")
    List<UserInfoEntity> findAllFamilyMembers(FamilyEntity family);

}
