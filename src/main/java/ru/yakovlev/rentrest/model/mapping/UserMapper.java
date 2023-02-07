package ru.yakovlev.rentrest.model.mapping;

import org.mapstruct.Mapper;
import ru.yakovlev.rentrest.model.dto.user.UserDto;
import ru.yakovlev.rentrest.model.entity.AppUser;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto modelToDto(AppUser model);
}
