package ru.azenizzka.telegram.handlers;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.azenizzka.entities.Person;
import ru.azenizzka.telegram.commands.*;
import ru.azenizzka.telegram.messages.ErrorMessage;
import ru.azenizzka.utils.MessagesConfig;

@Component
@RequiredArgsConstructor
public class CommandsHandler implements Handler {
  private final List<Command> commands;

  @Override
  public List<SendMessage> handle(Update update, Person person) {
    String textMessage = update.getMessage().getText().toLowerCase();

    for (Command command : commands) {
      if (command.isRequiredAdminRights() && !person.isAdmin()) {
        continue;
      }

      if (textMessage.startsWith(command.getCommand())) {
        return command.handle(update, person);
      }
    }

    SendMessage errorMessage =
        new ErrorMessage(
            update.getMessage().getChatId().toString(), MessagesConfig.UNKNOWN_COMMAND_EXCEPTION);

    return List.of(errorMessage);
  }
}
