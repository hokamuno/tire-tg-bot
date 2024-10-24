package ru.azenizzka.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.azenizzka.telegram.TelegramBot;

@Getter
@Configuration
public class TelegramBotConfiguration {
  @Value("${bot.name}")
  private String name;

  @Value("${bot.token}")
  private String token;

  @Value("${bot.auditLogChatId}")
  private String auditLogChatId;

  @Bean
  public TelegramBotsApi telegramBotsApi(TelegramBot telegramBot) throws TelegramApiException {
    TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
    api.registerBot(telegramBot);

    return api;
  }
}
