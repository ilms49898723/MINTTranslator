package com.github.ilms49898723.minttranslator.symbols;

import java.util.List;

/**
 * Created by littlebird on 2017/07/15.
 */
public class Operator extends BaseSymbol {
    private String mName;
    private String mMINT;
    private String mLayer;
    private int mInputs;
    private int mOutputs;
    private int mControlInputTerm;
    private List<Integer> mInputTerms;
    private List<Integer> mOutputTerms;

    public Operator(String identifier) {
        super(identifier, SymbolType.OPERATOR, -1);
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setMINT(String mint) {
        mMINT = mint;
    }

    public String getMINT(String name) {
        return mMINT.replaceAll("#NAME", name);
    }

    public void setLayer(String layer) {
        mLayer = layer;
    }

    public String getLayer() {
        return mLayer;
    }

    public void setControlInputTerm(int term) {
        mControlInputTerm = term;
    }

    public int getControlInputTerm(int term) {
        return mControlInputTerm;
    }

    public void setInputs(int inputs) {
        mInputs = inputs;
    }

    public int getInputs() {
        return mInputs;
    }

    public void setOutputs(int outputs) {
        mOutputs = outputs;
    }

    public int getOutputs() {
        return mOutputs;
    }

    public void setInputTerms(List<Integer> inputTerms) {
        mInputTerms = inputTerms;
    }

    public List<Integer> getInputTerms() {
        return mInputTerms;
    }

    public void setOutputTerms(List<Integer> outputTerms) {
        mOutputTerms = outputTerms;
    }

    public List<Integer> getOutputTerms() {
        return mOutputTerms;
    }

    @Override
    public String toString() {
        return "Operator{" +
               "mName='" + mName + '\'' +
               ", mMINT='" + mMINT + '\'' +
               ", mLayer='" + mLayer + '\'' +
               ", mInputs=" + mInputs +
               ", mOutputs=" + mOutputs +
               ", mInputTerms=" + mInputTerms +
               ", mOutputTerms=" + mOutputTerms +
               '}';
    }
}
