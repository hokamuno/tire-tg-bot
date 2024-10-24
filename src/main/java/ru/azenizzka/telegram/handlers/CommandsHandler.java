package ru.azenizzka.telegram.handlers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.azenizzka.entities.Person;
import ru.azenizzka.telegram.commands.*;
import ru.azenizzka.telegram.messages.ErrorMessage;
import ru.azenizzka.utils.MessagesConfig;

@Component
public class CommandsHandler implements Handler {
  private final List<Command> commands;

  public CommandsHandler(
      BellCommand bellCommand,
      ReturnCommand returnCommand,
      InfoCommand infoCommand,
      SettingsCommand settingsCommand,
      ScheduleCommand scheduleCommand,
      BroadcastCommand broadcastCommand,
      DelAdminCommand delAdminCommand,
      SetAdminCommand setAdminCommand) {
    this.commands = new ArrayList<>();

    commands.add(returnCommand);
    commands.add(bellCommand);
    commands.add(settingsCommand);
    commands.add(scheduleCommand);
    commands.add(broadcastCommand);
    commands.add(delAdminCommand);
    commands.add(setAdminCommand);

    commands.add(infoCommand);
  }

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
