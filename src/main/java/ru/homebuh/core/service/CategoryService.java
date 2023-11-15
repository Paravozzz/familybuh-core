package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.CategoryCreate;
import ru.homebuh.core.controller.dto.CategoryUpdate;
import ru.homebuh.core.domain.CategoryEntity;
import ru.homebuh.core.mapper.CategoryMapper;
import ru.homebuh.core.repository.CategoryRepository;
import ru.homebuh.core.util.Constatnts;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
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
                        MessageFormat.format(Constatnts.NOT_FOUND_BY_ID_TEMPLATE, "Category", "id", categoryId)));
    }

    @Transactional
    public CategoryEntity create(CategoryCreate categoryCreate) {
        return categoryRepository.save(categoryMapper.map(categoryCreate));
    }

    @Transactional
    public CategoryEntity update(Long categoryId, CategoryUpdate categoryUpdate) {
        return categoryRepository.save(categoryMapper.map(categoryId, categoryUpdate));
    }
}
