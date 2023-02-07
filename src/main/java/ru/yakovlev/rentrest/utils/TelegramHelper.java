package ru.yakovlev.rentrest.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class TelegramHelper {
    public static ReplyKeyboardMarkup getGeolocationButtonKeyboardMarkup(){
        KeyboardRow geolocationKeyboardButton = new KeyboardRow();

        geolocationKeyboardButton.add(KeyboardButton
                .builder()
                .text("Геолокация")
                .requestLocation(true)
                .build());

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setKeyboard(List.of(
                geolocationKeyboardButton
        ));

        return replyKeyboardMarkup;
    }
}
