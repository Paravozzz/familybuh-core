package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.homebuh.core.controller.dto.UserInfoCreate;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.mapper.UserInfoMapper;
import ru.homebuh.core.repository.UserInfoRepository;

@Service
@RequiredArgsConstructor
public class UserInfoService {
    private final UserInfoRepository userInfoRepository;
    private final UserInfoMapper mapper;

    @Transactional
    public UserInfoEntity create(UserInfoCreate userInfoCreate) {
        return userInfoRepository.save(mapper.map(userInfoCreate));
    }


}
