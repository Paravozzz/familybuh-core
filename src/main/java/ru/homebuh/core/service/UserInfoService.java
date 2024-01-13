package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.UserInfoCreate;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.mapper.UserInfoMapper;
import ru.homebuh.core.repository.UserInfoRepository;
import ru.homebuh.core.util.Constants;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class UserInfoService {
    private final UserInfoRepository userInfoRepository;
    private final UserInfoMapper mapper;

    public void isUserExists(String id) {
        getUserInfo(id);
    }

    @Transactional
    public UserInfoEntity create(UserInfoCreate userInfoCreate) {
        return userInfoRepository.save(mapper.map(userInfoCreate));
    }

    public UserInfoEntity getUserInfo(String id) {
        return userInfoRepository.findByIdIgnoreCase(id)
                .orElseThrow(notFoundByIdExceptionSupplier(id));
    }

    public static Supplier<ResponseStatusException> notFoundByIdExceptionSupplier(String id) {
        return () -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                MessageFormat.format(Constants.NOT_FOUND_BY_PARAM_TEMPLATE, Constants.USER, "id", id));
    }

    @Transactional
    public void save(UserInfoEntity userInfoEntity) {
        userInfoRepository.save(userInfoEntity);
    }

    @Transactional
    public void saveAll(Collection<UserInfoEntity> userInfoEntiies) {
        userInfoRepository.saveAll(userInfoEntiies);
    }

    public List<UserInfoEntity> findAllFamilyMembers(String userId) {
        UserInfoEntity userInfo = userInfoRepository.findByIdIgnoreCase(userId)
                .orElseThrow(notFoundByIdExceptionSupplier(userId));
        if (userInfo.getFamily() == null) {
            return Collections.singletonList(userInfo);
        } else {
            return userInfoRepository.findAllFamilyMembers(userInfo.getFamily());
        }
    }
}
