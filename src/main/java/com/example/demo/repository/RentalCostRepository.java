package com.example.demo.repository;

import com.example.demo.entities.RentalCost;
import io.micrometer.common.lang.Nullable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RentalCostRepository extends CrudRepository<RentalCost, Long> {
    @Override
    Iterable<RentalCost> findAll();

    // find tool record by tool code
    Optional<RentalCost> findByType(String tool_type);

    @Nullable
    Iterable<RentalCost> findByDailyCharge(String daily_charge);

    @Nullable
    Iterable<RentalCost> findByWeekdayCharge(Boolean weekday_charge);

    @Nullable
    Iterable<RentalCost> findByWeekendCharge(Boolean weekend_charge);

    @Nullable
    Iterable<RentalCost> findByHolidayCharge(Boolean holiday_charge);

}
