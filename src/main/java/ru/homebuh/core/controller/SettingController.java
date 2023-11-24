package ru.homebuh.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ru.homebuh.core.controller.dto.SettingCreate;
import ru.homebuh.core.controller.dto.SettingDto;
import ru.homebuh.core.service.SettingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class SettingController {

    private final SettingService settingService;

    @GetMapping("user/setting/name/{name}")
    SettingDto findById(
            final JwtAuthenticationToken token,
            @PathVariable(value = "name", required = true) String name) {
        return settingService.findUserSettingByName(token.getName(), name);
    }

    @PostMapping("user/setting")
    SettingDto create(
            final JwtAuthenticationToken token,
            @RequestBody SettingCreate settingCreate) {
        return settingService.save(token.getName(), settingCreate);
    }
}
