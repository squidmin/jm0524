package com.toolrental.demo.event;

import org.springframework.context.ApplicationEvent;

public class ExitEvent extends ApplicationEvent {

    public ExitEvent(Object source) {
        super(source);
    }

}
