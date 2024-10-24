package ru.azenizzka.telegram.handlers;

import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.azenizzka.entities.Person;
import ru.azenizzka.exceptions.BellTypeConvertException;
import ru.azenizzka.services.BellScheduleService;
import ru.azenizzka.telegram.keyboards.KeyboardType;
import ru.azenizzka.telegram.messages.CustomMessage;
import ru.azenizzka.telegram.messages.ErrorMessage;
import ru.azenizzka.utils.BellUtil;

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
