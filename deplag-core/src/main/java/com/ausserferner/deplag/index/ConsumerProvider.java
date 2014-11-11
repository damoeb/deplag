package com.ausserferner.deplag.index;

import com.ausserferner.deplag.analysis.Consumer;

public interface ConsumerProvider<T> {

    void addConsumer(Consumer<T> consumer);
    T getLastConsumed();
}
