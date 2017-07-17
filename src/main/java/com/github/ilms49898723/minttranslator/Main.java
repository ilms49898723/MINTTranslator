package com.github.ilms49898723.minttranslator;

import com.github.ilms49898723.minttranslator.translator.Translator;

/**
 * Created by littlebird on 2017/07/15.
 */
public class Main {
    public static void main(String[] args) {

        Translator translator = new Translator();
        translator.start("lfr", "test.json");
    }
}
