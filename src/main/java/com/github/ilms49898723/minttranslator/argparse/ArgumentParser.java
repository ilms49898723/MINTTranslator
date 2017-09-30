package com.github.ilms49898723.minttranslator.argparse;

import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.List;

public class ArgumentParser {
    private CommandLine mCommandLine;

    public ArgumentParser(String[] args) {
        Options options = new Options();
        Option lfrInput = new Option("l", "lfr", true, "LFR input file");
        Option ucfInput = new Option("u", "ucf", true, "UCF input file (default: mint.json)");
        Option output = new Option("o", "output", true, "MINT output file");
        Option deviceName = new Option("n", "name", true, "Device name, \'device\' if not given");
        lfrInput.setArgs(Option.UNLIMITED_VALUES);
        lfrInput.setRequired(true);
        ucfInput.setArgs(1);
        ucfInput.setRequired(false);
        output.setArgs(1);
        output.setRequired(true);
        deviceName.setArgs(1);
        deviceName.setRequired(false);
        options.addOption(lfrInput);
        options.addOption(ucfInput);
        options.addOption(output);
        options.addOption(deviceName);
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

    public List<String> getLFRInputPath() {
        return Arrays.asList(mCommandLine.getOptionValues("lfr"));
    }

    public String getUCFInputPath() {
        return mCommandLine.getOptionValue("ucf", "mint.json");
    }

    public String getOutputPath() {
        return mCommandLine.getOptionValue("output");
    }

    public String getDeviceName() {
        return mCommandLine.getOptionValue("name", "device");
    }
}
