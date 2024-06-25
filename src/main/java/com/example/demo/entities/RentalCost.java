package com.example.demo.entities;

import jakarta.persistence.*;

@Entity(name = "rental_cost")
public class RentalCost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tool_type")
    private String type;
    @Column(name = "daily_charge")
    private String dailyCharge;
    @Column(name = "weekday_charge")
    private Boolean weekdayCharge;
    @Column(name = "weekend_charge")
    private Boolean weekendCharge;
    @Column(name = "holiday_charge")
    private Boolean holidayCharge;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getToolType() {
        return type;
    }

    public void setToolType(String toolType) {
        this.type = toolType;
    }

    public String getDailyCharge() {
        return dailyCharge;
    }

    public void setDailyCharge(String dailyCharge) {
        this.dailyCharge = dailyCharge;
    }

    public Boolean getWeekdayCharge() {
        return weekdayCharge;
    }

    public void setWeekdayCharge(Boolean weekdayCharge) {
        this.weekdayCharge = weekdayCharge;
    }

    public Boolean getWeekendCharge() {
        return weekendCharge;
    }

    public void setWeekendCharge(Boolean weekendCharge) {
        this.weekendCharge = weekendCharge;
    }

    public Boolean getHolidayCharge() {
        return holidayCharge;
    }

    public void setHolidayCharge(Boolean holidayCharge) {
        this.holidayCharge = holidayCharge;
    }
}
