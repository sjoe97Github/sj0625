package com.example.demo.data;

import com.example.demo.entities.Tool;
import com.example.demo.store.rentals.RentalRequest;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class DemoTestData {
    @JsonProperty
    ArrayList<Tool> tools = new ArrayList<>();
    @JsonProperty
    ArrayList<RentalRequest> rentalRequests = new ArrayList<>();
    @JsonProperty
    ArrayList<ScenarioRequest> scenarios = new ArrayList<>();

    public DemoTestData() {
    }

    public ArrayList<Tool> getTools() {
        //  Note that in a more widely used interface/class we should make a deep copy of the array, or
        //  at least make a shallow copy as shown below.  However, this is a simple test data
        //  container class; so we are not concerned with the original array being modified.
        //return new ArrayList<>(tools);
        return tools;
    }

    public void setTools(ArrayList<Tool> tools) {
        //  Note that in a more widely used interface/class we should make a deep copy of the array, or
        //  at least make a shallow copy as shown below.  However, this is a simple test data
        //  container class; so we are not concerned with the original array being modified.
        //this.tools = new ArrayList<>(tools);
        this.tools = tools;
    }

    public DemoTestData addTool(Tool tool) {
        this.tools.add(tool);
        return this;
    }

    public ArrayList<RentalRequest> getRentalRequests() {
        //  Note that in a more widely used interface/class we should make a deep copy of the array, or
        //  at least make a shallow copy as shown below.  However, this is a simple test data
        //  container class; so we are not concerned with the original array being modified.
        //return new ArrayList<>(rentalRequests);
        return rentalRequests;
    }

    public void setRentalRequests(ArrayList<RentalRequest> rentalRequests) {
        //  Note that in a more widely used interface/class we should make a deep copy of the array, or
        //  at least make a shallow copy as shown below.  However, this is a simple test data
        //  container class; so we are not concerned with the original array being modified.
        //this.rentalRequests = new ArrayList<>(rentalRequests);
        this.rentalRequests = rentalRequests;
    }

    public DemoTestData addRentalRequest(RentalRequest rentalRequest) {
        this.rentalRequests.add(rentalRequest);
        return this;
    }

    public Tool getTool(String toolCode) {
        List<Tool> filteredTools = tools.stream().filter((t) -> t.getTool_code().equals(toolCode)).toList();
        return filteredTools.isEmpty() ? null : filteredTools.get(0);
    }

    public RentalRequest getRentalRequest(String toolCode) {
        List<RentalRequest> filteredRequests = rentalRequests.stream().filter((rr) -> rr.getToolCode().equals(toolCode)).toList();
        return filteredRequests.isEmpty() ? null : filteredRequests.get(0);
    }

    public ArrayList<ScenarioRequest> getScenarios() {
        //  Note that in a more widely used interface/class we should make a deep copy of the array, or
        //  at least make a shallow copy as shown below.  However, this is a simple test data
        //  container class; so we are not concerned with the original array being modified.
        //return new ArrayList<>(rentalRequests);
        return scenarios;
    }

    public void setScenarios(ArrayList<ScenarioRequest> scenarios) {
        //  Note that in a more widely used interface/class we should make a deep copy of the array, or
        //  at least make a shallow copy as shown below.  However, this is a simple test data
        //  container class; so we are not concerned with the original array being modified.
        //return new ArrayList<>(rentalRequests);
        this.scenarios = scenarios;
    }
}
