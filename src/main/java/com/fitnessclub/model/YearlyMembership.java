package com.fitnessclub.model;

public class YearlyMembership implements MembershipPackage {
    @Override
    public int durationDays() {
        return 365;
    }

    @Override
    public String displayName() {
        return "YearlyMembership";
    }
}
