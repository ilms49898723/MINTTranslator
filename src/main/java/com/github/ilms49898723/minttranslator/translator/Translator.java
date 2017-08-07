package com.github.ilms49898723.minttranslator.translator;

import com.github.ilms49898723.minttranslator.antlr.LFRLexer;
import com.github.ilms49898723.minttranslator.antlr.LFRParser;
import com.github.ilms49898723.minttranslator.graph.DeviceGraph;
import com.github.ilms49898723.minttranslator.lfr.LFRProcessor;
import com.github.ilms49898723.minttranslator.ucf.UCFProcessor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by littlebird on 2017/07/16.
 */
public class Translator {
    private SymbolTable mSymbolTable;
    private MINTConfiguration mConfiguration;
    private MINTOutputWriter mWriter;
    private DeviceGraph mDeviceGraph;
    private Map<String, ModuleWriter> mModules;

    public Translator() {
        mConfiguration = new MINTConfiguration();
        mSymbolTable = new SymbolTable();
        mDeviceGraph = new DeviceGraph();
        mModules = new HashMap<>();
    }

    public void start(List<String> lfr, String ucf, String output, String name) {
        mWriter = new MINTOutputWriter(output);
        mWriter.setDeviceName(name);
        parseUCF(ucf);
        parseLFR(lfr);
        writeMINT();
    }

    private void parseUCF(String filename) {
        UCFProcessor ucfProcessor = new UCFProcessor(filename, mSymbolTable, mConfiguration);
        StatusCode status = ucfProcessor.parse();
        if (status != StatusCode.SUCCESS) {
            System.exit(1);
        }
    }

    private void parseLFR(List<String> filenames) {
        for (String filename : filenames) {
            InputStream input = null;
            try {
                input = new FileInputStream(filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (input == null) {
                System.exit(1);
            }
            LFRLexer lexer = null;
            try {
                lexer = new LFRLexer(CharStreams.fromStream(input));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (lexer == null) {
                System.exit(1);
            }
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LFRParser parser = new LFRParser(tokens);
            LFRParser.LfrContext context = parser.lfr();
            ParseTreeWalker walker = new ParseTreeWalker();
            LFRProcessor processor = new LFRProcessor();
            processor.setFilename(filename);
            processor.setSymbolTable(mSymbolTable);
            processor.setMINTConfiguration(mConfiguration);
            processor.setDeviceGraph(mDeviceGraph);
            processor.setModules(mModules);
            walker.walk(processor, context);
            if (processor.getFinalStatus() != StatusCode.SUCCESS) {
                System.exit(1);
            }
        }
    }

    private void writeMINT() {
        List<String> modulesList = mDeviceGraph.topologicalSort();
        Collections.reverse(modulesList);
        Map<String, Boolean> used = new HashMap<>();
        for (String module : modulesList) {
            used.put(module, false);
        }
        for (String module : modulesList) {
            if (!used.get(module)) {
                String flow = mModules.get(module).getFlowMINT(module);
                String control = mModules.get(module).getControlMINT(module);
                if (!flow.isEmpty()) {
                    mWriter.writeFlow(mModules.get(module).getFlowMINT(module));
                }
                if (!control.isEmpty()) {
                    mWriter.writeControl(mModules.get(module).getControlMINT(module));
                }
                searchAndMark(module, used);
            }
        }
        mWriter.flush();
    }

    private void searchAndMark(String current, Map<String, Boolean> used) {
        used.put(current, true);
        for (String outVertex : mDeviceGraph.getOutVertices(current)) {
            if (!used.get(outVertex)) {
                searchAndMark(outVertex, used);
            }
        }
    }
}
