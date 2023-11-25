package ru.homebuh.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.homebuh.core.controller.dto.OperationDto;
import ru.homebuh.core.domain.OperationEntity;

@Mapper(componentModel = "spring")
public interface OperationMapper {

    @Mapping(target = "operationId", source = "id")
    @Mapping(target = "currencyCode", source = "debitAccount.currency.code")
    @Mapping(target = "accountId", source = "debitAccount.id")
    @Mapping(target = "category", source = "category.name")
    OperationDto mapExpense(OperationEntity source);

    @Mapping(target = "operationId", source = "id")
    @Mapping(target = "currencyCode", source = "creditAccount.currency.code")
    @Mapping(target = "accountId", source = "creditAccount.id")
    @Mapping(target = "category", source = "category.name")
    OperationDto mapIncome(OperationEntity source);

}
