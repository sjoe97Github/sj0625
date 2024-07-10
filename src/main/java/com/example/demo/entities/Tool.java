package com.example.demo.entities;

// create a tool entity in the entities package
//
// TODO - Refactor to remove the snake case naming convention in methods.
//
public class Tool {
    private Long id;
    private String code;
    private String type;
    private String brand;
    private RentalCost rentalCosts;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTool_code() {
        return code;
    }

    public void setTool_code(String tool_code) {
        this.code = tool_code;
    }

    public String getTool_type() {
        return type;
    }

    public void setTool_type(String tool_type) {
        this.type = tool_type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String branc) {
        this.brand = branc;
    }

    public RentalCost getRentalCosts() {
        return rentalCosts;
    }

    public void setRentalCost(RentalCost rentalCosts) {
        this.rentalCosts = rentalCosts;
    }
}
