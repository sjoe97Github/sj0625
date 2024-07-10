package com.example.demo.mapper;

import com.example.demo.entities.RentalCost;
import com.example.demo.entities.Tool;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ToolMapper {
    List<Tool> findAll();

    Tool findByCode(String code);

    Tool findByType(String type);

    Tool findByBrand(String brand);
}