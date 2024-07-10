package com.example.demo.services;

import com.example.demo.entities.RentalCost;
import com.example.demo.mapper.RentalCostMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * RentalCostService -
 * IMPORTANT:  There are no uses of this service in the current codebase.
 */
@Service
public class RentalCostService {
    private final RentalCostMapper rentalCostMapper;

    public RentalCostService(RentalCostMapper rentalCostMapper) {
        this.rentalCostMapper = rentalCostMapper;
    }

    public List<RentalCost> getAllRentalCosts() {
        return rentalCostMapper.findAll();
    }

    public RentalCost getRentalCostByType(String type) {
        return rentalCostMapper.findByType(type);
    }

    public List<RentalCost> getRentalCostsByDailyCharge(String dailyCharge) {
        return rentalCostMapper.findByDailyCharge(dailyCharge);
    }

    public List<RentalCost> getRentalCostsByWeekdayCharge(Boolean weekdayCharge) {
        return rentalCostMapper.findByWeekdayCharge(weekdayCharge);
    }

    public List<RentalCost> getRentalCostsByWeekendCharge(Boolean weekendCharge) {
        return rentalCostMapper.findByWeekendCharge(weekendCharge);
    }

    public List<RentalCost> getRentalCostsByHolidayCharge(Boolean holidayCharge) {
        return rentalCostMapper.findByHolidayCharge(holidayCharge);
    }
    private static List<RentalCost> toList(Iterable<RentalCost> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }

    private static List<RentalCost> toList(Optional<RentalCost> optional) {
        return optional.map(List::of).orElse(List.of());
    }
}