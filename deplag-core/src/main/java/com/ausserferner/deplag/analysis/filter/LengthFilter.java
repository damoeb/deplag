package com.ausserferner.deplag.analysis.filter;

import com.ausserferner.deplag.analysis.EndOfSentenceToken;
import com.ausserferner.deplag.analysis.Token;
import org.apache.log4j.Logger;


public class LengthFilter implements Filter<Token> {

    private static final Logger LOGGER = Logger.getLogger(LengthFilter.class);

    private int minLength;
    private int maxLength;

    public LengthFilter(int minLength, int maxLength) {
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    public boolean filter(Token token) {
        if(token instanceof EndOfSentenceToken)  {
            return true;
        }

        final int len = token.getValue().length();
        boolean shouldFilter = len < minLength || len > maxLength;

        if (shouldFilter) {
            LOGGER.debug("filter " + token);
        }

        return shouldFilter;
    }

}
