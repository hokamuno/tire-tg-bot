package ru.azenizzka.telegram.handlers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.azenizzka.entities.Person;
import ru.azenizzka.services.LessonScheduleService;
import ru.azenizzka.telegram.TelegramBot;
import ru.azenizzka.telegram.keyboards.KeyboardType;
import ru.azenizzka.telegram.messages.CustomMessage;
import ru.azenizzka.telegram.messages.ErrorMessage;
import ru.azenizzka.utils.Day;
import ru.azenizzka.utils.DayUtil;
import ru.azenizzka.utils.MessagesConfig;

@Component
public class RecessHandler implements Handler {
  private final LessonScheduleService lessonScheduleService;

  public RecessHandler(LessonScheduleService lessonScheduleService) {
    this.lessonScheduleService = lessonScheduleService;
  }

  @Override
  public List<SendMessage> handle(Update update, Person person) {
    person.setInputType(InputType.COMMAND);

    CustomMessage message = new CustomMessage(person.getChatId(), KeyboardType.MAIN);
    String textMessage = update.getMessage().getText().toLowerCase();

    try {
      Day day = DayUtil.convertStrToDay(textMessage);

      CustomMessage pleaseWaitMessage =
          new CustomMessage(
              person.getChatId(),
              KeyboardType.MAIN,
              "⏳ Пожалуйста, подождите. Получаю расписание с сайта..");
      TelegramBot.getInstance().sendMessage(List.of(pleaseWaitMessage));

      List<List<String>> lessons = lessonScheduleService.getLessons(person.getGroupNum(), day);

      StringBuilder result =
          new StringBuilder(
              "Расписание *" + person.getGroupNum() + "* группы\n*" + textMessage + "*\n\n");

      if (lessons.isEmpty()) {
        return List.of(
            new CustomMessage(
                person.getChatId(), KeyboardType.MAIN, MessagesConfig.NO_LESSONS_MESSAGE));
      }

      for (List<String> list : lessons) {
        result.append("*").append(list.get(0)).append(" пара:* ").append(list.get(1)).append("\n");
        result.append("*Кабинет:* ").append(list.get(2)).append("\n\n");
      }

      message.setText(result.toString());

      InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
      List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
      List<InlineKeyboardButton> rowInline = new ArrayList<>();

      InlineKeyboardButton button = new InlineKeyboardButton("Поддержать разработку бота");
      button.setUrl("https://www.donationalerts.com/r/azenizzka");

      rowInline.add(button);
      rowsInline.add(rowInline);

      markupInline.setKeyboard(rowsInline);
      message.setReplyMarkup(markupInline);
    } catch (Exception e) {
      return List.of(new ErrorMessage(person.getChatId(), e.getMessage()));
    }

    return List.of(message);
  }
}
