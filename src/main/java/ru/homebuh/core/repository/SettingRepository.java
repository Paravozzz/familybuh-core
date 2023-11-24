package ru.homebuh.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.homebuh.core.domain.SettingEntity;

import java.util.List;

@Repository
public interface SettingRepository extends JpaRepository<SettingEntity, Long> {

    @Query("select user.settings from UserInfoEntity user where user.id = ?1")
    List<SettingEntity> findAllByUserId(String id);

}
