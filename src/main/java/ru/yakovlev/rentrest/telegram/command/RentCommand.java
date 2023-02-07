package ru.yakovlev.rentrest.telegram.command;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.DefaultBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.yakovlev.rentrest.exception.AccessRuntimeException;
import ru.yakovlev.rentrest.exception.BusinessRuntimeException;
import ru.yakovlev.rentrest.model.entity.AppUser;
import ru.yakovlev.rentrest.model.entity.Rent;
import ru.yakovlev.rentrest.model.enums.*;
import ru.yakovlev.rentrest.service.rent.RentService;
import ru.yakovlev.rentrest.service.transport.TransportService;
import ru.yakovlev.rentrest.service.user.UserService;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

@Slf4j
public class RentCommand extends DefaultBotCommand {
    private UserService userService;
    private TransportService transportService;
    private RentService rentService;

    private String paymentToken = "381764678:TEST:35192";
    private int electricBicycleBasePrice = 100;
    private int electricScooterBasePrice = 70;

    public RentCommand(String commandIdentifier, String description, UserService userService, TransportService transportService, RentService rentService) {
        super(commandIdentifier, description);

        this.userService = userService;
        this.transportService = transportService;
        this.rentService = rentService;
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, org.telegram.telegrambots.meta.api.objects.User user, Chat chat, Integer integer, String[] strings) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chat.getId().toString());

        try {
            AppUser appUser = userService.findByUsername(user.getUserName());

            if(appUser.getRole() == UserRoleEnum.ADMIN){
                sendMessage.setText("Администратор не может открыть аренду");
                absSender.execute(sendMessage);
            }
            else if(strings.length != 1){
                sendMessage.setText("Неправильная команда /help");
                absSender.execute(sendMessage);
            }
            else{
                String transportName = strings[0].toUpperCase(Locale.ROOT);

                var transport = transportService.findByNameAndStatus(transportName, TransportRentStatusEnum.FREE);

                var rents = rentService.findTelegramUserRents(user.getUserName(), RentStatusEnum.OPEN);

                if(!rents.isEmpty()){
                    sendMessage.setText("У вас уже открыта аренда");
                    absSender.execute(sendMessage);
                }
                else if(transport.isEmpty()){
                    sendMessage.setText("Нет такого транспорта или он занят");
                    absSender.execute(sendMessage);
                }
                else {
                    appUser.setTelegramAction(TelegramAction.RENT);
                    appUser.setTelegramTransportName(transportName);
                    userService.save(appUser);

                    SendInvoice sendInvoice = SendInvoice.builder()
                            .chatId(chat.getId().toString())
                            .title(transportName)
                            .description("Оплата базовой стоимости аренды транспорта")
                            .payload("myPayload")
                            .providerToken(paymentToken)
                            .currency("RUB")
                            .startParameter("test")
                            .prices(List.of(
                                    LabeledPrice.builder()
                                            .amount(
                                                    transport.get().getType() == TransportTypeEnum.ELECTRIC_BICYCLE
                                                            ? electricBicycleBasePrice * 100
                                                            : electricScooterBasePrice  * 100
                                                    )
                                            .label("Заплатить")
                                            .build()
                            ))
                            .build();

                    absSender.execute(sendInvoice);
                }
            }
        }
        catch (AccessRuntimeException exception){
            log.error(exception.getMessage());

            if(exception.getHttpStatus() == HttpStatus.NOT_FOUND){
                sendMessage.setText("Зарегистрируйтесь /help");
            }
            else{
                sendMessage.setText(exception.getMessage());
            }

            absSender.execute(sendMessage);
        }
        catch (BusinessRuntimeException exception){
            log.error(exception.getMessage());
            sendMessage.setText(exception.getMessage());
            absSender.execute(sendMessage);
        }
    }
}
