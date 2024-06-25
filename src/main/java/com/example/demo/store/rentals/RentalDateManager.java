package com.example.demo.store.rentals;

import com.example.demo.store.rentals.exceptions.RentalRequestException;

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
 * Singleton convenience class for managing rental dates.
 * - See comment on the getInstance() method for more information on the singleton pattern used here.
 *
 * - This class provides a single point of access for:
 *     - determining whether a given date is a holiday
 *     - determining whether a given date is a weekend
 *     - converting date strings to Date objects
 *     - converting Date objects to date strings
 *     - validating date strings
 *
 * - Mutators (setters) support a fluent approach to overriding default configuration.
 *
 *   Caveats - This class hardcodes:
 *       - The default date formats for input and output, in accordance with the demo specification.
 *       - The default holidays, also in accordance with the demo specification.
 *       - *** This class could be entirely static, but is implemented as a singleton partly to allow for future
 *             design flexibility (e.g. future support for overriding default holidays, date formats, etc..).
 *
 *   TODO -
 *      - Consider refactoring, providing setters and getters to allow overriding default holidays and date formats.
 *      - Revisit the use of Calendar, LocalDate, Date, and related classes, to ensure utmost consistency and efficiency.
 *
 */
public class RentalDateManager {
    public enum RentalHolidays {
        July4th,
        LaborDay
    }

    private static final String DEFAULT_INPUT_DATE_FORMAT = "MM/dd/yy";
    private static final String DEFAULT_OUTPUT_DATE_FORMAT = "MM/dd/yy";

    private final DateFormat inputDateFormat = new SimpleDateFormat(DEFAULT_INPUT_DATE_FORMAT);
    private final DateFormat outputDateFormat = new SimpleDateFormat(DEFAULT_OUTPUT_DATE_FORMAT);

    private final List<RentalHolidays> holidays = List.of(RentalHolidays.July4th, RentalHolidays.LaborDay);

    private RentalDateManager() {}

    //
    //  Despite the fact that this simple demo application does not require support for multiple threads, a thread-safe
    //  singleton pattern is used here to demonstrate a best practice for creating a singleton.
    //
    //  The pattern uses volatile to leverage built-in synchronization of the Java Memory Model.  This effectively
    //  ensures that all threads synchronize their access to the INSTANCE variable for the null check; and then
    //  synchronize their access to modify the INSTANCE variable.
    //
    //  It would be simpler to remove the volatile keyword on INSTANCE and simply synchronize the getInstance() method
    //  itself but that approach introduces unnecessary extra synchronization overhead for the majority of cases where
    //  the INSTANCE is already initialized.
    //
    private static volatile RentalDateManager INSTANCE;
    public static RentalDateManager getInstance() {
        if (INSTANCE == null) {
            synchronized (RentalDateManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RentalDateManager();
                }
            }
        }
        return INSTANCE;
    }

    Date stringToDate(String inputDate) throws RentalRequestException {
        try {
            return inputDateFormat.parse(inputDate);
        } catch (Exception e) {
            //  Ignore the exception and return null, as that is the expected result/contract for this method
            throw new RentalRequestException("Invalid date format: " + inputDate, e);
        }
    }

    public String dateToString(Date date) {
        return outputDateFormat.format(date);
    }

    public boolean isValidInputDateFormat(String inputDate) {
        // compare the date format of the inputDate string with inputDateFormat
        try {
            // parse the input date thereby validating that it is in the correct format
            inputDateFormat.parse(inputDate);
            return true;
        } catch (Exception e) {
            //  Ignore the exception and return false, as that is the expected result/contract for this method
            return false;
        }
    }

    public boolean isWeekDay(Date thisDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(thisDay);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        return !(dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);
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

    private static Date actualDateForLaborDay(int year) {
        LocalDate firstOfSeptember = LocalDate.of(year, Month.SEPTEMBER, 1);
        LocalDate firstMondayInSeptember = firstOfSeptember.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));

        // Convert LocalDate to Calendar and then to a Date
        Calendar calendar = Calendar.getInstance();
        calendar.set(firstMondayInSeptember.getYear(), firstMondayInSeptember.getMonthValue() - 1, firstMondayInSeptember.getDayOfMonth());
        return calendar.getTime();
    }

    //  This method is used to compare two dates, ignoring the time portion.
    static boolean matchOnlyOnDay(Date date1, Date date2) {
        // TODO - Unnecessary Overhead?  Consider refactoring using Calendar instances, compare the day, month, and year
        //          of each instance with each other.
        LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        //  Note - converting to a LocalDate instance allows comparing day, month, and year, without the time portion
        return localDate1.equals(localDate2);
    }

}
