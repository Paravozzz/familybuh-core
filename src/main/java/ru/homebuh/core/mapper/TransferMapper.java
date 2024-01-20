package ru.homebuh.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.homebuh.core.controller.dto.TransferDto;
import ru.homebuh.core.domain.TransferEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    @Mapping(target = "transferId", source = "id")
    @Mapping(target = "amount", source = "income.amount")
    @Mapping(target = "currencyCode", source = "expense.account.currency.code")
    @Mapping(target = "expenseAccountName", source = "expense.account.name")
    @Mapping(target = "incomeAccountName", source = "income.account.name")
    TransferDto mapToDto(TransferEntity source);

    default List<TransferDto> mapToDto(Iterable<TransferEntity> source) {
        if (source == null)
            return Collections.emptyList();

        List<TransferDto> result = new ArrayList<>();

        for (TransferEntity entity : source) {
            if (entity != null)
                result.add(mapToDto(entity));
        }

        return result;
    }

}
