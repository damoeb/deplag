package com.ausserferner.deplag.index;

import com.ausserferner.deplag.index.shingle.FingerPrint;
import com.ausserferner.deplag.store.Range;
import com.mongodb.*;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;

import java.net.UnknownHostException;
import java.util.*;

@Controller
public class MongoIndex extends Index {

    private static Logger LOGGER = Logger.getLogger(MongoIndex.class);

    private static final String _HASH = "hash";
    private static final String _DOC = "document";
    private static final String _RANGES = "ranges";

    private static final String DEFAULT_DATABASE = "db";
    private static final String DEFAULT_COLLECTION = "foo";

    private Mongo mongo;
    private DB db;
    private DBCollection coll;

    public MongoIndex() throws UnknownHostException {
//        onInit();
    }

    public void onInit() throws UnknownHostException {
        LOGGER.info(String.format("init: mongo-database: '%s'", DEFAULT_DATABASE));
        LOGGER.info(String.format("init: mongo-collection: '%s'", DEFAULT_COLLECTION));
        mongo = new Mongo();
        db = mongo.getDB(DEFAULT_DATABASE);

//        db.dropDatabase();

        coll = db.getCollection(DEFAULT_COLLECTION);
        coll.createIndex(new BasicDBObject(_HASH, 1));
//        coll.ensureIndex(new BasicDBObject(_HASH, 1));
    }

//    @Override
//    public void index(String documentId, String hash, List<Range> ranges) {
//        LOGGER.debug(String.format("index '%s' in document '%s'", hash, documentId));
//        BasicDBObject doc = new BasicDBObject();
//        doc.put(_DOC, documentId);
//        doc.put(_HASH, hash);
////        doc.put("timestamp", System.currentTimeMillis());
////        doc.put(_RANGES, ranges);
//        doc.put(_RANGES, stringify(ranges));
//        coll.insert(doc);
//    }

    @Override
    public void indexAll(String documentId, Map<String, List<Range>> hashToRangesMap) {
//        LOGGER.debug(String.format("index '%s' in document '%s'", hash, documentId));

        List<DBObject> bulk = new ArrayList<>(hashToRangesMap.size() * 10);

        for (final String hash : hashToRangesMap.keySet()) {
//            index(documentId, hash, hashToRangesMap.get(hash));
//            for(Range ranges:hashToRangesMap.get(hash)) {
            BasicDBObject doc = new BasicDBObject();
            doc.put(_DOC, documentId);
            doc.put(_HASH, hash);
//        doc.put("timestamp", System.currentTimeMillis());
            doc.put(_RANGES, stringify(hashToRangesMap.get(hash)));
            bulk.add(doc);
//            }
        }

        coll.insert(bulk);
    }

    @Override
    public Map<String, List<Range>> get(String hashOfNeedle) {

        Map<String, List<Range>> docToRanges = new HashMap<>(100);

        BasicDBObject query = new BasicDBObject();
        query.put(_HASH, hashOfNeedle);

        DBCursor cur = coll.find(query);
        while(cur.hasNext()) {
            DBObject i = cur.next();
            docToRanges.put((String)i.get(_DOC), destringify((String)i.get(_RANGES)));
        }


        return docToRanges;
    }

    @Override
    public Map<String, SortedSet<Hit>> getHits(FingerPrint fingerPrint) {
        BasicDBObject query = new BasicDBObject();

        query.put(_HASH, fingerPrint.getHash());

        DBCursor cur = coll.find(query);

        Map<String, SortedSet<Hit>> docToRanges = new HashMap<>(200);

        while(cur.hasNext()) {
            DBObject i = cur.next();

            String docId = (String)i.get(_DOC);
            SortedSet<Hit> hits = new TreeSet<>(Hit.DEFAULT_COMPARATOR);
            for(Range range: destringify((String)i.get(_RANGES))) {
                Hit hit = new Hit();
                hit.setDocumentId(docId);
                hit.setRangeOfNeedle(fingerPrint.getRange());
                hit.setRangeOnDocument(range);
                hits.add(hit);
            }
            docToRanges.put(docId, hits);

//            docToRanges.put((String)i.get(_DOC), destringify((String)i.get(_RANGES)));
        }
        return docToRanges;
    }


    @Override
    public void delete(String documentId) {

    }

    @Override
    public void clear() {
        db.dropDatabase();
    }

    @Override
    public long getHashCount() {
        return coll.getCount();
    }

    @Override
    public void commit() {
//        coll.createIndex(new BasicDBObject(_HASH, 1));
        coll.ensureIndex(new BasicDBObject(_HASH, 1));
    }


    private String stringify(final List<Range> ranges) {
        final StringBuilder b = new StringBuilder(ranges.size() * 8);
        final Iterator<Range> iterator = ranges.iterator();

        while (iterator.hasNext()) {
            final Range range = iterator.next();
            b.append(range.getPosition());
            b.append(':');
            b.append(range.getLength());
            if (iterator.hasNext()) {
                b.append(',');
            }
        }
        return b.toString();
    }

    private List<Range> destringify(final String value) {
        final List<Range> response = new ArrayList<>();

        final StringTokenizer t = new StringTokenizer(value, ",:");
//    TODO check if second token exists
        while (t.hasMoreTokens()) {
            int pos = Integer.parseInt(t.nextToken());
            int len = Integer.parseInt(t.nextToken());
            response.add(new Range(pos, len));
        }

        return response;
    }
}
