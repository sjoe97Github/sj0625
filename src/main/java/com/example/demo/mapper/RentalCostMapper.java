package com.example.demo.mapper;

import com.example.demo.entities.RentalCost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

@Mapper
public interface RentalCostMapper {

    List<RentalCost> findAll();

    RentalCost findByType(String type);

    List<RentalCost> findByDailyCharge(String dailyCharge);

    List<RentalCost> findByWeekdayCharge(Boolean weekdayCharge);

    List<RentalCost> findByWeekendCharge(Boolean weekendCharge);

    List<RentalCost> findByHolidayCharge(Boolean holidayCharge);
}