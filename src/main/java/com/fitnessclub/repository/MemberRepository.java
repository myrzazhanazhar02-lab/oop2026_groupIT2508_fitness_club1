package com.fitnessclub.repository;

import com.fitnessclub.model.Member;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member create(String name, String email, String phone, Integer membershipTypeId, LocalDate membershipEndDate);

    Optional<Member> findById(int id);

    List<Member> findAll();

    Member updateMembership(int memberId, Integer membershipTypeId, LocalDate membershipEndDate);
}
