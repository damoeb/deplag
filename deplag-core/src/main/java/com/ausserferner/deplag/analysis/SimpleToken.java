package com.ausserferner.deplag.analysis;

public class SimpleToken implements Token {

    private int position;
    private String value;
//    private String documentId;

    public SimpleToken() {
        //
    }

    @Override
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
/*
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
*/
    @Override
    public String toString() {
        return value + " [pos:" + position+"]";
    }

}
