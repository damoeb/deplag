package com.ausserferner.deplag.store;

import com.ausserferner.deplag.Document;
import com.ausserferner.deplag.index.Hit;

import java.io.Reader;


public interface Store {

    void store(Document document, Reader reader);
    
    void delete(String documentId);

    Reader getDocumentStream(String documentId);

    String newDocumentId();

    Fragment getFragment(Hit hit);

    long getDocumentCount();

    long getChunkCount();

    boolean hasDocumentWithHash(String hash);

    boolean hasDocumentWithUrl(String url);
}
