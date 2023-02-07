package ru.yakovlev.rentrest.telegram.command;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.DefaultBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.yakovlev.rentrest.model.entity.AppUser;
import ru.yakovlev.rentrest.model.enums.UserRoleEnum;
import ru.yakovlev.rentrest.service.user.UserService;

@Slf4j
public class StartCommand extends DefaultBotCommand {
    private UserService userService;

    public StartCommand(String commandIdentifier, String description, UserService userService) {
        super(commandIdentifier, description);

        this.userService = userService;
    }

    @Override
    public void execute(AbsSender absSender, org.telegram.telegrambots.meta.api.objects.User user, Chat chat, Integer integer, String[] strings) {
        try {
            SendMessage sendMessage = new SendMessage();

            sendMessage.setParseMode("html");
            sendMessage.setChatId(chat.getId().toString());

            AppUser appUserWithTelegramUsername = new AppUser();
            appUserWithTelegramUsername.setUsername(user.getUserName());

            if(userService.isPresent(appUserWithTelegramUsername)){
                sendMessage.setText(String.format("Пользователь %s уже зарегистрирован", user.getUserName()));
            }
            else{
                String telegramUsername = user.getUserName();
                AppUser appUser = new AppUser();
                appUser.setRole(UserRoleEnum.USER);
                appUser.setUsername(telegramUsername);
                appUser.setChatId(chat.getId().toString());
                AppUser savedAppUser = userService.save(appUser);
                if(savedAppUser == null){
                    sendMessage.setText("Ошибка регистрации, попробуйте позже");
                }
                else{
                    sendMessage.setText(String.format("Вы зарегистрировались!"));
                }
            }

            absSender.execute(sendMessage);
        } catch (TelegramApiException exception){
            log.error(exception.getMessage());
        }
    }
}
