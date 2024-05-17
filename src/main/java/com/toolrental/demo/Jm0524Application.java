package com.toolrental.demo;

import com.toolrental.demo.cli.ToolCLI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for the application.
 */
@SpringBootApplication
public class Jm0524Application implements CommandLineRunner {

    /**
     * The CLI tool for the application.
     */
    private final ToolCLI toolCLI;

    public Jm0524Application(ToolCLI toolCLI) {
        this.toolCLI = toolCLI;
    }

    /**
     * Starts the CLI tool.
     *
     * @param args The command line arguments.
     * @throws InterruptedException If the thread is interrupted.
     */
    @Override
    public void run(String... args) throws InterruptedException {
        Thread cliThread = new Thread(toolCLI);
        cliThread.start();
        cliThread.join();
    }

    /**
     * Main method for the application.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(Jm0524Application.class, args);
    }

}
