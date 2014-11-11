package com.ausserferner.deplag.store;

import com.ausserferner.deplag.index.Hit;

import java.io.Serializable;

public class Fragment implements Serializable {

    private String documentId;
    private String text;
    private String pre;
    private String post;
    private Hit hit;

    public Fragment() {
    }

    public Fragment(String documentId, String text) {
        this.documentId = documentId;
        this.text = text;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Hit getHit() {
        return hit;
    }

    public void setHit(Hit hit) {
        this.hit = hit;
    }

    public String getPre() {
        return pre;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }
}
