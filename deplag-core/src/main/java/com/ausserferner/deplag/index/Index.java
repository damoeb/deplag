package com.ausserferner.deplag.index;

import com.ausserferner.deplag.analysis.TokenIterator;
import com.ausserferner.deplag.index.shingle.DefaultFingerPrintIterator;
import com.ausserferner.deplag.index.shingle.FingerPrint;
import com.ausserferner.deplag.index.shingle.FingerPrintIterator;
import com.ausserferner.deplag.store.Range;
import org.apache.log4j.Logger;

import java.io.Reader;
import java.util.*;

public abstract class Index {

    protected static final Logger LOGGER = Logger.getLogger(Index.class);

//    public abstract void index(String documentId, String hash, List<Range> ranges);

    public abstract Map<String, List<Range>> get(String hashOfNeedle);

    public abstract void delete(String documentId);

    public abstract void commit();

    //public List<Hit> match(Reader reader) {
    public Map<String, SortedSet<Hit>> match(Reader reader) {

        LOGGER.debug("match");

        final int fragmentSize = 10;
        final int clusterSize = 2000;
        final Map<String, SortedSet<Hit>> docIdToHits = new HashMap<>(100);
        final Map<String, SortedSet<Hit>> docIdToHitsCluster = new HashMap<>(10);
        final Map<String, SortedSet<Hit>> docIdToHitsFragment = new HashMap<>(10);
        final List<FingerPrint> fps = new ArrayList<>(clusterSize);
        FingerPrint needle;

        final FingerPrintIterator iterator = new DefaultFingerPrintIterator(new TokenIterator(reader));
        while(iterator.hasNext()) {

            // -- create cluster
            fps.clear();
            for(int i=0; i<clusterSize && iterator.hasNext(); i++) {
                FingerPrint next = iterator.next();
                //if(i % (Constant.FINGER_PRINT_SIZE-1)==0 || i+1>=clusterSize || !iterator.hasNext()) {
                //if(i % 2==0 || i+1>=clusterSize || !iterator.hasNext()) {
                fps.add(next);
                //}
            }

            docIdToHitsCluster.clear();

            for(int i=0; i<fps.size(); i+=fragmentSize) {

                needle = fps.get(i);
                docIdToHitsFragment.clear();
                docIdToHitsFragment.putAll(getHits(needle));

                // todo minimize requests
                for(int j=1;
                    j<fragmentSize &&
                            //(j<3 || !docIdToHitsFragment.isEmpty()) &&
                            j+i<fps.size();
                    j++) {

                    needle = fps.get(i + j);

                    mergeDocuments(docIdToHitsFragment, getHits(needle));
                }

                //mergeHitsInSet(docIdToHitsFragment);

                mergeDocuments(docIdToHitsCluster, docIdToHitsFragment);
                mergeHitsInSet(docIdToHitsCluster);
            }

            mergeDocuments(docIdToHits, docIdToHitsCluster);
            mergeHitsInSet(docIdToHits);
            // todo keep best match
            // todo remove hits with len<n
        }

        return docIdToHits;

//        while (iterator.hasNext()) {
//
//            final FingerPrint needle = iterator.next();
//
//            final String hashOfNeedle = needle.getHash();
//
//            // find documents with hash
//            final Map<String, List<Range>> documentIdToRanges = get(hashOfNeedle);
//
//            if (documentIdToRanges != null && !documentIdToRanges.isEmpty()) {
//
//                LOGGER.info(String.format("found matches for '%s'", hashOfNeedle));
//                for (String documentId : documentIdToRanges.keySet()) {
//
//                    if (!documentIdToHitsMap.containsKey(documentId)) {
//                        documentIdToHitsMap.put(documentId, new LinkedList<Hit>());
//                    }
//
//                    final List<Hit> hitsOnDocument = documentIdToHitsMap.get(documentId);
//
//                    // get ranges
//                    for (Range rangeOfMatch : documentIdToRanges.get(documentId)) {
//
//                        if (!canJoinWithPreviousHit(needle.getRange(), rangeOfMatch, hitsOnDocument)) {
//                            Hit hit = new Hit();
//                            hit.setDocumentId(documentId);
//                            hit.setRangeOfNeedle(needle.getRange());
//                            hit.setRangeOnDocument(rangeOfMatch);
//                            hitsOnDocument.add(hit);
//                        }
//                    }
//                }
//            }
//        }
//
//        List<Hit> hits = new LinkedList<>();
//        for (List<Hit> hitsOnDocument : documentIdToHitsMap.values()) {
//            hits.addAll(hitsOnDocument);
//        }
//        return hits;
//    }
//    protected boolean canJoinWithPreviousHit(Range rangeOfNeedle, Range rangeOfMatch, List<Hit> hits) {
//        for (Hit hit : hits) {
//            if (Hit.canJoinRanges(hit.getRangeOfNeedle(), rangeOfNeedle) && Hit.canJoinRanges(hit.getRangeOnDocument(), rangeOfMatch)) {
//                hit.setRangeOfNeedle(Hit.joinRanges(hit.getRangeOfNeedle(), rangeOfNeedle));
//                hit.setRangeOnDocument(Hit.joinRanges(hit.getRangeOnDocument(), rangeOfMatch));
//                return true;
//            }
//        }
//        return false;
    }

    private void mergeHitsInSet(Map<String, SortedSet<Hit>> docIdToHits) {
        for(final String key:docIdToHits.keySet()) {
            SortedSet<Hit> unmerged = docIdToHits.get(key);
            if(unmerged.size()<2) continue;

            final SortedSet<Hit> merged = new TreeSet<>(Hit.DEFAULT_COMPARATOR);
            for (Hit next : unmerged) {
                if (merged.isEmpty()) {
                    merged.add(next);
                }
                Hit last = merged.last();
//                Range lastRange = last.getRangeOnDocument();
                // boolean canMerge = lastRange.getPosition() + lastRange.getLength() > next.getRangeOnDocument().getPosition();
                // if (canMerge) {
                if (last.getRangeOnDocument().canJoin(next.getRangeOnDocument()) && last.getRangeOfNeedle().canJoin(next.getRangeOfNeedle())) {
                    merged.remove(last);
                    last.getRangeOfNeedle().join(next.getRangeOfNeedle());
                    last.getRangeOnDocument().join(next.getRangeOnDocument());
                    merged.add(last);
                } else {
                    merged.add(next);
                }
            }
            docIdToHits.put(key,merged);
        }
    }

    private void mergeDocuments(Map<String, SortedSet<Hit>> target, Map<String, SortedSet<Hit>> source) {
        for(String key:source.keySet()) {
            if(target.containsKey(key)) {
                target.get(key).addAll(source.get(key));
            } else {
                target.put(key, source.get(key));
            }
        }
    }

    public void storeAll(String documentId, Map<String, List<Range>> hashToRangesMap) {
//        for (final String hash : hashToRangesMap.keySet()) {
//            index(documentId, hash, hashToRangesMap.get(hash));
//        }
        indexAll(documentId, hashToRangesMap);
    }

    protected abstract void indexAll(String documentId, Map<String, List<Range>> hashToRangesMap);

    public abstract void clear();

    public abstract long getHashCount();

    public abstract Map<String, SortedSet<Hit>> getHits(FingerPrint fingerPrint);
}
