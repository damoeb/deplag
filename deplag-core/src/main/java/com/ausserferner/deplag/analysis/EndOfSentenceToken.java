package com.ausserferner.deplag.analysis;

/**
 * End of sentence
 */
public class EndOfSentenceToken implements Token {

    private String value;
    private int position;

    public EndOfSentenceToken(int position) {
        this.value = "eos";
        this.position = position;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue()+" [pos: "+getPosition()+"]";
    }
}
