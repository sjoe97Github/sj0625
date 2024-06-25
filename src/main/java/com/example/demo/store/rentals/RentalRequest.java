package com.example.demo.store.rentals;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RentalRequest {
    private String toolCode;
    private String checkoutDate;
    private int numberOfRentalDays;
    private String discount;

    public RentalRequest() {}

    // primary purpose is to facilitate testing
    public RentalRequest clone() {
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

    public BigDecimal getDiscountAsBigDecimal() {
        return new BigDecimal(discount).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
    }
}
