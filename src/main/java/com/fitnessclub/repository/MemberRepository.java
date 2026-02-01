package com.fitnessclub.repository;

import com.fitnessclub.model.Member;

import java.time.LocalDate;

public interface MemberRepository extends Repository<Member, Integer> {

    Member create(
            String name,
            String email,
            String phone,
            Integer membershipTypeId,
            LocalDate membershipEndDate
    );

    Member updateMembership(
            int memberId,
            Integer membershipTypeId,
            LocalDate membershipEndDate
    );
}

