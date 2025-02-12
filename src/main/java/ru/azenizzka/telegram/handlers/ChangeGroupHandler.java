package ru.azenizzka.telegram.handlers;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.azenizzka.entities.Person;
import ru.azenizzka.services.LessonScheduleService;
import ru.azenizzka.telegram.keyboards.KeyboardType;
import ru.azenizzka.telegram.messages.CustomMessage;
import ru.azenizzka.telegram.messages.ErrorMessage;
import ru.azenizzka.utils.MessagesConfig;

@Component
@RequiredArgsConstructor
public class ChangeGroupHandler implements Handler {
  private final LessonScheduleService lessonScheduleService;

  @Override
  public List<SendMessage> handle(Update update, Person person) {
    String textMessage = update.getMessage().getText().toLowerCase();

    person.setInputType(InputType.COMMAND);

    SendMessage message = new CustomMessage(person.getChatId(), KeyboardType.MAIN);

    int group;

    try {
      group = Integer.parseInt(textMessage);

      if (!lessonScheduleService.isGroupExists(group)) {
        throw new NumberFormatException();
      }

      message.setText(MessagesConfig.SUCCESS_CHANGE_GROUP_MESSAGE);

      person.setGroupNum(group);
    } catch (Exception e) {
      message = new ErrorMessage(person.getChatId(), MessagesConfig.GROUP_NOT_FOUND_EXCEPTION);
    }

    return List.of(message);
  }
}
