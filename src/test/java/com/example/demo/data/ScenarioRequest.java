package com.example.demo.data;

import com.example.demo.store.rentals.RentalRequest;

import java.math.BigDecimal;

public class ScenarioRequest {
    String scenarioName;
    RentalRequest rentalRequest;
    BigDecimal expectedFinalCharge;

    public ScenarioRequest() {
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public RentalRequest getRentalRequest() {
        return rentalRequest;
    }

    public void setRentalRequest(RentalRequest rentalRequest) {
        this.rentalRequest = rentalRequest;
    }

    public BigDecimal getExpectedFinalCharge() {
        return expectedFinalCharge;
    }

    public void setExpectedFinalCharge(BigDecimal expectedFinalCharge) {
        this.expectedFinalCharge = expectedFinalCharge;
    }
}
