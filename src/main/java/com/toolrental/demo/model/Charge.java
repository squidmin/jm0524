package com.toolrental.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "charge")
@Getter
@Setter
@ToString
public class Charge {

    /**
     * The type of tool. Also specifies the daily rental charge and the days for which the daily rental charge applies.
     */
    @Id
    @Column(name = "tool_type")
    private String toolType;

    /**
     * The daily charge amount for a given type of tool (e.g., Ladder, Chainsaw, Jackhammer).
     */
    @Column(name = "daily_charge")
    private Double dailyCharge;

    /**
     * Whether a weekday charge applies for this type of tool.
     */
    @Column(name = "weekday_charge")
    private Boolean weekdayCharge;

    /**
     * Whether a weekend charge applies for this type of tool.
     */
    @Column(name = "weekend_charge")
    private Boolean weekendCharge;

    /**
     * Whether a holiday charge applies for this type of tool.
     */
    @Column(name = "holiday_charge")
    private Boolean holidayCharge;

}
