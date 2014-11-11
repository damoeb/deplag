package com.ausserferner.deplag.analysis.filter;

import com.ausserferner.deplag.analysis.EndOfSentenceToken;
import com.ausserferner.deplag.analysis.Token;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class StopwordFilter implements Filter<Token> {

    private static final Logger LOGGER = Logger.getLogger(StopwordFilter.class);

    private Set<String> stopwords;

    public static String[] DEFAULT_STOPWORDS = new String[]{"aber", "als", "am", "an", "auch",
            "auf", "aus", "bei", "bin", "bis", "bist", "da", "dadurch", "daher",
            "darum", "das", "daß", "dass", "dein", "deine", "dem", "den", "der",
            "des", "dessen", "deshalb", "die", "dies", "dieser", "dieses",
            "doch", "dort", "du", "durch", "ein", "eine", "einem", "einen",
            "einer", "eines", "er", "es", "euer", "eure", "für", "hatte",
            "hatten", "hattest", "hattet", "hier", "hinter", "ich", "ihr",
            "ihre", "im", "in", "ist", "ja", "jede", "jedem", "jeden", "jeder",
            "jedes", "jener", "jenes", "jetzt", "kann", "kannst", "können",
            "könnt", "machen", "mein", "meine", "mit", "muß", "mußt", "musst",
            "müssen", "müßt", "nach", "nachdem", "nein", "nicht", "nun", "oder",
            "seid", "sein", "seine", "sich", "sie", "sind", "soll", "sollen",
            "sollst", "sollt", "sonst", "soweit", "sowie", "und", "unser",
            "unsere", "unter", "vom", "von", "vor", "wann", "warum", "was",
            "weiter", "weitere", "wenn", "wer", "werde", "werden", "werdet",
            "weshalb", "wie", "wieder", "wieso", "wir", "wird", "wirst", "wo",
            "woher", "wohin", "zu", "zum", "zur", "über"
    };

    public StopwordFilter() {
        initStopwords(Arrays.asList(DEFAULT_STOPWORDS));
    }

    public StopwordFilter(Collection<String> words) {
        initStopwords(words);
    }

    private void initStopwords(Collection<String> words) {
        stopwords = new HashSet<String>(words.size());
        stopwords.addAll(words);
    }

    @Override
    public boolean filter(Token token) {
        if(token instanceof EndOfSentenceToken)  {
            return true;
        }
        final String value = token.getValue();
        boolean shouldFilter = getStopwords().contains(value.toLowerCase());

        if (shouldFilter) {
            LOGGER.debug("filter " + token);
        }

        return shouldFilter;
    }

    public Set<String> getStopwords() {
        return stopwords;
    }

}
