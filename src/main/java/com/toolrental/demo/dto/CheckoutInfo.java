package com.toolrental.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutInfo {

    private String toolCode;
    private String rentalDays;
    private String discountPercentage;
    private String checkoutDate;

}
