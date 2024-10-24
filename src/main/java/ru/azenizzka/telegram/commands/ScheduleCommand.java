package ru.azenizzka.app.telegram.commands;

import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.azenizzka.app.entities.Person;
import ru.azenizzka.app.telegram.handlers.InputType;
import ru.azenizzka.app.telegram.keyboards.KeyboardType;
import ru.azenizzka.app.telegram.messages.CustomMessage;
import ru.azenizzka.app.telegram.messages.ErrorMessage;
import ru.azenizzka.app.utils.MessagesConfig;

@Component
public class ScheduleCommand implements Command {
  @Override
  public String getCommand() {
    return MessagesConfig.LESSON_SCHEDULE_COMMAND;
  }

  @Override
  public boolean isRequiredAdminRights() {
    return false;
  }

  @Override
  public List<SendMessage> handle(Update update, Person person) {
    if (person.getGroupNum() == 0) {
      return List.of(
          new ErrorMessage(person.getChatId(), MessagesConfig.GROUP_NOT_DEFINED_EXCEPTION));
    }

    SendMessage message = new CustomMessage(person.getChatId(), KeyboardType.DAY);

    person.setInputType(InputType.DAY);
    message.setText(MessagesConfig.DAY_MESSAGE);

    return List.of(message);
  }
}
