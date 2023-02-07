package ru.yakovlev.rentrest.telegram.command;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.DefaultBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.yakovlev.rentrest.exception.AccessRuntimeException;
import ru.yakovlev.rentrest.exception.BusinessRuntimeException;
import ru.yakovlev.rentrest.model.entity.AppUser;
import ru.yakovlev.rentrest.model.entity.Rent;
import ru.yakovlev.rentrest.model.enums.RentStatusEnum;
import ru.yakovlev.rentrest.model.enums.TelegramAction;
import ru.yakovlev.rentrest.model.enums.UserRoleEnum;
import ru.yakovlev.rentrest.service.rent.RentService;
import ru.yakovlev.rentrest.service.transport.TransportService;
import ru.yakovlev.rentrest.service.user.UserService;
import ru.yakovlev.rentrest.utils.TelegramHelper;

import java.util.Locale;

@Slf4j
public class CloseCommand extends DefaultBotCommand {
    private UserService userService;
    private RentService rentService;
    private TransportService transportService;

    public CloseCommand(String commandIdentifier, String description, UserService userService, RentService rentService, TransportService transportService) {
        super(commandIdentifier, description);

        this.userService = userService;
        this.rentService = rentService;
        this.transportService = transportService;
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, org.telegram.telegrambots.meta.api.objects.User user, Chat chat, Integer integer, String[] strings) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chat.getId().toString());

        try{
            AppUser appUser = userService.findByUsername(user.getUserName());

            if(appUser.getRole() == UserRoleEnum.ADMIN){
                sendMessage.setText("Администратор не может закрыть аренду");
            }
            else{
                Rent rent = rentService.findUserRent(RentStatusEnum.OPEN, appUser);
                userService.updateTelegramParameters(user.getUserName(), TelegramAction.CLOSE, null, rent.getTransport().getName().toUpperCase(Locale.ROOT));
                sendMessage.setText("Ваша геолокация?");
                sendMessage.setReplyMarkup(TelegramHelper.getGeolocationButtonKeyboardMarkup());
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
        }
        catch (BusinessRuntimeException exception){
            log.error(exception.getMessage());
            sendMessage.setText(exception.getMessage());
        }
        finally {
            absSender.execute(sendMessage);
        }
    }
}
