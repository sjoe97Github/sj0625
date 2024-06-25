package com.example.demo.entities;

import jakarta.persistence.*;

// create a tool entity in the entities package
@Entity
public class Tool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tool_code")
    private String code;
    @Column(name = "tool_type")

    private String type;
    private String brand;

    // Getters and Setters
    public Long getId() {
        return id;
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

    @OneToOne
    @JoinColumn(name = "tool_type", referencedColumnName = "tool_type",
            insertable = false, updatable = false)
    private RentalCost rentalCosts;

    public RentalCost getRentalCosts() {
        return rentalCosts;
    }

    // Currently, the sole purpose of this mutator is to facilitate testing
    public void setRentalCost(RentalCost rentalCosts) {
        this.rentalCosts = rentalCosts;
    }
}
