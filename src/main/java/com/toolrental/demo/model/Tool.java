package com.toolrental.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tool")
@Getter
@Setter
@ToString
public class Tool {

    /**
     * Unique identifier for a tool instance
     */
    @Id
    private String code;

    /**
     * The type of tool. Also specifies the daily rental charge and the days for which the daily rental charge applies.
     */
    private String type;

    /**
     * The brand of the ladder, chain saw, or jackhammer.
     */
    private String brand;

}
