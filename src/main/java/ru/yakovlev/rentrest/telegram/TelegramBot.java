package ru.yakovlev.rentrest.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.yakovlev.rentrest.exception.BusinessRuntimeException;
import ru.yakovlev.rentrest.model.entity.AppUser;
import ru.yakovlev.rentrest.model.entity.Rent;
import ru.yakovlev.rentrest.model.entity.Transport;
import ru.yakovlev.rentrest.model.enums.TelegramAction;
import ru.yakovlev.rentrest.model.enums.TransportRentStatusEnum;
import ru.yakovlev.rentrest.model.enums.TransportTypeEnum;
import ru.yakovlev.rentrest.model.mapping.RentMapper;
import ru.yakovlev.rentrest.service.parking.ParkingService;
import ru.yakovlev.rentrest.service.rent.RentService;
import ru.yakovlev.rentrest.service.security.SecurityService;
import ru.yakovlev.rentrest.service.transport.TransportService;
import ru.yakovlev.rentrest.service.user.UserService;
import ru.yakovlev.rentrest.telegram.command.*;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingCommandBot {
    @Value("${telegram.bot.name}")
    private String botName;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.payment.token}")
    private String paymentToken;

    private int electricBicycleBasePrice = 100;
    private int electricBicyclePricePerMinute = 4;
    private int electricScooterBasePrice = 70;
    private int electricScooterPricePerMinute = 3;

    private UserService userService;
    private RentService rentService;
    private TransportService transportService;
    private ParkingService parkingService;
    private SecurityService securityService;

    public TelegramBot(UserService userService,
                       RentService rentService,
                       RentMapper rentMapper,
                       TransportService transportService,
                       ParkingService parkingService,
                       SecurityService securityService
    ) {
        this.userService = userService;
        this.rentService = rentService;
        this.transportService = transportService;
        this.parkingService = parkingService;
        this.securityService = securityService;

        this.register(new HelpCommand("help", "Выводит информация о доступных командах", userService));
        this.register(new StartCommand("start", "Регистрация пользователя", userService));
        this.register(new InfoCommand("info", "Информация о текущем пользователе", rentService, userService));
        this.register(new RentCommand("rent", "Аренда транспорта", userService, transportService, rentService));
        this.register(new CloseCommand("close", "Закрытие аренды ТС", userService, rentService, transportService));
        this.register(new MapCommand("map", "Карта", securityService, userService));
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if(update.getMessage() != null && update.getMessage().getSuccessfulPayment() != null){
            AppUser appUser = userService.findByUsername(update.getMessage().getChat().getUserName());
            userService.updateTelegramParameters(appUser.getUsername(), null, null, null);
            Transport transport = transportService.findByNameAndStatus(appUser.getTelegramTransportName(), TransportRentStatusEnum.FREE).get();
            Rent rent = rentService.createRent(appUser, transport.getId());

            SendMessage sendMessage = new SendMessage();
            sendMessage.setParseMode("html");
            sendMessage.setChatId(update.getMessage().getChatId().toString());

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(String.format(
                    "Вы арендовали: %s\nНачало аренды: %s\nНачальная парковка: %s\n",
                    rent.getTransport().getName(),
                    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(rent.getStartRentDatetime()),
                    rent.getTransport().getParking().getName()
            ));

            if(rent.getTransport().getType() == TransportTypeEnum.ELECTRIC_BICYCLE){
                stringBuilder.append(String.format("Базовая стоимость: %d.00 RUB\n", electricBicycleBasePrice));
                stringBuilder.append(String.format("Стоимость минуты: %d.00 RUB\n\n", electricBicyclePricePerMinute));
            }

            if(rent.getTransport().getType() == TransportTypeEnum.ELECTRIC_SCOOTER){
                stringBuilder.append(String.format("Базовая стоимость: %d.00 RUB\n", electricScooterBasePrice));
                stringBuilder.append(String.format("Стоимость минуты: %d.00 RUB\n\n", electricScooterPricePerMinute));
            }

            sendMessage.setText(stringBuilder.toString());

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            return;
        }

        if(update.getPreCheckoutQuery() != null){
            AnswerPreCheckoutQuery answerPreCheckoutQuery = AnswerPreCheckoutQuery.builder()
                    .preCheckoutQueryId(update.getPreCheckoutQuery().getId())
                    .ok(true)
                    .build();

            try {
                execute(answerPreCheckoutQuery);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            return;
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("html");
        sendMessage.setChatId(update.getMessage().getChatId().toString());

        try{
            if(update.getMessage().getLocation() != null){
                String telegramUsername = update.getMessage().getChat().getUserName();
                AppUser appUser = userService.findByUsername(telegramUsername);

                if(appUser.getTelegramAction() == TelegramAction.CLOSE){
                    Double lat = update.getMessage().getLocation().getLatitude();
                    Double lon = update.getMessage().getLocation().getLongitude();

                    var parking = parkingService.findParkingByCoordinates(lat, lon);

                    if(parking.isPresent()) {
                        Rent rent = rentService.findOpenByUserAndTransportName(appUser, appUser.getTelegramTransportName())
                                .orElseThrow(() -> new BusinessRuntimeException(
                                        String.format("У вас нет открытой аренды транспорта %s", appUser.getTelegramTransportName()), HttpStatus.BAD_REQUEST));

                        Rent closedRent = rentService.closeRent(appUser, rent.getId(), lat, lon);

                        userService.updateTelegramParameters(telegramUsername, null, null, null);

                        if (closedRent != null) {
                            int sum = closedRent.getAmount().intValue();

                            if(closedRent.getTransport().getType() == TransportTypeEnum.ELECTRIC_BICYCLE){
                                sum -= electricBicycleBasePrice;
                            }
                            else{
                                sum -= electricScooterBasePrice;
                            }

                            Long minutes = ChronoUnit.MINUTES.between(closedRent.getStartRentDatetime(), closedRent.getEndRentDatetime());
                            if(minutes == 0){
                                minutes = 1L;
                            }

                            sendMessage.setText("Аренда закрыта, списано " + sum + ".00 RUB за " + minutes + " мин.");

                            execute(sendMessage);

                            return;
                        } else {
                            throw new BusinessRuntimeException("Аренда не закрыта", HttpStatus.BAD_REQUEST);
                        }
                    }
                    else{
                        throw new BusinessRuntimeException("Вы не в зоне парковки", HttpStatus.BAD_REQUEST);
                    }
                }
                else{
                    sendMessage.setText("Используйте команду /help");
                }
            }
            else{
                sendMessage.setText("Используйте команду /help");
            }

            execute(sendMessage);
        }
        catch (TelegramApiException exception){
            log.error(exception.getMessage());
        }
        catch (BusinessRuntimeException exception){
            sendMessage.setText(exception.getMessage());

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            AppUser appUser = userService.findByUsername(update.getMessage().getChat().getUserName());
            if(appUser.getTelegramAction() != null || (appUser.getTelegramTransportType() != null && appUser.getTelegramTransportName() != null)){
                userService.updateTelegramParameters(update.getMessage().getChat().getUserName(), null, null, null);
            }
        }
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
