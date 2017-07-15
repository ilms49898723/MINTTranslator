package com.github.ilms49898723.minttranslator.translator;

import com.github.ilms49898723.minttranslator.symbols.BaseSymbol;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by littlebird on 2017/07/15.
 */
public class SymbolTable {
    private Map<String, BaseSymbol> mSymbols;

    public SymbolTable() {
        mSymbols = new HashMap<>();
    }
}
