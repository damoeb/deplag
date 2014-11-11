package com.ausserferner.deplag.analysis;

public class TokenUtils {

    private static final String DELIMITER_TOKEN = " \"'!?.-,;„\n\t[]{}–‐()=|:+*~#/\\<>&%$§^°_“\r\u00A0\u000C\u00E2\u0080\u0A69\u0A68\u0A67";
    private static final String DELIMITER_SENTENCE = "!?.";

    public static boolean isSentenceDelimiter(int c) {
        return DELIMITER_SENTENCE.contains(String.valueOf((char) c));
    }

    public static boolean isTokenDelimiter(int c) {

        switch (c) {
            case 'ä':
            case 'ö':
            case 'ü':
            case 'ß':
            case 'Ö':
            case 'Ä':
            case 'Ü':
                return false;
        }
        return c < 10 || c > 126 || DELIMITER_TOKEN.contains(String.valueOf((char) c));
    }
}
