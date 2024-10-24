package ru.azenizzka.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.azenizzka.app.entities.Person;

public interface PersonRepository extends JpaRepository<Person, Integer> {
  boolean existsByChatId(String chatId);

  Person findByChatId(String chatId);
}
