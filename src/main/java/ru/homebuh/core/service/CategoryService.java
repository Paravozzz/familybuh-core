package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.homebuh.core.domain.CategoryEntity;
import ru.homebuh.core.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository currencyRepository;

    public List<CategoryEntity> findAllByUserId(String id) {
        return currencyRepository.findAllByUserId(id);
    }

}
