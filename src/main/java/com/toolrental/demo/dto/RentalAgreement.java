package com.toolrental.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Represents a rental agreement for a tool.
 */
@Data
@Builder
public class RentalAgreement {

    /**
     * The code of the tool (identifier).
     */
    private String toolCode;

    /**
     * The type of tool.
     */
    private String toolType;

    /**
     * The brand of the tool.
     */
    private String toolBrand;

    /**
     * Number of days the tool is rented for.
     */
    private String rentalDays;

    /**
     * The date the tool is checked out.
     */
    private String checkoutDate;

    /**
     * Calculated as <code>checkoutDate + rentalDays</code>.
     */
    private String dueDate;

    /**
     * Amount per day, specified by the tool type (call 'charge' table).
     */
    private String dailyRentalCharge;

    /**
     * Count of chargeable days, from day after checkout through and including due date, excluding "no charge" days
     * as specified by the <code>toolType</code> (call 'charge' table).
     */
    private String chargeDays;

    /**
     * Calculated as <code>chargeDays * dailyRentalCharge</code>.
     * Resulting total rounded "half up" to the nearest cent.
     */
    private String preDiscountCharge;

    /**
     * Discount percentage. Whole number 0-100.
     */
    private String discountPercent;

    /**
     * Calculated as <code>preDiscountCharge * discountPercentage</code>.
     * Rounded "half up" to the nearest cent.
     */
    private String discountAmount;

    /**
     * Calculated as <code>preDiscountCharge - discountAmount</code>.
     * Resulting total rounds "half up" to the nearest cent.
     */
    private String finalCharge;

    /**
     * Formatter for displaying currency values.
     */
    private NumberFormat currencyFormatter;

    @Override
    public String toString() {
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        return "Tool code: " + toolCode + "\n" +
            "Tool type: " + toolType + "\n" +
            "Tool brand: " + toolBrand + "\n" +
            "Rental days: " + rentalDays + "\n" +
            "Checkout date: " + checkoutDate + "\n" +
            "Due date: " + dueDate + "\n" +
            "Daily rental charge: " + currencyFormatter.format(Double.parseDouble(dailyRentalCharge)) + "\n" +
            "Charge days: " + chargeDays + "\n" +
            "Pre-discount charge: " + currencyFormatter.format(Double.parseDouble(preDiscountCharge)) + "\n" +
            "Discount percent: " + discountPercent + "%\n" +
            "Discount amount: " + currencyFormatter.format(Double.parseDouble(discountAmount)) + "\n" +
            "Final charge: " + currencyFormatter.format(Double.parseDouble(finalCharge));
    }

}
