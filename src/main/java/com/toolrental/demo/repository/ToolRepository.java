package com.toolrental.demo.repository;

import com.toolrental.demo.model.Tool;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * <code>ToolRepository</code> interface extends <code>JpaRepository</code> to provide CRUD operations for
 * the <code>Tool</code> entity.
 */
public interface ToolRepository extends JpaRepository<Tool, Long> {

    /**
     * Find a <code>Tool</code> by its code.
     * @param code The code of the <code>Tool</code> to find.
     * @return An <code>Optional</code> containing the <code>Tool</code> if found, otherwise empty.
     */
    Optional<Tool> findByCode(String code);

}
