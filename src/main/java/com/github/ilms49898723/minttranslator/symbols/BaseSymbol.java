package com.github.ilms49898723.minttranslator.symbols;

/**
 * Created by littlebird on 2017/07/15.
 */
public class BaseSymbol {
    private String mIdentifier;
    private SymbolType mSymbolType;

    public BaseSymbol() {
        mIdentifier = "";
        mSymbolType = SymbolType.UNDEFINED;
    }

    public BaseSymbol(String identifier, SymbolType symbolType) {
        mIdentifier = identifier;
        mSymbolType = symbolType;
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public SymbolType getSymbolType() {
        return mSymbolType;
    }
}
