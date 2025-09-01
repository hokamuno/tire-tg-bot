package ru.azenizzka.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.azenizzka.repositories.PersonRepository;
import ru.azenizzka.utils.Day;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheService {

  private final PersonRepository personRepository;
  private final LessonScheduleService lessonScheduleService;

  @Transactional
  public String warmCache() {
    List<Integer> groupNums = personRepository.findAllGroupNums();
    Day[] days = Day.values();

    int successfullyWarmedCounter = 0;
    int failedWarmedCounter = 0;

    StringBuilder sb = new StringBuilder();

    for (Day day : days) {
      log.info("Warm day {}", day.toString());
      sb.append("\n\nDay: ").append(day.name());

      for (int groupNum : groupNums) {
        sb.append("\n**").append(groupNum).append("**: ");

        try {
          lessonScheduleService.getLessons(groupNum, day);
          successfullyWarmedCounter++;
          log.info("Successfully warmed group {}", groupNum);
          sb.append("✅");
        } catch (Exception e) {
          failedWarmedCounter++;
          log.info("Failed warmed group {}", groupNum);
          sb.append("❌ ").append(e.getMessage());
        }
      }
    }

    sb.insert(
        0,
        "**Successfully:** " + successfullyWarmedCounter + "\n**Failed:** " + failedWarmedCounter);

    return sb.toString();
  }
}
