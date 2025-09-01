package ru.azenizzka.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.azenizzka.repositories.PersonRepository;
import ru.azenizzka.utils.Day;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheService {

  private final PersonRepository personRepository;
  private final AsyncCacheService asyncCacheService;

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
            asyncCacheService
                .warmGroupAsync(currentGroupNum, day)
                .handle(
                    (result, exception) -> {
                      if (exception != null) {
                        failedWarmedCounter.incrementAndGet();
                        String errorMessage = getShortErrorMessage(exception);
                        sb.append("\n**")
                            .append(currentGroupNum)
                            .append("**: ❌ ")
                            .append(errorMessage);
                        log.info(
                            "Detailed error for group {}: {}",
                            currentGroupNum,
                            exception.getMessage());
                      } else if (result) {
                        successfullyWarmedCounter.incrementAndGet();
                        sb.append("\n**").append(currentGroupNum).append("**: ✅");
                      }

                      return null;
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

    log.info("result string: {}", sb);

    return sb.toString();
  }

  private String getShortErrorMessage(Throwable exception) {
    String message = exception.getMessage();

    if (message == null) return "Null exception message";

    if (message.contains("\n")) return message.split("\n")[0];
    if (message.length() > 50) return message.substring(0, 47) + "...";

    return message;
  }
}
