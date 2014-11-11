package com.ausserferner.deplag.analysis.filter;


public interface Filter<T> {
    boolean filter(T toFilter);
}
