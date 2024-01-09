package ru.homebuh.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.homebuh.core.controller.dto.InitCreate;
import ru.homebuh.core.service.ControllerServiceFacade;

@RequiredArgsConstructor
@Controller
@RequestMapping("api")
public class InitController {

    private final ControllerServiceFacade controllerServiceFacade;

    /**
     * Проверяет, все ли начальные параметры заданы для текущего пользователя
     * @param token JWT
     */
    @GetMapping("init")
    public ResponseEntity<Boolean> isUserInit(final JwtAuthenticationToken token) {
        return new ResponseEntity<>(controllerServiceFacade.isUserInit(token.getName()), HttpStatus.OK);
    }

    /**
     * Инициализация пользователя
     * @param token JWT
     * @param initCreate
     */
    @PostMapping("init")
    @ResponseStatus(HttpStatus.OK)
    public void initUser(final JwtAuthenticationToken token, @RequestBody InitCreate initCreate) {
        controllerServiceFacade.initUser(token.getName(), initCreate);
    }
}
