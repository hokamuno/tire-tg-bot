package ru.azenizzka.telegram.commands;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.azenizzka.entities.Person;
import ru.azenizzka.services.PersonService;
import ru.azenizzka.telegram.messages.ErrorMessage;
import ru.azenizzka.telegram.messages.NotifyMessage;
import ru.azenizzka.utils.MessagesConfig;

@Component
public class DelAdminCommand implements Command {
  private final PersonService personService;

  public DelAdminCommand(PersonService personService) {
    this.personService = personService;
  }

  @Override
  public String getCommand() {
    return MessagesConfig.DEL_ADMIN_COMMAND;
  }

  @Override
  public boolean isRequiredAdminRights() {
    return true;
  }

  @Override
  public List<SendMessage> handle(Update update, Person person) {
    String adminChatId = update.getMessage().getText().replace(getCommand() + " ", "");

    List<SendMessage> list = new ArrayList<>();

    if (!personService.isExistsByChatId(adminChatId)) {
      list.add(new ErrorMessage(person.getChatId(), MessagesConfig.PERSON_NOT_FOUND_EXCEPTION));
      return list;
    }

    Person admin = personService.findByChatId(adminChatId);

    if (!admin.isAdmin()) {
      list.add(new ErrorMessage(person.getChatId(), MessagesConfig.PERSON_ALREADY_NOT_ADMIN));
      return list;
    }

    admin.setAdmin(false);

    list.add(new NotifyMessage(person.getChatId(), MessagesConfig.SUCCES_DEL_ADMIN));
    list.add(new NotifyMessage(admin.getChatId(), MessagesConfig.YOU_ARE_NOT_ADMIN));

    personService.save(admin);

    return list;
  }
}
