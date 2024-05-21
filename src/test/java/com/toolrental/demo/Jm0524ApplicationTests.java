package com.toolrental.demo;

import com.toolrental.demo.service.ChargeService;
import com.toolrental.demo.service.CheckoutService;
import com.toolrental.demo.service.ToolService;
import com.toolrental.demo.testconfig.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
@ActiveProfiles("test")
class Jm0524ApplicationTests {

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private ToolService toolService;

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private CheckoutService checkoutService;

    @Test
    void contextLoads() {
    }

}
