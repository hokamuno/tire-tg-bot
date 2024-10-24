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
public class ChangeGroupCommand implements Command {
  @Override
  public String getCommand() {
    return MessagesConfig.CHANGE_GROUP_COMMAND;
  }

  @Override
  public boolean isRequiredAdminRights() {
    return false;
  }

  @Override
  public List<SendMessage> handle(Update update, Person person) {
    CustomMessage message = new CustomMessage(person.getChatId(), KeyboardType.RETURN);
    message.setText(MessagesConfig.CHANGE_GROUP_MESSAGE);
    person.setInputType(InputType.GROUP);

    return List.of(message);
  }
}
