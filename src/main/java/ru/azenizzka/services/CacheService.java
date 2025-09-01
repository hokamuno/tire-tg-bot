package ru.azenizzka.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.azenizzka.repositories.PersonRepository;
import ru.azenizzka.utils.Day;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheService {

  private final PersonRepository personRepository;
  private final LessonScheduleService lessonScheduleService;

  @Autowired private final CacheService self;

  public String warmCache() {
    List<Integer> groupNums = personRepository.findAllGroupNums();
    Day[] days = Day.values();

    AtomicInteger successfullyWarmedCounter = new AtomicInteger(0);
    AtomicInteger failedWarmedCounter = new AtomicInteger(0);
    StringBuilder sb = new StringBuilder();

    List<CompletableFuture<Void>> allFutures = new ArrayList<>();

    for (Day day : days) {
      sb.append("\n\nDay: ").append(day.name());

      for (int groupNum : groupNums) {
        final int currentGroupNum = groupNum;

        CompletableFuture<Void> future =
            self.warmGroupAsync(currentGroupNum, day)
                .thenAccept(
                    result -> {
                      if (result) {
                        successfullyWarmedCounter.incrementAndGet();
                        sb.append("\n**").append(currentGroupNum).append("**: ✅");
                      } else {
                        failedWarmedCounter.incrementAndGet();
                        sb.append("\n**").append(currentGroupNum).append("**: ❌");
                      }
                    });

        allFutures.add(future);
      }
    }

    CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();

    sb.insert(
        0,
        "**Successfully:** "
            + successfullyWarmedCounter.get()
            + "\n**Failed:** "
            + failedWarmedCounter.get());

    return sb.toString();
  }

  @Async("cacheWarmExecutor")
  public CompletableFuture<Boolean> warmGroupAsync(int groupNum, Day day) {
    try {
      lessonScheduleService.getLessons(groupNum, day);
      return CompletableFuture.completedFuture(true);
    } catch (Exception e) {
      log.warn("Failed to warm cache for group {} day {}: {}", groupNum, day, e.getMessage());
      return CompletableFuture.completedFuture(false);
    }
  }
}
