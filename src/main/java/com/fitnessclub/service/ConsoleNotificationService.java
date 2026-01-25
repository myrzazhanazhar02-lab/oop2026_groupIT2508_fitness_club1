package com.fitnessclub.service;

public class ConsoleNotificationService implements NotificationService {
    @Override
    public void send(String message) {
        System.out.println("[notification] " + message);
    }
}
