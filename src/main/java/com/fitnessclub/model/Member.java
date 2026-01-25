package com.fitnessclub.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Member {
    private final Integer id;
    private final String name;
    private final String email;
    private final String phone;
    private final Integer membershipTypeId;
    private final LocalDate membershipEndDate;
    private final LocalDateTime createdAt;

    public Member(Integer id,
                  String name,
                  String email,
                  String phone,
                  Integer membershipTypeId,
                  LocalDate membershipEndDate,
                  LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.membershipTypeId = membershipTypeId;
        this.membershipEndDate = membershipEndDate;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Integer getMembershipTypeId() {
        return membershipTypeId;
    }

    public LocalDate getMembershipEndDate() {
        return membershipEndDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
