package ru.homebuh.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.homebuh.core.controller.dto.UserInfoCreate;
import ru.homebuh.core.domain.UserInfoEntity;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {

    @Mapping(target = "currencies", ignore = true)
    UserInfoEntity map(UserInfoCreate source);
}
