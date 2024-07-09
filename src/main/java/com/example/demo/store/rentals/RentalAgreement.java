package com.example.demo.store.rentals;

import com.example.demo.entities.Tool;
import com.example.demo.json.serializers.StringAsPercentageSerializer;
import com.example.demo.store.rentals.exceptions.RentalRequestException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents a rental agreement during and after checkout with any given rental request.
 * There are methods to calculate billable days ("Charge days"), due date, pre-discount charge, etc.
 *
 * The toString() method formats instances for console output, per the specification.
 *
 * Important - JSON serialization is a work in progress.
 *      This specification did not call for JSON serialization. However, I wanted to "play around" with
 *      custom JSON serializers and various annotations to control formatting, renaming and ordering serialized
 *      fields (i.e. @JsonFormat, @JsonSerialize, @JsonPropertyOrder, and renaming serialized field names using
 *      the @JsonProperty annotation.
 *
 *      I also wanted to introduce a POST endpoint taking a RentalRequest in the body and returning a finalized
 *      RentalAgreement in the response.  This allowed me a quick and dirty way to validate the checkout process, In
 *      particular the RentalRequestController.  (Also see the checkout() and checkoutAsJson() methods which return
 *      JSON and the formatted string output from RentalAgreement.toString() method, respectively.)
 *
 *      Lastly, there is still some field formatting work required to format the "money" fields per the specification.
 */
@JsonPropertyOrder({
        "Tool code", "Tool type", "Tool brand", "Rental days", "Checkout date", "Due date", "Daily rental charge",
        "Charge days", "Pre-discount charge", "Discount percent", "Discount amount", "Final charge"
})
public class RentalAgreement {
    RentalDateManager rentalDateManager = RentalDateManager.getInstance();

    @JsonIgnore
    private Tool tool;
    @JsonProperty("Rental days")
    private int requestedRentalDays;
    @JsonProperty("Checkout date")
    private String checkoutDate;
    @JsonIgnore
    private Date startDate;
    @JsonProperty("Due date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yy")
    private Date dueDate;
    @JsonProperty("Discount percent")
    @JsonSerialize(using = StringAsPercentageSerializer.class)
    private String discountPercent;
    @JsonProperty("Charge days")
    private int billableDays;
    @JsonProperty("Pre-discount charge")
    private BigDecimal preDiscountedCharge;
    @JsonProperty("Discount amount")
    private BigDecimal discountAmount;
    @JsonProperty("Final Charge")
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
        numberOfWeekdays = requestedRentalDays - (numberOfWeekendDays + numberOfHolidays);
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

    @JsonProperty("Tool code")
    String getToolCode() {
        return tool.getTool_code();
    }

    @JsonProperty("Tool type")
    String getToolType() {
        return tool.getTool_type();
    }

    @JsonProperty("Tool brand")
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

    @JsonProperty("Daily rental charge")
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

    //  This method is used to generate a string representation of the RentalAgreement object
    //  with is formatted according to the demo specifications.
    //
    //  CAVEAT:
    //    This project is built with JDK 17 which supports multi-line strings, therefore the formatting
    //    of the output string is much cleaner than it would be in earlier versions of Java.
    //
    //    If you want to refactor to use a version of Java prior to JDK 15, you will need to refactor to use
    //    something more complex as shown below or similar (possibly using StringBuilder):
    //            return "Tool code: " + getToolCode() + "\n" +
    //            "Tool type: " + getToolType() + "\n" +
    //            "Tool brand: " + getToolBrand() + "\n" +
    //            "Rental days: " + getRequestedRentalDays() + "\n" +
    //            "Checkout date: " + getCheckoutDate() + "\n" +
    //            "Due date: " + rentalDateManager.dateToString(getDueDate()) + "\n" +
    //            "Daily rental charge: " + unitedStatesCurrencyFormatter.format(getDailyRentalCharge()) + "\n" +
    //            "Charge days: " + getBillableDays() + "\n" +
    //            "Pre-discount charge: " + unitedStatesCurrencyFormatter.format(getPreDiscountedCharge()) + "\n" +
    //            "Discount percent: " + percentageFormatter.format((new BigDecimal(discountPercent).divide(new BigDecimal(100)))) + "\n" +
    //            "Discount amount: " + unitedStatesCurrencyFormatter.format(getDiscountAmount()) + "\n" +
    //            "Final charge: " + unitedStatesCurrencyFormatter.format(getFinalCharge());
    //
    public String toString() {
        NumberFormat unitedStatesCurrencyFormatter = NumberFormat.getCurrencyInstance();
        NumberFormat percentageFormatter = NumberFormat.getPercentInstance();
        percentageFormatter.setMaximumFractionDigits(2);

        return """
                    Tool code: %s
                    Tool type: %s
                    Tool brand: %s
                    Rental days: %d
                    Checkout date: %s
                    Due date: %s
                    Daily rental charge: %s
                    Charge days: %d
                    Pre-discount charge: %s
                    Discount percent: %s
                    Discount amount: %s
                    Final charge: %s
                """
                .formatted(
                    getToolCode(),
                    getToolType(),
                    getToolBrand(),
                    getRequestedRentalDays(),
                    getCheckoutDate(),
                    rentalDateManager.dateToString(getDueDate()),
                    unitedStatesCurrencyFormatter.format(getDailyRentalCharge()),
                    getBillableDays(),
                    unitedStatesCurrencyFormatter.format(getPreDiscountedCharge()),
                    percentageFormatter.format((new BigDecimal(discountPercent).divide(new BigDecimal(100)))),
                    unitedStatesCurrencyFormatter.format(getDiscountAmount()),
                    unitedStatesCurrencyFormatter.format(getFinalCharge())
                );
    }
}
