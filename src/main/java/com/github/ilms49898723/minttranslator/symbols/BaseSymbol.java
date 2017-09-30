package com.github.ilms49898723.minttranslator.symbols;

import com.github.ilms49898723.minttranslator.symbols.info.Layer;

/**
 * Created by littlebird on 2017/07/15.
 */
public abstract class BaseSymbol {
    private String mIdentifier;
    private String mMINTIdentifier;
    private SymbolType mSymbolType;
    private Layer mLayer;
    private int mScope;

    public BaseSymbol(String identifier, SymbolType symbolType, int scope, Layer layer) {
        mIdentifier = identifier;
        mMINTIdentifier = "#NAME_" + identifier;
        mSymbolType = symbolType;
        mScope = scope;
        mLayer = layer;
    }

    public void setLayer(String layer) {
        if (layer.equals("flow")) {
            mLayer = Layer.FLOW;
        } else {
            mLayer = Layer.CONTROL;
        }
    }

    public void setLayer(Layer layer) {
        mLayer = layer;
    }

    public Layer getLayer() {
        return mLayer;
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public String getMINTIdentifier() {
        return mMINTIdentifier;
    }

    public SymbolType getSymbolType() {
        return mSymbolType;
    }

    public int getScope() {
        return mScope;
    }

    public boolean isFlowComponent() {
        return getLayer() == Layer.FLOW;
    }

    public boolean isControlComponent() {
        return getLayer() == Layer.CONTROL;
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

    public static String getErrorTerm() {
        return "ERROR -1";
    }
}
