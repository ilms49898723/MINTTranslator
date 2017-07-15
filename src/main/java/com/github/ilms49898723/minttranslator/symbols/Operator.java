package com.github.ilms49898723.minttranslator.symbols;

/**
 * Created by littlebird on 2017/07/15.
 */
public class Operator extends BaseSymbol {
    private String mMINT;

    public Operator(String identifier) {
        super(identifier, SymbolType.OPERATOR);
    }

    public String getMINT(String name) {
        return mMINT.replaceAll("$NAME", name);
    }
}
