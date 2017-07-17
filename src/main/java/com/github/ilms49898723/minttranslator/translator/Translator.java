package com.github.ilms49898723.minttranslator.translator;

import com.github.ilms49898723.minttranslator.antlr.LFRLexer;
import com.github.ilms49898723.minttranslator.antlr.LFRParser;
import com.github.ilms49898723.minttranslator.lfr.LFRProcessor;
import com.github.ilms49898723.minttranslator.ucf.UCFProcessor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

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
        String input = "module Test(); assign b = a + c * (b~); endmodule";
        LFRLexer lexer = new LFRLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LFRParser parser = new LFRParser(tokens);
        LFRParser.LfrContext context = parser.lfr();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new LFRProcessor(), context);
    }
}
