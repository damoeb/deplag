package com.ausserferner.deplag.analysis.filter;

import com.ausserferner.deplag.analysis.EndOfSentenceToken;
import com.ausserferner.deplag.analysis.Token;
import org.apache.log4j.Logger;


public class NumberFilter implements Filter<Token> {

    private static final Logger LOGGER = Logger.getLogger(NumberFilter.class);

    @Override
    public boolean filter(Token token) {
        if(token instanceof EndOfSentenceToken)  {
            return true;
        }
        final String value = token.getValue();
        boolean shouldFilter = value.charAt(0) >= (int) '0' && value.charAt(0) <= '9';

        if (shouldFilter) {
            LOGGER.debug("filter " + token);
        }

        return shouldFilter;
    }

}
