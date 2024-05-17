package com.toolrental.demo.cli;

import com.toolrental.demo.service.CheckoutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * ToolCLI is a Spring Boot <code>CommandLineRunner</code> that consists of a <code>CheckoutService</code> which reads
 * user input from the console.
 */
@Component
@Slf4j
public class ToolCLI implements Runnable {

    /**
     * The service that is used to check out tools.
     */
    private final CheckoutService checkoutService;

    public ToolCLI(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    /**
     * This method is called when the application is run. It reads user input from the console and contains the main
     * application loop / logic.
     */
    @Override
    public void run() {
        checkoutService.checkout(new BufferedReader(new InputStreamReader(System.in)));
    }

}
