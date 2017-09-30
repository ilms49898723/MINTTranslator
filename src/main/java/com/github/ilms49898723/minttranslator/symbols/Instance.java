package com.github.ilms49898723.minttranslator.symbols;

import com.github.ilms49898723.minttranslator.symbols.info.Layer;

public class Instance extends BaseSymbol {
    public Instance(String identifier, int scope) {
        super(identifier, SymbolType.INSTANCE, scope, Layer.MODULE);
    }
}
