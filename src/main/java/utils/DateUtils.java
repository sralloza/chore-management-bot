package utils;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class DateUtils {
  private final DateProvider dateProvider;

  @Inject
  public DateUtils(DateProvider dateProvider) {
    this.dateProvider = dateProvider;
  }

  public LocalDate getTwoDaysAgoDate() {
    return dateProvider.getCurrentDate().minusDays(2);
  }

  public LocalDate getYesterdayDate() {
    return dateProvider.getCurrentDate().minusDays(1);
  }

  public LocalDate getTodayDate() {
    return dateProvider.getCurrentDate();
  }

  public LocalDate getTomorrowDate() {
    return dateProvider.getCurrentDate().plusDays(1);
  }

  public LocalDate getTwoDaysAheadDate() {
    return dateProvider.getCurrentDate().plusDays(2);
  }

  public String getWeekdayName(LocalDate localDate) {
    Locale spanishLocale = new Locale("es", "ES");
    String dayName = localDate.format(DateTimeFormatter.ofPattern("EEEE", spanishLocale));
    return dayName.substring(0, 1).toUpperCase() + dayName.substring(1);
  }

  public int getWeekNumberByDeltaDays(int deltaDays) {
    LocalDate localDate = dateProvider.getCurrentDate().plusDays(deltaDays);
    Locale locale = new Locale("es", "ES");
    return localDate.get(WeekFields.of(locale).weekOfYear());
  }
}
