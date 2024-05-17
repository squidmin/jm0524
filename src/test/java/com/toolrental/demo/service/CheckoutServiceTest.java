package com.toolrental.demo.service;

import com.toolrental.demo.event.ExitEvent;
import com.toolrental.demo.model.Charge;
import com.toolrental.demo.model.Tool;
import com.toolrental.demo.testconfig.TestConfig;
import com.toolrental.demo.util.LocalDateUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

@SpringBootTest(classes = TestConfig.class)
@ActiveProfiles("test")
class CheckoutServiceTest {

    private CheckoutService checkoutService;
    private ApplicationEventPublisher eventPublisherMock;
    private Logger loggerMock;

    @Autowired
    private ToolService toolServiceMock;

    @Autowired
    private ChargeService chargeServiceMock;

    private LocalDateUtil localDateUtilMock;

    @BeforeEach
    void setUp() {
        eventPublisherMock = Mockito.mock(ApplicationEventPublisher.class);
        localDateUtilMock = Mockito.mock(LocalDateUtil.class);
        loggerMock = Mockito.mock(Logger.class);
        checkoutService = new CheckoutService(eventPublisherMock, toolServiceMock, chargeServiceMock, localDateUtilMock);
        checkoutService.setLogger(loggerMock);
    }

    @Test
    void testGetValidInputValidRange() throws IOException {
        StringReader input = new StringReader("5\n");
        BufferedReader reader = new BufferedReader(input);

        String result = checkoutService.getNumericInput(reader, "Enter a number:", 1, 10);
        Assertions.assertEquals("5", result);
    }

