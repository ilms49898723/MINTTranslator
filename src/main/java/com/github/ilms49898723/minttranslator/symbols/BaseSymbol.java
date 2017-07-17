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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BaseSymbol that = (BaseSymbol) o;

        if (mIdentifier != null ? !mIdentifier.equals(that.mIdentifier) : that.mIdentifier != null) {
            return false;
        }
        return mSymbolType == that.mSymbolType;
    }

    @Override
    public int hashCode() {
        int result = mIdentifier != null ? mIdentifier.hashCode() : 0;
        result = 31 * result + (mSymbolType != null ? mSymbolType.hashCode() : 0);
        return result;
    }
}
