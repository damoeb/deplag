package com.ausserferner.deplag.index;

import com.ausserferner.deplag.analysis.Token;
import com.ausserferner.deplag.index.shingle.CharacterConsumer;
import com.ausserferner.deplag.analysis.SimpleToken;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static junit.framework.Assert.assertEquals;

public class TokenTest {
    
    @Test
    public void testTokenGeneration() throws FileNotFoundException {

        CharacterConsumer charConsumer = new CharacterConsumer("wf");

        Scanner s = new Scanner(new FileInputStream("/home/damoeb/Downloads/diss2"));
        StringBuilder b = new StringBuilder(400);
        while(s.hasNextLine()) {
            b.append(s.nextLine());
            b.append('\n');
        }
        Token lastToken = null;
        Token checkedToken = null;
        String document = b.toString();
        for(int i=0; i<document.length(); i++) {
            charConsumer.consume(document.charAt(i));

            lastToken = charConsumer.getLastConsumed();

            if (lastToken != null && !lastToken.equals(checkedToken)) {
                assertEquals(lastToken.getValue(), document.substring(lastToken.getPosition(), lastToken.getPosition() + lastToken.getValue().length()));
                checkedToken = lastToken;
            }

        }
    }
}
