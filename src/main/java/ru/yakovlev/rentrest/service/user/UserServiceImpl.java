package ru.yakovlev.rentrest.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yakovlev.rentrest.access.repository.UserRepository;
import ru.yakovlev.rentrest.exception.AccessRuntimeException;
import ru.yakovlev.rentrest.model.entity.AppUser;
import ru.yakovlev.rentrest.model.enums.TelegramAction;
import ru.yakovlev.rentrest.model.enums.TransportTypeEnum;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository appUserRepository;

    @Override
    public AppUser findByUsername(String telegramUsername) {
        return appUserRepository.findByUsername(telegramUsername).orElseThrow(() -> new AccessRuntimeException("Пользователь не найден.", HttpStatus.NOT_FOUND));
    }

    @Override
    public AppUser findById(Long id) {
        return appUserRepository.findById(id).orElseThrow(() -> new AccessRuntimeException("Пользователь не найден.", HttpStatus.NOT_FOUND));
    }

    @Override
    public AppUser save(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    @Override
    public boolean isPresent(AppUser appUser) {
        Example<AppUser> appUserExample = Example.of(appUser);

        return appUserRepository.findOne(appUserExample).isPresent();
    }

    @Override
    public AppUser updateTelegramParameters(String telegramUsername, TelegramAction action, TransportTypeEnum transportType, String transportName) {
        AppUser appUser = findByUsername(telegramUsername);

        appUser.setTelegramTransportType(transportType);
        appUser.setTelegramAction(action);
        appUser.setTelegramTransportName(transportName);

        return save(appUser);
    }
}
