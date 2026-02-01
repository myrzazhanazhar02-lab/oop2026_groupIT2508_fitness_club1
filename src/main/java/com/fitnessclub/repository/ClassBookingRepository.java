package com.fitnessclub.repository;

import com.fitnessclub.model.ClassBooking;

import java.util.List;

public interface ClassBookingRepository extends Repository<ClassBooking, Integer> {

    ClassBooking create(int memberId, int classId);

    boolean existsByMemberAndClass(int memberId, int classId);

    int countBookingsForClass(int classId);

    List<ClassBooking> findByMember(int memberId);
}