    @Test
    void testGetValidInputInvalidRange() throws IOException {
        StringReader input = new StringReader("15\n");
        BufferedReader reader = new BufferedReader(input);

        String result = checkoutService.getNumericInput(reader, "Enter a number:", 1, 10);
        Assertions.assertNull(result);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(loggerMock, Mockito.atLeastOnce())
            .warn(captor.capture(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Assertions.assertTrue(captor.getAllValues().contains("Please enter a number between {} and {}."));
    }

    @Test
    void testGetValidInputInvalidFormat() throws IOException {
        StringReader input = new StringReader("abc\n");
        BufferedReader reader = new BufferedReader(input);

        String result = checkoutService.getNumericInput(reader, "Enter a number:", 1, 10);
        Assertions.assertNull(result);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(loggerMock, Mockito.atLeastOnce()).warn(captor.capture());
        Assertions.assertTrue(captor.getAllValues().contains("Invalid input. Please enter a valid number."));
    }

    @Test
    void testGetValidInputExit() throws IOException {
        StringReader input = new StringReader("exit\n");
        BufferedReader reader = new BufferedReader(input);

        String result = checkoutService.getNumericInput(reader, "Enter a number:", 1, 10);
        Assertions.assertNull(result);

        Mockito.verify(eventPublisherMock, Mockito.times(1))
            .publishEvent(ArgumentMatchers.any(ExitEvent.class));
    }

    @Test
    void testCheckoutWithEmptyInput() {
        Tool tool = new Tool();
        tool.setCode("TL1");
        tool.setType("TYPE1");

        Charge charge = new Charge();
        charge.setToolType("TYPE1");
        charge.setDailyCharge(1.99);

        StringReader input = new StringReader("TL1\n\n5\n10\n2024-07-01\nexit\n");
        BufferedReader reader = new BufferedReader(input);

        Mockito.when(toolServiceMock.findToolByCode("TL1")).thenReturn(Optional.of(tool));
        Mockito.when(chargeServiceMock.findChargeByToolType("TYPE1")).thenReturn(Optional.of(charge));

        Mockito.doNothing().when(eventPublisherMock).publishEvent(ArgumentMatchers.any(ExitEvent.class));

        Mockito.when(
            localDateUtilMock.getNoChargeDays(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean(),
                ArgumentMatchers.anyBoolean(),
                ArgumentMatchers.anyBoolean()
            )
        ).thenReturn(1);

        Assertions.assertDoesNotThrow(() -> checkoutService.checkout(reader));

        Mockito.verify(eventPublisherMock, Mockito.times(1))
            .publishEvent(ArgumentMatchers.any(ExitEvent.class));
    }

    @Test
    void testCheckoutWithInvalidRentalDays() {
        Tool tool = new Tool();
        tool.setCode("TL1");
        tool.setType("TYPE1");

        Charge charge = new Charge();
        charge.setToolType("TYPE1");
        charge.setDailyCharge(1.99);

        StringReader input = new StringReader("TL1\nabc\n5\n10\n2024-07-01\nexit\n");
        BufferedReader reader = new BufferedReader(input);

        Mockito.when(toolServiceMock.findToolByCode("TL1")).thenReturn(Optional.of(tool));
        Mockito.when(chargeServiceMock.findChargeByToolType("TYPE1")).thenReturn(Optional.of(charge));

        Mockito.doNothing().when(eventPublisherMock).publishEvent(ArgumentMatchers.any(ExitEvent.class));

        Mockito.when(
            localDateUtilMock.getNoChargeDays(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean(),
                ArgumentMatchers.anyBoolean(),
                ArgumentMatchers.anyBoolean()
            )
        ).thenReturn(1);

        Assertions.assertDoesNotThrow(() -> checkoutService.checkout(reader));

        Mockito.verify(eventPublisherMock, Mockito.times(1))
            .publishEvent(ArgumentMatchers.any(ExitEvent.class));
    }

    @Test
    void testCheckoutToolNotFound() {
        ApplicationEventPublisher eventPublisherMock = Mockito.mock(ApplicationEventPublisher.class);
        LocalDateUtil localDateUtilMock = Mockito.mock(LocalDateUtil.class);
        ToolService toolServiceMock = Mockito.mock(ToolService.class);
        ChargeService chargeServiceMock = Mockito.mock(ChargeService.class);
        Logger loggerMock = Mockito.mock(Logger.class);
        CheckoutService checkoutService = new CheckoutService(eventPublisherMock, toolServiceMock, chargeServiceMock, localDateUtilMock);
        checkoutService.setLogger(loggerMock);

        StringReader input = new StringReader("TL1\n5\n10\n2024-07-01\n");
        BufferedReader reader = new BufferedReader(input);

        Mockito.when(toolServiceMock.findToolByCode("TL1")).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> checkoutService.checkout(reader));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(loggerMock, Mockito.times(1)).warn(captor.capture());
        Assertions.assertEquals("Tool not found. Please try again.", captor.getValue());
    }

    @Test
    void testCheckoutChargeNotFound() {
        ApplicationEventPublisher eventPublisherMock = Mockito.mock(ApplicationEventPublisher.class);
        LocalDateUtil localDateUtilMock = Mockito.mock(LocalDateUtil.class);
        ToolService toolServiceMock = Mockito.mock(ToolService.class);
        ChargeService chargeServiceMock = Mockito.mock(ChargeService.class);
        Logger loggerMock = Mockito.mock(Logger.class);
        CheckoutService checkoutService = new CheckoutService(eventPublisherMock, toolServiceMock, chargeServiceMock, localDateUtilMock);
        checkoutService.setLogger(loggerMock);

        Tool tool = new Tool();
        tool.setCode("TL1");
        tool.setType("TYPE1");

        StringReader input = new StringReader("TL1\n5\n10\n2024-07-01\n");
        BufferedReader reader = new BufferedReader(input);

        Mockito.when(toolServiceMock.findToolByCode("TL1")).thenReturn(Optional.of(tool));
        Mockito.when(chargeServiceMock.findChargeByToolType("TYPE1")).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> checkoutService.checkout(reader));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(loggerMock, Mockito.times(1)).warn(captor.capture());
        Assertions.assertEquals("Charge not found. Please try again.", captor.getValue());
    }

