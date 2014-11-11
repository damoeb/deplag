package com.ausserferner.deplag.index.shingle;

import com.ausserferner.deplag.analysis.*;
import com.ausserferner.deplag.index.ConsumerProvider;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class CharacterConsumer implements Consumer<Character>, ConsumerProvider<Token> {

    private static final Logger LOGGER = Logger.getLogger(CharacterConsumer.class);

    private Set<Consumer<Token>> consumers = new HashSet<>(2);

    private final List<Character> chars = new LinkedList<>();
    private int startOfToken = 0;
    private int cursor = 0;

    private String documentId;
    private Token token;

    public CharacterConsumer(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public void consume(Character c) {
        if(TokenUtils.isSentenceDelimiter(c)) {
            notifyConsumers(new EndOfSentenceToken(cursor));
            chars.clear();
            startOfToken = cursor +1;

        } else {
            if (TokenUtils.isTokenDelimiter(c)) {
                if (!chars.isEmpty()) {

                    token = createToken();
                    notifyConsumers(token);
                    chars.clear();
                }
                startOfToken = cursor +1;
            } else {
                chars.add(c);
            }
        }
        cursor++;
    }

    private Token createToken() {
        // create token
        SimpleToken newToken = new SimpleToken();
        //newToken.setDocumentId(documentId);
        newToken.setPosition(startOfToken);
//        newToken.setValue(toString(chars));
        newToken.setValue(StringUtils.join(chars, ""));
//        newToken.setValue(Normalizer.normalize(StringUtils.join(chars, ""), Normalizer.Form.NFD));
        return newToken;
    }

    private void notifyConsumers(Token newToken) {

        LOGGER.debug("let consume token " + newToken);

        // notify consumer
        for (Consumer<Token> consumer : consumers) {
            consumer.consume(newToken);
        }
    }

    /*
    private String toString(List<Character> someChars) {
//        StringUtils.join(someChars,"");
        StringBuilder b = new StringBuilder(someChars.size());
        for (Character c : someChars) {
            b.append(c);
        }
        return b.toString();
    }
    */

    @Override
    public void addConsumer(Consumer<Token> consumer) {
        consumers.add(consumer);
    }

    @Override
    public Token getLastConsumed() {
        return token;
    }

}
