package com.fitnessclub.repository;

import com.fitnessclub.model.MembershipType;

import java.util.List;
import java.util.Optional;

public interface MembershipTypeRepository {
    Optional<MembershipType> findById(int id);

    List<MembershipType> findAll();
}
