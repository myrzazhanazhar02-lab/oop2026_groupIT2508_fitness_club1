package com.fitnessclub.model;

public class VisitBasedMembership implements MembershipPackage {
    @Override
    public int durationDays() {
        return 60;
    }

    @Override
    public String displayName() {
        return "VisitBasedMembership";
    }
}
