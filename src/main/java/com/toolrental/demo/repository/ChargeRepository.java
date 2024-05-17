package com.toolrental.demo.repository;

import com.toolrental.demo.model.Charge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for the <code>Charge</code> entity.
 */
public interface ChargeRepository extends JpaRepository<Charge, Long> {

    /**
     * Find a charge by the tool type.
     * @param toolType The type of tool being rented.
     * @return The charge entry matching the given <code>toolType</code>.
     */
    Optional<Charge> findByToolType(String toolType);

}
