package com.toolrental.demo.service;

import com.toolrental.demo.model.Tool;
import com.toolrental.demo.repository.ToolRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The <code>ToolService</code> class is a service class that provides methods to interact
 * with the <code>ToolRepository</code>.
 */
@Service
public class ToolService {

    /**
     * The <code>ToolRepository</code> object that provides methods to interact with the database.
     */
    private final ToolRepository toolRepository;

    public ToolService(ToolRepository toolRepository) { this.toolRepository = toolRepository; }

    /**
     * This method finds a tool by its code.
     *
     * @param code The code of the tool to find.
     * @return An <code>Optional</code> object containing the tool if it exists.
     */
    public Optional<Tool> findToolByCode(String code) {
        return toolRepository.findByCode(code);
    }

}
