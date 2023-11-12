package ru.homebuh.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.homebuh.core.domain.CategoryEntity;
import ru.homebuh.core.service.CategoryService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("user/categories")
    Collection<CategoryEntity> getAll(final JwtAuthenticationToken token) {
        return categoryService.findAllByUserId(token.getName());
    }
}
