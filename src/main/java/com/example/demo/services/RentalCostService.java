package com.example.demo.services;

import com.example.demo.entities.RentalCost;
import com.example.demo.repository.RentalCostRepository;
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
    private final RentalCostRepository rentalCostRepository;

    public RentalCostService(RentalCostRepository rentalCostRepository) {
        this.rentalCostRepository = rentalCostRepository;
    }

    public List<RentalCost> findAll() {
        return toList(rentalCostRepository.findAll());
    }

    public List<RentalCost> findByType(String tool_type) {
        return toList(rentalCostRepository.findByType(tool_type));
    }

    private static List<RentalCost> toList(Iterable<RentalCost> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }

    private static List<RentalCost> toList(Optional<RentalCost> optional) {
        return optional.map(List::of).orElse(List.of());
    }
}