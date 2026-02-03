package com.fitnessclub.model;

import com.fitnessclub.exception.InvalidInputException;

public class MembershipFactory {

    private MembershipFactory() { }

    public static MembershipPackage create(String type) {
        if (type == null) throw new InvalidInputException("Membership type is required");

        return switch (type.trim().toLowerCase()) {
            case "monthly", "month", "m" -> new MonthlyMembership();
            case "yearly", "year", "y" -> new YearlyMembership();
            case "visitbased", "visit", "v" -> new VisitBasedMembership();
            default -> throw new InvalidInputException("Unknown membership type: " + type);
        };
    }
}
