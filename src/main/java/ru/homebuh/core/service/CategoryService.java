package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.CategoryCreate;
import ru.homebuh.core.controller.dto.CategoryUpdate;
import ru.homebuh.core.domain.CategoryEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.mapper.CategoryMapper;
import ru.homebuh.core.repository.CategoryRepository;
import ru.homebuh.core.repository.UserInfoRepository;
import ru.homebuh.core.util.Constatnts;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserInfoRepository userInfoRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryEntity> findAllByUserId(String id) {
        return categoryRepository.findAllByUserId(id);
    }

    public CategoryEntity findUserCategoryById(String userInfoId, Long categoryId) {
        List<CategoryEntity> usersCategories = categoryRepository.findAllByUserId(userInfoId);
        return usersCategories.stream()
                .filter(categoryEntity -> Objects.equals(categoryEntity.getId(), categoryId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constatnts.NOT_FOUND_BY_PARAM_TEMPLATE, "Category", "id", categoryId)));
    }

    @Transactional
    public CategoryEntity create(String userInfoId, CategoryCreate categoryCreate) {
        CategoryEntity newCategory = categoryMapper.map(categoryCreate);
        UserInfoEntity userInfo = userInfoRepository.findByIdIgnoreCase(userInfoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constatnts.NOT_FOUND_BY_PARAM_TEMPLATE, "User", "id", userInfoId)));

        List<CategoryEntity> categories = userInfo.getCategories();
        if (categories.contains(newCategory)) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    MessageFormat.format(Constatnts.DUPLICATE_BY_PARAM_TEMPLATE, "Category", "name", newCategory.getName()));
        }
        categories.add(newCategory);
        userInfoRepository.save(userInfo);
        return newCategory;
    }

    @Transactional
    public CategoryEntity update(Long categoryId, CategoryUpdate categoryUpdate) {
        return categoryRepository.save(categoryMapper.map(categoryId, categoryUpdate));
    }
}
