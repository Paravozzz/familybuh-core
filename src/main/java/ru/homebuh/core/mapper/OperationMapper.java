package ru.homebuh.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.homebuh.core.controller.dto.OperationDto;
import ru.homebuh.core.domain.OperationEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OperationMapper {

    @Mapping(target = "operationId", source = "id")
    @Mapping(target = "currencyCode", source = "account.currency.code")
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "category", source = "category.name")
    OperationDto mapToDto(OperationEntity source);

    default List<OperationDto> mapToDto(Iterable<OperationEntity> source) {
        if (source == null)
            return Collections.emptyList();

        List<OperationDto> result = new ArrayList<>();

        for (OperationEntity entity : source) {
            if (entity != null)
                result.add(mapToDto(entity));
        }

        return result;
    }

}
