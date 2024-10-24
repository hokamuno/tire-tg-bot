package ru.azenizzka.app.services;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DateService {
  private static final DateTimeZone zone = DateTimeZone.forID("Asia/Novosibirsk");
  private static DateTime time = new DateTime(zone);

  public static int getRawDay() {
    updateTime();
    return time.getDayOfWeek();
  }

  private static void updateTime() {
    time = new DateTime(zone);
  }
}
