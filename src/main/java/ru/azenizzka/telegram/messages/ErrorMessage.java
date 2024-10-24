package ru.azenizzka.app.telegram.messages;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.azenizzka.app.telegram.keyboards.MainKeyboard;
import ru.azenizzka.app.utils.MessagesConfig;

public class ErrorMessage extends SendMessage {
  public ErrorMessage(String chatId, String errorMessage) {
    setChatId(chatId);
    setText(String.format(MessagesConfig.ERROR_TEMPLATE, errorMessage));
    enableMarkdown(true);

    MainKeyboard.addKeyboard(this);
  }
}
