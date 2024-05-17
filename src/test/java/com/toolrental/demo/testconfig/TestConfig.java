package com.toolrental.demo.testconfig;

import com.toolrental.demo.repository.ChargeRepository;
import com.toolrental.demo.repository.ToolRepository;
import com.toolrental.demo.service.ChargeService;
import com.toolrental.demo.service.CheckoutService;
import com.toolrental.demo.service.ToolService;
import com.toolrental.demo.util.LocalDateUtil;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles("test")
@PropertySource("classpath:application-test.yml")
@ComponentScan(basePackages = "com.toolrental.demo")
public class TestConfig {

    @Bean
    public ToolRepository toolRepositoryMock() {
        return Mockito.mock(ToolRepository.class);
    }

    @Bean
    public ChargeRepository chargeRepositoryMock() {
        return Mockito.mock(ChargeRepository.class);
    }

    @Bean
    public ToolService toolServiceMock() {
        return Mockito.mock(ToolService.class);
    }

    @Bean
    public ChargeService chargeServiceMock() {
        return Mockito.mock(ChargeService.class);
    }

    @Bean
    public LocalDateUtil localDateUtil() {
        return new LocalDateUtil();
    }

    @Bean
    public CheckoutService checkoutService() {
        return new CheckoutService(eventPublisher(), toolServiceMock(), chargeServiceMock(), localDateUtil());
    }

    @Bean
    public ApplicationEventPublisher eventPublisher() {
        return Mockito.mock(ApplicationEventPublisher.class);
    }

}
