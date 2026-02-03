package com.fitnessclub.model;

public class MonthlyMembership implements MembershipPackage {
    @Override
    public int durationDays() {
        return 30;
    }

    @Override
    public String displayName() {
        return "MonthlyMembership";
    }
}
