package com.github.ilms49898723.minttranslator.lfr;

import com.github.ilms49898723.minttranslator.antlr.LFRBaseListener;
import com.github.ilms49898723.minttranslator.antlr.LFRParser;
import com.github.ilms49898723.minttranslator.graph.DeviceGraph;
import com.github.ilms49898723.minttranslator.symbols.*;
import com.github.ilms49898723.minttranslator.translator.*;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

/**
 * Created by littlebird on 2017/07/15.
 */
public class LFRProcessor extends LFRBaseListener {
    private SymbolTable mSymbolTable;
    private MINTConfiguration mConfiguration;
    private StatusCode mFinalStatus;
    private DeviceGraph mDeviceGraph;
    private ModuleNameGenerator mModuleNameGenerator;
    private ModuleWriter mModuleWriter;
    private Module mModule;
    private String mFilename;
    private Map<String, ModuleWriter> mModules;
    private Stack<String> mExprStack;

    public LFRProcessor() {
        mFinalStatus = StatusCode.SUCCESS;
        mExprStack = new Stack<>();
    }

    public void setFilename(String filename) {
        mFilename = filename;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        mSymbolTable = symbolTable;
    }

    public void setMINTConfiguration(MINTConfiguration configuration) {
        mConfiguration = configuration;
    }

    public void setDeviceGraph(DeviceGraph deviceGraph) {
        mDeviceGraph = deviceGraph;
    }

