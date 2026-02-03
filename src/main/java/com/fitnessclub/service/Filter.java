package com.fitnessclub.service;

@FunctionalInterface
public interface Filter<T> {
    boolean test(T item);
}
