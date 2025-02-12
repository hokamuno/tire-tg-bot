package ru.azenizzka.telegram.handlers;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.azenizzka.configuration.TelegramBotConfiguration;
import ru.azenizzka.entities.Person;
import ru.azenizzka.telegram.messages.ErrorMessage;
import ru.azenizzka.utils.MessagesConfig;

@Component
@RequiredArgsConstructor
public class MasterHandler implements Handler {

  private final CommandsHandler commandsHandler;
  private final BellTypeHandler bellTypeHandler;
  private final ChangeGroupHandler changeGroupHandler;
  private final SettingHandler settingHandler;
  private final RecessHandler recessHandler;
  private final AuditLogHandler auditLogHandler;

  private final TelegramBotConfiguration configuration;

  @Override
  public List<SendMessage> handle(Update update, Person person) {
    List<SendMessage> messages = new ArrayList<>(auditLogHandler.handle(update, person));

    if (update.getMessage().getText().equals(MessagesConfig.RETURN_COMMAND)) {
      person.setInputType(InputType.COMMAND);
    }

    if (person.getChatId().equals(configuration.getAuditLogChatId())) {
      return messages;
    }

    if (person.isBanned()) {
      SendMessage message = new ErrorMessage(person.getChatId(), "Ошибка доступа к боту..");
      messages.add(message);

      return messages;
    }

    switch (person.getInputType()) {
      case COMMAND -> messages.addAll(commandsHandler.handle(update, person));
      case BELL_TYPE -> messages.addAll(bellTypeHandler.handle(update, person));
      case GROUP -> messages.addAll(changeGroupHandler.handle(update, person));
      case SETTINGS_MAIN -> messages.addAll(settingHandler.handle(update, person));
      case DAY -> messages.addAll(recessHandler.handle(update, person));
    }

    return messages;
  }
}
