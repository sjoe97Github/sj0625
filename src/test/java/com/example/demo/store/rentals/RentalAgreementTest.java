package com.example.demo.store.rentals;

import com.example.demo.entities.RentalCost;
import com.example.demo.entities.Tool;
import com.example.demo.services.ToolService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.demo.store.rentals.CheckoutService.validateRequestAndInitializeRentalAgreement;
import static org.junit.jupiter.api.Assertions.*;

class RentalAgreementTest {
    // Using mockito define the mock Took and RentalCost entities, and the ToolService
    // and RentalCostService services.
    // Define the CheckoutManager object.
    static Tool mockTool = Mockito.mock(Tool.class);
    static RentalCost mockRentalCost = Mockito.mock(RentalCost.class);
    static ToolService mockToolService = Mockito.mock(ToolService.class);

    static Tool toolCHNS;
    static Tool toolLADW;
    static Tool toolJAKD;
    static Tool toolJAKR;

    static List<Tool> reusableToolList = new ArrayList<>();
    static RentalRequest reusableSingleDayWeekDayRentalRequest;
    static RentalRequest reusableTenDayRentalRequest;

    @BeforeAll
    static void setUp() {
        //  The RentalCost and Tool instances created in this method match the
        //  default H2 database data.  (See schema.sql and data.sql files in the resources directory)
        //  The purpose for hard-coding this instances is to eliminate the need for the tests to
        //  use or mock the repositories; and allow the results to be the same as if the controllers were
        //  being used to retrieve the data from the database.
        RentalCost ladderRentalCost = new RentalCost();
        ladderRentalCost.setToolType("Ladder");
        ladderRentalCost.setDailyCharge("1.99");
        ladderRentalCost.setWeekdayCharge(true);
        ladderRentalCost.setWeekendCharge(true);
        ladderRentalCost.setHolidayCharge(false);

        RentalCost chainsawRentalCost = new RentalCost();
        chainsawRentalCost.setToolType("Chainsaw");
        chainsawRentalCost.setDailyCharge("1.49");
        chainsawRentalCost.setWeekdayCharge(true);
        chainsawRentalCost.setWeekendCharge(false);
        chainsawRentalCost.setHolidayCharge(true);

        RentalCost jackHammerRentalCost = new RentalCost();
        jackHammerRentalCost.setToolType("Jackhammer");
        jackHammerRentalCost.setDailyCharge("2.99");
        jackHammerRentalCost.setWeekdayCharge(true);
        jackHammerRentalCost.setWeekendCharge(false);
        jackHammerRentalCost.setHolidayCharge(false);

        toolCHNS = new Tool();
        toolCHNS.setTool_code("CHNS");
        toolCHNS.setTool_type("Chainsaw");
        toolCHNS.setBrand("Stihl");
        toolCHNS.setRentalCost(chainsawRentalCost);

        toolLADW = new Tool();
        toolLADW.setTool_code("LADW");
        toolLADW.setTool_type("Ladder");
        toolLADW.setBrand("Werner");
        toolLADW.setRentalCost(ladderRentalCost);

        toolJAKD = new Tool();
        toolJAKD.setTool_code("JAKD");
        toolJAKD.setTool_type("Jackhammer");
        toolJAKD.setBrand("DeWalt");
        toolJAKD.setRentalCost(jackHammerRentalCost);

        toolJAKR = new Tool();
        toolJAKR.setTool_code("JAKR");
        toolJAKR.setTool_type("Jackhammer");
        toolJAKR.setBrand("Ridgid");
        toolJAKR.setRentalCost(jackHammerRentalCost);

        reusableTenDayRentalRequest = new RentalRequest();
        reusableTenDayRentalRequest.setToolCode("JAKR")
                .setCheckoutDate("07/02/24")
                .setNumberOfRentalDays(7)
                .setDiscount("10");

        reusableSingleDayWeekDayRentalRequest = new RentalRequest();
        reusableSingleDayWeekDayRentalRequest.setToolCode("JAKR")
                .setCheckoutDate("07/02/24")
                .setNumberOfRentalDays(1)
                .setDiscount("10");

        reusableToolList.add(toolJAKR);
    }

