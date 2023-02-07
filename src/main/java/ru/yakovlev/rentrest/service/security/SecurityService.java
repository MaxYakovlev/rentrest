package ru.yakovlev.rentrest.service.security;

import io.jsonwebtoken.Claims;
import ru.yakovlev.rentrest.model.enums.UserRoleEnum;

public interface SecurityService {
    Claims parseJwt(String jwt);
    boolean hasAccess(UserRoleEnum role, String httpMethod, String requestPath);
    String createJwt(Long userId, String username, UserRoleEnum userRole);
}
