package com.example.demo.store.rentals;

import com.example.demo.entities.Tool;
import com.example.demo.store.rentals.exceptions.RentalRequestException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RentalAgreement {
    RentalDateManager rentalDateManager = RentalDateManager.getInstance();

    private Tool tool;
    private int requestedRentalDays;
    private String checkoutDate;
    private Date startDate;
    private Date dueDate;
    private String discountPercent;
    private int billableDays;
    private BigDecimal preDiscountedCharge;
    private BigDecimal discountAmount;
    private BigDecimal finalCharge;

    private int numberOfWeekendDays;
    private int numberOfHolidays;
    private int numberOfWeekdays;

    public RentalAgreement() {
    }

    // protected constructor for testing
    protected RentalAgreement(Tool tool, int requestedRentalDays, String specifiedCheckOutDate, String discountPercent) {
        this.tool = tool;
        this.requestedRentalDays = requestedRentalDays;
        this.checkoutDate = specifiedCheckOutDate;
        this.discountPercent = discountPercent;
    }

    RentalAgreement finalizeAgreement() throws RentalRequestException {
        ArrayList<Date> possiblyBillableDays = buildListOfPossiblyBillableDays();

        //  Calculate the number of weekend, holiday, and weekday days in the rental period
        calculateNumberOfEachTypeOfRentalDay(possiblyBillableDays);

        //  Calculate the billable days
        calculateBillableDays();

        //  Calculate the pre-discount charge
        calculatePreDiscountCharge();

        //  Calculate the discount amount
        calculateDiscountAmount();

        //  Calculate the final charge
        calculateFinalCharge();

        return this;
    }

    ArrayList<Date> buildListOfPossiblyBillableDays() {
        // build an ordered list of rental days starting with the checkout date
        // ArrayList is used to maintain order; so directly return ArrayList so that the interface communicates order
        ArrayList<Date> possibleBillableDays = IntStream.rangeClosed(1, requestedRentalDays)
                .mapToObj(day -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(startDate);
                    cal.add(Calendar.DAY_OF_MONTH, day);
                    return cal.getTime();
                })
                .collect(Collectors.toCollection(ArrayList::new));

        setDueDate(possibleBillableDays);
        return possibleBillableDays;
    }

    // package-private for testing
    Date setDueDate(ArrayList<Date> possiblyBillableDays) {
        return (this.dueDate = possiblyBillableDays.get(possiblyBillableDays.size() - 1));
    }

    // package-private for testing
    void calculateNumberOfEachTypeOfRentalDay(ArrayList<Date> rentalDays) {
        numberOfWeekendDays = (int) rentalDays.stream().filter(rentalDateManager::isWeekend).count();
        numberOfHolidays = (int) rentalDays.stream().filter(rentalDateManager::isHoliday).count();
        numberOfWeekdays = (int) rentalDays.stream().filter((rentalDay) -> rentalDateManager.isWeekDay(rentalDay) && !rentalDateManager.isHoliday(rentalDay)).count();
    }

    // package-private for testing
    int getNumberOfWeekendDays() {
        return numberOfWeekendDays;
    }

    // package-private for testing
    int getNumberOfHolidays() {
        return numberOfHolidays;
    }

    // package-private for testing
    int getNumberOfWeekdays() {
        return numberOfWeekdays;
    }

    // package-private for testing
    void calculateBillableDays() {
        billableDays =  (tool.getRentalCosts().getWeekdayCharge() ? numberOfWeekdays : 0) +
                        (tool.getRentalCosts().getHolidayCharge() ? numberOfHolidays : 0) +
                        (tool.getRentalCosts().getWeekendCharge() ? numberOfWeekendDays : 0);
    }

    // package-private for testing
    int getBillableDays() {
        return billableDays;
    }

    // package-private for testing
    void calculatePreDiscountCharge() {
        BigDecimal billableDaysAsBigDecimal = new BigDecimal(billableDays);
        preDiscountedCharge = getDailyRentalCharge().multiply(billableDaysAsBigDecimal).setScale(2, RoundingMode.HALF_UP);
    }

    // package-private for testing
    BigDecimal getPreDiscountedCharge() {
        return preDiscountedCharge;
    }

    // package-private for testing
    void calculateDiscountAmount() {
        BigDecimal discountPercentAsBigDecimal = (new BigDecimal(discountPercent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
        discountAmount = getPreDiscountedCharge().multiply(discountPercentAsBigDecimal).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    // package-private for testing
    void calculateFinalCharge() {
        finalCharge = getPreDiscountedCharge().subtract(getDiscountAmount()).setScale(2, RoundingMode.HALF_UP);
    }

    // package-private for testing
    BigDecimal getFinalCharge() {
        return finalCharge;
    }

    // package-private for testing
    RentalAgreement setTool(Tool tool) {
        this.tool = tool;
        return this;
    }

    String getToolCode() {
        return tool.getTool_code();
    }

    String getToolType() {
        return tool.getTool_type();
    }

    String getToolBrand() {
        return tool.getBrand();
    }

    int getRequestedRentalDays() {
        return requestedRentalDays;
    }

    String getDiscountPercent() {
        return discountPercent;
    }

    RentalAgreement setDiscountPercent(String discountPercent) {
        this.discountPercent = discountPercent;
        return this;
    }

    RentalAgreement setRequestedRentalDays(int requestedRentalDays) {
        this.requestedRentalDays = requestedRentalDays;
        return this;
    }

    //  This mutator has a duel purpose.  It sets the checkout date and creates start date object from
    //  the checkout date string.
    RentalAgreement setCheckoutDate(String checkoutDate) throws RentalRequestException {
        this.checkoutDate = checkoutDate;

        //  parse checkout date and set the start date
        this.startDate = rentalDateManager.stringToDate(checkoutDate);
        return this;
    }

    String getCheckoutDate() {
        return checkoutDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    Date getDueDate() {
        return dueDate;
    }

    BigDecimal getDailyRentalCharge() {
        return new BigDecimal(this.tool.getRentalCosts().getDailyCharge());
    }

    boolean isWeekdayCharge() {
        return this.tool.getRentalCosts().getWeekdayCharge();
    }

    boolean isWeekendCharge() {
        return this.tool.getRentalCosts().getWeekendCharge();
    }

    boolean isHolidayCharge() {
        return this.tool.getRentalCosts().getHolidayCharge();
    }
}
