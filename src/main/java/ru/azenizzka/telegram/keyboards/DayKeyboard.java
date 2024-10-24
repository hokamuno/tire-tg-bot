package ru.azenizzka.app.telegram.keyboards;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.azenizzka.app.utils.MessagesConfig;

public class DayKeyboard {
  public static void addKeyboard(SendMessage message) {
    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
    List<KeyboardRow> keyboard = new ArrayList<>();
    KeyboardRow row = new KeyboardRow();

    row.add("Понедельник");
    row.add("Вторник");
    row.add("Среда");

    keyboard.add(row);
    row = new KeyboardRow();

    row.add("Четверг");
    row.add("Пятница");
    row.add("Суббота");

    keyboard.add(row);
    row = new KeyboardRow();

    row.add("Сегодня");

    keyboard.add(row);
    row = new KeyboardRow();

    row.add(MessagesConfig.RETURN_COMMAND);

    keyboard.add(row);

    keyboardMarkup.setResizeKeyboard(true);
    keyboardMarkup.setKeyboard(keyboard);
    message.setReplyMarkup(keyboardMarkup);
  }
}
