package com.github.ilms49898723.minttranslator.argparse;

import org.apache.commons.cli.*;

public class ArgumentParser {
    private CommandLine mCommandLine;

    public ArgumentParser(String[] args) {
        Options options = new Options();
        Option lfrInput = new Option("l", "lfr", true, "LFR input file");
        Option ucfInput = new Option("u", "ucf", true, "UCF input file");
        Option output = new Option("o", "output", true, "MINT output file");
        lfrInput.setArgs(1);
        lfrInput.setRequired(true);
        ucfInput.setArgs(1);
        ucfInput.setRequired(true);
        output.setArgs(1);
        output.setRequired(true);
        options.addOption(lfrInput);
        options.addOption(ucfInput);
        options.addOption(output);
        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        try {
            mCommandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            helpFormatter.printHelp("MINTTranslator", options);
            System.exit(1);
        }
    }

    public String getLFRInputPath() {
        return mCommandLine.getOptionValue("lfr");
    }

    public String getUCFInputPath() {
        return mCommandLine.getOptionValue("ucf");
    }

    public String getOutputPath() {
        return mCommandLine.getOptionValue("output");
    }
}
