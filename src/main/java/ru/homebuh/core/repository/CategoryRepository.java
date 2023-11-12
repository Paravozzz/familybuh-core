package ru.homebuh.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.homebuh.core.domain.CategoryEntity;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    //TODO: Когда появятся группы, то нужно будет делать выборку с учётом групп пользователей
    @Query("select user.categories from UserInfoEntity user where user.id = ?1")
    List<CategoryEntity> findAllByUserId(String id);

}
