package ru.azenizzka.telegram.commands;

import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.azenizzka.entities.Person;
import ru.azenizzka.services.CacheService;
import ru.azenizzka.telegram.messages.CustomMessage;
import ru.azenizzka.utils.MessagesConfig;

@Component
@RequiredArgsConstructor
public class WarmCacheCommand implements Command {
  private final CacheService cacheService;

  @Override
  public String getCommand() {
    return MessagesConfig.WARM_CACHE_COMMAND;
  }

  @Override
  public boolean isRequiredAdminRights() {
    return true;
  }

  @Override
  public List<SendMessage> handle(Update update, Person person) {
    List<SendMessage> result = new LinkedList<>();
    String cacheWarmResult = cacheService.warmCache();

    String[] messagesText = cacheWarmResult.split("\r\n\n");

    for (String text : messagesText) {
      result.add(new CustomMessage(person.getChatId(), text));
    }

    return result;
  }
}
