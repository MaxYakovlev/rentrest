package ru.yakovlev.rentrest.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yakovlev.rentrest.model.enums.UserRoleEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SecurityServiceImpl implements SecurityService{
    @Value("${security.jwt.secret}")
    private String secret;

    private final List<String> userAllowedAccess = List.of(
            "PATCH /rents/\\d+/close/{0,1}",
            "POST /rents/{0,1}",
            "GET /rents/current-user/{0,1}",
            "GET /users/current/{0,1}",
            "GET /parkings/{0,1}",
            "GET /parkings/\\d+/{0,1}",
            "GET /transports/{0,1}",
            "GET /transports/\\d+/{0,1}"
    );

    private final List<String> adminAllowedAccess = List.of(
            "GET /parkings/{0,1}",
            "GET /parkings/\\d+/{0,1}",
            "POST /parkings/{0,1}",
            "DELETE /parkings/\\d+/{0,1}",
            "PATCH /parkings/\\d+/{0,1}",
            "PATCH /parkings/update-coords-area/{0,1}",
            "GET /rents/{0,1}",
            "PATCH /rents/close/\\d+/{0,1}",
            "GET /transports/{0,1}",
            "DELETE /transports/\\d+/{0,1}",
            "PATCH /transports/\\d+/{0,1}",
            "PATCH /transports/update-coords/{0,1}",
            "POST /transports/{0,1}",
            "GET /users/current/{0,1}",
            "GET /transports/\\d+/{0,1}"
    );

    @Override
    public String createJwt(Long userId, String username, UserRoleEnum userRole) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("id", userId);
        claims.put("email", username);
        claims.put("role", userRole);

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    @Override
    public Claims parseJwt(String jwt) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(jwt)
                .getBody();
    }

    @Override
    public boolean hasAccess(UserRoleEnum role, String httpMethod, String requestPath){
        String requestPathWithMethod = httpMethod + " " + requestPath;

        if(role == UserRoleEnum.ADMIN){
            for(String access: adminAllowedAccess){
                Pattern accessPattern = Pattern.compile(access);
                Matcher accessMatcher = accessPattern.matcher(requestPathWithMethod);

                if(accessMatcher.matches()){
                    return true;
                }
            }
        }
        else if(role == UserRoleEnum.USER){
            for(String access: userAllowedAccess){
                Pattern accessPattern = Pattern.compile(access);
                Matcher accessMatcher = accessPattern.matcher(requestPathWithMethod);

                if(accessMatcher.matches()){
                    return true;
                }
            }
        }

        return false;
    }
}
