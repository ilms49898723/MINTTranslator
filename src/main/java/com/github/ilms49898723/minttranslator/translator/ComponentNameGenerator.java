package com.github.ilms49898723.minttranslator.translator;

public class ComponentNameGenerator {
    private int mComponentCounter;
    private int mChannelCounter;

    public ComponentNameGenerator() {
        mComponentCounter = 0;
        mChannelCounter = 0;
    }

    public String nextComponent(String type) {
        return "#NAME_" + type + "_cp" + (mComponentCounter++);
    }

    public String nextChannel() {
        return "#NAME_ch" + (mChannelCounter++);
    }
}