    @Test
    void testVariousRentalDayTypesUsingVariousCheckoutDates() {
        RentalRequest rentalRequest = new RentalRequest();
        rentalRequest.setToolCode("JAKR")
                .setCheckoutDate("07/04/24")
                .setNumberOfRentalDays(1)
                .setDiscount("10");

        // when ToolService is called to find a tool by code, return null.
        Mockito.when(mockToolService.findByCode(Mockito.anyString())).thenReturn(reusableToolList);

        RentalAgreement rentalAgreement = assertDoesNotThrow(() -> validateRequestAndInitializeRentalAgreement(rentalRequest, mockToolService));
        ArrayList<Date> billableDays = rentalAgreement.buildListOfPossiblyBillableDays();
        assertEquals(rentalRequest.getNumberOfRentalDays(), billableDays.size());

        //
        //  For this test the list of possible billable days is 07/05/24, not 07/04/24 because; per specification rental days
        //  are not calculated on the checkout date.
        //  The total number of each day type in this test should be:
        //      0 holiday, 0 weekend days, and 1 weekdays
        //
        rentalAgreement.calculateNumberOfEachTypeOfRentalDay(billableDays);
        assertEquals(0, rentalAgreement.getNumberOfHolidays());
        assertEquals(0, rentalAgreement.getNumberOfWeekendDays());
        assertEquals(1, rentalAgreement.getNumberOfWeekdays());

        //  ---------------------------------------------------------------------------
        //  Reset the checkout date to the day before a 07/04/24, which is a holiday.
        //  ---------------------------------------------------------------------------
        rentalRequest.setCheckoutDate("07/03/24");
        rentalAgreement = assertDoesNotThrow(() -> validateRequestAndInitializeRentalAgreement(rentalRequest, mockToolService));
        billableDays = rentalAgreement.buildListOfPossiblyBillableDays();
        assertEquals(rentalRequest.getNumberOfRentalDays(), billableDays.size());
        //  In this case, the expected number of each type of day is:
        //      1 holiday, 0 weekend days, and 0 weekdays
        rentalAgreement.calculateNumberOfEachTypeOfRentalDay(billableDays);
        assertEquals(1, rentalAgreement.getNumberOfHolidays());
        assertEquals(0, rentalAgreement.getNumberOfWeekendDays());
        assertEquals(0, rentalAgreement.getNumberOfWeekdays());

        //  -----------------------------------------------------------------------------------------------------------
        //  Reset the checkout date to 07/02/24, which is a Tuesday; so the first
        //  billable day should be Wednesday 07/03/24, which is a weekday).
        //  -----------------------------------------------------------------------------------------------------------
        rentalRequest.setCheckoutDate("07/02/24");
        rentalAgreement = assertDoesNotThrow(() -> validateRequestAndInitializeRentalAgreement(rentalRequest, mockToolService));
        billableDays = rentalAgreement.buildListOfPossiblyBillableDays();
        assertEquals(rentalRequest.getNumberOfRentalDays(), billableDays.size());
        //  In this case, the expected number of each type of day is:
        //      0 holiday, 0 weekend days, and 1 weekdays
        rentalAgreement.calculateNumberOfEachTypeOfRentalDay(billableDays);
        assertEquals(0, rentalAgreement.getNumberOfHolidays());
        assertEquals(0, rentalAgreement.getNumberOfWeekendDays());
        assertEquals(1, rentalAgreement.getNumberOfWeekdays());

        //  -----------------------------------------------------------------------------------------------------------
        //  Reset the checkout date to 07/05/24, which is a Friday; so the first
        //  billable day should be the following day 07/06/24, which is a Saturday (a weekend day).
        //  -----------------------------------------------------------------------------------------------------------
        rentalRequest.setCheckoutDate("07/05/24");
        rentalAgreement = assertDoesNotThrow(() -> validateRequestAndInitializeRentalAgreement(rentalRequest, mockToolService));
        billableDays = rentalAgreement.buildListOfPossiblyBillableDays();
        assertEquals(rentalRequest.getNumberOfRentalDays(), billableDays.size());
        //  In this case, the expected number of each type of day is:
        //      0 holiday, 1 weekend days, and 0 weekdays
        rentalAgreement.calculateNumberOfEachTypeOfRentalDay(billableDays);
        assertEquals(0, rentalAgreement.getNumberOfHolidays());
        assertEquals(1, rentalAgreement.getNumberOfWeekendDays());
        assertEquals(0, rentalAgreement.getNumberOfWeekdays());

        //  -----------------------------------------------------------------------------------------------------------
        //  Reset the checkout date to 07/06/24, which is a Saturday; so the first
        //  billable day should be the following day 07/07/24, which is a Sunday (a weekend day).
        //  -----------------------------------------------------------------------------------------------------------
        rentalRequest.setCheckoutDate("07/06/24");
        rentalAgreement = assertDoesNotThrow(() -> validateRequestAndInitializeRentalAgreement(rentalRequest, mockToolService));
        billableDays = rentalAgreement.buildListOfPossiblyBillableDays();
        assertEquals(rentalRequest.getNumberOfRentalDays(), billableDays.size());
        //  In this case, the expected number of each type of day is:
        //      0 holiday, 1 weekend days, and 0 weekdays
        rentalAgreement.calculateNumberOfEachTypeOfRentalDay(billableDays);
        assertEquals(0, rentalAgreement.getNumberOfHolidays());
        assertEquals(1, rentalAgreement.getNumberOfWeekendDays());
        assertEquals(0, rentalAgreement.getNumberOfWeekdays());

        //  -----------------------------------------------------------------------------------------------------------
        //  Reset the checkout date to 07/07/24, which is a Sunday; so the first
        //  billable day should be the following day 07/08/24, which is a Monday (a weekday).
        //  -----------------------------------------------------------------------------------------------------------
        rentalRequest.setCheckoutDate("07/07/24");
        rentalAgreement = assertDoesNotThrow(() -> validateRequestAndInitializeRentalAgreement(rentalRequest, mockToolService));
        billableDays = rentalAgreement.buildListOfPossiblyBillableDays();
        assertEquals(rentalRequest.getNumberOfRentalDays(), billableDays.size());
        //  In this case, the expected number of each type of day is:
        //      0 holiday, 0 weekend days, and 1 weekdays
        rentalAgreement.calculateNumberOfEachTypeOfRentalDay(billableDays);
        assertEquals(0, rentalAgreement.getNumberOfHolidays());
        assertEquals(0, rentalAgreement.getNumberOfWeekendDays());
        assertEquals(1, rentalAgreement.getNumberOfWeekdays());

        //  -----------------------------------------------------------------------------------------------------------
        //  Reset the checkout date to 09/01/24, which is a Sunday; so the first
        //  billable day should be the following day is Monday 09/02/24 which is Labor Day (a holiday).
        //  -----------------------------------------------------------------------------------------------------------
        rentalRequest.setCheckoutDate("09/01/24");
        rentalAgreement = assertDoesNotThrow(() -> validateRequestAndInitializeRentalAgreement(rentalRequest, mockToolService));
        billableDays = rentalAgreement.buildListOfPossiblyBillableDays();
        assertEquals(rentalRequest.getNumberOfRentalDays(), billableDays.size());
        //  In this case, the expected number of each type of day is:
        //      1 holiday, 0 weekend days, and 0 weekdays
        rentalAgreement.calculateNumberOfEachTypeOfRentalDay(billableDays);
        assertEquals(1, rentalAgreement.getNumberOfHolidays());
        assertEquals(0, rentalAgreement.getNumberOfWeekendDays());
        assertEquals(0, rentalAgreement.getNumberOfWeekdays());
    }

