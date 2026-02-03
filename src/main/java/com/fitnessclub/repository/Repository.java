package com.fitnessclub.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository contract with minimal CRUD-style operations.
 *
 * @param <T>  entity type
 * @param <ID> identifier type
 */
public interface Repository<T, ID> {
    T create(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();
}
