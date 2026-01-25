package com.fitnessclub.repository;
import com.fitnessclub.model.FitnessClass;
import java.util.List;
import java.util.Optional;

public interface FitnessClassRepository {
    Optional<FitnessClass> findById(int id);

    List<FitnessClass> findAll();
}
