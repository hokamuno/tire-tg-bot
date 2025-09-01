package ru.azenizzka.services;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ru.azenizzka.utils.Day;

@Component
@Slf4j
public class LessonScheduleService {

  private static final TrustManager[] TRUST_ALL_CERTS =
      new TrustManager[] {
        new X509TrustManager() {
          public X509Certificate[] getAcceptedIssuers() {
            return null;
          }

          public void checkClientTrusted(X509Certificate[] certs, String authType) {}

          public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        }
      };

  @Cacheable(value = "schedule", key = "{#groupNum, #day.name()}")
  public List<List<String>> getLessons(int groupNum, Day day) throws Exception {
    String url = findGroupUrl(groupNum);
    Document document = getDocumentByUrl(url);
    Elements rows = document.select("table").first().select("tr");

    int neededRow = findNeededRow(rows);
    int groupColumn = findGroupColumn(rows, neededRow, groupNum);
    int groupCount = getGroupCount(rows, neededRow);

    List<List<String>> lessonsList = new ArrayList<>();

    int dayNum = calculateDayNum(day);

    if (dayNum == 6) return lessonsList;

    int increment = calculateIncrement(groupNum);

    int startRowIndex = (neededRow + increment) + 6 * dayNum;
    int endRowIndex = startRowIndex + 6;
    int need = groupCount * 3;

    for (int rowIndex = startRowIndex; rowIndex < endRowIndex; rowIndex++) {
      Element row = rows.get(rowIndex);
      Elements columns = row.select("td");

      int currentIncrement = increment;
      int currentStartRowIndex;
      int currentRowIndex;

      int startColumnIndex = findStartColumnIndex(columns);
      while (startColumnIndex == -1) {
        currentIncrement++;
        currentStartRowIndex = (neededRow + currentIncrement) + (6 * dayNum);
        currentRowIndex = currentStartRowIndex;

        if (currentRowIndex >= rows.size()) {
          break;
        }

        row = rows.get(currentRowIndex);
        columns = row.select("td");
        startColumnIndex = findStartColumnIndex(columns);
      }

      if (startColumnIndex == -1) {
        continue;
      }

      int index = startColumnIndex;

      int decrement = 0;
      for (int i = columns.size(); i > (need + index); i--) {
        if (i - 1 < columns.size() && columns.get(i - 1).text().isEmpty()) {
          decrement++;
        } else {
          break;
        }
      }

      if ((columns.size() - decrement - index) % 3 != 0) {
        throw new Exception("В строке недостаточно столбцов! Может быть вызвано из-за ВПР");
      }

      int targetIndex = index + 1 + (groupColumn * 3);
      if (targetIndex >= columns.size()) {
        continue;
      }

      String lesson = columns.get(targetIndex).text();
      String num = columns.get(index + (groupColumn * 3)).text();
      String cabinet = columns.get(index + 2 + (groupColumn * 3)).text();

      if (!lesson.isEmpty()) {
        List<String> tempList = new ArrayList<>();
        tempList.add(num);
        tempList.add(lesson);
        tempList.add(cabinet);
        lessonsList.add(tempList);
      }
    }
    return lessonsList;
  }

  private String findGroupUrl(int groupNum) throws Exception {
    Document mainDocument = getDocumentByUrl("https://www.ntmm.ru/student/raspisanie.php");
    Elements elements = mainDocument.select("a[href]");

    for (Element hyperLink : elements) {
      String hyperText = hyperLink.text();
      if (hyperText.contains(String.valueOf(groupNum))) {
        return "https://www.ntmm.ru"
            + hyperLink.attr("href").replace(".htm", ".files")
            + "/sheet001.htm";
      }
    }
    throw new Exception("Такой группы не существует!");
  }

  private int findNeededRow(Elements rows) {
    for (int i = 0; i < Math.min(10, rows.size()); i++) {
      Element row = rows.get(i);
      AtomicInteger counter = new AtomicInteger();

      for (Element column : row.select("td")) {
        if (!column.text().isEmpty()) counter.getAndIncrement();
      }

      if (counter.get() > 3) return i;
    }

    return 0;
  }

  private int findGroupColumn(Elements rows, int neededRow, int groupNum) throws Exception {
    Element row = rows.get(neededRow);
    Elements columns = row.select("td");
    int out = -1;

    for (Element column : columns) {
      String group = column.text();
      if (!group.isEmpty()) out++;

      if (group.endsWith("-" + groupNum)) return out;
    }
    throw new Exception("Колонка группы не найдена");
  }

  private int getGroupCount(Elements rows, int neededRow) {
    Element row = rows.get(neededRow);
    int count = 0;
    for (Element column : row.select("td")) {
      if (!column.text().isEmpty()) count++;
    }
    return count;
  }

  private int findStartColumnIndex(Elements columns) {
    for (int i = 0; i < columns.size(); i++) {
      Pattern pattern = Pattern.compile("^\\d+");
      if (pattern.matcher(columns.get(i).text()).matches()) return i;
    }
    return -1;
  }

  private int calculateDayNum(Day day) {
    return switch (day) {
      case TODAY -> DateService.getRawDay() - 1;
      case MONDAY -> 0;
      case TUESDAY -> 1;
      case WEDNESDAY -> 2;
      case THURSDAY -> 3;
      case FRIDAY -> 4;
      case SATURDAY -> 5;
    };
  }

  private int calculateIncrement(int groupNum) {
    String groupStr = String.valueOf(groupNum);

    if (Integer.parseInt(String.valueOf(groupStr.charAt(0))) >= 3) return 2;

    return 3;
  }

  public boolean isGroupExists(int groupNum) throws Exception {
    try {
      Document mainDocument = getDocumentByUrl("https://www.ntmm.ru/student/raspisanie.php");
      Elements elements = mainDocument.select("a[href]");

      for (Element hyperLink : elements) {
        String hyperText = hyperLink.text();
        if (hyperText.contains(String.valueOf(groupNum))) return true;
      }
      return false;
    } catch (IOException e) {
      throw new Exception("Сайт недоступен!");
    }
  }

  private Document getDocumentByUrl(String url) throws IOException {
    try {
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, TRUST_ALL_CERTS, new java.security.SecureRandom());
      SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

      return Jsoup.connect(url).sslSocketFactory(sslSocketFactory).timeout(10000).get();
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      throw new IOException("SSL error", e);
    }
  }
}
