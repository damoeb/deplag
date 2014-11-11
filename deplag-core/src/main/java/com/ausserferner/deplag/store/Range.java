package com.ausserferner.deplag.store;

import com.ausserferner.deplag.DeplagException;
import com.ausserferner.deplag.index.Hit;

import java.io.Serializable;

public class Range implements Serializable {
    private int position;
    private int length;

    public Range(int position, int length) {
        this.position = position;
        this.length = length;
    }

    public Range() {
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void join(Range other) {
        if (!canJoin(other)) {
            throw new IllegalArgumentException("Ranges are not close enough.");
        }
        Range first = this;
        Range second = other;
        if (other.getPosition() < getPosition()) {
            first = other;
            second = this;
        }
        int newPosition = first.getPosition();
        int newLength = second.getPosition() - first.getPosition() + second.getLength();
        setPosition(newPosition);
        setLength(newLength);
    }

    public boolean canJoin(Range other) {
        return getPosition() + getLength() + 20 > other.getPosition();
    }
}
