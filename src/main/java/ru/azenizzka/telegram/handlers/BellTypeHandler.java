package ru.azenizzka.app.telegram.handlers;

import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.azenizzka.app.entities.Person;
import ru.azenizzka.app.exceptions.BellTypeConvertException;
import ru.azenizzka.app.services.BellScheduleService;
import ru.azenizzka.app.telegram.keyboards.KeyboardType;
import ru.azenizzka.app.telegram.messages.CustomMessage;
import ru.azenizzka.app.telegram.messages.ErrorMessage;
import ru.azenizzka.app.utils.BellUtil;

@Component
public class BellTypeHandler implements Handler {

  @Override
  public List<SendMessage> handle(Update update, Person person) {
    person.setInputType(InputType.COMMAND);

    CustomMessage message = new CustomMessage(person.getChatId(), KeyboardType.MAIN);
    String textMessage = update.getMessage().getText().toLowerCase();

    try {
      message.setText(
          BellScheduleService.getStringWithSchedule(BellUtil.convertStrToBell(textMessage)));
    } catch (BellTypeConvertException e) {
      return List.of(new ErrorMessage(person.getChatId(), e.getMessage()));
    }

    return List.of(message);
  }
}
