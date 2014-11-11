package com.ausserferner.deplag.analysis;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;


public class TokenIterator implements Iterator<Token> {

    private SimpleToken nextToken = null;
    private Reader reader;
    private int position;

    public TokenIterator(Reader reader) {
        this.reader = reader;
        position = 0;
    }

    @Override
    public boolean hasNext() {
        if (nextToken == null) {
            try {
                StringBuilder value = new StringBuilder(10);
                int c = 0;
                int start = position;
                while ((c = reader.read()) != -1) {
                    position++;
                    if (TokenUtils.isTokenDelimiter(c)) {
                        if (value.length() == 0) {
                            start = position;
                            continue;
                        } else {
                            break;
                        }
                    }
                    value.append((char) c);
                }

                if (value.length() > 0) {
                    nextToken = new SimpleToken();
//                    nextToken.setValue(Normalizer.normalize(value.toString(), Normalizer.Form.NFD));
                    nextToken.setValue(value.toString());
                    nextToken.setPosition(start);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return nextToken != null;
    }

    @Override
    public Token next() {
        final Token next = nextToken;
        nextToken = null;
        return next;
    }

    @Override
    public void remove() {

    }

}
