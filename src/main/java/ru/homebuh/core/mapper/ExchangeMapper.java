package ru.homebuh.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.homebuh.core.controller.dto.ExchangeDto;
import ru.homebuh.core.domain.ExchangeEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ExchangeMapper {

    @Mapping(target = "exchangeId", source = "id")
    @Mapping(target = "incomeAmount", expression = "java(abs(source.getIncome() == null ? null : source.getIncome().getAmount()))")
    @Mapping(target = "expenseAmount", expression = "java(abs(source.getExpense() == null ? null : source.getExpense().getAmount()))")
    @Mapping(target = "incomeCurrency", source = "income.account.currency")
    @Mapping(target = "expenseCurrency", source = "expense.account.currency")
    @Mapping(target = "accountName", source = "expense.account.name")
    public abstract ExchangeDto mapToDto(ExchangeEntity source);
    public List<ExchangeDto> mapToDto(Iterable<ExchangeEntity> source) {
        if (source == null)
            return Collections.emptyList();

        List<ExchangeDto> result = new ArrayList<>();

        for (ExchangeEntity entity : source) {
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
