package ru.homebuh.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.homebuh.core.controller.dto.OperationDto;
import ru.homebuh.core.domain.OperationEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class OperationMapper {

    @Mapping(target = "operationId", source = "id")
    @Mapping(target = "amount", expression = "java(abs(source.getAmount()))")
    @Mapping(target = "currency", source = "account.currency")
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountName", source = "account.name")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "category", source = "category.name")
    public abstract OperationDto mapToDto(OperationEntity source);

    public List<OperationDto> mapToDto(Iterable<OperationEntity> source) {
        if (source == null)
            return Collections.emptyList();

        List<OperationDto> result = new ArrayList<>();

        for (OperationEntity entity : source) {
            if (entity != null)
                result.add(mapToDto(entity));
        }

        return result;
    }

    protected String abs(BigDecimal source) {
        if (source == null) {
            return null;
        }
        return source.abs().toString();
    }

}
