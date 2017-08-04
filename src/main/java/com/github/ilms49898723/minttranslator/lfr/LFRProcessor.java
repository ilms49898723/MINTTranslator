package com.github.ilms49898723.minttranslator.lfr;

import com.github.ilms49898723.minttranslator.antlr.LFRBaseListener;
import com.github.ilms49898723.minttranslator.antlr.LFRParser;
import com.github.ilms49898723.minttranslator.graph.DeviceGraph;
import com.github.ilms49898723.minttranslator.symbols.Component;
import com.github.ilms49898723.minttranslator.symbols.Module;
import com.github.ilms49898723.minttranslator.symbols.Operator;
import com.github.ilms49898723.minttranslator.symbols.SymbolType;
import com.github.ilms49898723.minttranslator.translator.*;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

/**
 * Created by littlebird on 2017/07/15.
 */
public class LFRProcessor extends LFRBaseListener {
    private SymbolTable mSymbolTable;
    private MINTConfiguration mConfiguration;
    private MINTOutputWriter mOutputWriter;
    private StatusCode mFinalStatus;
    private DeviceGraph mDeviceGraph;
    private ModuleNameGenerator mModuleNameGenerator;
    private ModuleWriter mModuleWriter;
    private String mModuleName;
    private String mExpr;
    private String mExprAssignTarget;
    private Map<String, ModuleWriter> mModules;
    private Stack<String> mExprStack;

    public LFRProcessor(SymbolTable symbolTable, MINTConfiguration configuration, MINTOutputWriter outputWriter) {
        mSymbolTable = symbolTable;
        mConfiguration = configuration;
        mOutputWriter = outputWriter;
        mFinalStatus = StatusCode.SUCCESS;
        mDeviceGraph = new DeviceGraph();
        mModules = new HashMap<>();
        mExprStack = new Stack<>();
    }

    public StatusCode getFinalStatus() {
        return mFinalStatus;
    }

    private void updateStatus(StatusCode code) {
        if (code != StatusCode.SUCCESS) {
            mFinalStatus = code;
        }
    }

    @Override
    public void exitLfr(LFRParser.LfrContext ctx) {
        System.out.println("FLOW");
        System.out.println(mModuleWriter.getFlowMINT());
        System.out.println("CONTROL");
        System.out.println(mModuleWriter.getControlMINT());
    }

    @Override
    public void enterVerilogModules(LFRParser.VerilogModulesContext ctx) {
        TerminalNode node = ctx.IDENTIFIER(0);
        String identifier = ctx.IDENTIFIER(0).getText();
        List<String> ports = new ArrayList<>();
        if (ctx.IDENTIFIER().size() > 1) {
            for (TerminalNode port : ctx.IDENTIFIER().subList(1, ctx.IDENTIFIER().size())) {
                ports.add(port.getSymbol().getText());
            }
        }
        mModuleName = identifier;
        mModuleNameGenerator = new ModuleNameGenerator(mConfiguration);
        mDeviceGraph.addVertex(identifier);
        mModuleWriter = new ModuleWriter();
        mModules.put(mModuleName, mModuleWriter);
        Module module = new Module(identifier, 0);
        module.addPorts(ports);
        StatusCode code = mSymbolTable.put(module);
        if (code != StatusCode.SUCCESS) {
            System.err.println("In LFR file");
            System.err.println("Error at line " + node.getSymbol().getLine() + ":");
            System.err.println("Invalid module identifier " + identifier);
            updateStatus(code);
        }
    }

    @Override
    public void exitVerilogModules(LFRParser.VerilogModulesContext ctx) {
        mSymbolTable.cleanup(0);
    }

    @Override
    public void enterFlowInputDecl(LFRParser.FlowInputDeclContext ctx) {
        for (TerminalNode node : ctx.IDENTIFIER()) {
            String identifier = node.getText();
            Component component = new Component(identifier, 1);
            StatusCode code = mSymbolTable.put(component);
            if (code != StatusCode.SUCCESS) {
                System.err.println("In LFR file");
                System.err.println("Error at line " + node.getSymbol().getLine() + ":");
                System.err.println("Invalid flow input identifier " + identifier);
                updateStatus(code);
            }
            mModuleWriter.write("PORT #NAME_" + identifier, ModuleWriter.Target.FLOW_INPUT);
            mModuleWriter.write("NODE #NAME_" + identifier, ModuleWriter.Target.FLOW_INPUT_NODE);
        }
    }

