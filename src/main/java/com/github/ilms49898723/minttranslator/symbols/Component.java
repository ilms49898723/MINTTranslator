package com.github.ilms49898723.minttranslator.symbols;

import com.github.ilms49898723.minttranslator.symbols.info.Layer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by littlebird on 2017/07/15.
 */
public class Component extends BaseSymbol {
    private List<Integer> mPorts;
    private int mPortCounter;

    public Component(String identifier, Layer layer, int scope) {
        super(identifier, SymbolType.COMPONENT, scope, layer);
        mPorts = new ArrayList<>();
        mPortCounter = 0;
        for (int i = 1; i <= 4; ++i) {
            mPorts.add(i);
        }
    }

    public int nextInput() {
        if (mPortCounter >= mPorts.size()) {
            return -1;
        } else {
            return mPorts.get(mPortCounter++);
        }
    }

    public int nextOutput() {
        if (mPortCounter >= mPorts.size()) {
            return -1;
        } else {
            return mPorts.get(mPortCounter++);
        }
    }

    public String nextInputTerm() {
        int nextPort = nextInput();
        if (nextPort == -1) {
            return getErrorTerm();
        } else {
            return getMINTIdentifier() + " " + nextPort;
        }
    }

    public String nextOutputTerm() {
        int nextPort = nextOutput();
        if (nextPort == -1) {
            return getErrorTerm();
        } else {
            return getMINTIdentifier() + " " + nextPort;
        }
    }
}
