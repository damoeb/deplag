package com.ausserferner.deplag.index.shingle;

import com.ausserferner.deplag.Constant;
import com.ausserferner.deplag.analysis.SimpleToken;
import com.ausserferner.deplag.analysis.Token;
import com.ausserferner.deplag.analysis.filter.Filter;
import com.ausserferner.deplag.analysis.filter.LengthFilter;
import com.ausserferner.deplag.analysis.filter.NumberFilter;
import com.ausserferner.deplag.analysis.filter.StopwordFilter;
import com.ausserferner.deplag.store.Range;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultFingerPrintGenerator implements FingerPrintGenerator {

    private static final Logger LOGGER = Logger.getLogger(DefaultFingerPrintGenerator.class);

    private final int fingerPrintSize;

    private Set<Filter<Token>> filters;

    public DefaultFingerPrintGenerator() {

        fingerPrintSize = Constant.FINGER_PRINT_SIZE;
        filters = new HashSet<>(5);

//        addFilter(new LengthFilter(2, 30));
//        addFilter(new StopwordFilter());
//        addFilter(new NumberFilter());
    }

    public FingerPrint createFingerPrint(List<Token> tokens) {

        if(tokens==null || tokens.isEmpty()) {
            return null;
        }

        // calc hash
        StringBuilder hash = new StringBuilder(fingerPrintSize * 3);
        for (Token token : tokens) {
            String value = token.getValue();
            hash.append(Character.toLowerCase(value.charAt(0)));
            hash.append(value.length());
        }

        Token first = tokens.get(0);
        Token last = tokens.get(tokens.size() - 1);
        Range r = new Range();
        r.setPosition(first.getPosition());
        r.setLength(last.getPosition() + last.getValue().length() - first.getPosition());

        FingerPrint next = new FingerPrint();
        next.setHash(hash.toString());
        next.setRange(r);
//        next.setDocumentId(first.getDocumentId());

        return next;
    }

    @Override
    public void addFilter(Filter<Token> filter) {
        filters.add(filter);
    }

    @Override
    public boolean shouldFilter(Token item) {
        for (Filter<Token> filter : filters) {
            if (filter.filter(item)) {
                return true;
            }
        }
        return false;
    }

    public int getFingerPrintSize() {
        return fingerPrintSize;
    }
}