    @Override
    public void enterFlowOutputDecl(LFRParser.FlowOutputDeclContext ctx) {
        for (TerminalNode node : ctx.IDENTIFIER()) {
            String identifier = node.getText();
            Component component = new Component(identifier, 1);
            StatusCode code = mSymbolTable.put(component);
            if (code != StatusCode.SUCCESS) {
                System.err.println("In LFR file");
                System.err.println("Error at line " + node.getSymbol().getLine() + ":");
                System.err.println("Invalid flow output identifier " + identifier);
                updateStatus(code);
            }
            mModuleWriter.write("PORT #NAME_" + identifier, ModuleWriter.Target.FLOW_OUTPUT);
            mModuleWriter.write("NODE #NAME_" + identifier, ModuleWriter.Target.FLOW_OUTPUT_NODE);
        }
    }

    @Override
    public void enterControlInputDecl(LFRParser.ControlInputDeclContext ctx) {
        for (TerminalNode node : ctx.IDENTIFIER()) {
            String identifier = node.getText();
            Component component = new Component(identifier, 1);
            StatusCode code = mSymbolTable.put(component);
            if (code != StatusCode.SUCCESS) {
                System.err.println("In LFR file");
                System.err.println("Error at line " + node.getSymbol().getLine() + ":");
                System.err.println("Invalid control input identifier " + identifier);
                updateStatus(code);
            }
            mModuleWriter.write("PORT #NAME_" + identifier, ModuleWriter.Target.CONTROL_INPUT);
            mModuleWriter.write("NODE #NAME_" + identifier, ModuleWriter.Target.CONTROL_INPUT_NODE);
        }
    }

    @Override
    public void enterNodeDecl(LFRParser.NodeDeclContext ctx) {
        for (TerminalNode node : ctx.IDENTIFIER()) {
            String identifier = node.getText();
            Component component = new Component(identifier, 1);
            StatusCode code = mSymbolTable.put(component);
            if (code != StatusCode.SUCCESS) {
                System.err.println("In LFR file");
                System.err.println("Error at line " + node.getSymbol().getLine() + ":");
                System.err.println("Invalid node identifier " + identifier);
                updateStatus(code);
            }
            mModuleWriter.write("NODE #NAME_" + identifier, ModuleWriter.Target.FLOW_COMPONENT);
        }
    }

    @Override
    public void enterAssignStmt(LFRParser.AssignStmtContext ctx) {
        mExpr = ctx.expr().getText();
        String targetIdentifier = ctx.IDENTIFIER().getText();
        Component target = (Component) mSymbolTable.get(targetIdentifier, SymbolType.COMPONENT);
        if (target == null) {
            System.err.println("In LFR file");
            System.err.println("Undeclared identifier " + targetIdentifier);
            updateStatus(StatusCode.FAIL);
            return;
        }
        mExprAssignTarget = target.getIdentifier();
    }

    @Override
    public void exitAssignStmt(LFRParser.AssignStmtContext ctx) {
        Component target = (Component) mSymbolTable.get(ctx.IDENTIFIER().getText(), SymbolType.COMPONENT);
        if (target == null) {
            System.err.println("assign left value " + target.getIdentifier() + " is not defined.");
            return;
        }
        List<String> outputs = new ArrayList<>();
        while (!mExprStack.empty()) {
            outputs.add(mExprStack.pop());
        }
        Collections.reverse(outputs);
        if (outputs.size() != 1) {
            System.err.println("ERROR!!!!!");
            return;
        }
        String channel = "CHANNEL " + mModuleNameGenerator.nextChannel();
        channel += " from " + outputs.get(0);
        channel += " to " + target.getMINTIdentifier() + " " + target.nextInput();
        channel += " w=" + mConfiguration.get("channelWidth");
        mModuleWriter.write(channel, ModuleWriter.Target.FLOW_CHANNEL);
    }

