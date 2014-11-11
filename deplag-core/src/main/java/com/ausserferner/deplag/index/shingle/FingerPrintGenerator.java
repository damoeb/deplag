package com.ausserferner.deplag.index.shingle;

import com.ausserferner.deplag.analysis.SimpleToken;
import com.ausserferner.deplag.analysis.Token;
import com.ausserferner.deplag.index.Filterable;

import java.util.List;

public interface FingerPrintGenerator extends Filterable<Token> {

    FingerPrint createFingerPrint(List<Token> tokens);
    int getFingerPrintSize();
}
