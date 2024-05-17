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

/**
 * Service class for handling tool checkout operations.
 */
@Service
public class CheckoutService {

    /**
     * <code>ApplicationEventPublisher</code> instance for publishing events.
     */
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Calls the <code>ToolRepository</code> to retrieve tool information.
     */
    private final ToolService toolService;

    /**
     * Calls the <code>ChargeRepository</code> to retrieve charge information.
     */
    private final ChargeService chargeService;

    /**
     * Utility class for handling <code>LocalDate</code> operations.
     */
    private final LocalDateUtil localDateUtil;

    /**
     * Flag to indicate if the application is shutting down.
     */
    private final AtomicBoolean shutdownInitiated = new AtomicBoolean(false);

    /**
     * Logger instance for logging messages.
     */
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

    /**
     * Sets the logger instance for the service.
     *
     * @param log The logger instance to set.
     */
    public void setLogger(Logger log) {
        this.log = log;
    }

    /**
     * Initiates the checkout process.
     *
     * @param reader The <code>BufferedReader</code> instance for reading user input.
     */
    public void checkout(BufferedReader reader) {
        try {
            while (!shutdownInitiated.get()) {
                greetingPrompt();

                String code = getStringInput(reader, "Enter tool code (type 'exit' to quit):");
                if (code == null || shutdownInitiated.get()) { break; }

                String rentalDays = getNumericInput(reader, "How many days are you renting the tool for? (type 'exit' to quit)", 1, Integer.MAX_VALUE);
                if (rentalDays == null || shutdownInitiated.get()) { break; }

                String discountPercentage = getNumericInput(reader, "Enter discount percentage (type 'exit' to quit):", 0, 100);
                if (discountPercentage == null || shutdownInitiated.get()) { break; }

                String checkoutDate = getStringInput(reader, "Enter checkout date (yyyy-MM-dd) (type 'exit' to quit):");
                if (checkoutDate == null || shutdownInitiated.get()) { break; }

                if (shutdownInitiated.get()) { break; }

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
                    .dueDate(dueDateFormatted)
                    .dailyRentalCharge(String.valueOf(dailyCharge))
                    .chargeDays(String.valueOf(chargeDays))
                    .preDiscountCharge(preDiscountCharge)
                    .discountPercent(discountPercentage)
                    .discountAmount(discountAmount)
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

    /**
     * Displays the greeting prompt to the user.
     */
    private void greetingPrompt() {
        log.info("=".repeat(60));
        log.info("{}Welcome to the Tool Rental Checkout System!{}", AnsiColor.GREEN, AnsiColor.RESET);
        log.info("{}Please enter the following information to checkout a tool:{}", AnsiColor.GREEN, AnsiColor.RESET);
        log.info("=".repeat(60));
    }

    /**
     * Calculates the number of chargeable days for the rental period.
     *
     * @param rentalDays The number of days the tool is being rented.
     * @param checkoutDate The date the tool is being checked out.
     * @param dueDate The date the tool is due to be returned.
     * @param weekdayCharge Whether to charge for weekdays.
     * @param weekendCharge Whether to charge for weekends.
     * @param holidayCharge Whether to charge for holidays.
     * @return The number of days to charge for the rental period.
     */
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

    /**
     * Calculates the pre-discount charge for the rental period.
     *
     * @param chargeDays The number of days to charge for the rental period.
     * @param dailyCharge The daily charge for the tool.
     * @return The pre-discount charge for the rental period.
     */
    private String getPreDiscountCharge(int chargeDays, double dailyCharge) {
        return String.valueOf(
            new BigDecimal(chargeDays * dailyCharge)
                .setScale(2, RoundingMode.HALF_UP)
        );
    }

    /**
     * Calculates the discount amount for the rental period.
     *
     * @param preDiscountCharge The pre-discount charge for the rental period.
     * @param discountPercentage The discount percentage to apply.
     * @return The discount amount for the rental period.
     */
    private String getDiscountAmount(double preDiscountCharge, double discountPercentage) {
        return String.valueOf(
            BigDecimal.valueOf(preDiscountCharge * (discountPercentage / 100))
                .setScale(2, RoundingMode.HALF_UP)
        );
    }

    /**
     * Prompts the user for input and returns the input as a <code>String</code>.
     *
     * @param reader The <code>BufferedReader</code> instance for reading user input.
     * @param prompt The prompt to display to the user.
     * @return The user input as a <code>String</code>.
     * @throws IOException If an I/O error occurs.
     */
    private String getStringInput(BufferedReader reader, String prompt) throws IOException {
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

    /**
     * Prompts the user for numeric input and returns the input as a <code>String</code>.
     *
     * @param reader The <code>BufferedReader</code> instance for reading user input.
     * @param prompt The prompt to display to the user.
     * @param minValue The minimum value allowed.
     * @param maxValue The maximum value allowed.
     * @return The user input as a <code>String</code>.
     * @throws IOException If an I/O error occurs.
     */
    String getNumericInput(BufferedReader reader, String prompt, int minValue, int maxValue) throws IOException {
        while (!shutdownInitiated.get()) {
            String input = getStringInput(reader, prompt);
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
