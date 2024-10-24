package ru.azenizzka.telegram.handlers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.azenizzka.configuration.TelegramBotConfiguration;
import ru.azenizzka.entities.Person;
import ru.azenizzka.telegram.messages.CustomMessage;
import ru.azenizzka.telegram.messages.NotifyMessage;

@Component
public class AuditLogHandler implements Handler {
  private final TelegramBotConfiguration configuration;

  public AuditLogHandler(TelegramBotConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public List<SendMessage> handle(Update update, Person person) {
    List<SendMessage> list = new ArrayList<>();

    SendMessage sendMessage;
    Message message = update.getMessage();

    if (person.getChatId().equals(configuration.getAuditLogChatId())) {
      if (message.getReplyToMessage() != null && message.getReplyToMessage().hasReplyMarkup()) {
        String replyToUserChatId =
            update
                .getMessage()
                .getReplyToMessage()
                .getReplyMarkup()
                .getKeyboard()
                .get(0)
                .get(0)
                .getText();

        sendMessage = new NotifyMessage(replyToUserChatId, message.getText());
        list.add(sendMessage);

        sendMessage =
            new NotifyMessage(
                configuration.getAuditLogChatId(), "Ваше сообщение было отправлено пользователю");
        list.add(sendMessage);
      }
    } else {
      sendMessage = new CustomMessage();
      sendMessage.enableMarkdown(false);
      sendMessage.setChatId(configuration.getAuditLogChatId());
      sendMessage.setText(
          String.format(
              "@%s [%d] (%s): \"%s\"",
              person.getUsername(), person.getGroupNum(), person.getUsername(), message.getText()));

      InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
      List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
      List<InlineKeyboardButton> rowInline = new ArrayList<>();

      InlineKeyboardButton button = new InlineKeyboardButton(person.getChatId());
      button.setCallbackData("reply_to_user");

      rowInline.add(button);
      rowsInline.add(rowInline);

      markupInline.setKeyboard(rowsInline);
      sendMessage.setReplyMarkup(markupInline);

      list.add(sendMessage);
    }

    return list;
  }
}
