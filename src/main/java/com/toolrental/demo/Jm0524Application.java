package com.toolrental.demo;

import com.toolrental.demo.cli.ToolCLI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Jm0524Application implements CommandLineRunner {

    @Autowired
    private ToolCLI toolCLI;

    public static void main(String[] args) {
        SpringApplication.run(Jm0524Application.class, args);
    }

    @Override
    public void run(String... args) throws InterruptedException {
        Thread cliThread = new Thread(toolCLI);
        cliThread.start();
        cliThread.join();
    }

}
