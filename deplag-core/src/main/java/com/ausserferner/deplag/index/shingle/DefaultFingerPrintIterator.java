package com.ausserferner.deplag.index.shingle;

import com.ausserferner.deplag.analysis.SimpleToken;
import com.ausserferner.deplag.analysis.Token;
import com.ausserferner.deplag.analysis.TokenIterator;

import java.util.LinkedList;
import java.util.List;

public class DefaultFingerPrintIterator implements FingerPrintIterator {

    private FingerPrint nextFingerPrint;
    private FingerPrintGenerator generator;

    private TokenIterator iterator;
    private final List<Token> tokens = new LinkedList<>();

    public DefaultFingerPrintIterator(TokenIterator iterator) {
        this.iterator = iterator;
        generator = new DefaultFingerPrintGenerator();
    }

    @Override
    public boolean hasNext() {
        if (nextFingerPrint == null) {

            while (tokens.size() < generator.getFingerPrintSize() && iterator.hasNext()) {
                Token token = iterator.next();

                if (generator.shouldFilter(token)) {
                    continue;
                }

                tokens.add(token);
            }

            if(tokens.size()==generator.getFingerPrintSize()) {
                nextFingerPrint = generator.createFingerPrint(tokens);
                // remove first token
                if(!tokens.isEmpty()) {
                    tokens.remove(0);
                }
            }
        }
        return nextFingerPrint != null;
    }

    @Override
    public FingerPrint next() {
        FingerPrint response = nextFingerPrint;
        nextFingerPrint = null;
        return response;
    }

    @Override
    public void remove() {

    }

}
