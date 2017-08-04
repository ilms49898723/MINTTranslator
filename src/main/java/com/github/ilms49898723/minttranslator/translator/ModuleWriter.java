package com.github.ilms49898723.minttranslator.translator;

import java.util.ArrayList;
import java.util.List;

public class ModuleWriter {
    public enum Target {
        FLOW_INPUT,
        FLOW_OUTPUT,
        FLOW_INPUT_NODE,
        FLOW_OUTPUT_NODE,
        FLOW_COMPONENT,
        FLOW_CHANNEL,
        CONTROL_INPUT,
        CONTROL_INPUT_NODE,
        CONTROL_CHANNEL
    }

    private List<String> mFlowInputDecl;
    private List<String> mFlowOutputDecl;
    private List<String> mFlowInputNodeDecl;
    private List<String> mFlowOutputNodeDecl;
    private List<String> mFlowComponentDecl;
    private List<String> mFlowChannelDecl;
    private List<String> mControlInputDecl;
    private List<String> mControlInputNodeDecl;
    private List<String> mControlChannelDecl;

    public ModuleWriter() {
        mFlowInputDecl = new ArrayList<>();
        mFlowOutputDecl = new ArrayList<>();
        mFlowInputNodeDecl = new ArrayList<>();
        mFlowOutputNodeDecl = new ArrayList<>();
        mFlowComponentDecl = new ArrayList<>();
        mFlowChannelDecl = new ArrayList<>();
        mControlInputDecl = new ArrayList<>();
        mControlInputNodeDecl = new ArrayList<>();
        mControlChannelDecl = new ArrayList<>();
    }

    public void write(String data, Target target) {
        switch (target) {
            case FLOW_INPUT:
                mFlowInputDecl.add(data);
                break;
            case FLOW_OUTPUT:
                mFlowOutputDecl.add(data);
                break;
            case FLOW_INPUT_NODE:
                mFlowInputNodeDecl.add(data);
                break;
            case FLOW_OUTPUT_NODE:
                mFlowOutputNodeDecl.add(data);
                break;
            case FLOW_COMPONENT:
                mFlowComponentDecl.add(data);
                break;
            case FLOW_CHANNEL:
                mFlowChannelDecl.add(data);
                break;
            case CONTROL_INPUT:
                mControlInputDecl.add(data);
                break;
            case CONTROL_INPUT_NODE:
                mControlInputNodeDecl.add(data);
                break;
            case CONTROL_CHANNEL:
                mControlChannelDecl.add(data);
                break;
        }
    }

    public String getFlowMINT() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String mint : mFlowInputDecl) {
            stringBuilder.append(mint).append("\n");
        }
        for (String mint : mFlowOutputDecl) {
            stringBuilder.append(mint).append("\n");
        }
        for (String mint : mFlowComponentDecl) {
            stringBuilder.append(mint).append("\n");
        }
        for (String mint : mFlowChannelDecl) {
            stringBuilder.append(mint).append("\n");
        }
        return stringBuilder.toString();
    }

    public String getControlMINT() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String mint : mControlInputDecl) {
            stringBuilder.append(mint).append("\n");
        }
        for (String mint : mControlChannelDecl) {
            stringBuilder.append(mint).append("\n");
        }
        return stringBuilder.toString();
    }
}
