package com.ausserferner.deplag.index;

import com.ausserferner.deplag.analysis.Consumer;
import com.ausserferner.deplag.index.shingle.FingerPrint;
import com.ausserferner.deplag.store.Range;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Aggregate fingerprints of a document and before indexing it
 */
public class FingerPrintConsumer implements Consumer<FingerPrint> {

    private static final Logger LOGGER = Logger.getLogger(FingerPrintConsumer.class);
    private Map<String, List<Range>> hashToRangesMap;
    private final Index index;
    private final String documentId;

    public FingerPrintConsumer(String documentId, Index index) {
        this.documentId = documentId;
        this.index = index;
        hashToRangesMap = new HashMap<>(1000);
    }

    @Override
    public void consume(FingerPrint toConsume) {
        LOGGER.debug("consume fingerprint " + toConsume);
        final String hash = toConsume.getHash();
        if (!hashToRangesMap.containsKey(hash)) {
            hashToRangesMap.put(hash, new LinkedList<Range>());
        }

        hashToRangesMap.get(hash).add(toConsume.getRange());
    }

    public void commit() {
        LOGGER.debug("commit " + hashToRangesMap.size() + " fingerprints");
        index.storeAll(documentId, hashToRangesMap);
        index.commit();
    }


}