    public void setModules(Map<String, ModuleWriter> modules) {
        mModules = modules;
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
    public void enterVerilogModules(LFRParser.VerilogModulesContext ctx) {
        String identifier = ctx.IDENTIFIER().getText();
        mModuleNameGenerator = new ModuleNameGenerator(mConfiguration);
        mDeviceGraph.addVertex(identifier);
        mModuleWriter = new ModuleWriter();
        mModules.put(identifier, mModuleWriter);
        mModule = new Module(identifier, 0);
        StatusCode code = mSymbolTable.put(mModule);
        if (code != StatusCode.SUCCESS) {
            System.err.println("In file " + mFilename);
            System.err.println("Error at line " + ctx.IDENTIFIER().getSymbol().getLine() + ":");
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
                System.err.println("In file " + mFilename);
                System.err.println("Error at line " + node.getSymbol().getLine() + ":");
                System.err.println("Invalid flow input identifier " + identifier);
                updateStatus(code);
            }
            mModule.addInput(identifier);
            mModule.addInputTerm(component.nextInput());
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
                System.err.println("In file " + mFilename);
                System.err.println("Error at line " + node.getSymbol().getLine() + ":");
                System.err.println("Invalid flow output identifier " + identifier);
                updateStatus(code);
            }
            mModule.addOutput(identifier);
            mModule.addOutputTerm(component.nextOutput());
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
                System.err.println("In file " + mFilename);
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
                System.err.println("In file " + mFilename);
                System.err.println("Error at line " + node.getSymbol().getLine() + ":");
                System.err.println("Invalid node identifier " + identifier);
                updateStatus(code);
            }
            mModuleWriter.write("NODE #NAME_" + identifier, ModuleWriter.Target.FLOW_COMPONENT);
        }
    }

    @Override
    public void enterAssignStmt(LFRParser.AssignStmtContext ctx) {
        for (TerminalNode node : ctx.IDENTIFIER()) {
            String targetIdentifier = node.getText();
            Component target = (Component) mSymbolTable.get(targetIdentifier, SymbolType.COMPONENT);
            if (target == null) {
                System.err.println("In file " + mFilename);
                System.err.println("Undeclared identifier " + targetIdentifier);
                updateStatus(StatusCode.FAIL);
            }
        }
        mExprStack = new Stack<>();
    }

    @Override
    public void exitAssignStmt(LFRParser.AssignStmtContext ctx) {
        if (mFinalStatus != StatusCode.SUCCESS) {
            return;
        }
        List<String> assignTargets = new ArrayList<>();
        for (TerminalNode node : ctx.IDENTIFIER()) {
            Component target = (Component) mSymbolTable.get(node.getText(), SymbolType.COMPONENT);
            int port = target.nextOutput();
            if (port == -1) {
                System.err.println("In file " + mFilename);
                System.err.println("At line " + node.getSymbol().getLine() + ":");
                System.err.println(node.getText() + " has no port available.");
                updateStatus(StatusCode.FAIL);
            }
            assignTargets.add(target.getMINTIdentifier() + " " + port);
        }
        List<String> exprOutputs = new ArrayList<>();
        while (!mExprStack.empty()) {
            exprOutputs.add(mExprStack.pop());
        }
        Collections.reverse(exprOutputs);
        if (exprOutputs.size() == 1) {
            StringBuilder channel = new StringBuilder();
            channel.append("CHANNEL ").append(mModuleNameGenerator.nextChannel());
            channel.append(" from ").append(exprOutputs.get(0)).append(" to ");
            boolean isFirst = true;
            for (String output : assignTargets) {
                if (!isFirst) {
                    channel.append(", ");
                } else {
                    isFirst = false;
                }
                channel.append(output);
            }
            channel.append(" w=").append(mConfiguration.get("channelWidth"));
            mModuleWriter.write(channel.toString(), ModuleWriter.Target.FLOW_CHANNEL);
        } else {
            if (exprOutputs.size() != assignTargets.size()) {
                System.err.println("In file " + mFilename);
                System.err.println("At line " + ctx.IDENTIFIER(0).getSymbol().getLine() + ":");
                System.err.println("Number of ports differs.");
                System.err.println("Left: " + assignTargets.size());
                System.err.println("Right: " + exprOutputs.size());
                updateStatus(StatusCode.FAIL);
                return;
            }
            for (int i = 0; i < assignTargets.size(); ++i) {
                String channel = "CHANNEL " + mModuleNameGenerator.nextChannel();
                channel += " from " + exprOutputs.get(i);
                channel += " to " + assignTargets.get(i);
                channel += " w=" + mConfiguration.get("channelWidth");
                mModuleWriter.write(channel, ModuleWriter.Target.FLOW_CHANNEL);
            }
        }
    }

    @Override
    public void exitInstanceStmt(LFRParser.InstanceStmtContext ctx) {
        String moduleName = ctx.IDENTIFIER(0).getText();
        String instanceName = ctx.IDENTIFIER(1).getText();
        Module module = (Module) mSymbolTable.get(moduleName, SymbolType.MODULE);
        if (module == null) {
            System.err.println("In file " + mFilename);
            System.err.println("At line " + ctx.IDENTIFIER(0).getSymbol().getLine() + ":");
            System.err.println("Module " + moduleName + " is not defined.");
            updateStatus(StatusCode.FAIL);
            return;
        }
        Instance instance = new Instance(instanceName, 1);
        StatusCode code = mSymbolTable.put(instance);
        if (code != StatusCode.SUCCESS) {
            System.err.println("In file " + mFilename);
            System.err.println("Error at line " + ctx.IDENTIFIER(0).getSymbol().getLine() + ":");
            System.err.println("Invalid instance name " + instanceName);
            updateStatus(code);
            return;
        }
        List<String> modulePorts = new ArrayList<>();
        List<String> wires = new ArrayList<>();
        for (int i = 2; i < ctx.IDENTIFIER().size(); i += 2) {
            modulePorts.add(ctx.IDENTIFIER(i).getText());
            wires.add(ctx.IDENTIFIER(i + 1).getText());
        }
        for (int i = 0; i < modulePorts.size(); ++i) {
            if (!module.getInputs().contains(modulePorts.get(i)) &&
                    !module.getOutputs().contains(modulePorts.get(i))) {
                System.err.println("In file " + mFilename);
                System.err.println("Error at line " + ctx.IDENTIFIER(0).getSymbol().getLine() + ":");
                System.err.println("Module " + moduleName + " has no port named " + modulePorts.get(i));
                updateStatus(StatusCode.FAIL);
                return;
            }
            if (!mSymbolTable.containsKey(wires.get(i))) {
                System.err.println("In file " + mFilename);
                System.err.println("Error at line " + ctx.IDENTIFIER(0).getSymbol().getLine() + ":");
                System.err.println(wires.get(i) + " is not declared.");
                updateStatus(StatusCode.FAIL);
                return;
            }
            Component component = (Component) mSymbolTable.get(wires.get(i), SymbolType.COMPONENT);
            int port = component.nextOutput();
            if (port == -1) {
                System.err.println("In file " + mFilename);
                System.err.println("At line " + ctx.IDENTIFIER(0).getSymbol().getLine() + ":");
                System.err.println(wires.get(i) + " has no port available.");
                updateStatus(StatusCode.FAIL);
                return;
            }
            String channel = "CHANNEL " + mModuleNameGenerator.nextChannel();
            if (module.getInputs().contains(modulePorts.get(i))) {
                int portIndex = module.getInputs().indexOf(modulePorts.get(i));
                channel += " from " + component.getMINTIdentifier() + " " + port;
                channel += " to " + module.getInputPortMINT(portIndex).replaceAll("#NAME", instanceName);
            } else {
                int portIndex = module.getOutputs().indexOf(modulePorts.get(i));
                channel += " from " + module.getOutputPortMINT(portIndex).replaceAll("#NAME", instanceName);
                channel += " to " + component.getMINTIdentifier() + " " + port;
            }
            channel += " w=" + mConfiguration.get("channelWidth");
            mModuleWriter.write(channel, ModuleWriter.Target.FLOW_CHANNEL);
        }
        String moduleFlow = mModules.get(moduleName).getModuleFlowMINT(instanceName);
        String moduleControl = mModules.get(moduleName).getControlMINT(instanceName);
        if (!moduleFlow.isEmpty()) {
            mModuleWriter.write(moduleFlow, ModuleWriter.Target.FLOW_CHANNEL);
        }
        if (!moduleControl.isEmpty()) {
            mModuleWriter.write(moduleControl, ModuleWriter.Target.FLOW_CHANNEL);
        }
        mDeviceGraph.addEdge(mModule.getIdentifier(), moduleName);
    }

    @Override
    public void exitValveStmt(LFRParser.ValveStmtContext ctx) {
        String valveIdentifier = ctx.IDENTIFIER(0).getText();
        Component valveComponent = new Component(valveIdentifier, 1);
        StatusCode status = mSymbolTable.put(valveComponent);
        if (status != StatusCode.SUCCESS) {
            System.err.println("In file " + mFilename);
            System.err.println("At line " + ctx.IDENTIFIER(0).getSymbol().getLine() + ":");
            System.err.println("Invalid identifier " + valveIdentifier);
            updateStatus(StatusCode.FAIL);
            return;
        }
        Component start = (Component) mSymbolTable.get(ctx.IDENTIFIER(1).getText(), SymbolType.COMPONENT);
        if (start == null) {
            System.err.println("In file " + mFilename);
            System.err.println("At line " + ctx.IDENTIFIER(1).getSymbol().getLine() + ":");
            System.err.println(ctx.IDENTIFIER(1).getText() + " is not defined.");
            updateStatus(StatusCode.FAIL);
            return;
        }
        Component end = (Component) mSymbolTable.get(ctx.IDENTIFIER(2).getText(), SymbolType.COMPONENT);
        if (end == null) {
            System.err.println("In file " + mFilename);
            System.err.println("At line " + ctx.IDENTIFIER(2).getSymbol().getLine() + ":");
            System.err.println(ctx.IDENTIFIER(1).getText() + " is not defined.");
            updateStatus(StatusCode.FAIL);
            return;
        }
        Component ctl = (Component) mSymbolTable.get(ctx.IDENTIFIER(3).getText(), SymbolType.COMPONENT);
        if (ctl == null) {
            System.err.println("In file " + mFilename);
            System.err.println("At line " + ctx.IDENTIFIER(3).getSymbol().getLine() + ":");
            System.err.println(ctx.IDENTIFIER(3).getText() + " is not defined.");
            updateStatus(StatusCode.FAIL);
            return;
        }
        int startPort = start.nextOutput();
        if (startPort == -1) {
            System.err.println("In file " + mFilename);
            System.err.println("At line " + ctx.IDENTIFIER(1).getSymbol().getLine() + ":");
            System.err.println(ctx.IDENTIFIER(1).getText() + " has no ports available.");
            updateStatus(StatusCode.FAIL);
            return;
        }
        int endPort = end.nextOutput();
        if (endPort == -1) {
            System.err.println("In file " + mFilename);
            System.err.println("At line " + ctx.IDENTIFIER(2).getSymbol().getLine() + ":");
            System.err.println(ctx.IDENTIFIER(2).getText() + " has no ports available.");
            updateStatus(StatusCode.FAIL);
            return;
        }
        int ctlPort = ctl.nextOutput();
        if (ctlPort == -1) {
            System.err.println("In file " + mFilename);
            System.err.println("At line " + ctx.IDENTIFIER(3).getSymbol().getLine() + ":");
            System.err.println(ctx.IDENTIFIER(2).getText() + " has no ports available.");
            updateStatus(StatusCode.FAIL);
            return;
        }
        String channelId = mModuleNameGenerator.nextChannel();
        String channel = "CHANNEL " + channelId;
        channel += " from " + start.getMINTIdentifier() + " " + startPort;
        channel += " to " + end.getMINTIdentifier() + " " + endPort;
        channel += " w=" + mConfiguration.get("channelWidth");
        mModuleWriter.write(channel, ModuleWriter.Target.FLOW_CHANNEL);
        String valve = "VALVE " + valveIdentifier + " ON " + channelId;
        valve += " w=1500 h=750";
        mModuleWriter.write(valve, ModuleWriter.Target.CONTROL_COMPONENT);
        String ctlChannel = "CHANNEL " + mModuleNameGenerator.nextChannel();
        ctlChannel += " from " + valveIdentifier + " ? ";
        ctlChannel += " to " + ctl.getMINTIdentifier() + " " + ctlPort;
        ctlChannel += " w=" + mConfiguration.get("channelWidth");
        mModuleWriter.write(ctlChannel, ModuleWriter.Target.CONTROL_CHANNEL);
    }

    @Override
    public void exitExpr(LFRParser.ExprContext ctx) {
        if (ctx.OPERATOR() == null) {
            return;
        }
        String op = ctx.OPERATOR().getText();
        Operator operator = (Operator) mSymbolTable.get(op, SymbolType.OPERATOR);
        String operatorComponent = mModuleNameGenerator.nextComponent();
        if (operator == null) {
            System.err.println("In LFR file");
            System.err.println("At line " + ctx.OPERATOR().getSymbol().getLine() + ":");
            System.err.println(op + " is not a valid operator.");
            updateStatus(StatusCode.FAIL);
            mExprStack.push(BaseSymbol.getErrorTerm());
            return;
        }
        if (operator.getInputs() != 1 && operator.getInputs() != 2) {
            System.err.println("In LFR file");
            System.err.println("At line " + ctx.OPERATOR().getSymbol().getLine() + ":");
            System.err.println("Operator " + ctx.OPERATOR().getText() + " is neither a unary nor binary operator.");
            updateStatus(StatusCode.FAIL);
            mExprStack.push(BaseSymbol.getErrorTerm());
            return;
        }
        if (operator.getInputs() == 2) {
            String b = mExprStack.pop();
            String a = mExprStack.pop();
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
        } else {
            String a = mExprStack.pop();
            mModuleWriter.write(operator.getMINT(operatorComponent), ModuleWriter.Target.FLOW_COMPONENT);
            String channel;
            channel = "CHANNEL " + mModuleNameGenerator.nextChannel();
            channel += " from " + a;
            channel += " to " + operatorComponent + " " + operator.getInputTerms().get(0);
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
                mExprStack.push(Component.getErrorTerm());
                return;
            }
            int portNumber = component.nextOutput();
            if (portNumber == -1) {
                System.err.println("In LFR file");
                System.err.println("At line " + ctx.IDENTIFIER().getSymbol().getLine() + ":");
                System.err.println(ctx.IDENTIFIER().getText() + " has no port available.");
                updateStatus(StatusCode.FAIL);
                mExprStack.push(Component.getErrorTerm());
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
                mExprStack.push(Component.getErrorTerm());
                return;
            }
            if (operator.getInputs() != inputs.size()) {
                System.err.println("In LFR file");
                System.err.println("At line " + ctx.IDENTIFIER().getSymbol().getLine() + ":");
                System.err.println(ctx.IDENTIFIER().getText() + ": inputs not matched.");
                updateStatus(StatusCode.FAIL);
                mExprStack.push(Component.getErrorTerm());
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
                String operatorComponent = mModuleNameGenerator.nextComponent();
                mModuleWriter.write(operator.getMINT(operatorComponent), ModuleWriter.Target.CONTROL_COMPONENT);
                for (int i = 0; i < inputs.size(); ++i) {
                    String input = inputs.get(i);
                }
            }
        }
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        super.visitErrorNode(node);
        System.exit(1);
    }
}
