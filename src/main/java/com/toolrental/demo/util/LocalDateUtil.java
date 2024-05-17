package com.toolrental.demo.util;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;

/**
 * Utility class for LocalDate operations
 */
@Component
@Getter
public class LocalDateUtil {

    /**
     * Returns the date of July 4th for the given year.
     *
     * @param year The year to get the date for.
     * @return The date of July 4th for the given year.
     */
    public LocalDate getJulyFourth(int year) {
        return LocalDate.of(year, 7, 4);
    }

    /**
     * Returns the first Monday of September for the given year.
     *
     * @param year The year to get the date for.
     * @return The first Monday of September for the given year.
     */
    public LocalDate getFirstMondayOfSeptember(int year) {
        return LocalDate.of(year, Month.SEPTEMBER, 1)
            .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
    }

    /**
     * Returns the number of days between the checkout date and the due date that are not charged.
     *
     * @param checkoutDate The checkout date.
     * @param dueDate The rental due date.
     * @param weekdayCharge Whether to charge for weekdays.
     * @param weekendCharge Whether to charge for weekends.
     * @param holidayCharge Whether to charge for holidays.
     * @return The number of days between the checkout date and the due date that are not charged.
     */
    public int getNoChargeDays(LocalDate checkoutDate,
                               LocalDate dueDate,
                               Boolean weekdayCharge,
                               Boolean weekendCharge,
                               Boolean holidayCharge) {

        int noChargeDays = 0;
        LocalDate current = checkoutDate;
        while (current.isBefore(dueDate)) {
            if (!weekdayCharge && isWeekday(current)) {
                noChargeDays++;
            } else if (!weekendCharge && isWeekend(current)) {
                noChargeDays++;
            } else if (!holidayCharge && isHoliday(current)) {
                noChargeDays++;
            }
            current = current.plusDays(1);
        }
        return noChargeDays;

    }

    /**
     * Returns a boolean indicating whether the given date is a weekday.
     *
     * @param date The date to check.
     * @return True if the date is a weekday, false otherwise.
     */
    private boolean isWeekday(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY;
    }

    /**
     * Returns a boolean indicating whether the given date occurs during a weekend.
     *
     * @param date The date to check.
     * @return True if the date is a weekend, false otherwise.
     */
    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    /**
     * Returns a boolean indicating whether the given date is a holiday.
     *
     * @param date The date to check.
     * @return True if the date is a holiday, false otherwise.
     */
    private boolean isHoliday(LocalDate date) {
        return date.equals(getJulyFourth(date.getYear())) || date.equals(getFirstMondayOfSeptember(date.getYear()));
    }

}
