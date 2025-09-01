package ru.azenizzka.services;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.azenizzka.utils.Day;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncCacheService {

  private final LessonScheduleService lessonScheduleService;

  @Async("cacheWarmExecutor")
  public CompletableFuture<Boolean> warmGroupAsync(int groupNum, Day day) {
    try {
      lessonScheduleService.getLessons(groupNum, day);
      log.debug("Successfully warmed cache for group {} day {}", groupNum, day);
      return CompletableFuture.completedFuture(true);
    } catch (Exception e) {
      log.warn("Failed to warm cache for group {} day {}: {}", groupNum, day, e.getMessage());
      CompletableFuture<Boolean> failedFuture = new CompletableFuture<>();
      failedFuture.completeExceptionally(e);
      return failedFuture;
    }
  }
}
