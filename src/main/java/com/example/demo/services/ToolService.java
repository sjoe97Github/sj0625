package com.example.demo.services;

import com.example.demo.entities.Tool;
import com.example.demo.mapper.ToolMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.List;

/**
 * ToolService
 *  In this simple example project. This service always ensures that the controller does not have to deal with
 *  null or optional results from the data access layer.
 *
 *  The ToolService really doesn't do much except force results from the repository
 *  into a List<Tool> type which might make the results easier to work with in the controller.   Actually, returning
 *  a List doesn't necessarily make the results easier to work with, but it provides a little more consistency and
 *  flexibility to the controller (e.g. flexibility to format the results returned to the api client). For example, currently
 *  the list is directly returned to the client and automatically converted to a JSON array.  If we wanted to build a
 *  more complex response, we could add additional fields to the response, or format the response in a different way:
 *  included error messages and status codes, and possibly associating the json array with a "data" field key in the
 *  response body, or something like that.
 *
 *  The primary advantage of the ToolService is that it provides a layer of abstraction between the controller and the
 *  Data Access Layer (DAL), the repository in this case; thereby providing more separation of concerns and allowing for
 *  easier swapping of the DAL layer implementation.  For example, if we wanted to switch from a JPA repository to a JDBCTemplate
 *  or a MyBatis implementation, we could do so without changing the controller code.  We would only need to change the
 *  ToolService implementation.
 *
 *  Important:
 */
@Service
public class ToolService {
    private final ToolMapper toolMapper;

    public ToolService(ToolMapper toolMapper) {
        this.toolMapper = toolMapper;
    }

    public List<Tool> getAllTools() {
        return toolMapper.findAll();
    }

    public List<Tool> getToolByCode(String code) {
        return Optional.ofNullable(toolMapper.findByCode(code))
                .map(List::of)
                .orElse(Collections.emptyList());
    }

    public List<Tool> getToolByType(String type) {
        return Optional.ofNullable(toolMapper.findByType(type))
                .map(List::of)
                .orElse(Collections.emptyList());
    }

    public List<Tool> getToolByBrand(String brand) {
        return Optional.ofNullable(toolMapper.findByBrand(brand))
                .map(List::of)
                .orElse(Collections.emptyList());
    }

    private static List<Tool> toList(Iterable<Tool> iterable) {
        return (List<Tool>) iterable;
    }

    private static List<Tool> toList(Optional<Tool> optional) {
        return optional.map(List::of).orElse(List.of());
    }
}