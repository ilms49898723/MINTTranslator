package com.github.ilms49898723.minttranslator.translator;

public class ModuleNameGenerator {
    private MINTConfiguration mConfiguration;

    private int mComponentCounter;
    private int mChannelCounter;

    public ModuleNameGenerator(MINTConfiguration configuration) {
        mConfiguration = configuration;
        mComponentCounter = 0;
        mChannelCounter = 0;
    }

    public String nextComponent() {
        return "#NAME_cp" + (mComponentCounter++);
    }

    public String nextChannel() {
        return "#NAME_ch" + (mChannelCounter++);
    }
}
