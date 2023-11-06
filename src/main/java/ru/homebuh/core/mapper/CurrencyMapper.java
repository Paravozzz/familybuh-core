package ru.homebuh.core.mapper;

import org.mapstruct.Mapper;
import ru.homebuh.core.controller.dto.CurrencyCreate;
import ru.homebuh.core.domain.CurrencyEntity;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {

    CurrencyEntity map(CurrencyCreate source);
}
