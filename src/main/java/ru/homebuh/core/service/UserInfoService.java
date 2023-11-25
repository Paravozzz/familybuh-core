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

@Service
@RequiredArgsConstructor
public class UserInfoService {
    private final UserInfoRepository userInfoRepository;
    private final UserInfoMapper mapper;

    public void isUserExists(String id) {
        findByIdIgnoreCase(id);
    }

    @Transactional
    public UserInfoEntity create(UserInfoCreate userInfoCreate) {
        return userInfoRepository.save(mapper.map(userInfoCreate));
    }

    public UserInfoEntity findByIdIgnoreCase(String id) {
        return userInfoRepository.findByIdIgnoreCase(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constants.NOT_FOUND_BY_PARAM_TEMPLATE, Constants.USER, "id", id)));
    }

    @Transactional
    public void save(UserInfoEntity userInfoEntity) {
        userInfoRepository.save(userInfoEntity);
    }

}
