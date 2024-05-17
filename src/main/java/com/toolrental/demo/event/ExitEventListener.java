package com.toolrental.demo.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Event listener for the <code>ExitEvent</code>.
 */
@Component
@Slf4j
public class ExitEventListener implements ApplicationListener<ExitEvent> {

    /**
     * The Spring application context.
     */
    private final ConfigurableApplicationContext appContext;

    public ExitEventListener(ConfigurableApplicationContext appContext) {
        this.appContext = appContext;
    }

    /**
     * Responds to the <code>ExitEvent</code> by shutting down the application.
     * @param event The event to respond to.
     */
    @Override
    public void onApplicationEvent(@NonNull ExitEvent event) {
        log.info("Shutting down the application...");
        appContext.close();
    }

}
