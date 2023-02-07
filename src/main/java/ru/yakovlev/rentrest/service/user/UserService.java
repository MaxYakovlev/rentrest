package ru.yakovlev.rentrest.service.user;

import ru.yakovlev.rentrest.model.entity.AppUser;
import ru.yakovlev.rentrest.model.enums.TelegramAction;
import ru.yakovlev.rentrest.model.enums.TransportTypeEnum;

public interface UserService {
    AppUser findByUsername(String username);
    AppUser findById(Long id);
    AppUser save(AppUser appUser);
    boolean isPresent(AppUser appUser);
    AppUser updateTelegramParameters(String telegramUsername, TelegramAction action, TransportTypeEnum telegramTransportType, String transportName);
}
