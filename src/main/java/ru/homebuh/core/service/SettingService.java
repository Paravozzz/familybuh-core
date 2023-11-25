package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.homebuh.core.controller.dto.SettingCreate;
import ru.homebuh.core.controller.dto.SettingDto;
import ru.homebuh.core.domain.SettingEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.mapper.SettingMapper;
import ru.homebuh.core.repository.SettingRepository;

import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class SettingService {
    private final SettingRepository settingRepository;
    private final SettingMapper settingMapper;
    private final UserInfoService userInfoService;

    private static Predicate<SettingEntity> settingFilterByName(String name) {
        return s -> name != null && !name.isBlank() && s.getName().equalsIgnoreCase(name);
    }

    public SettingDto findUserSettingByName(String userInfoId, String name) {
        List<SettingEntity> usersSettings = settingRepository.findAllByUserId(userInfoId);
        SettingEntity settingEntity = usersSettings.stream()
                .filter(settingFilterByName(name))
                .findFirst()
                .orElse(null);
        if (settingEntity == null) {
            settingEntity = new SettingEntity();
            settingEntity.setName(name);
            settingEntity.setValue("");
        }
        return settingMapper.map(settingEntity);
    }

    @Transactional
    public SettingDto save(String userInfoId, SettingCreate settingCreate) {
        SettingEntity newSetting = settingMapper.map(settingCreate);
        UserInfoEntity userInfo = userInfoService.findByIdIgnoreCase(userInfoId);

        List<SettingEntity> settings = userInfo.getSettings();
        final String name = newSetting.getName();
        SettingEntity existentSetting = settings.stream()
                .filter(settingFilterByName(name))
                .findFirst()
                .orElse(null);

        if (existentSetting != null) {
            existentSetting.setValue(newSetting.getValue());
            return settingMapper.map(settingRepository.save(existentSetting));
        } else {
            settings.add(newSetting);
            userInfoService.save(userInfo);
            return findUserSettingByName(userInfoId, newSetting.getName());
        }
    }
}
