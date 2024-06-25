package com.example.demo.controllers;

import com.example.demo.entities.Tool;
import com.example.demo.repository.ToolRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    ToolRepository toolRepository;

    HelloWorldController(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

    @GetMapping("/tools")
    public Iterable<Tool> tools() {
        return toolRepository.findAll();
    }

    @GetMapping("/tool/code/{tool_code}")
    public Iterable<Tool> toolByToolCode(@PathVariable String tool_code) {
        return toolRepository.findByCode(tool_code);
    }

    @GetMapping("/tool/type/{tool_type}")
    public Iterable<Tool> toolByToolType(@PathVariable String tool_type) {
        return toolRepository.findByType(tool_type);
    }

    @GetMapping("/tool/brand/{brand}")
    public Iterable<Tool> toolByBrand(@PathVariable String brand) {
        return toolRepository.findByBrand(brand);
    }
}