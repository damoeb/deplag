package com.ausserferner.deplag.index;

import com.ausserferner.deplag.DeplagException;
import com.ausserferner.deplag.Document;
import com.ausserferner.deplag.ErrorCode;
import com.ausserferner.deplag.index.shingle.CharacterConsumer;
import com.ausserferner.deplag.store.Fragment;
import com.ausserferner.deplag.store.Store;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import java.io.Closeable;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.*;

//@Controller
public class IndexWriter implements Closeable {

    private static Logger LOGGER = Logger.getLogger(IndexWriter.class);

    @Autowired
    @Qualifier("store")
    private Store store;

    @Autowired
    @Qualifier("index")
    private Index index;

    public String add(Document document) throws DeplagException {

        // check duplicates
        if (store.hasDocumentWithHash(document.getHash())) {
          throw new DeplagException(ErrorCode.DOCUMENT_ALREADY_INDEXED, String.format("Duplicate hash '%s' found", document.getHash()));
        }
        if (store.hasDocumentWithUrl(document.getUrl())) {
            throw new DeplagException(ErrorCode.DOCUMENT_ALREADY_INDEXED, String.format("Duplicate url '%s' found", document.getUrl()));
        }

        final String documentId = store.newDocumentId();
        document.setId(documentId);
        
        final long in = System.currentTimeMillis();

        FingerPrintConsumer fingerPrintConsumer = new FingerPrintConsumer(documentId, index);

        TokenConsumer tokenConsumer = new TokenConsumer();
        tokenConsumer.addConsumer(fingerPrintConsumer);

        CharacterConsumer charConsumer = new CharacterConsumer(documentId);
        charConsumer.addConsumer(tokenConsumer);

        // pass characters of stream to indexer and storer instance
//        ReaderMultiplexer multiplexer = new ReaderMultiplexer(new StringReader(document.getData()));
        ReaderMultiplexer multiplexer = new ReaderMultiplexer(document.getReader());
        multiplexer.addConsumer(charConsumer);

        store.store(document, multiplexer);

        fingerPrintConsumer.commit();

        LOGGER.info(String.format("document '%s' stored and indexed in %ssec", documentId, (System.currentTimeMillis() - in) / 1000d));

        return documentId;
    }


    public static String toMd5Hash(String document) {
        byte[] digest = DigestUtils.md5(document);

        BigInteger bigInt = new BigInteger(1,digest);
        String hashtext = bigInt.toString(16);
        // Now we need to zero pad it if you actually want the full 32 chars.
        while(hashtext.length() < 32 ){
            hashtext = "0"+hashtext;
        }
        return hashtext;
    }


    @Override
    public void close() {

    }

    public Map<String, List<Fragment>> match(String text) throws DeplagException {

        if(StringUtils.isBlank(text)) {
            throw new DeplagException(ErrorCode.PARAMETER_INVALID, "Document is null or empty", "document");
        }

        /*
        List<Hit> hits = index.match(new StringReader(text));
        Map<String,List<Fragment>> documentIdToHits = new HashMap<>(hits.size());

        for(Hit hit:hits) {
            final String id = hit.getDocumentId();
            if (!documentIdToHits.containsKey(id)) {
                documentIdToHits.put(id, new ArrayList<Fragment>());
            }
            Fragment f = store.getFragment(hit);
            documentIdToHits.get(id).add(f);
        }
        */

        Map<String, SortedSet<Hit>> docIdToHits = index.match(new StringReader(text));
        Map<String, List<Fragment>> docIdToFragments = new HashMap<>(docIdToHits.size());

        for(String docId:docIdToHits.keySet()) {
            List<Fragment> fragments = new ArrayList<>();
            for(Hit h : docIdToHits.get(docId)) {
                Fragment f = store.getFragment(h);
                fragments.add(f);
            }
            docIdToFragments.put(docId,fragments);
        }
        
        return docIdToFragments;
    }

    public Map<String,Object> getStatus() {
        Map<String,Object> map = new HashMap<>(3);
        map.put("documents", getDocumentCount());
        map.put("chunks", getChunkCount());
        map.put("hashes", getHashCount());
        return map;
    }

    public long getDocumentCount() {
        return store.getDocumentCount();
    }

    public long getChunkCount() {
        return store.getDocumentCount();
    }

    public long getHashCount() {
        return index.getHashCount();
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    public Index getIndex() {
        return index;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Store getStore() {
        return store;
    }
}
