package com.fitnessclub.model;

public class MembershipType {
    private final Integer id;
    private final String name;
    private final int durationDays;
    private final double price;
    private final Integer visitLimit;

    public MembershipType(Integer id, String name, int durationDays, double price, Integer visitLimit) {
        this.id = id;
        this.name = name;
        this.durationDays = durationDays;
        this.price = price;
        this.visitLimit = visitLimit;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public double getPrice() {
        return price;
    }

    public Integer getVisitLimit() {
        return visitLimit;
    }
}
