package com.github.ilms49898723.minttranslator.translator;

import com.github.ilms49898723.minttranslator.symbols.BaseSymbol;
import com.github.ilms49898723.minttranslator.symbols.SymbolType;

import java.util.*;

import static com.github.ilms49898723.minttranslator.symbols.SymbolType.COMPONENT;

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

    public BaseSymbol get(String identifier, SymbolType symbolType) {
        BaseSymbol result = mSymbols.getOrDefault(identifier, null);
        return (result.getSymbolType() == symbolType) ? result : null;
    }

    public Set<String> keySet() {
        return mSymbols.keySet();
    }

    public boolean containsKey(String key) {
        return mSymbols.containsKey(key);
    }

    public void cleanup(int scope) {
        List<String> toDelete = new ArrayList<>();
        for (String key : mSymbols.keySet()) {
            if (mSymbols.get(key).getScope() > scope) {
                toDelete.add(key);
            }
        }
        for (String key : toDelete) {
            mSymbols.remove(key);
        }
    }

    public void dump() {
        for (String key : mSymbols.keySet()) {
            System.out.println(mSymbols.get(key));
        }
    }
}
