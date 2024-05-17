package com.toolrental.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.text.NumberFormat;
import java.util.Locale;

@Data
@Builder
public class RentalAgreement {

    private String toolCode;
    private String toolType;
    private String toolBrand;
    private String rentalDays;
    private String checkoutDate;
    private String dueDate;
    private String dailyRentalCharge;
    private String chargeDays;
    private String preDiscountCharge;
    private String discountPercent;
    private String discountAmount;
    private String finalCharge;

    private NumberFormat currencyFormatter;

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
