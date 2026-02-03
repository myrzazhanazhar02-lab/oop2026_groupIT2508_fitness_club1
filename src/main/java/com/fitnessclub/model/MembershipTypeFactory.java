package com.fitnessclub.model;

/**
 * Factory that provides common membership presets.
 */
public final class MembershipTypeFactory {

    private MembershipTypeFactory() {
    }

    public static MembershipType unlimitedMonthly(String displayName, double price) {
        return new MembershipTypeBuilder()
                .withName(displayName)
                .withDurationDays(30)
                .withPrice(price)
                .withVisitLimit(null)
                .build();
    }

    public static MembershipType limitedVisits(String displayName, int durationDays, double price, int visitLimit) {
        return new MembershipTypeBuilder()
                .withName(displayName)
                .withDurationDays(durationDays)
                .withPrice(price)
                .withVisitLimit(visitLimit)
                .build();
    }

    public static MembershipType dropIn(String displayName, double price) {
        return new MembershipTypeBuilder()
                .withName(displayName)
                .withDurationDays(1)
                .withPrice(price)
                .withVisitLimit(1)
                .build();
    }
}
