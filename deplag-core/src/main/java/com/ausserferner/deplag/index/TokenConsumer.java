package com.ausserferner.deplag.index;

import com.ausserferner.deplag.analysis.*;
import com.ausserferner.deplag.index.shingle.DefaultFingerPrintGenerator;
import com.ausserferner.deplag.index.shingle.FingerPrint;
import com.ausserferner.deplag.index.shingle.FingerPrintGenerator;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TokenConsumer implements Consumer<Token>, ConsumerProvider<FingerPrint> {

    private static final Logger LOGGER = Logger.getLogger(TokenConsumer.class);

    private FingerPrintGenerator generator;

    private final List<Token> tokens = new LinkedList<>();
    private Set<Consumer<FingerPrint>> consumers = new HashSet<>(2);
    private FingerPrint lastFingerPrint;


    public TokenConsumer() {
        generator = new DefaultFingerPrintGenerator();
    }

    @Override
    public void consume(Token toConsume) {
        if(toConsume instanceof EndOfSentenceToken) {
            tokens.clear();
            LOGGER.debug("eos " + toConsume);
        } else {

            if (generator.shouldFilter(toConsume)) {
                LOGGER.debug("-f " + toConsume);
            } else {
                LOGGER.debug("+c " + toConsume);
                tokens.add(toConsume);

                if (tokens.size() >= generator.getFingerPrintSize()) {
                    lastFingerPrint = generator.createFingerPrint(tokens);
                    notifyConsumers(lastFingerPrint);
                    // remove first token
                    if(!tokens.isEmpty()) {
                        tokens.remove(0);
                    }

                }
            }
        }
    }

    private void notifyConsumers(FingerPrint toConsume) {

        LOGGER.debug("let consume FingerPrint " + toConsume);

        // notify consumer
        for (Consumer<FingerPrint> consumer : consumers) {
            consumer.consume(toConsume);
        }
    }

    @Override
    public void addConsumer(Consumer<FingerPrint> consumer) {
        if (consumer != null) {
            consumers.add(consumer);
        }
    }

    @Override
    public FingerPrint getLastConsumed() {
        return lastFingerPrint;
    }
}
