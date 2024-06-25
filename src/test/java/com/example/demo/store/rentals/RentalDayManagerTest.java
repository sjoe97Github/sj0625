package com.example.demo.store.rentals;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RentalManagerTest {
    @Test
    public void validateCorrectJulyFourthDate() {
        //  Create a few constants representing the actual date of July 4th in three different years;
        //  one where July 4th falls on the exact date of July 4th, and
        //  one where the actual day of July 4th falls on a Saturday, and
        //  one where the actual day of July 4th falls on a Sunday.

        //  Create the three July 4th data objects.
        //  Note - these actual dates can be confirmed, simply, by referring to a calendar.
        Date actualDayIsSaturday = new Date(2020, 7, 4);
        Date actualDayIsSunday = new Date(2021, 7, 4);
        Date actualDayIsMonday = new Date(2022, 7, 4);



        RentalManager rentalManager = new RentalManager();
        assertTrue(rentalManager.isJulyFourth("07/04/2020"));
    }
}