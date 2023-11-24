package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.SettingCreate;
import ru.homebuh.core.controller.dto.SettingUpdate;
import ru.homebuh.core.domain.SettingEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.mapper.SettingMapper;
import ru.homebuh.core.repository.SettingRepository;
import ru.homebuh.core.repository.UserInfoRepository;
import ru.homebuh.core.util.Constants;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SettingService {
    private final SettingRepository settingRepository;
    private final UserInfoRepository userInfoRepository;
    private final SettingMapper settingMapper;

    public List<SettingEntity> findAllByUserId(String id) {
        return settingRepository.findAllByUserId(id);
    }

    public SettingEntity findUserSettingById(String userInfoId, Long settingId) {
        List<SettingEntity> usersSettings = settingRepository.findAllByUserId(userInfoId);
        return usersSettings.stream()
                .filter(settingEntity -> Objects.equals(settingEntity.getId(), settingId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constants.NOT_FOUND_BY_PARAM_TEMPLATE, Constants.SETTING, "id", settingId)));
    }

    @Transactional
    public SettingEntity create(String userInfoId, SettingCreate settingCreate) {
        SettingEntity newSetting = settingMapper.map(settingCreate);
        UserInfoEntity userInfo = userInfoRepository.findByIdIgnoreCase(userInfoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constants.NOT_FOUND_BY_PARAM_TEMPLATE, Constants.USER, "id", userInfoId)));

        List<SettingEntity> settings = userInfo.getSettings();
        if (settings.contains(newSetting)) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    MessageFormat.format(Constants.DUPLICATE_BY_PARAM_TEMPLATE, Constants.SETTING, "name", newSetting.getName()));
        }
        settings.add(newSetting);
        userInfoRepository.save(userInfo);
        return newSetting;
    }

    @Transactional
    public SettingEntity update(String userInfoId, Long settingId, SettingUpdate settingUpdate) {
        UserInfoEntity userInfo = userInfoRepository.findByIdIgnoreCase(userInfoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        MessageFormat.format(Constants.NOT_FOUND_BY_PARAM_TEMPLATE, Constants.USER, "id", userInfoId)));

        List<SettingEntity> settings = userInfo.getSettings();
        if (settings.stream().noneMatch(c -> Objects.equals(c.getId(), settingId))) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    MessageFormat.format(Constants.DUPLICATE_BY_PARAM_TEMPLATE, Constants.SETTING, "id", settingId));
        }
        return settingRepository.save(settingMapper.map(settingId, settingUpdate));
    }
}
