package com.fitnessclub.model;

import java.time.LocalDateTime;
public class ClassBooking {
    private final Integer id;
    private final Integer memberId;
    private final Integer classId;
    private final LocalDateTime bookedAt;

    public ClassBooking(Integer id, Integer memberId, Integer classId, LocalDateTime bookedAt) {
        this.id = id;
        this.memberId = memberId;
        this.classId = classId;
        this.bookedAt = bookedAt;
    }

    public Integer getId() {
        return id;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public Integer getClassId() {
        return classId;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }
}
