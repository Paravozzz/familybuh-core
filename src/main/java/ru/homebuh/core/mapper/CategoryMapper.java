package ru.homebuh.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.homebuh.core.controller.dto.CategoryCreate;
import ru.homebuh.core.controller.dto.CategoryUpdate;
import ru.homebuh.core.domain.CategoryEntity;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    CategoryEntity map(CategoryCreate source);

    @Mapping(target = "id", source = "categoryId")
    CategoryEntity map(Long categoryId, CategoryUpdate source);
}
