package com.github.ilms49898723.minttranslator.symbols;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by littlebird on 2017/07/15.
 */
public class Component extends BaseSymbol {
    public enum Layer {
        FLOW, CONTROL
    }

    private List<Integer> mPorts;
    private String mMINTIdentifier;
    private int mPortCounter;
    private Layer mLayer;

    public Component(String identifier, Layer layer, int scope) {
        super(identifier, SymbolType.COMPONENT, scope);
        mLayer = layer;
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

    public List<Integer> getPorts() {
        return mPorts;
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

    public boolean isFlowComponent() {
        return mLayer == Layer.FLOW;
    }

    public boolean isControlComponent() {
        return mLayer == Layer.CONTROL;
    }
}
