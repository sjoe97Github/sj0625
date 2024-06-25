package com.example.demo.controllers;

import com.example.demo.entities.Tool;
import com.example.demo.services.ToolService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ToolController
 *  The specs does not call for an api layer; however, this simple REST controller provides a convenient way to test the
 *  overall functionality of the application.
 *
 *  In this example, the service layer always returns a List<Tool> type.  If the list is empty there are no matching
 *  results.
 */
@RestController
public class ToolController {
    private static ObjectMapper objectMapper = new ObjectMapper();

    ToolService toolService;

    ToolController(ToolService toolService) {
        this.toolService = toolService;
    }

    @GetMapping("/tools")
    public Iterable<Tool> tools() {
        return toolService.findAll();
    }

    @GetMapping("/tool/code/{tool_code}")
    public Iterable<Tool> toolByToolCode(@PathVariable String tool_code) {
        return toolService.findByCode(tool_code);
    }

    @GetMapping("/tool")
    public Iterable<Tool> toolByToolByCodeRequestParam(@RequestParam(name = "code") String tool_code) {
        return toolService.findByCode(tool_code);
    }

    @GetMapping("/tool/type/{tool_type}")
    public Iterable<Tool> toolByToolType(@PathVariable String tool_type) {
        return toolService.findByType(tool_type);
    }

    //
    //  The following method is an example of a very rudimentary way to leverage the list returned by the service layer
    //  to build and return a more complex response.   In reality, the response formatting work could also be delegated to
    //  a helper, a filter, or simply by leveraging Spring's global exception handling capability to trap and fomat
    //  the exception(s); or some combination of these.
    //
    @GetMapping("/tool/brand/{brand}")
    public ResponseEntity<String> toolByBrand(@PathVariable String brand) {
        List<Tool> results = toolService.findByBrand(brand);
        if (results.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    "No tool(s) found for brand: " + brand
            );
        } else {
            try {
                return ResponseEntity.ok(objectMapper.writeValueAsString(results));
            } catch (Exception e) {
                return new ResponseEntity<>("Error processing request to lookup a tool of brand: " + brand, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}