package ru.homebuh.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.homebuh.core.domain.CategoryEntity;

import java.util.Collection;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    @Query("select user.categories from UserInfoEntity user where user.id = ?1")
    List<CategoryEntity> findAllByUserId(String id);

    @Query("select user.categories from UserInfoEntity user where user.id in ?1")
    List<CategoryEntity> findAllByUserIdIn(Collection<String> id);

}
