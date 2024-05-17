package com.toolrental.demo.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO class for the checkout information.
 */
@Getter
@Setter
public class CheckoutInfo {

    /**
     * The tool code of the tool to be checked out.
     */
    private String toolCode;

    /**
     * The number of days the tool will be rented.
     */
    private String rentalDays;

    /**
     * The discount percentage to be applied to the rental.
     */
    private String discountPercentage;

    /**
     * The date the tool will be checked out.
     */
    private String checkoutDate;

}
