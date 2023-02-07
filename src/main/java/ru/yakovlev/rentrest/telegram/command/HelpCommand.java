package ru.yakovlev.rentrest.telegram.command;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.DefaultBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.yakovlev.rentrest.exception.AccessRuntimeException;
import ru.yakovlev.rentrest.model.entity.AppUser;
import ru.yakovlev.rentrest.model.enums.UserRoleEnum;
import ru.yakovlev.rentrest.service.user.UserService;

@Slf4j
public class HelpCommand extends DefaultBotCommand {
    private UserService userService;

    public HelpCommand(@NonNull String command, @NonNull String description, UserService userService) {
        super(command, description);
        this.userService = userService;
    }

    @Override
    @SneakyThrows
    public void execute(AbsSender absSender, User user, Chat chat, Integer integer, String[] strings) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("html");
        sendMessage.setChatId(chat.getId().toString());

        try{
            AppUser appUser = userService.findByUsername(user.getUserName());

            sendMessage.setText(getHelpText(appUser.getRole()));

            absSender.execute(sendMessage);
        }
        catch (AccessRuntimeException exception){
            log.error(exception.getMessage());
            sendMessage.setText("Зарегистрируйтесь /help");
            absSender.execute(sendMessage);
        }
        catch (Exception exception){
            log.error(exception.getMessage());
        }
    }

    public String getHelpText(UserRoleEnum role){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("/start - регистрация\n");
        stringBuilder.append("/info - информация о текущем пользователе\n");
        stringBuilder.append("/map - карта\n");

        if(role == UserRoleEnum.USER) {
            stringBuilder.append("/rent [название ТС] - аренда ТС\n");
            stringBuilder.append("/close - закрыть аренду ТС\n");
        }

        return stringBuilder.toString();
    }
}
