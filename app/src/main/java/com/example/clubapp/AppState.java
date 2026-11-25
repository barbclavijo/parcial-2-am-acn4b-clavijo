package com.example.clubapp;

import java.util.ArrayList;
import java.util.List;

public class AppState {

    public static class Registration {
        public final String sourceId;
        public final String name;
        public final String detail;
        public final String ticketId;

        public Registration(String sourceId, String name, String detail, String ticketId) {
            this.sourceId = sourceId;
            this.name = name;
            this.detail = detail;
            this.ticketId = ticketId;
        }
    }

    private static AppState instance;

    private final List<Registration> registrations = new ArrayList<>();
    private int ticketCounter = 0;

    private AppState() {}

    public static AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }

    public boolean isRegistered(String sourceId) {
        for (Registration registration : registrations) {
            if (registration.sourceId.equals(sourceId)) {
                return true;
            }
        }
        return false;
    }

    public void addRegistration(String sourceId, String name, String detail) {
        if (isRegistered(sourceId)) {
            return;
        }
        ticketCounter++;
        String ticketId = "#PAS-" + (4800 + ticketCounter);
        registrations.add(new Registration(sourceId, name, detail, ticketId));
    }

    public void removeRegistration(String sourceId) {
        registrations.removeIf(r -> r.sourceId.equals(sourceId));
    }
}