package ru.homebuh.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.homebuh.core.controller.dto.SettingCreate;
import ru.homebuh.core.controller.dto.SettingDto;
import ru.homebuh.core.domain.SettingEntity;

@Mapper(componentModel = "spring")
public interface SettingMapper {

    @Mapping(target = "id", ignore = true)
    SettingEntity map(SettingCreate source);

    SettingDto map(SettingEntity source);
}
