package com.toolrental.demo.service;

import com.toolrental.demo.model.Charge;
import com.toolrental.demo.repository.ChargeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChargeService {

    private final ChargeRepository chargeRepository;

    public ChargeService(ChargeRepository chargeRepository) {
        this.chargeRepository = chargeRepository;
    }

    public List<Charge> getAllCharges() {
        return chargeRepository.findAll();
    }

    public Optional<Charge> findChargeByToolType(String toolType) {
        return chargeRepository.findByToolType(toolType);
    }

}
