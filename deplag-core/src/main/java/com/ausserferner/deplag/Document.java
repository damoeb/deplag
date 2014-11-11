package com.ausserferner.deplag;

import java.io.*;

public class Document implements Serializable {
    
    private String id;
    private String url;
    private String hash;
    private Reader reader;

    public Document() {
        //
    }

    public Document(Reader reader) {
        this.reader = reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Reader getReader() {
        return reader;
    }

    public String getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
