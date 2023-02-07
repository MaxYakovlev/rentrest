package ru.yakovlev.rentrest.telegram.command;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.DefaultBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.yakovlev.rentrest.exception.AccessRuntimeException;
import ru.yakovlev.rentrest.model.entity.AppUser;
import ru.yakovlev.rentrest.model.entity.Rent;
import ru.yakovlev.rentrest.model.enums.RentStatusEnum;
import ru.yakovlev.rentrest.model.enums.TransportTypeEnum;
import ru.yakovlev.rentrest.model.enums.UserRoleEnum;
import ru.yakovlev.rentrest.service.rent.RentService;
import ru.yakovlev.rentrest.service.user.UserService;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
public class InfoCommand extends DefaultBotCommand {
    private RentService rentService;
    private UserService userService;

    private int electricBicycleBasePrice = 100;
    private int electricBicyclePricePerMinute = 4;
    private int electricScooterBasePrice = 70;
    private int electricScooterPricePerMinute = 3;

    public InfoCommand(String commandIdentifier, String description, RentService rentService, UserService userService) {
        super(commandIdentifier, description);

        this.rentService = rentService;
        this.userService = userService;
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, org.telegram.telegrambots.meta.api.objects.User user, Chat chat, Integer integer, String[] strings) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setParseMode("html");
        sendMessage.setChatId(chat.getId().toString());

        try{
            AppUser appUser = userService.findByUsername(user.getUserName());
            List<Rent> openRents = rentService.findTelegramUserRents(user.getUserName(), RentStatusEnum.OPEN);
            List<Rent> closedRents = rentService.findTelegramUserRents(user.getUserName(), RentStatusEnum.CLOSE);
            String info = getUserInfo(openRents, closedRents, appUser);

            sendMessage.setText(info);

            absSender.execute(sendMessage);
        }
        catch (AccessRuntimeException exception){
            log.error(exception.getMessage());
            sendMessage.setText("Зарегистрируйтесь /help");
            absSender.execute(sendMessage);
        }
    }

    private String getUserInfo(List<Rent> openRents, List<Rent> closedRents, AppUser appUser){
        StringBuilder stringBuilder = new StringBuilder();

        if(appUser.getRole() == UserRoleEnum.USER) {
            if(openRents.size() != 0) {
                stringBuilder.append("<b>Открытая аренда</b>\n\n");

                for(var rent: openRents){
                    stringBuilder.append(String.format("Начало аренды: %s\n", DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(rent.getStartRentDatetime())));
                    stringBuilder.append(String.format("Транспорт: %s\n", rent.getTransport().getName()));
                    stringBuilder.append(String.format("Начальная парковка: %s\n", rent.getStartParking().getName()));

                    if(rent.getTransport().getType() == TransportTypeEnum.ELECTRIC_BICYCLE){
                        stringBuilder.append(String.format("Базовая стоимость: %d.00 RUB\n", electricBicycleBasePrice));
                        stringBuilder.append(String.format("Стоимость минуты: %d.00 RUB\n\n", electricBicyclePricePerMinute));
                    }

                    if(rent.getTransport().getType() == TransportTypeEnum.ELECTRIC_SCOOTER){
                        stringBuilder.append(String.format("Базовая стоимость: %d.00 RUB\n", electricScooterBasePrice));
                        stringBuilder.append(String.format("Стоимость минуты: %d.00 RUB\n\n", electricScooterPricePerMinute));
                    }
                }
            }

            if(closedRents.size() != 0){
                stringBuilder.append("<b>Закрытые аренды</b>\n\n");

                for(var rent: closedRents){
                    stringBuilder.append(String.format("Начало: %s\n", DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(rent.getStartRentDatetime())));
                    stringBuilder.append(String.format("Конец: %s\n", DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(rent.getEndRentDatetime())));
                    stringBuilder.append(String.format("Стоимость: %d.00 RUB\n", rent.getAmount().intValue()));
                    stringBuilder.append(String.format("Транспорт: %s\n", rent.getTransport().getName()));
                    stringBuilder.append(String.format("Начальная парковка: %s\n", rent.getStartParking().getName()));
                    stringBuilder.append(String.format("Конечная парковка: %s\n", rent.getEndParking().getName()));

                    Long minutes = ChronoUnit.MINUTES.between(rent.getStartRentDatetime(), rent.getEndRentDatetime());

                    if(minutes == 0){
                        minutes = 1L;
                    }

                    stringBuilder.append(String.format("Длительность: %d мин.\n", minutes));

                    if(rent.getTransport().getType() == TransportTypeEnum.ELECTRIC_BICYCLE){
                        stringBuilder.append(String.format("Базовая стоимость: %d.00 RUB\n", electricBicycleBasePrice));
                        stringBuilder.append(String.format("Стоимость минуты: %d.00 RUB\n\n", electricBicyclePricePerMinute));
                    }

                    if(rent.getTransport().getType() == TransportTypeEnum.ELECTRIC_SCOOTER){
                        stringBuilder.append(String.format("Базовая стоимость: %d.00 RUB\n", electricScooterBasePrice));
                        stringBuilder.append(String.format("Стоимость минуты: %d.00 RUB\n\n", electricScooterPricePerMinute));
                    }
                }
            }

            if(openRents.size() == 0 && closedRents.size() == 0){
                stringBuilder.append("Нет открытых или закрытыхаренд");
            }
        }
        else{
            stringBuilder.append("Администратор");
        }

        return stringBuilder.toString().trim();
    }
}
