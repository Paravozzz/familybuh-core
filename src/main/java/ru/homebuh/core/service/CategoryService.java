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
import ru.homebuh.core.util.Constants;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final UserInfoService userInfoService;

    /**
     * Найти все категории пользователя и его семьи
     *
     * @param userId идентификатор пользователя
     * @return список всех категорий пользователя и его семьи
     */
    public List<CategoryEntity> findAllFamilyCategoriesByUserId(String userId) {
        List<UserInfoEntity> familyMembers = userInfoService.findAllFamilyMembers(userId);
        Set<String> membersIds = familyMembers.stream().map(UserInfoEntity::getId).collect(Collectors.toSet());
        return categoryRepository.findAllByUserIdIn(membersIds);
    }


    /**
     * Найти все расходные категории пользователя
     *
     * @param id идентификатор пользователя
     * @return список расходных категорий
     */
    public List<CategoryEntity> findAllExpenseByUserId(String id) {
        return categoryRepository.findAllByUserId(id).stream().filter(category -> !category.isIncome()).toList();
    }

    /**
     * Найти все доходные категории пользователя
     *
     * @param id идентификатор пользователя
     * @return список доходных категорий
     */
    public List<CategoryEntity> findAllIncomeByUserId(String id) {
        return categoryRepository.findAllByUserId(id).stream().filter(CategoryEntity::isIncome).toList();
    }

    /**
     * Найти категорию принадлежащую пользователю
     *
     * @param categoryId идентификатор категории
     * @return искомая категория
     * @throws ResponseStatusException если категория или пользователь не найдены
     */
    public CategoryEntity getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constants.NOT_FOUND_BY_PARAM_TEMPLATE, Constants.CATEGORY, "id", categoryId)));
    }

    /**
     * Создать новую категорию у определённого пользователя
     *
     * @param userInfoId     идентификатор пользователя
     * @param categoryCreate данные для создания категории
     * @return созданную категорию
     */
    @Transactional
    public CategoryEntity create(String userInfoId, CategoryCreate categoryCreate) {
        CategoryEntity newCategory = categoryMapper.map(categoryCreate);
        UserInfoEntity userInfo = userInfoService.getUserInfo(userInfoId);

        List<CategoryEntity> categories = userInfo.getCategories();
        if (categories.contains(newCategory)) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    MessageFormat.format(Constants.DUPLICATE_BY_PARAM_TEMPLATE, Constants.CATEGORY, "name", newCategory.getName()));
        }
        categories.add(newCategory);
        userInfoService.save(userInfo);
        return categories.get(categories.size() - 1);
    }

    /**
     * Обновить категорию у определённого пользователя
     *
     * @param categoryId     идентификатор обновляемой категории
     * @param categoryUpdate данные для обновления категории
     * @return обновлённая категория
     */
    @Transactional
    public CategoryEntity update(Long categoryId, CategoryUpdate categoryUpdate) {

        //TODO: Проверить что у пользователя или семьи нет категории с таким же именем
        return categoryRepository.save(categoryMapper.map(categoryId, categoryUpdate));
    }
}
