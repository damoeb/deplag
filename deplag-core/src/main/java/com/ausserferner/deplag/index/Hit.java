package com.ausserferner.deplag.index;

import com.ausserferner.deplag.store.Range;

import java.util.Comparator;

public class Hit {
    private String documentId;
    private Range rangeOfNeedle;
    private Range rangeOnDocument;
    public static final Comparator<? super Hit> DEFAULT_COMPARATOR = new Comparator<Hit>() {
        @Override
        public int compare(Hit h1, Hit h2) {
            Integer p1 = h1.getRangeOfNeedle().getPosition();
            Integer p2 = h2.getRangeOfNeedle().getPosition();
            return p1.compareTo(p2);
        }
    };

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Range getRangeOfNeedle() {
        return rangeOfNeedle;
    }

    public void setRangeOfNeedle(Range rangeOfNeedle) {
        this.rangeOfNeedle = rangeOfNeedle;
    }

    public Range getRangeOnDocument() {
        return rangeOnDocument;
    }

    public void setRangeOnDocument(Range rangeOnDocument) {
        this.rangeOnDocument = rangeOnDocument;
    }
/*
    public static boolean canJoinRanges(Range rangeA, Range rangeB) {
        Range first = rangeB, second = rangeA;
        if (rangeA.getPosition() < rangeB.getPosition()) {
            first = rangeA;
            second = rangeB;
        }

        return first.getPosition() + first.getLength() + 1 >= second.getPosition();
    }

    public static Range joinRanges(Range rangeA, Range rangeB) {
        Range joined = new Range();

        Range first = rangeB, second = rangeA;
        if (rangeA.getPosition() < rangeB.getPosition()) {
            first = rangeA;
            second = rangeB;
        }
        joined.setPosition(first.getPosition());
        joined.setLength(second.getPosition() + second.getLength() - first.getPosition());
        return joined;
    }
*/

}
