package ru.azenizzka.app.telegram.messages;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.azenizzka.app.telegram.keyboards.MainKeyboard;
import ru.azenizzka.app.utils.MessagesConfig;

public class NotifyMessage extends SendMessage {
  public NotifyMessage(String chatId, String notifyMessage) {
    setChatId(chatId);
    setText(String.format(MessagesConfig.NOTIFY_TEMPLATE, notifyMessage));
    enableMarkdown(true);

    MainKeyboard.addKeyboard(this);
  }
}
