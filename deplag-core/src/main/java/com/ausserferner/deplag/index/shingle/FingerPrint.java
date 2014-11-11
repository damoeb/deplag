package com.ausserferner.deplag.index.shingle;

import com.ausserferner.deplag.store.Range;

public class FingerPrint {
    private String hash;
    private String documentId;
    private Range range;

    public FingerPrint() {
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    @Override
    public String toString() {
        return hash + " [pos:"+range.getPosition()+" len:"+range.getLength()+"]";
    }
}
