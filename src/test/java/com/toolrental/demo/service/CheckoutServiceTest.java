package com.toolrental.demo.service;

import com.toolrental.demo.event.ExitEvent;
import com.toolrental.demo.model.Charge;
import com.toolrental.demo.model.Tool;
import com.toolrental.demo.testconfig.TestConfig;
import com.toolrental.demo.util.ApplicationConstants.Brand;
import com.toolrental.demo.util.ApplicationConstants.ToolCode;
import com.toolrental.demo.util.ApplicationConstants.ToolType;
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
    void testGetNumericInputValidRange() throws IOException {
        Assertions.assertEquals(
            "5",
            checkoutService.getNumericInput(
                new BufferedReader(new StringReader("5\n")),
                "Enter a number: (type 'exit' to quit)",
                1,
                10
            )
        );
    }

    @Test
    void testGetNumericInputInvalidRange() throws IOException {
        Assertions.assertNull(
            checkoutService.getNumericInput(
                new BufferedReader(new StringReader("15\n")),
                "Enter a number: (type 'exit' to quit)",
                1,
                10
            )
        );

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(loggerMock, Mockito.atLeastOnce())
            .warn(captor.capture(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Assertions.assertTrue(captor.getAllValues().contains("Please enter a number between {} and {}."));
    }

    @Test
    void testGetNumericInputInvalidFormat() throws IOException {
        Assertions.assertNull(
            checkoutService.getNumericInput(
                new BufferedReader(new StringReader("abc\n")),
                "Enter a number: (type 'exit' to quit)",
                1,
                10
            )
        );

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(loggerMock, Mockito.atLeastOnce()).warn(captor.capture());
        Assertions.assertTrue(captor.getAllValues().contains("Invalid input. Please enter a valid number."));
    }

    @Test
    void testGetNumericInputExit() throws IOException {
        Assertions.assertNull(
            checkoutService.getNumericInput(
                new BufferedReader(new StringReader("exit\n")),
                "Enter a number: (type 'exit' to quit)",
                1,
                10
            )
        );
    }

    @Test
    void testGetStringInputValidString() throws IOException {
        String userInput = ToolCode.CHNS;
        Assertions.assertEquals(
            ToolCode.CHNS,
            checkoutService.getStringInput(
                new BufferedReader(new StringReader(userInput + "\n")),
                "Enter tool code (type 'exit' to quit):"
            )
        );
    }

    @Test
    void testGetStringInputEmptyString() throws IOException {
        Assertions.assertNull(
            checkoutService.getStringInput(
                new BufferedReader(new StringReader("")),
                "Enter tool code (type 'exit' to quit):"
            )
        );
    }

    @Test
    void testCheckoutWithEmptyInput() {
        Tool tool = new Tool();
        tool.setCode(ToolCode.CHNS);
        tool.setType(ToolType.CHAINSAW);

        Charge charge = new Charge();
        charge.setToolType(ToolType.CHAINSAW);
        charge.setDailyCharge(1.99);

        Mockito.when(toolServiceMock.findToolByCode(ToolCode.CHNS)).thenReturn(Optional.of(tool));
        Mockito.when(chargeServiceMock.findChargeByToolType(ToolType.CHAINSAW)).thenReturn(Optional.of(charge));

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

        Assertions.assertDoesNotThrow(() ->
            checkoutService.checkout(new BufferedReader(new StringReader(ToolCode.CHNS + "\n\n5\n10\n2024-07-01\nexit\n")))
        );

        Mockito.verify(eventPublisherMock, Mockito.times(1))
            .publishEvent(ArgumentMatchers.any(ExitEvent.class));
    }

    @Test
    void testCheckoutWithInvalidRentalDays() {
        Tool tool = new Tool();
        tool.setCode(ToolCode.CHNS);
        tool.setType(ToolType.CHAINSAW);

        Charge charge = new Charge();
        charge.setToolType(ToolType.CHAINSAW);
        charge.setDailyCharge(1.99);

        Mockito.when(toolServiceMock.findToolByCode(ToolCode.CHNS)).thenReturn(Optional.of(tool));
        Mockito.when(chargeServiceMock.findChargeByToolType(ToolType.CHAINSAW)).thenReturn(Optional.of(charge));

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

        Assertions.assertDoesNotThrow(() ->
            checkoutService.checkout(new BufferedReader(new StringReader(ToolCode.CHNS + "\nabc\n5\n10\n2024-07-01\nexit\n")))
        );

        Mockito.verify(eventPublisherMock, Mockito.times(1))
            .publishEvent(ArgumentMatchers.any(ExitEvent.class));
    }

    @Test
    void testCheckoutToolNotFound() {
        Mockito.when(toolServiceMock.findToolByCode(ToolCode.CHNS)).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() ->
            checkoutService.checkout(new BufferedReader(new StringReader(ToolCode.CHNS + "\n5\n10\n2024-07-01\n")))
        );

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(loggerMock, Mockito.times(1)).warn(captor.capture());
        Assertions.assertEquals("Tool not found. Please try again.", captor.getValue());
    }

    @Test
    void testCheckoutChargeNotFound() {
        Tool tool = new Tool();
        tool.setCode(ToolCode.CHNS);
        tool.setType(ToolType.CHAINSAW);

        Mockito.when(toolServiceMock.findToolByCode(ToolCode.CHNS)).thenReturn(Optional.of(tool));
        Mockito.when(chargeServiceMock.findChargeByToolType(ToolType.CHAINSAW)).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() ->
            checkoutService.checkout(new BufferedReader(new StringReader(ToolCode.CHNS + "\n5\n10\n2024-07-01\n")))
        );

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(loggerMock, Mockito.times(1)).warn(captor.capture());
        Assertions.assertEquals("Charge not found. Please try again.", captor.getValue());
    }

    @Test
    void testCheckout_JAKR_090315_5Days_101PercentDiscount() {
        Tool tool = new Tool();
        tool.setCode(ToolCode.JAKR);
        tool.setType(ToolType.JACKHAMMER);
        tool.setBrand(Brand.RIDGID);

        Charge charge = new Charge();
        charge.setToolType(ToolType.JACKHAMMER);
        charge.setDailyCharge(2.99);
        charge.setWeekdayCharge(true);
        charge.setWeekendCharge(false);
        charge.setHolidayCharge(false);

        Mockito.when(toolServiceMock.findToolByCode(ToolCode.JAKR)).thenReturn(Optional.of(tool));
        Mockito.when(chargeServiceMock.findChargeByToolType(ToolType.JACKHAMMER)).thenReturn(Optional.of(charge));
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

        Assertions.assertDoesNotThrow(() ->
            checkoutService.checkout(new BufferedReader(new StringReader(ToolCode.JAKR + "\n5\n101\n2015-09-03\nexit\n")))
        );

        Mockito.verify(eventPublisherMock, Mockito.times(1))
            .publishEvent(ArgumentMatchers.any(ExitEvent.class));
    }

    @Test
    void testCheckout_LADW_070220_3Days_10PercentDiscount() {
        Tool tool = new Tool();
        tool.setCode(ToolCode.LADW);
        tool.setType(ToolType.LADDER);
        tool.setBrand(Brand.Werner);

        Charge charge = new Charge();
        charge.setToolType(ToolType.LADDER);
        charge.setDailyCharge(1.99);
        charge.setWeekdayCharge(true);
        charge.setWeekendCharge(true);
        charge.setHolidayCharge(false);

        Mockito.when(toolServiceMock.findToolByCode(ToolCode.LADW)).thenReturn(Optional.of(tool));
        Mockito.when(chargeServiceMock.findChargeByToolType(ToolType.LADDER)).thenReturn(Optional.of(charge));
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

        Assertions.assertDoesNotThrow(() ->
            checkoutService.checkout(new BufferedReader(new StringReader("LADW\n3\n10\n2020-07-02\nexit\n")))
        );

        Mockito.verify(eventPublisherMock, Mockito.times(1))
            .publishEvent(ArgumentMatchers.any(ExitEvent.class));
    }

    @Test
    void testCheckout_CHNS_070215_5Days_25PercentDiscount() {
        Tool tool = new Tool();
        tool.setCode(ToolCode.CHNS);
        tool.setType(ToolType.CHAINSAW);
        tool.setBrand(Brand.STIHL);

        Charge charge = new Charge();
        charge.setToolType(ToolType.CHAINSAW);
        charge.setDailyCharge(1.49);
        charge.setWeekdayCharge(true);
        charge.setWeekendCharge(false);
        charge.setHolidayCharge(true);

        Mockito.when(toolServiceMock.findToolByCode(ToolCode.CHNS)).thenReturn(Optional.of(tool));
        Mockito.when(chargeServiceMock.findChargeByToolType(ToolType.CHAINSAW)).thenReturn(Optional.of(charge));
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

        Assertions.assertDoesNotThrow(() ->
            checkoutService.checkout(new BufferedReader(new StringReader("CHNS\n5\n25\n2015-07-02\nexit\n")))
        );

        Mockito.verify(eventPublisherMock, Mockito.times(1))
            .publishEvent(ArgumentMatchers.any(ExitEvent.class));
    }

    @Test
    void testCheckout_JAKD_090315_6Days_0PercentDiscount() {
        Tool tool = new Tool();
        tool.setCode(ToolCode.JAKD);
        tool.setType(ToolType.JACKHAMMER);
        tool.setBrand(Brand.DEWALT);

        Charge charge = new Charge();
        charge.setToolType(ToolType.JACKHAMMER);
        charge.setDailyCharge(2.99);
        charge.setWeekdayCharge(true);
        charge.setWeekendCharge(false);
        charge.setHolidayCharge(false);

        Mockito.when(toolServiceMock.findToolByCode(ToolCode.JAKD)).thenReturn(Optional.of(tool));
        Mockito.when(chargeServiceMock.findChargeByToolType(ToolType.JACKHAMMER)).thenReturn(Optional.of(charge));
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

        Assertions.assertDoesNotThrow(() ->
            checkoutService.checkout(new BufferedReader(new StringReader(ToolCode.JAKD + "\n6\n0\n2015-09-03\nexit\n")))
        );

        Mockito.verify(eventPublisherMock, Mockito.times(1))
            .publishEvent(ArgumentMatchers.any(ExitEvent.class));
    }

    @Test
    void testCheckout_JAKR_070215_9Days_0PercentDiscount() {
        Tool tool = new Tool();
        tool.setCode(ToolCode.JAKR);
        tool.setType(ToolType.JACKHAMMER);
        tool.setBrand(Brand.RIDGID);

        Charge charge = new Charge();
        charge.setToolType(ToolType.JACKHAMMER);
        charge.setDailyCharge(2.99);
        charge.setWeekdayCharge(true);
        charge.setWeekendCharge(false);
        charge.setHolidayCharge(false);

        Mockito.when(toolServiceMock.findToolByCode(ToolCode.JAKR)).thenReturn(Optional.of(tool));
        Mockito.when(chargeServiceMock.findChargeByToolType(ToolType.JACKHAMMER)).thenReturn(Optional.of(charge));
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

        Assertions.assertDoesNotThrow(() ->
            checkoutService.checkout(new BufferedReader(new StringReader(ToolCode.JAKR + "\n9\n0\n2015-07-02\nexit\n")))
        );

        Mockito.verify(eventPublisherMock, Mockito.times(1))
            .publishEvent(ArgumentMatchers.any(ExitEvent.class));
    }

    @Test
    void testCheckout_JAKR_070220_4Days_50PercentDiscount() {
        Tool tool = new Tool();
        tool.setCode(ToolCode.JAKR);
        tool.setType(ToolType.JACKHAMMER);
        tool.setBrand(Brand.RIDGID);

        Charge charge = new Charge();
        charge.setToolType(ToolType.JACKHAMMER);
        charge.setDailyCharge(2.99);
        charge.setWeekdayCharge(true);
        charge.setWeekendCharge(false);
        charge.setHolidayCharge(false);

        Mockito.when(toolServiceMock.findToolByCode(ToolCode.JAKR)).thenReturn(Optional.of(tool));
        Mockito.when(chargeServiceMock.findChargeByToolType(ToolType.JACKHAMMER)).thenReturn(Optional.of(charge));
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

        Assertions.assertDoesNotThrow(() ->
            checkoutService.checkout(new BufferedReader(new StringReader(ToolCode.JAKR + "\n4\n50\n2020-07-02\nexit\n")))
        );

        Mockito.verify(eventPublisherMock, Mockito.times(1))
            .publishEvent(ArgumentMatchers.any(ExitEvent.class));
    }

}
