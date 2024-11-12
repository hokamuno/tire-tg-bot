package ru.azenizzka.telegram.commands;

import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.azenizzka.entities.Person;
import ru.azenizzka.telegram.keyboards.KeyboardType;
import ru.azenizzka.telegram.messages.CustomMessage;
import ru.azenizzka.utils.MessagesConfig;

@Component
public class StartCommand implements Command {
  @Override
  public String getCommand() {
    return MessagesConfig.START_COMMAND;
  }

  @Override
  public boolean isRequiredAdminRights() {
    return false;
  }

  @Override
  public List<SendMessage> handle(Update update, Person person) {
    SendMessage message =
        new CustomMessage(person.getChatId(), KeyboardType.MAIN, MessagesConfig.START_MESSAGE);
    return List.of(message);
  }
}
