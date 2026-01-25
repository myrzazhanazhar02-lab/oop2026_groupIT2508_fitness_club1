package com.fitnessclub.model;

import java.time.LocalDateTime;

public class FitnessClass {
    private final Integer id;
    private final String name;
    private final int capacity;
    private final LocalDateTime startTime;

    public FitnessClass(Integer id, String name, int capacity, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.startTime = startTime;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
}
