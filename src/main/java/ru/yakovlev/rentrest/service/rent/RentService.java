package ru.yakovlev.rentrest.service.rent;

import ru.yakovlev.rentrest.model.entity.AppUser;
import ru.yakovlev.rentrest.model.entity.Rent;
import ru.yakovlev.rentrest.model.enums.RentStatusEnum;

import java.util.List;
import java.util.Optional;

public interface RentService {
    Rent createRent(AppUser appUser, Long transportId);
    Rent findById(Long id);
    Rent closeRent(AppUser appUser, Long rentId, Double lat, Double lon);
    List<Rent> findUserRents(RentStatusEnum rentStatus);
    List<Rent> findTelegramUserRents(String telegramUsername, RentStatusEnum rentStatus);
    List<Rent> findAll();
    Optional<Rent> findOpenByUserAndTransportName(AppUser appUser, String transportName);
    Rent save(Rent rent);
    Rent findUserRent(RentStatusEnum rentStatus, AppUser appUser);
}
