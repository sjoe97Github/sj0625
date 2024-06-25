package com.example.demo.repository;

import com.example.demo.entities.Tool;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface ToolRepository extends CrudRepository<Tool, Long> {
    @Override
    Iterable<Tool> findAll();

    // find tool record by tool code
    @Nullable
    Iterable<Tool> findByCode(String tool_code);

    @Nullable
    Iterable<Tool> findByType(String tool_type);

    // By default, JPA will return an Iterable<Tool> which is never null; rather an Iterable<Tool> with no elements.
    // is returned if no matching records are found.
    // However, we can force the return type to an Optional<Tool> to indicate
    // that the method may return a null value in case no matching records are found.
    // Note - that this is jus an example of how to use Optional in a repository method, Iterable types are easier
    // to work with in most cases.
    Optional<Tool> findByBrand(String brand);
}
