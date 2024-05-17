package com.toolrental.demo.repository;

import com.toolrental.demo.model.Charge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChargeRepository extends JpaRepository<Charge, Long> {

    Optional<Charge> findByToolType(String toolType);

}
