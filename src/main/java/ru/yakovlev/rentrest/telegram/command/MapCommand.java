package ru.yakovlev.rentrest.telegram.command;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.DefaultBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.yakovlev.rentrest.exception.AccessRuntimeException;
import ru.yakovlev.rentrest.model.entity.AppUser;
import ru.yakovlev.rentrest.service.security.SecurityService;
import ru.yakovlev.rentrest.service.user.UserService;

@Slf4j
public class MapCommand extends DefaultBotCommand {
    private SecurityService securityService;
    private UserService userService;
    private static final String MAP_URL = "http://127.0.0.1:8000?token=";

    public MapCommand(String commandIdentifier, String description, SecurityService securityService, UserService userService) {
        super(commandIdentifier, description);
        this.securityService = securityService;
        this.userService = userService;
    }

    @Override
    @SneakyThrows
    public void execute(AbsSender absSender, User user, Chat chat, Integer integer, String[] strings) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chat.getId().toString());
        sendMessage.setParseMode("html");

        try {
            AppUser appUser = userService.findByUsername(user.getUserName());

            String jwt = securityService.createJwt(appUser.getId(), appUser.getUsername(), appUser.getRole());

            sendMessage.setText("<a href=\"" + MAP_URL + jwt + "\">Карта</a>");

            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        catch (AccessRuntimeException exception){
            sendMessage.setText("Зарегистрируйтесь /help");
            absSender.execute(sendMessage);
        }
    }
}
