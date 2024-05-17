package com.toolrental.demo.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ExitEventListener implements ApplicationListener<ExitEvent> {

    @Autowired
    private ConfigurableApplicationContext context;

    @Override
    public void onApplicationEvent(@NonNull ExitEvent event) {
        System.out.println("Exit event received. Shutting down the application...");
        context.close();
    }

}
