package ru.azenizzka.app.telegram.commands;

import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.azenizzka.app.entities.Person;
import ru.azenizzka.app.telegram.handlers.InputType;
import ru.azenizzka.app.telegram.keyboards.KeyboardType;
import ru.azenizzka.app.telegram.messages.CustomMessage;
import ru.azenizzka.app.utils.MessagesConfig;

@Component
public class SettingsCommand implements Command {
  @Override
  public String getCommand() {
    return MessagesConfig.SETTINGS_COMMAND;
  }

  @Override
  public boolean isRequiredAdminRights() {
    return false;
  }

  @Override
  public List<SendMessage> handle(Update update, Person person) {
    SendMessage sendMessage = new CustomMessage(person.getChatId(), KeyboardType.SETTINGS_MAIN);

    String result = "*Настройки:*\n\n" + "\uD83D\uDCDA Группа: " + person.getGroupNum();

    sendMessage.setText(result);
    person.setInputType(InputType.SETTINGS_MAIN);

    return List.of(sendMessage);
  }
}
