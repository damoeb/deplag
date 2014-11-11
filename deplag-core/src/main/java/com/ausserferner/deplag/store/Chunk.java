package com.ausserferner.deplag.store;

public class Chunk {

    private String payload;
    private int position;

    public Chunk(String payload, int position) {
        this.payload = payload;
        this.position = position;
    }

    public String getPayload() {
        return payload;
    }

    public int getPosition() {
        return position;
    }

    public int getLength() {
        return payload.length();
    }
}