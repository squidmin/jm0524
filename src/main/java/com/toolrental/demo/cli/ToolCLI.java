package com.toolrental.demo.cli;

import com.toolrental.demo.service.CheckoutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
@Slf4j
public class ToolCLI implements Runnable {

    private final CheckoutService checkoutService;

    public ToolCLI(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @Override
    public void run() {
        checkoutService.checkout(new BufferedReader(new InputStreamReader(System.in)));
    }

}