    @Test
    void calculateBillableDays() {
        // when ToolService is called to find a tool by code, return null.
        Mockito.when(mockToolService.findByCode(Mockito.anyString())).thenReturn(reusableToolList);

        // First test with multiple days...
        //
        //  For this test the list of possible billable days is as follows:
        //      07/03/24, 07/04/24, 07/05/24, 07/06/24, 07/07/24, 07/08/24, 07/09/24
        //      07/04/24 is Thursday, a weekday, and this day is also the Fourth of July (a holiday)
        //      07/06/24 is a Saturday, a weekend day
        //      07/07/24 is a Sunday, also a weekend day
        //  The total number of each day type in this test should be:
        //      1 holiday, 2 weekend days, and 4 weekdays
        //
        //  Given that the reusable tool list contains the static toolJAKR tool (see the setup() method):
        //      RentalCoast for this tool is a Jackhammer with a daily charge of $2.99, and the discount is 10%; and the
        //      day types that can be charged are weekdays, but not weekends and holidays (See the RentalCost instance
        //      associated with tookJAKR in the setup() method)
        //
        //  Therefore, the expected number of billable days should be equal to the number of weekdays in the list of
        //  possible billable days.
        //
        RentalAgreement rentalAgreement = assertDoesNotThrow(() -> validateRequestAndInitializeRentalAgreement(reusableTenDayRentalRequest, mockToolService));
        ArrayList<Date> billableDays = rentalAgreement.buildListOfPossiblyBillableDays();
        assertEquals(reusableTenDayRentalRequest.getNumberOfRentalDays(), billableDays.size());

        rentalAgreement.calculateNumberOfEachTypeOfRentalDay(billableDays);
        rentalAgreement.calculateBillableDays();
        assertEquals(4, rentalAgreement.getBillableDays());

        //  -----------------------------------------------------------------------------------------------------------
        //  The reusable single day rental request has a checkout date to 07/02/24, which is a Tuesday; so the first
        //  billable day should be Wednesday 07/03/24, which is a weekday).
        //  -----------------------------------------------------------------------------------------------------------
        rentalAgreement = assertDoesNotThrow(() -> validateRequestAndInitializeRentalAgreement(reusableSingleDayWeekDayRentalRequest, mockToolService));
        billableDays = rentalAgreement.buildListOfPossiblyBillableDays();
        assertEquals(reusableSingleDayWeekDayRentalRequest.getNumberOfRentalDays(), billableDays.size());
        //  In this case, the expected number of each type of day is:
        //      0 holiday, 0 weekend days, and 1 weekdays
        rentalAgreement.calculateNumberOfEachTypeOfRentalDay(billableDays);
        rentalAgreement.calculateBillableDays();
        assertEquals(1, rentalAgreement.getBillableDays());

        //  -----------------------------------------------------------------------------------------------------------
        //  Reset the checkout date to 07/02/24, which is a Tuesday; so the first
        //  billable day should be Wednesday 07/03/24, which is a weekday).
        //  -----------------------------------------------------------------------------------------------------------
        rentalAgreement = assertDoesNotThrow(() -> validateRequestAndInitializeRentalAgreement(reusableSingleDayWeekDayRentalRequest, mockToolService));
        billableDays = rentalAgreement.buildListOfPossiblyBillableDays();
        assertEquals(reusableSingleDayWeekDayRentalRequest.getNumberOfRentalDays(), billableDays.size());
        //  In this case, the expected number of each type of day is:
        //      0 holiday, 0 weekend days, and 1 weekdays
        rentalAgreement.calculateNumberOfEachTypeOfRentalDay(billableDays);
        rentalAgreement.calculateBillableDays();
        assertEquals(1, rentalAgreement.getBillableDays());

        //  -----------------------------------------------------------------------------------------------------------
        //  Reset the checkout date to the day before a 07/04/24, which is a holiday.
        //  therefore the billable day should be 07/04/24 but holidays are not billable for the test tool being used.
        //  -----------------------------------------------------------------------------------------------------------
        RentalRequest clonedSingleDayRequest = reusableSingleDayWeekDayRentalRequest.clone();
        clonedSingleDayRequest.setCheckoutDate("07/03/24");
        rentalAgreement = assertDoesNotThrow(() -> validateRequestAndInitializeRentalAgreement(clonedSingleDayRequest, mockToolService));
        billableDays = rentalAgreement.buildListOfPossiblyBillableDays();
        assertEquals(clonedSingleDayRequest.getNumberOfRentalDays(), billableDays.size());
        //  In this case, the expected number of each type of day is:
        //      0 holiday, 0 weekend days, and 1 weekdays
        rentalAgreement.calculateNumberOfEachTypeOfRentalDay(billableDays);
        rentalAgreement.calculateBillableDays();
        assertEquals(0, rentalAgreement.getBillableDays());

        //  -----------------------------------------------------------------------------------------------------------
        //  Reset the checkout date to 07/06/24, which is a Saturday; so the first
        //  billable day should be the following day 07/07/24, which is a Sunday (a weekend day); however
        //  weekends are not billable for the test tool being used.
        //  -----------------------------------------------------------------------------------------------------------
        clonedSingleDayRequest.setCheckoutDate("07/06/24");
        rentalAgreement = assertDoesNotThrow(() -> validateRequestAndInitializeRentalAgreement(clonedSingleDayRequest, mockToolService));
        billableDays = rentalAgreement.buildListOfPossiblyBillableDays();
        assertEquals(clonedSingleDayRequest.getNumberOfRentalDays(), billableDays.size());
        //  In this case, the expected number of each type of day is:
        //      0 holiday, 0 weekend days, and 1 weekdays
        rentalAgreement.calculateNumberOfEachTypeOfRentalDay(billableDays);
        rentalAgreement.calculateBillableDays();
        assertEquals(0, rentalAgreement.getBillableDays());
    }

//    @Test
//    void testPreDiscountChargeDiscountAmountFinalCharge() {
//        // when ToolService is called to find a tool by code, return null.
//        Mockito.when(mockToolService.findByCode(Mockito.anyString())).thenReturn(reusableToolList);
//
//        // First test with multiple days...
//        //
//        //  For this test the list of possible billable days is as follows:
//        //      07/03/24, 07/04/24, 07/05/24, 07/06/24, 07/07/24, 07/08/24, 07/09/24
//        //      07/04/24 is Thursday, a weekday, and this day is also the Fourth of July (a holiday)
//        //      07/06/24 is a Saturday, a weekend day
//        //      07/07/24 is a Sunday, also a weekend day
//        //  The total number of each day type in this test should be:
//        //      1 holiday, 2 weekend days, and 4 weekdays
//        //
//        //  Given that the reusable tool list contains the static toolJAKR tool (see the setup() method):
//        //      RentalCoast for this tool is a Jackhammer with a daily charge of $2.99, and the discount is 10%; and the
//        //      day types that can be charged are weekdays, but not weekends and holidays (See the RentalCost instance
//        //      associated with tookJAKR in the setup() method)
//        //
//        //  Therefore, the expected number of billable days should be equal to the number of weekdays in the list of
//        //  possible billable days.
//        //
//        RentalAgreement = assertDoesNotThrow(() ->
//                          validateRequestAndInitializeRentalAgreement(reusableTenDayRentalRequest, mockToolService));
//        ArrayList<Date> billableDays = rentalAgreement.buildListOfPossiblyBillableDays();
//        assertEquals(reusableTenDayRentalRequest.getNumberOfRentalDays(), billableDays.size());
//
//        rentalAgreement.calculateNumberOfEachTypeOfRentalDay(billableDays);
//        rentalAgreement.calculateBillableDays();
//        assertEquals(4, rentalAgreement.getBillableDays());
//
//        //  Given the test tool being used, the per day charge is $2.99; therefore, the pre-discount charge should be
//        //  ($2.99 * 4) = $11.96 (rounded up by half)
//        rentalAgreement.calculatePreDiscountCharge();
//        BigDecimal expectedCharge = new BigDecimal("11.96").setScale(2, RoundingMode.HALF_UP);
//        BigDecimal actualCharge = rentalAgreement.getPreDiscountedCharge();
//        assertEquals(expectedCharge, actualCharge);
//    }