    @Test
    void testCheckout_JAKR_090315_5Days_101PercentDiscount() {
        Tool tool = new Tool();
        tool.setCode("JAKR");
        tool.setType("Jackhammer");
        tool.setBrand("Ridgid");

        Charge charge = new Charge();
        charge.setToolType("Jackhammer");
        charge.setDailyCharge(2.99);
        charge.setWeekdayCharge(true);
        charge.setWeekendCharge(false);
        charge.setHolidayCharge(false);

        StringReader input = new StringReader("JAKR\n5\n101\n2015-09-03\nexit\n");
        BufferedReader reader = new BufferedReader(input);

        Mockito.when(toolServiceMock.findToolByCode("JAKR")).thenReturn(Optional.of(tool));
        Mockito.when(chargeServiceMock.findChargeByToolType("Jackhammer")).thenReturn(Optional.of(charge));
        Mockito.when(
            localDateUtilMock.getNoChargeDays(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean(),
                ArgumentMatchers.anyBoolean(),
                ArgumentMatchers.anyBoolean()
            )
        ).thenReturn(2);

        Mockito.doNothing().when(eventPublisherMock).publishEvent(ArgumentMatchers.any(ExitEvent.class));

        Assertions.assertDoesNotThrow(() -> checkoutService.checkout(reader));

        Mockito.verify(eventPublisherMock, Mockito.times(1))
            .publishEvent(ArgumentMatchers.any(ExitEvent.class));
    }

    @Test
    void testCheckout_LADW_070220_3Days_10PercentDiscount() {
        Tool tool = new Tool();
        tool.setCode("LADW");
        tool.setType("Ladder");
        tool.setBrand("Werner");

        Charge charge = new Charge();
        charge.setToolType("Ladder");
        charge.setDailyCharge(1.99);
        charge.setWeekdayCharge(true);
        charge.setWeekendCharge(true);
        charge.setHolidayCharge(false);

        StringReader input = new StringReader("LADW\n3\n10\n2020-07-02\nexit\n");
        BufferedReader reader = new BufferedReader(input);

        Mockito.when(toolServiceMock.findToolByCode("LADW")).thenReturn(Optional.of(tool));
        Mockito.when(chargeServiceMock.findChargeByToolType("Ladder")).thenReturn(Optional.of(charge));
        Mockito.when(
            localDateUtilMock.getNoChargeDays(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean(),
                ArgumentMatchers.anyBoolean(),
                ArgumentMatchers.anyBoolean()
            )
        ).thenReturn(1);

        Mockito.doNothing().when(eventPublisherMock).publishEvent(ArgumentMatchers.any(ExitEvent.class));

        Assertions.assertDoesNotThrow(() -> checkoutService.checkout(reader));

        Mockito.verify(eventPublisherMock, Mockito.times(1))
            .publishEvent(ArgumentMatchers.any(ExitEvent.class));
    }

    @Test
    void testCheckout_CHNS_070215_5Days_25PercentDiscount() {
        Tool tool = new Tool();
        tool.setCode("CHNS");
        tool.setType("Chainsaw");
        tool.setBrand("Stihl");

        Charge charge = new Charge();
        charge.setToolType("Chainsaw");
        charge.setDailyCharge(1.49);
        charge.setWeekdayCharge(true);
        charge.setWeekendCharge(false);
        charge.setHolidayCharge(true);

        StringReader input = new StringReader("CHNS\n5\n25\n2015-07-02\nexit\n");
        BufferedReader reader = new BufferedReader(input);

        Mockito.when(toolServiceMock.findToolByCode("CHNS")).thenReturn(Optional.of(tool));
        Mockito.when(chargeServiceMock.findChargeByToolType("Chainsaw")).thenReturn(Optional.of(charge));
        Mockito.when(
            localDateUtilMock.getNoChargeDays(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean(),
                ArgumentMatchers.anyBoolean(),
                ArgumentMatchers.anyBoolean()
            )
        ).thenReturn(1);

        Mockito.doNothing().when(eventPublisherMock).publishEvent(ArgumentMatchers.any(ExitEvent.class));

        Assertions.assertDoesNotThrow(() -> checkoutService.checkout(reader));

        Mockito.verify(eventPublisherMock, Mockito.times(1))
            .publishEvent(ArgumentMatchers.any(ExitEvent.class));
    }

