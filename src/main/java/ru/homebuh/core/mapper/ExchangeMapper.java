package ru.homebuh.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.homebuh.core.controller.dto.ExchangeDto;
import ru.homebuh.core.domain.ExchangeEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ExchangeMapper {

    @Mapping(target = "exchangeId", source = "id")
    @Mapping(target = "incomeAmount", source = "income.amount")
    @Mapping(target = "expenseAmount", source = "expense.amount")
    @Mapping(target = "incomeCurrencyCode", source = "income.account.currency.code")
    @Mapping(target = "expenseCurrencyCode", source = "expense.account.currency.code")
    @Mapping(target = "accountName", source = "expense.account.name")
    ExchangeDto mapToDto(ExchangeEntity source);

    default List<ExchangeDto> mapToDto(Iterable<ExchangeEntity> source) {
        if (source == null)
            return Collections.emptyList();

        List<ExchangeDto> result = new ArrayList<>();

        for (ExchangeEntity entity : source) {
            if (entity != null)
                result.add(mapToDto(entity));
        }

        return result;
    }

}
