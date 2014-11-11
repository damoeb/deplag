package com.ausserferner.deplag.index;

import com.ausserferner.deplag.analysis.filter.Filter;

public interface Filterable<T> {
    void addFilter(Filter<T> filter);

    boolean shouldFilter(T item);
}
