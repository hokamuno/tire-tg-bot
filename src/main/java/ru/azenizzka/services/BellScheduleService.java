package ru.azenizzka.services;

import ru.azenizzka.utils.BellType;

public class BellScheduleService {
  private static byte[][][] bellRecess;

  private static final byte[][][] bellMain = {
    {{8, 30}, {9, 15}, {9, 20}, {10, 5}},
    {{10, 20}, {11, 5}, {11, 10}, {11, 55}},
    {{12, 25}, {13, 10}, {13, 15}, {14, 0}},
    {{14, 30}, {15, 15}, {15, 20}, {16, 5}},
    {{16, 15}, {17, 0}, {17, 5}, {17, 50}},
    {{18, 0}, {18, 45}, {18, 50}, {19, 35}}
  };

  private static final byte[][][] bellSaturday = {
    {{8, 30}, {9, 15}, {9, 20}, {10, 5}},
    {{10, 20}, {11, 5}, {11, 10}, {11, 55}},
    {{12, 5}, {12, 50}, {12, 55}, {13, 40}},
    {{13, 50}, {14, 35}, {14, 40}, {15, 25}},
    {{15, 35}, {16, 20}, {16, 25}, {17, 10}},
    {{17, 20}, {18, 5}, {18, 10}, {18, 55}}
  };

  private static final byte[][][] bellMonday = {
    {{9, 20}, {10, 5}, {10, 10}, {10, 55}},
    {{11, 15}, {12, 0}, {12, 5}, {12, 50}},
    {{13, 20}, {14, 5}, {14, 10}, {14, 55}},
    {{15, 15}, {16, 0}, {16, 5}, {16, 50}},
    {{17, 0}, {17, 45}, {17, 50}, {18, 35}},
    {{18, 45}, {19, 30}, {19, 35}, {20, 20}}
  };

  private static final String timeSeparator = ":";
  private static final String space = " ";
  private static final String partsSeparator = "    ";
  private static final String endOfLine = "\n";

  public static String getStringWithSchedule(BellType bellType) {
    StringBuilder out = new StringBuilder();

    switch (bellType) {
      case MAIN -> {
        out.append("*Основное* расписание звонков\n\n");
        bellRecess = bellMain;
      }
      case MONDAY -> {
        out.append("Расписание звонков на *Понедельник*\n\n");
        bellRecess = bellMonday;
      }
      case SATURDAY -> {
        out.append("Расписание звонков на *Субботу*\n\n");
        bellRecess = bellSaturday;
      }
    }

    for (int lesson = 0; lesson < 6; lesson++) {
      out.append("*").append(lesson + 1).append(" пара:*\n");
      for (int time = 0; time < 4; time++) {
        String from = String.valueOf(bellRecess[lesson][time][0]);
        String to = String.valueOf(bellRecess[lesson][time][1]);

        if (from.length() == 1) {
          from = "0" + from;
        } else if (to.length() == 1) {
          to = "0" + to;
        }

        out.append(from).append(timeSeparator).append(to);

        if (time == 0 || time == 2) {
          out.append(space);
        } else if (time == 1) {
          out.append(partsSeparator);
        }
      }
      out.append(endOfLine);
    }

    return out.toString();
  }
}
