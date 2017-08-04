package com.github.ilms49898723.minttranslator.translator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class MINTOutputWriter {
    private Writer mWriter;
    private String mDeviceName;
    private List<String> mFlowBuffer;
    private List<String> mControlBuffer;

    public MINTOutputWriter(String filename) {
        try {
            mWriter = new BufferedWriter(new FileWriter(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mDeviceName = "device";
        mFlowBuffer = new ArrayList<>();
        mControlBuffer = new ArrayList<>();
    }

    public void writeFlow(String text) {
        mFlowBuffer.add(text);
    }

    public void writeFlow(List<String> text) {
        mFlowBuffer.addAll(text);
    }

    public void writeControl(String text) {
        mControlBuffer.add(text);
    }

    public void writeControl(List<String> text) {
        mControlBuffer.addAll(text);
    }

    public void flush() {
        try {
            mWriter.write("DEVICE " + mDeviceName + "\n\n");
            if (!mFlowBuffer.isEmpty()) {
                mWriter.write("LAYER FLOW\n\n");
                for (String text : mFlowBuffer) {
                    mWriter.write(text + "\n");
                }
                mWriter.write("\nEND LAYER\n\n");
            }
            if (!mControlBuffer.isEmpty()) {
                mWriter.write("LAYER CONTROL\n\n");
                for (String text : mControlBuffer) {
                    mWriter.write(text + "\n");
                }
                mWriter.write("\nEND LAYER\n\n");
            }
            mWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
