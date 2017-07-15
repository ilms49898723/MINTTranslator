package com.github.ilms49898723.minttranslator;

import com.github.ilms49898723.minttranslator.antlr.LFRLexer;
import com.github.ilms49898723.minttranslator.antlr.LFRParser;
import com.github.ilms49898723.minttranslator.lfr.LFRProcessor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * Created by littlebird on 2017/07/15.
 */
public class Main {
    public static void main(String[] args) {
        String input = "module Test(); assign b = a + c * (b~); endmodule";
        LFRLexer lexer = new LFRLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LFRParser parser = new LFRParser(tokens);
        LFRParser.LfrContext context = parser.lfr();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new LFRProcessor(), context);
    }
}
