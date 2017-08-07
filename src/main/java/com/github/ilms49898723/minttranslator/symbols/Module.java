package com.github.ilms49898723.minttranslator.symbols;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by littlebird on 2017/07/15.
 */
public class Module extends BaseSymbol {
    private List<String> mInputs;
    private List<String> mOutputs;
    private List<Integer> mInputTerms;
    private List<Integer> mOutputTerms;

    public Module(String identifier, int scope) {
        super(identifier, SymbolType.MODULE, scope);
        mInputs = new ArrayList<>();
        mOutputs = new ArrayList<>();
        mInputTerms = new ArrayList<>();
        mOutputTerms = new ArrayList<>();
    }

    public void addInput(String input) {
        mInputs.add(input);
    }

    public void addOutput(String output) {
        mOutputs.add(output);
    }

    public void addInputTerm(int term) {
        mInputTerms.add(term);
    }

    public void addOutputTerm(int term) {
        mOutputTerms.add(term);
    }

    public List<String> getInputs() {
        return mInputs;
    }

    public List<String> getOutputs() {
        return mOutputs;
    }

    public List<Integer> getInputTerms() {
        return mInputTerms;
    }

    public List<Integer> getOutputTerms() {
        return mOutputTerms;
    }

    public String getInputPortMINT(int index) {
        return "#NAME_" + mInputs.get(index) + " " + mInputTerms.get(index);
    }

    public String getOutputPortMINT(int index) {
        return "#NAME_" + mOutputs.get(index) + " " + mOutputTerms.get(index);
    }
}
