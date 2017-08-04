package com.github.ilms49898723.minttranslator.symbols;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by littlebird on 2017/07/15.
 */
public class Module extends BaseSymbol {
    private List<String> mInputs;
    private List<String> mOutputs;
    private List<String> mPorts;

    public Module(String identifier, int scope) {
        super(identifier, SymbolType.MODULE, scope);
        mInputs = new ArrayList<>();
        mOutputs = new ArrayList<>();
        mPorts = new ArrayList<>();
    }

    public void addInputs(List<String> inputs) {
        mInputs.addAll(inputs);
    }

    public void addOutputs(List<String> outputs) {
        mOutputs.addAll(outputs);
    }

    public void addPorts(List<String> ports) {
        mPorts = ports;
    }

    public List<String> getInputs() {
        return mInputs;
    }

    public List<String> getOutputs() {
        return mOutputs;
    }

    public List<String> getPorts() {
        return mPorts;
    }
}
