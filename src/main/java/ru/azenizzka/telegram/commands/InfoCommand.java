package ru.azenizzka.app.telegram.commands;

import java.util.LinkedList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.azenizzka.app.entities.Person;
import ru.azenizzka.app.services.PersonService;
import ru.azenizzka.app.telegram.keyboards.KeyboardType;
import ru.azenizzka.app.telegram.messages.CustomMessage;
import ru.azenizzka.app.utils.MessagesConfig;

@Component
public class InfoCommand implements Command {
  private final PersonService personService;

  public InfoCommand(PersonService personService) {
    this.personService = personService;
  }

  @Override
  public String getCommand() {
    return MessagesConfig.INFO_COMMAND;
  }

  @Override
  public boolean isRequiredAdminRights() {
    return true;
  }

  @Override
  public List<SendMessage> handle(Update update, Person person) {
    List<SendMessage> messages = new LinkedList<>();
    SendMessage message = new CustomMessage(person.getChatId(), KeyboardType.MAIN);
    message.enableMarkdown(false);

    List<Person> personList = personService.findAll();

    StringBuilder result = new StringBuilder();

    int counter = 0;
    boolean isSet = false;

    result.append(String.format(MessagesConfig.INFO_HEADER, personList.size()));

    for (Person user : personList) {
      counter++;

      result
          .append("\uD83D\uDC68\uD83C\uDFFF\u200D\uD83E\uDDB2 @")
          .append(user.getUsername())
          .append(" cID:")
          .append(user.getChatId())
          .append("\n");
      result.append("\uD83D\uDCDA group: ").append(user.getGroupNum()).append("\n");
      result.append("\uD83D\uDC68\uD83C\uDFFF\u200D\uD83E\uDDB3 isAdmin: ");
      result.append(user.isAdmin() ? "\uD83D\uDFE2\n\n" : "\uD83D\uDD34\n\n");

      if (counter == 10) {
        isSet = true;
        counter = 0;

        message.setText(result.toString());
        messages.add(message);

        message = new CustomMessage(person.getChatId(), KeyboardType.MAIN);
        message.enableMarkdown(false);

        result = new StringBuilder();
      }
    }

    if (personList.size() % 10 != 0) {
      message.setText(result.toString());
      messages.add(message);
    }

    if (!isSet) {
      message.setText(result.toString());
      messages.add(message);
    }

    return messages;
  }
}
