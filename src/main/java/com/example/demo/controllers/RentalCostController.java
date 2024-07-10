package com.example.demo.controllers;

import com.example.demo.entities.RentalCost;
import com.example.demo.services.RentalCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rental-cost")
public class RentalCostController {

    private final RentalCostService rentalCostService;

    @Autowired
    public RentalCostController(RentalCostService rentalCostService) {
        this.rentalCostService = rentalCostService;
    }

    @GetMapping("/all")
    public List<RentalCost> getAllRentalCosts() {
        return rentalCostService.getAllRentalCosts();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<RentalCost> rentalCostsByType(@PathVariable String type) {
        return ResponseEntity.ok(rentalCostService.getRentalCostByType(type));
    }

    @GetMapping()
    public RentalCost rentalCostsByTypeRequestParam(@RequestParam(name = "type") String type) {
        return rentalCostService.getRentalCostByType(type);
    }
}