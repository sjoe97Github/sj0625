package com.example.demo.store.rentals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * The RentalRequest class represents a rental request made by a customer.
 *
 * The Cloneable interface to allow creating a copy of a RentalRequest object, primarily for testing purposes.
 */
public class RentalRequest implements Cloneable{
    @JsonProperty
    private String toolCode;

    @JsonProperty
    private String checkoutDate;

    @JsonProperty
    private int numberOfRentalDays;

    @JsonProperty
    private String discount;

    public RentalRequest() {}

    // primary purpose is to facilitate testing
    protected RentalRequest clone() {
        return new RentalRequest()
                .setToolCode(toolCode)
                .setCheckoutDate(checkoutDate)
                .setNumberOfRentalDays(numberOfRentalDays)
                .setDiscount(discount);
    }

    public String getToolCode() {
        return toolCode;
    }

    public RentalRequest setToolCode(String toolCode) {
        this.toolCode = toolCode;
        return this;
    }

    public String getCheckoutDate() {
        return checkoutDate;
    }

    public RentalRequest setCheckoutDate(String checkoutDate) {
        this.checkoutDate = checkoutDate;
        return this;
    }

    public int getNumberOfRentalDays() {
        return numberOfRentalDays;
    }

    public RentalRequest setNumberOfRentalDays(int numberOfRentalDays) {
        this.numberOfRentalDays = numberOfRentalDays;
        return this;
    }

    public String getDiscount() {
        return discount;
    }

    public RentalRequest setDiscount(String discount) {
        this.discount = discount;
        return this;
    }
}