    @Override
    public void exitExpr(LFRParser.ExprContext ctx) {
        if (ctx.OPERATOR() != null) {
            String b = mExprStack.pop();
            String a = mExprStack.pop();
            String op = ctx.OPERATOR().getText();
            Operator operator = (Operator) mSymbolTable.get(op, SymbolType.OPERATOR);
            String operatorComponent = mModuleNameGenerator.nextComponent();
            if (operator == null) {
                System.err.println("In LFR file");
                System.err.println("At line " + ctx.OPERATOR().getSymbol().getLine() + ":");
                System.err.println(op + " is not a valid operator.");
                updateStatus(StatusCode.FAIL);
                return;
            }
            if (operator.getInputs() != 2) {
                System.err.println("In LFR file");
                System.err.println("At line " + ctx.OPERATOR().getSymbol().getLine() + ":");
                System.err.println("Operator " + ctx.OPERATOR().getText() + " is not a binary operator.");
                updateStatus(StatusCode.FAIL);
            }
            mModuleWriter.write(operator.getMINT(operatorComponent), ModuleWriter.Target.FLOW_COMPONENT);
            String channel;
            channel = "CHANNEL " + mModuleNameGenerator.nextChannel();
            channel += " from " + a;
            channel += " to " + operatorComponent + " " + operator.getInputTerms().get(0);
            channel += " w=" + mConfiguration.get("channelWidth");
            mModuleWriter.write(channel, ModuleWriter.Target.FLOW_CHANNEL);
            channel = "CHANNEL " + mModuleNameGenerator.nextChannel();
            channel += " from " + b;
            channel += " to " + operatorComponent + " " + operator.getInputTerms().get(1);
            channel += " w=" + mConfiguration.get("channelWidth");
            mModuleWriter.write(channel, ModuleWriter.Target.FLOW_CHANNEL);
            for (int outputTerm : operator.getOutputTerms()) {
                mExprStack.push(operatorComponent + " " + outputTerm);
            }
        }
    }

    @Override
    public void exitPrimary(LFRParser.PrimaryContext ctx) {
        if (ctx.expr().isEmpty()) {
            Component component = (Component) mSymbolTable.get(ctx.IDENTIFIER().getText(), SymbolType.COMPONENT);
            if (component == null) {
                System.err.println("In LFR file");
                System.err.println("At line " + ctx.IDENTIFIER().getSymbol().getLine() + ":");
                System.err.println(ctx.IDENTIFIER().getText() + " is not a component.");
                updateStatus(StatusCode.FAIL);
                mExprStack.push(Component.getErrorPort());
                return;
            }
            int portNumber = component.nextOutput();
            if (portNumber == -1) {
                System.err.println("In LFR file");
                System.err.println("At line " + ctx.IDENTIFIER().getSymbol().getLine() + ":");
                System.err.println(ctx.IDENTIFIER().getText() + " has no port available.");
                updateStatus(StatusCode.FAIL);
                mExprStack.push(Component.getErrorPort());
                return;
            }
            String output = component.getMINTIdentifier() + " " + portNumber;
            mExprStack.push(output);
        } else {
            String operatorIdentifier = ctx.IDENTIFIER().getText();
            List<String> inputs = new ArrayList<>();
            for (int i = 0; i < ctx.expr().size(); ++i) {
                inputs.add(mExprStack.pop());
            }
            Collections.reverse(inputs);
            Operator operator = (Operator) mSymbolTable.get(operatorIdentifier, SymbolType.OPERATOR);
            if (operator == null) {
                System.err.println("In LFR file");
                System.err.println("At line " + ctx.IDENTIFIER().getSymbol().getLine() + ":");
                System.err.println(ctx.IDENTIFIER().getText() + " is not an operator.");
                updateStatus(StatusCode.FAIL);
                mExprStack.push(Component.getErrorPort());
                return;
            }
            if (operator.getInputs() != inputs.size()) {
                System.err.println("In LFR file");
                System.err.println("At line " + ctx.IDENTIFIER().getSymbol().getLine() + ":");
                System.err.println(ctx.IDENTIFIER().getText() + ": inputs not matched.");
                updateStatus(StatusCode.FAIL);
                mExprStack.push(Component.getErrorPort());
                return;
            }
            if (operator.getLayer().equals("flow")) {
                String operatorComponent = mModuleNameGenerator.nextComponent();
                mModuleWriter.write(operator.getMINT(operatorComponent), ModuleWriter.Target.FLOW_COMPONENT);
                for (int i = 0; i < inputs.size(); ++i) {
                    String input = inputs.get(i);
                    int operatorPortNumber = operator.getInputTerms().get(i);
                    String channelBuffer;
                    channelBuffer = "CHANNEL " + mModuleNameGenerator.nextChannel();
                    channelBuffer += " from " + input;
                    channelBuffer += " to " + operatorComponent + " " + operatorPortNumber;
                    channelBuffer += " w=" + mConfiguration.get("channelWidth");
                    mModuleWriter.write(channelBuffer, ModuleWriter.Target.FLOW_CHANNEL);
                }
                for (int outputTerm : operator.getOutputTerms()) {
                    mExprStack.push(operatorComponent + " " + outputTerm);
                }
            } else {
                // TODO add control layer
            }
        }
    }
}
