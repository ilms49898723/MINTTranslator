package com.github.ilms49898723.minttranslator.translator;

import com.github.ilms49898723.minttranslator.symbols.BaseSymbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by littlebird on 2017/07/15.
 */
public class SymbolTable {
    private Map<String, BaseSymbol> mSymbols;

    public SymbolTable() {
        mSymbols = new HashMap<>();
    }

    public StatusCode put(BaseSymbol symbol) {
        if (mSymbols.containsKey(symbol.getIdentifier())) {
            return StatusCode.DUPLICATED_IDENTIFIER;
        } else {
            mSymbols.put(symbol.getIdentifier(), symbol);
            return StatusCode.SUCCESS;
        }
    }

    public BaseSymbol get(String identifier) {
        if (!mSymbols.containsKey(identifier)) {
            return null;
        } else {
            return mSymbols.get(identifier);
        }
    }

    public Set<String> keySet() {
        return mSymbols.keySet();
    }

    public boolean containsKey(String key) {
        return mSymbols.containsKey(key);
    }

    public void dump() {
        for (String key : mSymbols.keySet()) {
            System.out.println(mSymbols.get(key));
        }
    }
}
