package com.toolrental.demo.repository;

import com.toolrental.demo.model.Tool;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ToolRepository extends JpaRepository<Tool, Long> {

    Optional<Tool> findByCode(String code);

}
