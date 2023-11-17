package ru.homebuh.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ru.homebuh.core.controller.dto.CategoryCreate;
import ru.homebuh.core.controller.dto.CategoryUpdate;
import ru.homebuh.core.domain.CategoryEntity;
import ru.homebuh.core.service.CategoryService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("user/categories")
    Collection<CategoryEntity> getAll(
            final JwtAuthenticationToken token) {
        return categoryService.findAllByUserId(token.getName());
    }

    @GetMapping("user/category/{categoryId}")
    CategoryEntity findById(
            final JwtAuthenticationToken token,
            @RequestParam(value = "categoryId", required = true) Long categoryId) {
        return categoryService.findUserCategoryById(token.getName(), categoryId);
    }

    @PostMapping("user/category")
    CategoryEntity create(
            final JwtAuthenticationToken token,
            @RequestBody CategoryCreate categoryCreate) {
        return categoryService.create(token.getName(), categoryCreate);
    }

    @PutMapping("user/category/{categoryId}")
    CategoryEntity update(
            final JwtAuthenticationToken token,
            @RequestParam(value = "categoryId", required = true) Long categoryId,
            @RequestBody CategoryUpdate categoryUpdate) {
        return categoryService.update(categoryId, categoryUpdate);
    }
}
