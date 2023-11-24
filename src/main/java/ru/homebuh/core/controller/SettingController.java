package ru.homebuh.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ru.homebuh.core.controller.dto.SettingCreate;
import ru.homebuh.core.controller.dto.SettingUpdate;
import ru.homebuh.core.domain.SettingEntity;
import ru.homebuh.core.service.SettingService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class SettingController {

    private final SettingService settingService;

    @GetMapping("user/settings")
    Collection<SettingEntity> getAll(
            final JwtAuthenticationToken token) {
        return settingService.findAllByUserId(token.getName());
    }

    @GetMapping("user/setting/{settingId}")
    SettingEntity findById(
            final JwtAuthenticationToken token,
            @PathVariable(value = "settingId", required = true) Long settingId) {
        return settingService.findUserSettingById(token.getName(), settingId);
    }

    @PostMapping("user/setting")
    SettingEntity create(
            final JwtAuthenticationToken token,
            @RequestBody SettingCreate settingCreate) {
        return settingService.create(token.getName(), settingCreate);
    }

    @PutMapping("user/setting/{settingId}")
    SettingEntity update(
            final JwtAuthenticationToken token,
            @PathVariable(value = "settingId", required = true) Long settingId,
            @RequestBody SettingUpdate settingUpdate) {
        return settingService.update(token.getName(), settingId, settingUpdate);
    }
}
