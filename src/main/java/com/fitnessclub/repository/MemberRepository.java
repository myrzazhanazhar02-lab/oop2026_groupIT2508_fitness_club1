package com.fitnessclub.repository;

import com.fitnessclub.model.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MemberRepository extends Repository<Member, Integer> {

    /**
     * Convenience overload to create a member without constructing the entity manually.
     */
    default Member create(String name, String email, String phone, Integer membershipTypeId, LocalDate membershipEndDate) {
        Member draft = new Member(null, name, email, phone, membershipTypeId, membershipEndDate, LocalDateTime.now());
        return create(draft);
    }

    Member updateMembership(int memberId, Integer membershipTypeId, LocalDate membershipEndDate);

    /**
     * Returns members whose membership_end_date is on or after the given date.
     */
    List<Member> findActiveOn(LocalDate date);
}
