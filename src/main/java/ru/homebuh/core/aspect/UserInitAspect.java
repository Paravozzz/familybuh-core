package ru.homebuh.core.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.repository.UserInfoRepository;
import ru.homebuh.core.service.UserInitService;

import java.util.Optional;

@Aspect
@Component
@Log
@RequiredArgsConstructor
public class UserInitAspect {

    private final UserInitService userInitService;
    private final UserInfoRepository userInfoRepository;

    @Pointcut("@annotation(UserInitAnnotation) && args(token,..)")
    public void callAtUserInitAnnotation(JwtAuthenticationToken token) {
    }

    @Before(value = "callAtUserInitAnnotation(token)", argNames = "jp,token")
    public void beforeCallMethodWithAnnotation(JoinPoint jp, JwtAuthenticationToken token) {
        final String sub = token.getTokenAttributes().get("sub").toString();
        Optional<UserInfoEntity> userInfoOpt = userInfoRepository.findByIdIgnoreCase(sub);
        if (userInfoOpt.isPresent())
            return;

        userInitService.init(sub);

    }
}
