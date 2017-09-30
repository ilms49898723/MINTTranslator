package com.github.ilms49898723.minttranslator.translator;

import java.util.Set;

/**
 * MINT Configuration.
 */
public class MINTConfiguration {
    private int mDefaultPortRadius;
    private int mDefaultChannelWidth;
    private int mDefaultValveWidth;
    private int mDefaultValveLength;

    public MINTConfiguration() {
        mDefaultPortRadius = 20;
        mDefaultChannelWidth = 10;
        mDefaultValveWidth = 15;
        mDefaultValveLength = 30;
    }

    public StatusCode put(String key, int value) {
        switch (key) {
            case "portRadius":
                mDefaultPortRadius = value;
                break;
            case "channelWidth":
                mDefaultChannelWidth = value;
                break;
            case "valveWidth":
                mDefaultValveWidth = value;
                break;
            case "valveLength":
                mDefaultValveLength = value;
                break;
            default:
                return StatusCode.FAIL;
        }
        return StatusCode.SUCCESS;
    }

    public int getDefaultPortRadius() {
        return mDefaultPortRadius;
    }

    public int getDefaultChannelWidth() {
        return mDefaultChannelWidth;
    }

    public int getDefaultValveWidth() {
        return mDefaultValveWidth;
    }

    public int getDefaultValveLength() {
        return mDefaultValveLength;
    }
}
