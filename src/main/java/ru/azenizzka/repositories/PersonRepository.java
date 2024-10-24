package ru.azenizzka.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.azenizzka.entities.Person;

public interface PersonRepository extends JpaRepository<Person, Integer> {
  boolean existsByChatId(String chatId);

  Person findByChatId(String chatId);
}
