package com.ausserferner.deplag.index;

import com.ausserferner.deplag.analysis.Token;
import com.ausserferner.deplag.index.shingle.CharacterConsumer;
import com.ausserferner.deplag.analysis.SimpleToken;
import com.ausserferner.deplag.index.shingle.FingerPrint;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static junit.framework.Assert.assertEquals;

public class FingerPrintTest {

    @Test
    public void testFingerPrintGeneration() throws FileNotFoundException {
        CharacterConsumer charConsumer = new CharacterConsumer("wf");

        TokenConsumer tokenConsumer = new TokenConsumer();

        Scanner s = new Scanner(new FileInputStream("/home/damoeb/Downloads/diss2"));
        StringBuilder b = new StringBuilder(400);
        while(s.hasNextLine()) {
            b.append(s.nextLine());
            b.append('\n');
        }
        Token lastToken = null;
        Token checkedToken = null;
        FingerPrint lastFp = null;
        FingerPrint checkedFp = null;
        String document = b.toString();
        for(int i=0; i<document.length(); i++) {
            charConsumer.consume(document.charAt(i));

            lastToken = charConsumer.getLastConsumed();

            if (lastToken != null && !lastToken.equals(checkedToken)) {
                tokenConsumer.consume(lastToken);
                checkedToken = lastToken;

                lastFp = tokenConsumer.getLastConsumed();
                if (lastFp != null && !lastFp.equals(checkedFp)) {
                    // ends with last token
                    int pos = lastFp.getRange().getPosition()+lastFp.getRange().getLength();
                    assertEquals(lastToken.getValue(), document.substring(pos- lastToken.getValue().length(), pos));
                    checkedFp = lastFp;
                }
            }
        }
    }
}
