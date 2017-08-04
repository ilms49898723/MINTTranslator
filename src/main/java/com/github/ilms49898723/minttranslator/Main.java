package com.github.ilms49898723.minttranslator;

import com.github.ilms49898723.minttranslator.argparse.ArgumentParser;
import com.github.ilms49898723.minttranslator.translator.Translator;

/**
 * Created by littlebird on 2017/07/15.
 */
public class Main {
    public static void main(String[] args) {
        ArgumentParser argumentParser = new ArgumentParser(args);
        String lfr = argumentParser.getLFRInputPath();
        String ucf = argumentParser.getUCFInputPath();
        String mint = argumentParser.getOutputPath();
        Translator translator = new Translator();
        translator.start(lfr, ucf, mint);
    }
}
