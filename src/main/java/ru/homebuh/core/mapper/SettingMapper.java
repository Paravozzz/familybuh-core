package ru.homebuh.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.homebuh.core.controller.dto.SettingCreate;
import ru.homebuh.core.controller.dto.SettingUpdate;
import ru.homebuh.core.domain.SettingEntity;

@Mapper(componentModel = "spring")
public interface SettingMapper {

    @Mapping(target = "id", ignore = true)
    SettingEntity map(SettingCreate source);

    @Mapping(target = "id", source = "settingId")
    SettingEntity map(Long settingId, SettingUpdate source);
}
