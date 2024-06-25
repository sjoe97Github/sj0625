package com.example.demo.store.rentals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test various date operations, such as:
 * - Determine if a date is a holiday
 * - Determination of correct 4th of July date
 *      Test various where the actual July 4th falls on a weekday, Saturday, and Sunday; therefore the
 *      holiday would have been (or would be) celebrated on a different day.
 * - Determination of correct Labor Day date
 *      Also vary the years being tested to ensure that the correct Labor Day date is determined.
 * - Determine if a date is a weekend or a weekday
 */
public class RentalDayManagerTest {
    static RentalDateManager defaultRentalDateManager = RentalDateManager.getInstance();

    static Calendar actualDayIsSaturday = Calendar.getInstance();
    static Calendar actualDayIsSunday = Calendar.getInstance();
    static Calendar actualDayIsMonday = Calendar.getInstance();

    @BeforeEach
    public void setUp() {
        //  Create three July 4th data objects.
        //      one where July 4th falls on the exact date of July 4th, and
        //      one where the actual day of July 4th falls on a Saturday, and
        //      one where the actual day of July 4th falls on a Sunday.
        //  Note - these actual dates can be confirmed, simply, by referring to a calendar.
        actualDayIsSaturday.set(2020, Calendar.JULY, 4);
        actualDayIsSunday.set(2021, Calendar.JULY, 4);
        actualDayIsMonday.set(2022, Calendar.JULY, 4);
    }

    @Test
    // Test various dates to ensure that the correct July 4th determination
    public void validateCorrectJulyFourthDate() {
        // Test the actual july 4th dates for the years 2020 and 2021 because;
        // the celebration of july 4th in these years is actually on Friday and Monday respectively.

        // The celebrated date for July 4th, 2020 should be Friday, the 2nd of July
        assertFalse(defaultRentalDateManager.isHoliday(actualDayIsSaturday.getTime()));
        Calendar testDate20200702 = Calendar.getInstance();
        testDate20200702.set(2020, Calendar.JULY, 3);
        assertTrue(defaultRentalDateManager.isHoliday(testDate20200702.getTime()));

        // The celebrated date for July 4th, 2021 should be Monday, the 5th of July
        assertFalse(defaultRentalDateManager.isHoliday(actualDayIsSunday.getTime()));
        Calendar testDate20210705 = Calendar.getInstance();
        testDate20210705.set(2021, Calendar.JULY, 5);
        assertTrue(defaultRentalDateManager.isHoliday(testDate20210705.getTime()));

        // Now test a date where July 4th falls on a weekday; so the holiday is celebrated on that day.
        assertTrue(defaultRentalDateManager.isHoliday(actualDayIsMonday.getTime()));
    }

    @Test
    public void validateCorrectLaborDayDate() {
        // the celebration of labor day in 2022 is actually on Monday, the 5th of September.
        Calendar actualLaborDay = Calendar.getInstance();
        actualLaborDay.set(2022, Calendar.SEPTEMBER, 5);
        assertTrue(defaultRentalDateManager.isHoliday(actualLaborDay.getTime()));

        Calendar testDate20240902 = Calendar.getInstance();
        testDate20240902.set(2024, Calendar.SEPTEMBER, 2);
        assertTrue(defaultRentalDateManager.isHoliday(testDate20240902.getTime()));

        Calendar testDate20240909 = Calendar.getInstance();
        testDate20240909.set(2024, Calendar.SEPTEMBER, 9);
        assertFalse(defaultRentalDateManager.isHoliday(testDate20240909.getTime()));

        // Test with a year when 9/1 actually falls on a Monday
        Calendar testDate20140901 = Calendar.getInstance();
        testDate20140901.set(2014, Calendar.SEPTEMBER, 1);
        assertTrue(defaultRentalDateManager.isHoliday(testDate20140901.getTime()));
    }

    @Test
    public void validateWeekend() {
        // This is an example of grouping assertions that test similar conditions.
        assertAll(
                () -> assertTrue(defaultRentalDateManager.isWeekend(actualDayIsSaturday.getTime())),
                () -> assertTrue(defaultRentalDateManager.isWeekend(actualDayIsSunday.getTime()))
        );

        // Test a weekday
        assertFalse(defaultRentalDateManager.isWeekend(actualDayIsMonday.getTime()));
    }
}