    @Test
    void testPreDiscountChargeDiscountAmountFinalCharge() {
        // when ToolService is called to find a tool by code, return null.
        Mockito.when(mockToolService.findByCode(Mockito.anyString())).thenReturn(reusableToolList);

        // First test with multiple days...
        //
        //  For this test the list of possible billable days is as follows:
        //      07/03/24, 07/04/24, 07/05/24, 07/06/24, 07/07/24, 07/08/24, 07/09/24
        //      07/04/24 is Thursday, a weekday, and this day is also the Fourth of July (a holiday)
        //      07/06/24 is a Saturday, a weekend day
        //      07/07/24 is a Sunday, also a weekend day
        //  The total number of each day type in this test should be:
        //      1 holiday, 2 weekend days, and 4 weekdays
        //
        //  Given that the reusable tool list contains the static toolJAKR tool (see the setup() method):
        //      RentalCoast for this tool is a Jackhammer with a daily charge of $2.99, and the discount is 10%; and the
        //      day types that can be charged are weekdays, but not weekends and holidays (See the RentalCost instance
        //      associated with tookJAKR in the setup() method)
        //
        //  Therefore, the expected number of billable days should be equal to the number of weekdays in the list of
        //  possible billable days.
        //
        RentalAgreement rentalAgreement = assertDoesNotThrow(() -> validateRequestAndInitializeRentalAgreement(reusableTenDayRentalRequest, mockToolService));
        ArrayList<Date> billableDays = rentalAgreement.buildListOfPossiblyBillableDays();
        assertEquals(reusableTenDayRentalRequest.getNumberOfRentalDays(), billableDays.size());

        rentalAgreement.calculateNumberOfEachTypeOfRentalDay(billableDays);
        rentalAgreement.calculateBillableDays();
        assertEquals(4, rentalAgreement.getBillableDays());

        //  Verify pre-discount charge
        //
        //  Given the test tool being used, the per day charge is $2.99; therefore, the pre-discount charge should be
        //  ($2.99 * 4) = $11.96 (rounded up by half)
        rentalAgreement.calculatePreDiscountCharge();
        BigDecimal expectedPreDiscountCharge = new BigDecimal("11.96").setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualPreDiscountCharge = rentalAgreement.getPreDiscountedCharge();
        assertEquals(expectedPreDiscountCharge, actualPreDiscountCharge);

        //  Verify discount amount
        //
        rentalAgreement.calculateDiscountAmount();
        BigDecimal actualDiscountAmount = rentalAgreement.getDiscountAmount();
        //  The discount is 10%, so the discount amount should be $1.96 (rounded up by half) = $1.20
        BigDecimal expectedDiscountAmount = new BigDecimal("1.20").setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedDiscountAmount, actualDiscountAmount);

