package com.github.ilms49898723.minttranslator.symbols;

import com.github.ilms49898723.minttranslator.symbols.info.Layer;

import java.util.List;

/**
 * Created by littlebird on 2017/07/15.
 */
public class Operator extends BaseSymbol {
    private String mMINT;
    private int mInputs;
    private int mOutputs;
    private List<Integer> mInputTerms;
    private List<Integer> mOutputTerms;

    public Operator(String identifier) {
        super(identifier, SymbolType.OPERATOR, -1, Layer.MODULE);
    }

    public void setMINT(String mint) {
        mMINT = mint;
    }

    public String getMINT(String name) {
        return mMINT.replaceAll("#NAME", name);
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
               ", mMINT='" + mMINT + '\'' +
               ", mInputs=" + mInputs +
               ", mOutputs=" + mOutputs +
               ", mInputTerms=" + mInputTerms +
               ", mOutputTerms=" + mOutputTerms +
               '}';
    }
}
