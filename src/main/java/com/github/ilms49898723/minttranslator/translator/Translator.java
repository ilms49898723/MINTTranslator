package com.github.ilms49898723.minttranslator.translator;

import com.github.ilms49898723.minttranslator.antlr.LFRLexer;
import com.github.ilms49898723.minttranslator.antlr.LFRParser;
import com.github.ilms49898723.minttranslator.lfr.LFRProcessor;
import com.github.ilms49898723.minttranslator.ucf.UCFProcessor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by littlebird on 2017/07/16.
 */
public class Translator {
    private MINTConfiguration mConfiguration;
    private SymbolTable mSymbolTable;

    public Translator() {
        mConfiguration = new MINTConfiguration();
        mSymbolTable = new SymbolTable();
    }

    public void start(String lfr, String ucf) {
        parseUCF(ucf);
        parseLFR(lfr);
    }

    private void parseUCF(String filename) {
        UCFProcessor ucfProcessor = new UCFProcessor(filename, mSymbolTable, mConfiguration);
        ucfProcessor.parse();
    }

    private void parseLFR(String filename) {
        InputStream input = null;
        try {
            input = new FileInputStream("test.v");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        LFRLexer lexer = null;
        try {
            lexer = new LFRLexer(CharStreams.fromStream(input));
        } catch (IOException e) {
            e.printStackTrace();
        }
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LFRParser parser = new LFRParser(tokens);
        LFRParser.LfrContext context = parser.lfr();
        ParseTreeWalker walker = new ParseTreeWalker();
        LFRProcessor processor = new LFRProcessor(mSymbolTable, mConfiguration);
        walker.walk(processor, context);
    }
}
