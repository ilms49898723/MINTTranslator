package com.github.ilms49898723.minttranslator.symbols;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by littlebird on 2017/07/15.
 */
public class Component extends BaseSymbol {
    private List<Integer> mPorts;
    private String mMINTIdentifier;
    private int mPortCounter;

    public Component(String identifier, int scope) {
        super(identifier, SymbolType.COMPONENT, scope);
        mPorts = new ArrayList<>();
        mMINTIdentifier = "#NAME_" + identifier;
        mPortCounter = 0;
        for (int i = 1; i <= 4; ++i) {
            mPorts.add(i);
        }
    }

    public String getMINTIdentifier() {
        return mMINTIdentifier;
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

    public List<Integer> getPorts() {
        return mPorts;
    }

    public static String getErrorPort() {
        return "ERROR -1";
    }
}
