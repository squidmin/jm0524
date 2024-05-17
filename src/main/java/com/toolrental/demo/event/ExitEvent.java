package com.toolrental.demo.event;

import org.springframework.context.ApplicationEvent;

/**
 * Event that is triggered when the application is exiting.
 */
public class ExitEvent extends ApplicationEvent {

    public ExitEvent(Object source) {
        super(source);
    }

}
