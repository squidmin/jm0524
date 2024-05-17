package com.toolrental.demo.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;

@Component
@Getter
public class LocalDateUtil {

    private final int currentYear;
    private final LocalDate currentDate;
    private final LocalDate julyFourth;
    private final LocalDate firstMondayOfSeptember;
    private final LocalDate currentYearJulyFourth;
    private final LocalDate currentYearFirstMondayOfSeptember;

    public LocalDateUtil(@Value("${current-year}") int currentYear) {
        this.currentYear = currentYear;
        currentDate = LocalDate.now();
        julyFourth = getJulyFourth(currentYear);
        firstMondayOfSeptember = getFirstMondayOfSeptember(currentYear);
        currentYearJulyFourth = getCurrentYearJulyFourth();
        currentYearFirstMondayOfSeptember = getCurrentYearFirstMondayOfSeptember();
    }

    public LocalDate getJulyFourth(int year) {
        return LocalDate.of(year, 7, 4);
    }

    public LocalDate getFirstMondayOfSeptember(int year) {
        return LocalDate.of(year, Month.SEPTEMBER, 1)
            .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
    }

    public LocalDate getCurrentYearJulyFourth() {
        return getJulyFourth(currentDate.getYear());
    }

    public LocalDate getCurrentYearFirstMondayOfSeptember() {
        return getFirstMondayOfSeptember(currentDate.getYear());
    }

    public int getNoChargeDays(LocalDate checkoutDate,
                               LocalDate dueDate,
                               Boolean weekdayCharge,
                               Boolean weekendCharge,
                               Boolean holidayCharge) {
        int noChargeDays = 0;
        LocalDate current = checkoutDate;
        while (current.isBefore(dueDate)) {
            if (!weekdayCharge && (current.getDayOfWeek() != DayOfWeek.SATURDAY && current.getDayOfWeek() != DayOfWeek.SUNDAY)) {
                noChargeDays++;
            } else if (!weekendCharge && (current.getDayOfWeek() == DayOfWeek.SATURDAY || current.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                noChargeDays++;
            } else if (!holidayCharge && (current.equals(currentYearJulyFourth) || current.equals(currentYearFirstMondayOfSeptember))) {
                noChargeDays++;
            }
            current = current.plusDays(1);
        }
        return noChargeDays;
    }

}
