package ru.yakovlev.rentrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yakovlev.rentrest.model.dto.user.UserDto;
import ru.yakovlev.rentrest.model.entity.AppUser;
import ru.yakovlev.rentrest.model.mapping.UserMapper;
import ru.yakovlev.rentrest.security.SecurityContext;
import ru.yakovlev.rentrest.service.user.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/current")
    public UserDto findCurrentUser(){
        AppUser appUser = userService.findById(SecurityContext.get().getId());

        return userMapper.modelToDto(appUser);
    }
}
