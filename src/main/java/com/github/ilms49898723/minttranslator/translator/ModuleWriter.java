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
        CONTROL_COMPONENT,
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
    private List<String> mControlComponentDecl;
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
        mControlComponentDecl = new ArrayList<>();
        mControlChannelDecl = new ArrayList<>();
    }

    public void write(String data, Target target) {
        if (data.isEmpty()) {
            return;
        }
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
            case CONTROL_COMPONENT:
                mControlComponentDecl.add(data);
                break;
            case CONTROL_CHANNEL:
                mControlChannelDecl.add(data);
                break;
        }
    }

    public void writeAll(List<String> data, Target target) {
        for (String mint : data) {
            write(mint, target);
        }
    }

    public List<String> getFlowMINT(String name) {
        List<String> result = new ArrayList<>();
        for (String mint : mFlowInputDecl) {
            result.add(mint.replaceAll("#NAME", name));
        }
        for (String mint : mFlowOutputDecl) {
            result.add(mint.replaceAll("#NAME", name));
        }
        for (String mint : mFlowComponentDecl) {
            result.add(mint.replaceAll("#NAME", name));
        }
        for (String mint : mFlowChannelDecl) {
            result.add(mint.replaceAll("#NAME", name));
        }
        return result;
    }

    public List<String> getModuleFlowMINT(String name) {
        List<String> result = new ArrayList<>();
        for (String mint : mFlowInputNodeDecl) {
            result.add(mint.replaceAll("#NAME", name));
        }
        for (String mint : mFlowOutputNodeDecl) {
            result.add(mint.replaceAll("#NAME", name));
        }
        for (String mint : mFlowComponentDecl) {
            result.add(mint.replaceAll("#NAME", name));
        }
        for (String mint : mFlowChannelDecl) {
            result.add(mint.replaceAll("#NAME", name));
        }
        return result;
    }

    public List<String> getControlMINT(String name) {
        List<String> result = new ArrayList<>();
        for (String mint : mControlInputDecl) {
            result.add(mint.replaceAll("#NAME", name));
        }
        for (String mint : mControlComponentDecl) {
            result.add(mint.replaceAll("#NAME", name));
        }
        for (String mint : mControlChannelDecl) {
            result.add(mint.replaceAll("#NAME", name));
        }
        return result;
    }
}
