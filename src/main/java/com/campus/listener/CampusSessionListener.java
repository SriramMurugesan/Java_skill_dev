package com.campus.listener;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@WebListener
public class CampusSessionListener
        implements HttpSessionListener {

    @Override
    public void sessionCreated(
            HttpSessionEvent event) {

        System.out.println(
                "New session created"
        );
    }

    @Override
    public void sessionDestroyed(
            HttpSessionEvent event) {

        System.out.println(
                "Session destroyed"
        );
    }
}