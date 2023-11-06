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

import java.util.Optional;

@Aspect
@Component
@Log
@RequiredArgsConstructor
public class UserInitAspect {

    @Pointcut("@annotation(UserInitAnnotation) && args(token,..)")
    public void callAtUserInitAnnotation(JwtAuthenticationToken token) {
    }

    @Before(value = "callAtUserInitAnnotation(token)", argNames = "jp,token")
    public void beforeCallMethodWithAnnotation(JoinPoint jp, JwtAuthenticationToken token) {
        final String sub = token.getTokenAttributes().get("sub").toString();
        log.info(sub);
    }
}
