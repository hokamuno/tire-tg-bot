package ru.azenizzka.services;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.azenizzka.utils.Day;
import ru.azenizzka.utils.DayUtil;

// TODO: полностью переписать всю эту залупу

@Component
public class LessonScheduleService {
  private String url;
  private Elements rows;

  private int groupColumn;
  private int neededRow = 0;

  public List<List<String>> getLessons(int groupNum, Day day) throws Exception {
    initUrl(groupNum);
    initNeededRow();
    initGroupColumn(groupNum);
    List<List<String>> lessonsList = new ArrayList<>();

    int dayNum;

    if (day == Day.TODAY) {
      dayNum = DateService.getRawDay();
    } else {
      dayNum = DayUtil.convertDayToInt(day);
    }

    dayNum--;

    switch (day) {
      case TODAY -> dayNum = (DateService.getRawDay() - 1);
      case MONDAY -> dayNum = 0;
      case TUESDAY -> dayNum = 1;
      case WEDNESDAY -> dayNum = 2;
      case THURSDAY -> dayNum = 3;
      case FRIDAY -> dayNum = 4;
      case SATURDAY -> dayNum = 5;
    }

    if (dayNum == 6) {
      return lessonsList;
    }

    int increment = 3;

    // TODO: и вот эту херню обязательно! пмять жалк..
    String groupStr = String.valueOf(groupNum);
    if (Integer.parseInt(String.valueOf(groupStr.charAt(0))) >= 3) {
      increment--;
    }

    int startRowIndex = (neededRow + increment) + 6 * dayNum;
    int endRowIndex = startRowIndex + 6;
    int need = getGroupCount() * 3;

    for (int rowIndex = startRowIndex; rowIndex < endRowIndex; rowIndex++) {
      Element row = rows.get(rowIndex);
      Elements columns = row.select("td");

      increment = 0;
      while (initStartColumnIndex(columns) == -1) {
        increment++;

        startRowIndex = (neededRow + increment) + (6 * dayNum);
        rowIndex = startRowIndex;

        endRowIndex = startRowIndex + 6;
        row = rows.get(rowIndex);
        columns = row.select("td");
      }

      int index = initStartColumnIndex(columns);

      int decrement = 0;

      for (int i = columns.size(); i > (need + index); i--) {
        if (columns.get(i - 1).text().isEmpty()) {
          decrement++;
        } else {
          break;
        }
      }

      if ((columns.size() - decrement - index) % 3 != 0) {
        throw new Exception("В строке недостаточно столбцов! Может быть вызвано из-за ВПР");
      }

      String lesson = columns.get(index + 1 + (groupColumn * 3)).text();
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

  private int initStartColumnIndex(Elements columns) {
    for (int i = 0; i < columns.size(); i++) {
      Pattern pattern = Pattern.compile("^\\d+");
      if (pattern.matcher(columns.get(i).text()).matches()) {
        return i;
      }
    }

    return -1;
  }

  private void initNeededRow() {
    Element row;

    for (int i = 0; i < 10; i++) { // Нахождение первой строки, где более трех заполненных колонок
      row = rows.get(i);

      int counter = 0;
      for (Element column : row.select("td")) {
        if (!column.text().isEmpty()) {
          counter++;
        }
      }

      if (counter > 3) {
        neededRow = i;
        break;
      }
    }
  }

  private int getGroupCount() {
    Element row = rows.get(neededRow);
    int count = 0;

    for (Element column : row.select("td")) {
      if (!column.text().isEmpty()) {
        count++;
      }
    }

    return count;
  }

  public boolean isGroupExists(int groupNum) throws Exception {
    Document mainDocument;

    try {
      mainDocument = getDocumentByUrl("https://www.ntmm.ru/student/raspisanie.php");
    } catch (IOException e) {
      throw new Exception("Сайт недоступен!");
    }

    Elements elements = mainDocument.select("a[href]");

    for (Element hyperLink : elements) {
      String hyperText = hyperLink.text();
      if (hyperText.contains(String.valueOf(groupNum))) {
        return true;
      }
    }

    return false;
  }

  // todo: ОСОБЕННО ЭТО!!! MID!!!!!!!
  private Document getDocumentByUrl(String url)
      throws IOException, NoSuchAlgorithmException, KeyManagementException {
    SSLContext sslContext = SSLContext.getInstance("TLS");

    sslContext.init(
        null,
        new TrustManager[] {
          new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
              return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {}

            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
          }
        },
        new java.security.SecureRandom());

    SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

    return Jsoup.connect(url).sslSocketFactory(sslSocketFactory).get();
  }

  private void initUrl(int groupNum) throws Exception {
    Document mainDocument = getDocumentByUrl("https://www.ntmm.ru/student/raspisanie.php");
    Elements elements = mainDocument.select("a[href]");

    for (Element hyperLink : elements) {
      String hyprText = hyperLink.text();

      if (hyprText.contains(String.valueOf(groupNum))) {
        url =
            "https://www.ntmm.ru"
                + hyperLink.attr("href").replace(".htm", ".files")
                + "/sheet001.htm";
        initDocument();

        return;
      }
    }

    throw new Exception("Такой группы не существует!");
  }

  private void initDocument() throws Exception {
    Document document;

    try {
      document = getDocumentByUrl(url);
    } catch (IOException e) {
      throw new Exception("Сайт недоступен");
    }

    rows = Objects.requireNonNull(document.select("table").first()).select("tr");
  }

  private void initGroupColumn(int groupNum) {
    Element row = rows.get(neededRow);
    Elements columns = row.select("td");
    int out = -1;

    for (Element column : columns) {
      String group = column.text();

      if (!group.isEmpty()) {
        out++;
      }

      if (group.endsWith(String.valueOf("-" + groupNum))) {
        groupColumn = out;
        return;
      }
    }
  }
}
