package com.example.demo.store.rentals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Convenience class for:
 * - configuration:
 *     - setting the input and output date formats used by the application
 *     - setting the list of holidays used by the application
 *
 *       Note: there a defaults representing the demo specification; however, this class provides mutators for
 *       overriding these defaults with custom values.
 *
 * - specialized date operations:
 *     - date formatting
 *     - Validation of date strings, based a specific format given as a ctor argument
 *     - Parsing of date strings, based on a specific format given as a ctor argument
 *     - Formatting of date strings, based on a specific format given as a ctor argument
 *
 * - Predicate functions used to determine:
 *   - Whether a given date is a holiday
 *   - Whether a given date is a weekend
 *
 * - Mutators (setters) support a fluent approach to overriding default configuration.
 */
public class RentalDayManager {
    public enum RentalHolidays {
        July4th,
        LaborDay
    }

    public static final String DEFAULT_INPUT_DATE_FORMAT = "MM/dd/yy";
    public static final String DEFAULT_OUTPUT_DATE_FORMAT = "MM/dd/yy";

    private String inputDateFormat = DEFAULT_INPUT_DATE_FORMAT;
    private String outputDateFormat = DEFAULT_OUTPUT_DATE_FORMAT;
    private List<RentalHolidays> holidays = List.of(RentalHolidays.July4th, RentalHolidays.LaborDay);

    public RentalDayManager() {
    }

    public String getInputDateFormat() {
        return inputDateFormat;
    }

    public RentalDayManager setInputDateFormat(String inputDateFormat) {
        this.inputDateFormat = inputDateFormat;
        return this;
    }

    public String getOutputDateFormat() {
        return outputDateFormat;
    }

    public RentalDayManager setOutputDateFormat(String outputDateFormat) {
        this.outputDateFormat = outputDateFormat;
        return this;
    }

    public List<RentalHolidays> getHolidays() {
        return holidays;
    }

    public RentalDayManager setHolidays(List<RentalHolidays> holidays) {
        this.holidays = holidays;
        return this;
    }

    public boolean isValidInputDateFormat(String inputDate) {
        // compare the date format of the inputDate string with inputDateFormat
        DateFormat df = new SimpleDateFormat(inputDateFormat);
        try {
            df.parse(inputDate);
            return true;
        } catch (Exception e) {
            //  Ignore the exception and return false, as that is the expected result/contract for this method
            return false;
        }
    }

    public String toFormattedOutputString(Date date) {
        DateFormat df = new SimpleDateFormat(outputDateFormat);
        return df.format(date);
    }

    public boolean isWeekend(Date thisDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(thisDay);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }

    public boolean isHoliday(Date thisDay) {
        return holidays.stream()
                .map(holiday -> actualDateForHoliday(thisDay, holiday))
                .anyMatch(holidayDate -> matchOnlyOnDay(thisDay, holidayDate));
    }

    private static Date actualDateForHoliday(Date thisDay, RentalHolidays holiday) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(thisDay);

        return switch (holiday) {
            case July4th -> actualDateForJulyFourth(cal.get(Calendar.YEAR));
            case LaborDay -> actualDateForLaborDay(cal.get(Calendar.YEAR));
            default -> new Date(cal.getTime().getTime());   // return a copy of the input date
        };
    }

    private static Date actualDateForJulyFourth(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, Calendar.JULY, 4);

        int actualJuly4thDay = cal.get(Calendar.DAY_OF_WEEK);
        switch (actualJuly4thDay) {
            case Calendar.SATURDAY:
                // if July 4th falls on a Saturday, the holiday is celebrated on the Friday before
                cal.add(Calendar.DAY_OF_MONTH, -1);
                break;
            case Calendar.SUNDAY:
                // if July 4th falls on a Sunday, the holiday is celebrated on the Monday after
                cal.add(Calendar.DAY_OF_MONTH, 1);
                break;
            default:
                // Do nothing - if July 4th falls on a weekday, the holiday is celebrated on that day
                break;
        }

        // return a distinct copy of the calendar's date
        // TODO - Overkill! We are already creating a new instance from the specified year.
        //          Consider simply returning the date from the calendar instance.
        return new Date(cal.getTime().getTime());
    }

    public static Date actualDateForLaborDay(int year) {
        LocalDate firstOfSeptember = LocalDate.of(year, Month.SEPTEMBER, 1);
        LocalDate firstMondayInSeptember = firstOfSeptember.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));

        // Convert LocalDate to Calendar and then to a Date
        Calendar calendar = Calendar.getInstance();
        calendar.set(firstMondayInSeptember.getYear(), firstMondayInSeptember.getMonthValue() - 1, firstMondayInSeptember.getDayOfMonth());
        return calendar.getTime();
    }

    public static boolean matchOnlyOnDay(Date date1, Date date2) {
        // TODO - Unnecessary Overhead?  Consider refactoring using Calendar instances, compare the day, month, and year
        //          of each instance with each other.
        LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        //  Note - converting to a LocalDate instance allows comparing day, month, and year, without the time portion
        return localDate1.equals(localDate2);
    }

}
