package ru.azenizzka.telegram.handlers;

import java.util.List;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.azenizzka.entities.Person;

public interface Handler {
  List<SendMessage> handle(Update update, Person person);
}
