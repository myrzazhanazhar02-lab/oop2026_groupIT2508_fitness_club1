package com.fitnessclub.model;

/**
 * Builder for {@link MembershipType} that keeps construction fluent.
 */
public class MembershipTypeBuilder {
    private Integer id;
    private String name;
    private int durationDays;
    private double price;
    private Integer visitLimit;

    public MembershipTypeBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public MembershipTypeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MembershipTypeBuilder withDurationDays(int durationDays) {
        this.durationDays = durationDays;
        return this;
    }

    public MembershipTypeBuilder withPrice(double price) {
        this.price = price;
        return this;
    }

    public MembershipTypeBuilder withVisitLimit(Integer visitLimit) {
        this.visitLimit = visitLimit;
        return this;
    }

    public MembershipType build() {
        return new MembershipType(id, name, durationDays, price, visitLimit);
    }
}
