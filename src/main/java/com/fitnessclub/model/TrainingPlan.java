package com.fitnessclub.model;

import java.util.ArrayList;
import java.util.List;

public class TrainingPlan {

    private final String goal;
    private final int daysPerWeek;
    private final List<String> exercises;

    private TrainingPlan(Builder builder) {
        this.goal = builder.goal;
        this.daysPerWeek = builder.daysPerWeek;
        this.exercises = builder.exercises;
    }

    public static class Builder {
        private String goal;
        private int daysPerWeek;
        private List<String> exercises = new ArrayList<>();

        public Builder goal(String goal) {
            this.goal = goal;
            return this;
        }

        public Builder daysPerWeek(int daysPerWeek) {
            this.daysPerWeek = daysPerWeek;
            return this;
        }

        public Builder addExercise(String exercise) {
            this.exercises.add(exercise);
            return this;
        }

        public TrainingPlan build() {
            return new TrainingPlan(this);
        }
    }

    @Override
    public String toString() {
        return "TrainingPlan{" +
                "goal='" + goal + '\'' +
                ", daysPerWeek=" + daysPerWeek +
                ", exercises=" + exercises +
                '}';
    }
}
