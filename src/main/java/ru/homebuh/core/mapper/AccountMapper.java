package ru.homebuh.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.homebuh.core.controller.dto.AccountCreate;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.repository.CurrencyRepository;
import ru.homebuh.core.repository.UserInfoRepository;

@Mapper(componentModel = "spring")
public abstract class AccountMapper {

    @Autowired
    protected UserInfoRepository userInfoRepository;

    @Autowired
    protected CurrencyRepository currencyRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userInfo", expression = "java(fetchUserInfo(source.getUserInfoId()))")
    @Mapping(target = "currency", expression = "java(fetchCurrency(source.getCurrencyNum()))")
    public abstract AccountEntity map(AccountCreate source);

    protected UserInfoEntity fetchUserInfo(Long id) {
        return userInfoRepository.getReferenceById(id);
    }

    protected CurrencyEntity fetchCurrency(String id) {
        return currencyRepository.getReferenceById(id);
    }
}
