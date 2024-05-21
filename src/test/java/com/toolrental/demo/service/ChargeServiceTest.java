package com.toolrental.demo.service;

import com.toolrental.demo.model.Charge;
import com.toolrental.demo.repository.ChargeRepository;
import com.toolrental.demo.testconfig.TestConfig;
import com.toolrental.demo.util.ApplicationConstants.ToolType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestConfig.class)
@ActiveProfiles("test")
class ChargeServiceTest {

    private ChargeService chargeService;

    @Autowired
    private ChargeRepository chargeRepositoryMock;

    @BeforeEach
    void setUp() {
        chargeService = new ChargeService(chargeRepositoryMock);
    }

    @Test
    void testFindChargeByToolType() {
        Charge charge = new Charge();
        charge.setToolType(ToolType.LADDER);
        when(chargeRepositoryMock.findByToolType(ToolType.LADDER)).thenReturn(Optional.of(charge));

        Optional<Charge> result = chargeService.findChargeByToolType(ToolType.LADDER);
        assertTrue(result.isPresent());
        assertEquals(ToolType.LADDER, result.get().getToolType());
    }

}
