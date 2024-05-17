package com.toolrental.demo.service;

import com.toolrental.demo.model.Tool;
import com.toolrental.demo.repository.ToolRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ToolService {

    private final ToolRepository toolRepository;

    public ToolService(ToolRepository toolRepository) { this.toolRepository = toolRepository; }

    public Optional<Tool> findToolByCode(String code) {
        return toolRepository.findByCode(code);
    }

    public List<Tool> getAllTools() { return toolRepository.findAll(); }

}
