package com.toolrental.demo.service;

import com.toolrental.demo.dto.RentalAgreement;
import com.toolrental.demo.event.ExitEvent;
import com.toolrental.demo.exception.InvalidNumericRangeException;
import com.toolrental.demo.model.Charge;
import com.toolrental.demo.model.Tool;
import com.toolrental.demo.util.AnsiColor;
import com.toolrental.demo.util.LocalDateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class CheckoutService {

    private final ApplicationEventPublisher eventPublisher;
    private final ToolService toolService;
    private final ChargeService chargeService;
    private final LocalDateUtil localDateUtil;
    private final AtomicBoolean shutdownInitiated = new AtomicBoolean(false);
    private Logger log = LoggerFactory.getLogger(CheckoutService.class);

    public CheckoutService(ApplicationEventPublisher eventPublisher,
                           ToolService toolService,
                           ChargeService chargeService,
                           LocalDateUtil localDateUtil) {
        this.eventPublisher = eventPublisher;
        this.toolService = toolService;
        this.chargeService = chargeService;
        this.localDateUtil = localDateUtil;
    }

    public void setLogger(Logger log) {
        this.log = log;
    }

    public void checkout(BufferedReader reader) {
        try {
            while (!shutdownInitiated.get()) {
                greetingPrompt();

                String code = getInput(reader, "Enter tool code (type 'exit' to quit):");
                if (code == null || shutdownInitiated.get()) {
                    break;
                }

                String rentalDays = getValidInput(reader, "How many days are you renting the tool for? (type 'exit' to quit)", 1, Integer.MAX_VALUE);
                if (rentalDays == null || shutdownInitiated.get()) {
                    break;
                }

                String discountPercentage = getValidInput(reader, "Enter discount percentage (type 'exit' to quit):", 0, 100);
                if (discountPercentage == null || shutdownInitiated.get()) {
                    break;
                }

                String checkoutDate = getInput(reader, "Enter checkout date (yyyy-MM-dd) (type 'exit' to quit):");
                if (checkoutDate == null || shutdownInitiated.get()) {
                    break;
                }

                if (shutdownInitiated.get()) {
                    break;
                }

                Tool tool = toolService.findToolByCode(code).orElse(null);
                if (tool == null) {
                    log.warn("Tool not found. Please try again.");
                    continue;
                } else {
                    log.info("Tool found: {}", tool);
                }

                Charge charge = chargeService.findChargeByToolType(tool.getType()).orElse(null);
                if (charge == null) {
                    log.warn("Charge not found. Please try again.");
                    continue;
                } else {
                    log.info("Charge found: {}", charge);
                }

                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yy");
                LocalDate checkoutLocalDate = LocalDate.parse(checkoutDate, inputFormatter);
                String checkoutDateFormatted = checkoutLocalDate.format(outputFormatter);

                LocalDate dueDate = checkoutLocalDate.plusDays(Long.parseLong(rentalDays));
                String dueDateFormatted = dueDate.format(outputFormatter);

                double dailyCharge = charge.getDailyCharge();
                int chargeDays = getChargeDays(
                    Integer.parseInt(rentalDays),
                    checkoutLocalDate,
                    dueDate,
                    charge.getWeekdayCharge(),
                    charge.getWeekendCharge(),
                    charge.getHolidayCharge()
                );

                String preDiscountCharge = getPreDiscountCharge(chargeDays, dailyCharge);
                String discountAmount = getDiscountAmount(
                    Double.parseDouble(preDiscountCharge),
                    Double.parseDouble(discountPercentage)
                );

                RentalAgreement agreement = RentalAgreement.builder()
                    .toolCode(code)
                    .toolType(tool.getType())
                    .toolBrand(tool.getBrand())
                    .rentalDays(rentalDays)
                    .checkoutDate(checkoutDateFormatted)
                    .dueDate(dueDateFormatted) // checkoutDate + rentalDays
                    .dailyRentalCharge(String.valueOf(dailyCharge)) // Amount per day, specified by the tool type (call 'charge' table)
                    .chargeDays(String.valueOf(chargeDays)) // Count of chargeable days, from day after checkout through and including due date, excluding "no charge" days as specified by the tool type
                    .preDiscountCharge(preDiscountCharge) // chargeDays * dailyRentalCharge. Resulting total rounding half up to the nearest cent
                    .discountPercent(discountPercentage)
                    .discountAmount(discountAmount) // preDiscountCharge * discountPercentage. Rounded half up to the nearest cent.
                    .finalCharge(String.valueOf(
                        BigDecimal.valueOf(Double.parseDouble(preDiscountCharge) - Double.parseDouble(discountAmount))
                            .setScale(2, RoundingMode.HALF_UP)
                    ))
                    .build();

                log.info("\n{}", agreement);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            log.info("Exiting application...");
        }
    }

    private void greetingPrompt() {
        log.info("=".repeat(60));
        log.info("{}Welcome to the Tool Rental Checkout System!{}", AnsiColor.GREEN, AnsiColor.RESET);
        log.info("{}Please enter the following information to checkout a tool:{}", AnsiColor.GREEN, AnsiColor.RESET);
        log.info("=".repeat(60));
    }

    private int getChargeDays(int rentalDays,
                              LocalDate checkoutDate,
                              LocalDate dueDate,
                              Boolean weekdayCharge,
                              Boolean weekendCharge,
                              Boolean holidayCharge) {

        return rentalDays - localDateUtil.getNoChargeDays(
            checkoutDate,
            dueDate,
            weekdayCharge,
            weekendCharge,
            holidayCharge
        );

    }

    private String getPreDiscountCharge(int chargeDays, double dailyCharge) {
        return String.valueOf(
            new BigDecimal(chargeDays * dailyCharge)
                .setScale(2, RoundingMode.HALF_UP)
        );
    }

    private String getDiscountAmount(double preDiscountCharge, double discountPercentage) {
        return String.valueOf(
            BigDecimal.valueOf(preDiscountCharge * (discountPercentage / 100))
                .setScale(2, RoundingMode.HALF_UP)
        );
    }

    private String getInput(BufferedReader reader, String prompt) throws IOException {
        while (!shutdownInitiated.get()) {
            log.info(prompt);
            String input = reader.readLine();
            if (input == null || "exit".equalsIgnoreCase(input.trim())) {
                if (input != null && "exit".equalsIgnoreCase(input.trim())) {
                    eventPublisher.publishEvent(new ExitEvent(this));
                    shutdownInitiated.set(true);
                }
                return null;
            }
            if (!input.trim().isEmpty()) {
                return input.trim();
            }
            log.info("Input cannot be empty. Please try again.");
        }
        return null;
    }

    String getValidInput(BufferedReader reader, String prompt, int minValue, int maxValue) throws IOException {
        while (!shutdownInitiated.get()) {
            String input = getInput(reader, prompt);
            if (input == null || "exit".equalsIgnoreCase(input.trim())) {
                if (input != null && "exit".equalsIgnoreCase(input.trim())) {
                    eventPublisher.publishEvent(new ExitEvent(this));
                    shutdownInitiated.set(true);
                }
                return null;
            }
            try {
                int value = Integer.parseInt(input);
                if (value >= minValue && value <= maxValue) {
                    return input;
                } else {
                    throw new InvalidNumericRangeException("Value specified is outside of the expected range.");
                }
            } catch (InvalidNumericRangeException e) {
                log.warn("Please enter a number between {} and {}.", minValue, maxValue);
            } catch (NumberFormatException e) {
                log.warn("Invalid input. Please enter a valid number.");
            }
        }
        return null;
    }

}
