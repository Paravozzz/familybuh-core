package ru.homebuh.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.homebuh.core.controller.dto.TransferDto;
import ru.homebuh.core.domain.TransferEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class TransferMapper {

    @Mapping(target = "transferId", source = "id")
    @Mapping(target = "amount", expression = "java(abs(source.getIncome() == null ? null : source.getIncome().getAmount()))")
    @Mapping(target = "currency", source = "expense.account.currency")
    @Mapping(target = "expenseAccountName", source = "expense.account.name")
    @Mapping(target = "incomeAccountName", source = "income.account.name")
    public abstract TransferDto mapToDto(TransferEntity source);

    public List<TransferDto> mapToDto(Iterable<TransferEntity> source) {
        if (source == null)
            return Collections.emptyList();

        List<TransferDto> result = new ArrayList<>();

        for (TransferEntity entity : source) {
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
