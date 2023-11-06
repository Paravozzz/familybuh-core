package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.homebuh.core.controller.dto.UserInfoCreate;

@Service
@RequiredArgsConstructor
public class UserInitService {

    private final UserInfoService userInfoService;

    @Transactional
    public void init(String id) {
        userInfoService.create(new UserInfoCreate(id));
    }

}