    @Test
    void testCheckout_JAKD_090315_6Days_0PercentDiscount() {
        Tool tool = new Tool();
        tool.setCode("JAKD");
        tool.setType("Jackhammer");
        tool.setBrand("DeWalt");

        Charge charge = new Charge();
        charge.setToolType("Jackhammer");
        charge.setDailyCharge(2.99);
        charge.setWeekdayCharge(true);
        charge.setWeekendCharge(false);
        charge.setHolidayCharge(false);

        StringReader input = new StringReader("JAKD\n6\n0\n2015-09-03\nexit\n");
        BufferedReader reader = new BufferedReader(input);

        Mockito.when(toolServiceMock.findToolByCode("JAKD")).thenReturn(Optional.of(tool));
        Mockito.when(chargeServiceMock.findChargeByToolType("Jackhammer")).thenReturn(Optional.of(charge));
        Mockito.when(
            localDateUtilMock.getNoChargeDays(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean(),
                ArgumentMatchers.anyBoolean(),
                ArgumentMatchers.anyBoolean()
            )
        ).thenReturn(2);

        Mockito.doNothing().when(eventPublisherMock).publishEvent(ArgumentMatchers.any(ExitEvent.class));

        Assertions.assertDoesNotThrow(() -> checkoutService.checkout(reader));

        Mockito.verify(eventPublisherMock, Mockito.times(1))
            .publishEvent(ArgumentMatchers.any(ExitEvent.class));
    }

    @Test
    void testCheckout_JAKR_070215_9Days_0PercentDiscount() {
        Tool tool = new Tool();
        tool.setCode("JAKR");
        tool.setType("Jackhammer");
        tool.setBrand("Ridgid");

        Charge charge = new Charge();
        charge.setToolType("Jackhammer");
        charge.setDailyCharge(2.99);
        charge.setWeekdayCharge(true);
        charge.setWeekendCharge(false);
        charge.setHolidayCharge(false);

        StringReader input = new StringReader("JAKR\n9\n0\n2015-07-02\nexit\n");
        BufferedReader reader = new BufferedReader(input);

        Mockito.when(toolServiceMock.findToolByCode("JAKR")).thenReturn(Optional.of(tool));
        Mockito.when(chargeServiceMock.findChargeByToolType("Jackhammer")).thenReturn(Optional.of(charge));
        Mockito.when(
            localDateUtilMock.getNoChargeDays(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean(),
                ArgumentMatchers.anyBoolean(),
                ArgumentMatchers.anyBoolean()
            )
        ).thenReturn(3);

        Mockito.doNothing().when(eventPublisherMock).publishEvent(ArgumentMatchers.any(ExitEvent.class));

        Assertions.assertDoesNotThrow(() -> checkoutService.checkout(reader));

        Mockito.verify(eventPublisherMock, Mockito.times(1))
            .publishEvent(ArgumentMatchers.any(ExitEvent.class));
    }

    @Test
    void testCheckout_JAKR_070220_4Days_50PercentDiscount() {
        Tool tool = new Tool();
        tool.setCode("JAKR");
        tool.setType("Jackhammer");
        tool.setBrand("Ridgid");

        Charge charge = new Charge();
        charge.setToolType("Jackhammer");
        charge.setDailyCharge(2.99);
        charge.setWeekdayCharge(true);
        charge.setWeekendCharge(false);
        charge.setHolidayCharge(false);

        StringReader input = new StringReader("JAKR\n4\n50\n2020-07-02\nexit\n");
        BufferedReader reader = new BufferedReader(input);

        Mockito.when(toolServiceMock.findToolByCode("JAKR")).thenReturn(Optional.of(tool));
        Mockito.when(chargeServiceMock.findChargeByToolType("Jackhammer")).thenReturn(Optional.of(charge));
        Mockito.when(
            localDateUtilMock.getNoChargeDays(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.anyBoolean(),
                ArgumentMatchers.anyBoolean(),
                ArgumentMatchers.anyBoolean()
            )
        ).thenReturn(1);

        Mockito.doNothing().when(eventPublisherMock).publishEvent(ArgumentMatchers.any(ExitEvent.class));

        Assertions.assertDoesNotThrow(() -> checkoutService.checkout(reader));

        Mockito.verify(eventPublisherMock, Mockito.times(1))
            .publishEvent(ArgumentMatchers.any(ExitEvent.class));
    }

}
