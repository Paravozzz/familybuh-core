package ru.homebuh.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.AccountCreate;
import ru.homebuh.core.controller.dto.MasterAccountCreate;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.repository.CurrencyRepository;
import ru.homebuh.core.repository.UserInfoRepository;
import ru.homebuh.core.util.Constatnts;

import java.text.MessageFormat;

@Mapper(componentModel = "spring")
public abstract class AccountMapper {

    @Autowired
    protected UserInfoRepository userInfoRepository;

    @Autowired
    protected CurrencyRepository currencyRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userInfo", expression = "java(fetchUserInfo(source.getUserInfoId().toLowerCase()))")
    @Mapping(target = "currency", expression = "java(fetchCurrency(source.getCurrencyCode()))")
    public abstract AccountEntity map(AccountCreate source);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "name", expression = "java(source.getUserInfoId().toLowerCase())")
    @Mapping(target = "userInfo", expression = "java(fetchUserInfo(source.getUserInfoId()))")
    @Mapping(target = "currency", expression = "java(fetchCurrency(source.getCurrencyCode()))")
    public abstract AccountEntity map(MasterAccountCreate source);

    protected UserInfoEntity fetchUserInfo(String id) {
        return userInfoRepository
                .findByIdIgnoreCase(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constatnts.NOT_FOUND_BY_ID_TEMPLATE, "UserInfo", "id", id)
                ));
    }

    protected CurrencyEntity fetchCurrency(String code) {
        return currencyRepository.
                findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constatnts.NOT_FOUND_BY_ID_TEMPLATE, "Currency", "code", code)
                ));
    }
}
