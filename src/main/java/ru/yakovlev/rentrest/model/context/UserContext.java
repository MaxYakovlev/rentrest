package ru.yakovlev.rentrest.model.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.yakovlev.rentrest.model.enums.UserRoleEnum;

@Getter
@Setter
@AllArgsConstructor
public class UserContext {
    private Long id;
    private UserRoleEnum userRole;
}
