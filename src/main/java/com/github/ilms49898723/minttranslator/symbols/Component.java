package com.github.ilms49898723.minttranslator.symbols;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by littlebird on 2017/07/15.
 */
public class Component extends BaseSymbol {
    private List<Integer> mInputs;
    private List<Integer> mOutputs;

    public Component(String identifier) {
        super(identifier, SymbolType.IDENTIFIER);
        mInputs = new ArrayList<>();
        mOutputs = new ArrayList<>();
    }
}
