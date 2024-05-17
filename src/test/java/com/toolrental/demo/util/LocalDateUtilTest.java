package com.toolrental.demo.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalDateUtilTest {

    private LocalDateUtil localDateUtil;

    @BeforeEach
    void setUp() {
        localDateUtil = new LocalDateUtil(2024);
    }

    @Test
    void testGetJulyFourth() {
        LocalDate julyFourth = localDateUtil.getJulyFourth(2024);
        assertEquals(LocalDate.of(2024, 7, 4), julyFourth);
    }

    @Test
    void testGetFirstMondayOfSeptember() {
        LocalDate firstMonday = localDateUtil.getFirstMondayOfSeptember(2024);
        assertEquals(LocalDate.of(2024, 9, 2), firstMonday);
    }

    @Test
    void testGetNoChargeDays() {
        LocalDate checkoutDate = LocalDate.of(2024, 7, 1);
        LocalDate dueDate = LocalDate.of(2024, 7, 10);
        int noChargeDays = localDateUtil.getNoChargeDays(checkoutDate, dueDate, true, false, true);
        assertEquals(2, noChargeDays); // July 4th is a no-charge day
    }

}
