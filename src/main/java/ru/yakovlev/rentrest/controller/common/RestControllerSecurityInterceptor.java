package ru.yakovlev.rentrest.controller.common;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.yakovlev.rentrest.exception.AccessRuntimeException;
import ru.yakovlev.rentrest.exception.SecurityRuntimeException;
import ru.yakovlev.rentrest.model.context.UserContext;
import ru.yakovlev.rentrest.model.enums.UserRoleEnum;
import ru.yakovlev.rentrest.security.SecurityContext;
import ru.yakovlev.rentrest.service.security.SecurityService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class RestControllerSecurityInterceptor implements HandlerInterceptor {
    private final SecurityService securityService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(request.getMethod().equals("OPTIONS")){
            return true;
        }

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(authorizationHeader == null
                || authorizationHeader.isEmpty()
                || authorizationHeader.isBlank()
                || authorizationHeader.split(" ").length != 2
        ){
            throw new SecurityRuntimeException("Невалидный токен", HttpStatus.UNAUTHORIZED);
        }

        String jwt = authorizationHeader.split(" ")[1];

        Claims claims = securityService.parseJwt(jwt);
        UserRoleEnum role = UserRoleEnum.valueOf(claims.get("role").toString());

        if(!securityService.hasAccess(role, request.getMethod(), request.getRequestURI())){
            throw new AccessRuntimeException("Нет прав доступа", HttpStatus.FORBIDDEN);
        }

        SecurityContext.set(new UserContext(
                Long.valueOf(claims.get("id").toString()),
                UserRoleEnum.valueOf(claims.get("role").toString())
        ));

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SecurityContext.clear();
    }
}
