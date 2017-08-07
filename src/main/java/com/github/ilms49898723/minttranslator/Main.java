package com.github.ilms49898723.minttranslator;

import com.github.ilms49898723.minttranslator.argparse.ArgumentParser;
import com.github.ilms49898723.minttranslator.translator.Translator;

import java.util.List;

/**
 * Created by littlebird on 2017/07/15.
 */
public class Main {
    public static void main(String[] args) {
        ArgumentParser argumentParser = new ArgumentParser(args);
        List<String> lfr = argumentParser.getLFRInputPath();
        String ucf = argumentParser.getUCFInputPath();
        String mint = argumentParser.getOutputPath();
        String name = argumentParser.getDeviceName();
        Translator translator = new Translator();
        translator.start(lfr, ucf, mint, name);
    }
}