        //  Verify final charge
        //
        rentalAgreement.calculateFinalCharge();
        BigDecimal actualFinalCharge = rentalAgreement.getFinalCharge();
        BigDecimal expectedFinalCharge = actualPreDiscountCharge.subtract(actualDiscountAmount).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedFinalCharge, actualFinalCharge);
    }

    /**
     * finalize agreement wraps all the method calls below into a single method call.
     * - buildListOfPossiblyBillableDays
     * - calculateNumberOfEachTypeOfRentalDay
     * - calculateBillableDays
     * - calculatePreDiscountCharge
     * - calculateDiscountAmount
     * - calculateFinalCharge
     *
     * To test the finalize method we need to mock the ToolService and the Tool entity, call finalize, and
     * then perform the same assertions as completed in other unit tests for each of the methods called by finalize.
     *
     * Two types of requests will be tested, single day and multiple day rental requests. The requests and tool list
     * created in the setup method will be used here.
     */
    @Test
    void finalizeAgreement() {
        // when ToolService is called to find a tool by code, return null.
        Mockito.when(mockToolService.findByCode(Mockito.anyString())).thenReturn(reusableToolList);

        // First test with multiple days...
        //
        //  For this test the list of possible billable days is as follows:
        //      07/03/24, 07/04/24, 07/05/24, 07/06/24, 07/07/24, 07/08/24, 07/09/24
        //      07/04/24 is Thursday, a weekday, and this day is also the Fourth of July (a holiday)
        //      07/06/24 is a Saturday, a weekend day
        //      07/07/24 is a Sunday, also a weekend day
        //  The total number of each day type in this test should be:
        //      1 holiday, 2 weekend days, and 4 weekdays
        //
        RentalAgreement rentalAgreement = assertDoesNotThrow(() -> validateRequestAndInitializeRentalAgreement(reusableTenDayRentalRequest, mockToolService));
        ArrayList<Date> billableDays = rentalAgreement.buildListOfPossiblyBillableDays();
        assertEquals(reusableTenDayRentalRequest.getNumberOfRentalDays(), billableDays.size());

        rentalAgreement.calculateNumberOfEachTypeOfRentalDay(billableDays);
        rentalAgreement.calculateBillableDays();
        assertEquals(4, rentalAgreement.getBillableDays());

        //  assert that finalize does not throw an exception
        assertDoesNotThrow(rentalAgreement::finalizeAgreement);

        //  Verify pre-discount charge
        //
        //  Given the test tool being used, the per day charge is $2.99; therefore, the pre-discount charge should be
        //  ($2.99 * 4) = $11.96 (rounded up by half)
        rentalAgreement.calculatePreDiscountCharge();
        BigDecimal expectedPreDiscountCharge = new BigDecimal("11.96").setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualPreDiscountCharge = rentalAgreement.getPreDiscountedCharge();
        assertEquals(expectedPreDiscountCharge, actualPreDiscountCharge);

        //  Verify discount amount
        //
        rentalAgreement.calculateDiscountAmount();
        BigDecimal actualDiscountAmount = rentalAgreement.getDiscountAmount();
        //  The discount is 10%, so the discount amount should be $1.96 (rounded up by half) = $1.20
        BigDecimal expectedDiscountAmount = new BigDecimal("1.20").setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedDiscountAmount, actualDiscountAmount);

        //  Verify final charge
        //
        rentalAgreement.calculateFinalCharge();
        BigDecimal actualFinalCharge = rentalAgreement.getFinalCharge();
        BigDecimal expectedFinalCharge = actualPreDiscountCharge.subtract(actualDiscountAmount).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedFinalCharge, actualFinalCharge);

        //  ---------------------------------------------------------------------------------------------------------
        //  Rerun validations using a single day rental request
        //  ---------------------------------------------------------------------------------------------------------
        rentalAgreement = assertDoesNotThrow(() -> validateRequestAndInitializeRentalAgreement(reusableSingleDayWeekDayRentalRequest, mockToolService));
        billableDays = rentalAgreement.buildListOfPossiblyBillableDays();
        assertEquals(reusableSingleDayWeekDayRentalRequest.getNumberOfRentalDays(), billableDays.size());
        //  In this case, the expected number of each type of day is:
        //      0 holiday, 0 weekend days, and 1 weekdays
        rentalAgreement.calculateNumberOfEachTypeOfRentalDay(billableDays);
        rentalAgreement.calculateBillableDays();
        assertEquals(1, rentalAgreement.getBillableDays());

        //  assert that finalize does not throw an exception
        assertDoesNotThrow(rentalAgreement::finalizeAgreement);

        //  Verify pre-discount charge
        //
        //  Given the test tool being used, the per day charge is $2.99; therefore, the pre-discount charge should be
        //  ($2.99 * 1 rounded up by half) = $2.00
        rentalAgreement.calculatePreDiscountCharge();
        expectedPreDiscountCharge = new BigDecimal("2.99").setScale(2, RoundingMode.HALF_UP);
        actualPreDiscountCharge = rentalAgreement.getPreDiscountedCharge();
        assertEquals(expectedPreDiscountCharge, actualPreDiscountCharge);

        //  Verify discount amount
        //
        rentalAgreement.calculateDiscountAmount();
        actualDiscountAmount = rentalAgreement.getDiscountAmount();
        //  The discount is 10%, so the discount amount should be $1.96 (rounded up by half) = $1.20
        expectedDiscountAmount = new BigDecimal("0.2999").setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedDiscountAmount, actualDiscountAmount);

        //  Verify final charge
        //
        rentalAgreement.calculateFinalCharge();
        actualFinalCharge = rentalAgreement.getFinalCharge();
        expectedFinalCharge = actualPreDiscountCharge.subtract(actualDiscountAmount).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedFinalCharge, actualFinalCharge);
    }
}