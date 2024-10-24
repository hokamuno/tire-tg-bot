package ru.azenizzka.app.services;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.azenizzka.app.entities.Person;
import ru.azenizzka.app.repositories.PersonRepository;

@Service
@Transactional(readOnly = true)
public class PersonService {
  private final PersonRepository personRepository;

  public PersonService(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  public boolean isExistsByChatId(String chatId) {
    return personRepository.existsByChatId(chatId);
  }

  public List<Person> findAll() {
    return personRepository.findAll();
  }

  public Person findByChatId(String chatId) {
    return personRepository.findByChatId(chatId);
  }

  @Transactional
  public void save(Person person) {
    personRepository.save(person);
  }
}
