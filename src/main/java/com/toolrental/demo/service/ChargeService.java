package com.toolrental.demo.service;

import com.toolrental.demo.model.Charge;
import com.toolrental.demo.repository.ChargeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class <code>Charge</code> entity.
 */
@Service
public class ChargeService {

    /**
     * Repository for <code>Charge</code> entity.
     */
    private final ChargeRepository chargeRepository;

    public ChargeService(ChargeRepository chargeRepository) {
        this.chargeRepository = chargeRepository;
    }

    /**
     * Find charge by tool type.
     *
     * @param toolType The tool type.
     * @return The optional <code>Charge</code> entry.
     */
    public Optional<Charge> findChargeByToolType(String toolType) {
        return chargeRepository.findByToolType(toolType);
    }

}
