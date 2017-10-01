package com.github.ilms49898723.minttranslator.lfr;

import com.github.ilms49898723.minttranslator.antlr.LFRBaseListener;
import com.github.ilms49898723.minttranslator.antlr.LFRParser;
import com.github.ilms49898723.minttranslator.errorhandling.ErrorCode;
import com.github.ilms49898723.minttranslator.errorhandling.ErrorHandler;
import com.github.ilms49898723.minttranslator.graph.DeviceGraph;
import com.github.ilms49898723.minttranslator.symbols.*;
import com.github.ilms49898723.minttranslator.symbols.info.Layer;
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
    private ComponentNameGenerator mComponentNameGenerator;
    private ModuleWriter mModuleWriter;
    private Module mModule;
    private String mFilename;
    private Map<String, ModuleWriter> mModules;
    private Map<String, String> mValveControllers;
    private Stack<String> mExprStack;

    public LFRProcessor() {
        mFinalStatus = StatusCode.SUCCESS;
        mExprStack = new Stack<>();
        mValveControllers = new HashMap<>();
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
        String identifier = ctx.IDENTIFIER(0).getText();
        mComponentNameGenerator = new ComponentNameGenerator();
        mDeviceGraph.addVertex(identifier);
        mModuleWriter = new ModuleWriter();
        mModules.put(identifier, mModuleWriter);
        mModule = new Module(identifier, 0);
        mValveControllers = new HashMap<>();
        StatusCode code = mSymbolTable.put(mModule);
        if (code != StatusCode.SUCCESS) {
            ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER(0), ErrorCode.INVALID_IDENTIFIER);
            updateStatus(code);
        }
    }

    @Override
    public void exitVerilogModules(LFRParser.VerilogModulesContext ctx) {
        Set<String> portIdentifiers = new HashSet<>();
        for (int i = 1; i < ctx.IDENTIFIER().size(); ++i) {
            portIdentifiers.add(ctx.IDENTIFIER(i).getText());
        }
        Set<String> modulePorts = new HashSet<>();
        modulePorts.addAll(mModule.getInputs());
        modulePorts.addAll(mModule.getOutputs());
        if (!portIdentifiers.equals(modulePorts)) {
            ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER(0), ErrorCode.PORT_NOT_LISTED);
        }
        mSymbolTable.cleanup(0);
    }

    @Override
    public void exitFlowInputDecl(LFRParser.FlowInputDeclContext ctx) {
        for (TerminalNode node : ctx.IDENTIFIER()) {
            String identifier = node.getText();
            Component component = new Component(identifier, Layer.FLOW, 1);
            StatusCode code = mSymbolTable.put(component);
            if (code != StatusCode.SUCCESS) {
                ErrorHandler.printErrorMessage(mFilename, node, ErrorCode.INVALID_IDENTIFIER);
                updateStatus(code);
            }
            mModule.addInput(identifier);
            mModule.addInputTerm(component.nextInput());
            mModuleWriter.write("PORT #NAME_" + identifier + " r=" + mConfiguration.getDefaultPortRadius(), ModuleWriter.Target.FLOW_INPUT);
            mModuleWriter.write("NODE #NAME_" + identifier, ModuleWriter.Target.FLOW_INPUT_NODE);
        }
    }

    @Override
    public void exitFlowOutputDecl(LFRParser.FlowOutputDeclContext ctx) {
        for (TerminalNode node : ctx.IDENTIFIER()) {
            String identifier = node.getText();
            Component component = new Component(identifier, Layer.FLOW, 1);
            StatusCode code = mSymbolTable.put(component);
            if (code != StatusCode.SUCCESS) {
                ErrorHandler.printErrorMessage(mFilename, node, ErrorCode.INVALID_IDENTIFIER);
                updateStatus(code);
            }
            mModule.addOutput(identifier);
            mModule.addOutputTerm(component.nextOutput());
            mModuleWriter.write("PORT #NAME_" + identifier + " r=" + mConfiguration.getDefaultPortRadius(), ModuleWriter.Target.FLOW_OUTPUT);
            mModuleWriter.write("NODE #NAME_" + identifier, ModuleWriter.Target.FLOW_OUTPUT_NODE);
        }
    }

    @Override
    public void exitControlInputDecl(LFRParser.ControlInputDeclContext ctx) {
        for (TerminalNode node : ctx.IDENTIFIER()) {
            String identifier = node.getText();
            Component component = new Component(identifier, Layer.CONTROL, 1);
            StatusCode code = mSymbolTable.put(component);
            if (code != StatusCode.SUCCESS) {
                ErrorHandler.printErrorMessage(mFilename, node, ErrorCode.INVALID_IDENTIFIER);
                updateStatus(code);
            }
            mValveControllers.put(identifier, component.getMINTIdentifier() + " " + component.nextOutput());
            mModuleWriter.write("PORT #NAME_" + identifier + " r=" + mConfiguration.getDefaultPortRadius(), ModuleWriter.Target.CONTROL_INPUT);
            mModuleWriter.write("NODE #NAME_" + identifier, ModuleWriter.Target.CONTROL_INPUT_NODE);
        }
    }

    @Override
    public void enterNodeDecl(LFRParser.NodeDeclContext ctx) {
        for (TerminalNode node : ctx.IDENTIFIER()) {
            String identifier = node.getText();
            Component component = new Component(identifier, Layer.FLOW, 1);
            StatusCode code = mSymbolTable.put(component);
            if (code != StatusCode.SUCCESS) {
                ErrorHandler.printErrorMessage(mFilename, node, ErrorCode.INVALID_IDENTIFIER);
                updateStatus(code);
            }
            mModuleWriter.write("NODE #NAME_" + identifier, ModuleWriter.Target.FLOW_COMPONENT);
        }
    }

    @Override
    public void enterAssignStmt(LFRParser.AssignStmtContext ctx) {
        for (LFRParser.AssignTargetContext targetContext : ctx.assignTarget()) {
            String targetIdentifier = targetContext.IDENTIFIER().getText();
            Component target = (Component) mSymbolTable.get(targetIdentifier, SymbolType.COMPONENT);
            if (target == null) {
                ErrorHandler.printErrorMessage(mFilename, targetContext.IDENTIFIER(), ErrorCode.UNDEFINED_SYMBOL);
                updateStatus(StatusCode.FAIL);
                continue;
            }
            if (!target.isFlowComponent()) {
                ErrorHandler.printErrorMessage(mFilename, targetContext.IDENTIFIER(), ErrorCode.LAYER_ERROR_FLOW);
                updateStatus(StatusCode.FAIL);
            }
            if (targetContext.valvePhase().IDENTIFIER() != null) {
                TerminalNode ctlNode = targetContext.valvePhase().IDENTIFIER();
                if (!mValveControllers.containsKey(ctlNode.getText())) {
                    ErrorHandler.printErrorMessage(mFilename, ctlNode, ErrorCode.UNDEFINED_SYMBOL);
                    updateStatus(StatusCode.FAIL);
                }
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
        List<String> assignValves = new ArrayList<>();
        for (LFRParser.AssignTargetContext targetContext : ctx.assignTarget()) {
            TerminalNode targetNode = targetContext.IDENTIFIER();
            Component target = (Component) mSymbolTable.get(targetNode.getText(), SymbolType.COMPONENT);
            int port = target.nextInput();
            if (port == -1) {
                ErrorHandler.printErrorMessage(mFilename, targetNode, ErrorCode.NO_VALID_PORTS);
                updateStatus(StatusCode.FAIL);
            }
            assignTargets.add(target.getMINTIdentifier() + " " + port);
            if (targetContext.valvePhase().IDENTIFIER() != null) {
                String targetId = targetContext.valvePhase().IDENTIFIER().getText();
                assignValves.add(targetId);
            } else {
                assignValves.add(null);
            }
        }
        List<String> exprOutputs = new ArrayList<>();
        while (!mExprStack.empty()) {
            exprOutputs.add(mExprStack.pop());
        }
        Collections.reverse(exprOutputs);
        if (exprOutputs.size() != assignTargets.size()) {
            ErrorHandler.printErrorMessage(mFilename, ctx.assignTarget(0).IDENTIFIER(), ErrorCode.ASSIGN_PORTS_NOT_MATCH);
            updateStatus(StatusCode.FAIL);
            return;
        }
        for (int i = 0; i < assignTargets.size(); ++i) {
            String channelId = mComponentNameGenerator.nextChannel();
            String channel = "CHANNEL " + channelId;
            channel += " from " + exprOutputs.get(i);
            channel += " to " + assignTargets.get(i);
            channel += " w=" + mConfiguration.getDefaultChannelWidth();
            mModuleWriter.write(channel, ModuleWriter.Target.FLOW_CHANNEL);
            if (assignValves.get(i) != null) {
                String valveIdentifier = mComponentNameGenerator.nextComponent("valve");
                String valve = "VALVE " + valveIdentifier + " on " + channelId;
                valve += " w=" + mConfiguration.getDefaultValveWidth() + " l=" + mConfiguration.getDefaultValveLength();
                mModuleWriter.write(valve, ModuleWriter.Target.CONTROL_COMPONENT);
                String ctlChannel = "CHANNEL " + mComponentNameGenerator.nextChannel();
                ctlChannel += " from " + valveIdentifier + " 1";
                ctlChannel += " to " + mValveControllers.get(assignValves.get(i));
                ctlChannel += " w=" + mConfiguration.getDefaultChannelWidth();
                mModuleWriter.write(ctlChannel, ModuleWriter.Target.CONTROL_CHANNEL);
                mValveControllers.put(assignValves.get(i), valveIdentifier + " 2");
            }
        }
    }

    @Override
    public void exitInstanceStmt(LFRParser.InstanceStmtContext ctx) {
        String moduleName = ctx.IDENTIFIER(0).getText();
        String instanceName = ctx.IDENTIFIER(1).getText();
        Module module = (Module) mSymbolTable.get(moduleName, SymbolType.MODULE);
        if (module == null) {
            ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER(0), ErrorCode.UNDEFINED_MODULE);
            updateStatus(StatusCode.FAIL);
            return;
        }
        Instance instance = new Instance(instanceName, 1);
        StatusCode code = mSymbolTable.put(instance);
        if (code != StatusCode.SUCCESS) {
            ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER(1), ErrorCode.INVALID_IDENTIFIER);
            updateStatus(code);
            return;
        }
        List<String> modulePorts = new ArrayList<>();
        List<String> wires = new ArrayList<>();
        List<String> valveControls = new ArrayList<>();
        for (int i = 2, j = 0; i < ctx.IDENTIFIER().size(); i += 2, ++j) {
            modulePorts.add(ctx.IDENTIFIER(i).getText());
            wires.add(ctx.IDENTIFIER(i + 1).getText());
            if (ctx.valvePhase(j).IDENTIFIER() != null) {
                valveControls.add(ctx.valvePhase(j).IDENTIFIER().getText());
                if (!mValveControllers.containsKey(valveControls.get(j))) {
                    ErrorHandler.printErrorMessage(mFilename, ctx.valvePhase(j).IDENTIFIER(), ErrorCode.UNDEFINED_SYMBOL);
                    updateStatus(StatusCode.FAIL);
                    return;
                }
            } else {
                valveControls.add(null);
            }
        }
        for (int i = 0; i < modulePorts.size(); ++i) {
            if (!module.getInputs().contains(modulePorts.get(i)) &&
                    !module.getOutputs().contains(modulePorts.get(i))) {
                System.out.println("Error at " + modulePorts.get(i));
                ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER((i + 1) * 2), ErrorCode.PORT_NAME_NOT_MATCH);
                updateStatus(StatusCode.FAIL);
                return;
            }
            if (!mSymbolTable.containsKey(wires.get(i))) {
                ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER((i + 1) * 2 + 1), ErrorCode.UNDEFINED_SYMBOL);
                updateStatus(StatusCode.FAIL);
                return;
            }
            Component component = (Component) mSymbolTable.get(wires.get(i), SymbolType.COMPONENT);
            if (!component.isFlowComponent()) {
                ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER((i + 1) * 2 + 1), ErrorCode.LAYER_ERROR_FLOW);
                updateStatus(StatusCode.FAIL);
                return;
            }
            int port = component.nextOutput();
            if (port == -1) {
                ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER((i + 1) * 2 + 1), ErrorCode.NO_VALID_PORTS);
                updateStatus(StatusCode.FAIL);
                return;
            }
            String channelId = mComponentNameGenerator.nextChannel();
            String channel = "CHANNEL " + channelId;
            if (module.getInputs().contains(modulePorts.get(i))) {
                int portIndex = module.getInputs().indexOf(modulePorts.get(i));
                channel += " from " + component.getMINTIdentifier() + " " + port;
                channel += " to " + module.getInputPortMINT(portIndex).replaceAll("#NAME", instanceName);
            } else {
                int portIndex = module.getOutputs().indexOf(modulePorts.get(i));
                channel += " from " + module.getOutputPortMINT(portIndex).replaceAll("#NAME", instanceName);
                channel += " to " + component.getMINTIdentifier() + " " + port;
            }
            channel += " w=" + mConfiguration.getDefaultChannelWidth();
            mModuleWriter.write(channel, ModuleWriter.Target.FLOW_CHANNEL);
            if (valveControls.get(i) != null) {
                String valveId = mComponentNameGenerator.nextComponent("valve");
                String valve = "VALVE " + valveId + " on " + channelId;
                valve += " w=" + mConfiguration.getDefaultValveWidth() + " l=" + mConfiguration.getDefaultValveLength();
                mModuleWriter.write(valve, ModuleWriter.Target.CONTROL_COMPONENT);
                String ctlChannel = "CHANNEL " + mComponentNameGenerator.nextChannel();
                ctlChannel += " from " + valveId + " 1";
                ctlChannel += " to " + mValveControllers.get(valveControls.get(i));
                ctlChannel += " w=" + mConfiguration.getDefaultChannelWidth();
                mModuleWriter.write(ctlChannel, ModuleWriter.Target.CONTROL_CHANNEL);
                mValveControllers.put(valveControls.get(i), valveId + " 2");
            }
        }
        List<String> moduleFlow = mModules.get(moduleName).getModuleFlowMINT(instanceName);
        List<String> moduleControl = mModules.get(moduleName).getControlMINT(instanceName);
        if (!moduleFlow.isEmpty()) {
            mModuleWriter.writeAll(moduleFlow, ModuleWriter.Target.FLOW_COMPONENT);
        }
        if (!moduleControl.isEmpty()) {
            mModuleWriter.writeAll(moduleControl, ModuleWriter.Target.CONTROL_COMPONENT);
        }
        mDeviceGraph.addEdge(mModule.getIdentifier(), moduleName);
    }

    @Override
    public void exitValveStmt(LFRParser.ValveStmtContext ctx) {
        String valveIdentifier = mComponentNameGenerator.nextComponent("valve");
        Component start = (Component) mSymbolTable.get(ctx.IDENTIFIER(0).getText(), SymbolType.COMPONENT);
        if (start == null) {
            ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER(0), ErrorCode.UNDEFINED_SYMBOL);
            updateStatus(StatusCode.FAIL);
            return;
        }
        if (!start.isFlowComponent()) {
            ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER(0), ErrorCode.LAYER_ERROR_FLOW);
            updateStatus(StatusCode.FAIL);
            return;
        }
        Component end = (Component) mSymbolTable.get(ctx.IDENTIFIER(1).getText(), SymbolType.COMPONENT);
        if (end == null) {
            ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER(1), ErrorCode.UNDEFINED_SYMBOL);
            updateStatus(StatusCode.FAIL);
            return;
        }
        if (!end.isFlowComponent()) {
            ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER(1), ErrorCode.LAYER_ERROR_FLOW);
            updateStatus(StatusCode.FAIL);
            return;
        }
        int startPort = start.nextOutput();
        if (startPort == -1) {
            ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER(0), ErrorCode.NO_VALID_PORTS);
            updateStatus(StatusCode.FAIL);
            return;
        }
        int endPort = end.nextInput();
        if (endPort == -1) {
            ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER(1), ErrorCode.NO_VALID_PORTS);
            updateStatus(StatusCode.FAIL);
            return;
        }
        if (!mValveControllers.containsKey(ctx.IDENTIFIER(2).getText())) {
            ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER(2), ErrorCode.UNDEFINED_SYMBOL);
            updateStatus(StatusCode.FAIL);
            return;
        }
        String channelId = mComponentNameGenerator.nextChannel();
        String channel = "CHANNEL " + channelId;
        channel += " from " + start.getMINTIdentifier() + " " + startPort;
        channel += " to " + end.getMINTIdentifier() + " " + endPort;
        channel += " w=" + mConfiguration.getDefaultChannelWidth();
        mModuleWriter.write(channel, ModuleWriter.Target.FLOW_CHANNEL);
        String valve = "VALVE " + valveIdentifier + " on " + channelId;
        valve += " w=" + mConfiguration.getDefaultValveWidth() + " l=" + mConfiguration.getDefaultValveLength();
        mModuleWriter.write(valve, ModuleWriter.Target.CONTROL_COMPONENT);
        String ctlChannel = "CHANNEL " + mComponentNameGenerator.nextChannel();
        ctlChannel += " from " + valveIdentifier + " 1";
        ctlChannel += " to " + mValveControllers.get(ctx.IDENTIFIER(2).getText());
        ctlChannel += " w=" + mConfiguration.getDefaultChannelWidth();
        mModuleWriter.write(ctlChannel, ModuleWriter.Target.CONTROL_CHANNEL);
        mValveControllers.put(ctx.IDENTIFIER(2).getText(), valveIdentifier + " 2");
    }

    @Override
    public void exitPrimary(LFRParser.PrimaryContext ctx) {
        if (ctx.expr().isEmpty()) {
            Component component = (Component) mSymbolTable.get(ctx.IDENTIFIER().getText(), SymbolType.COMPONENT);
            if (component == null) {
                ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER(), ErrorCode.UNDEFINED_SYMBOL);
                updateStatus(StatusCode.FAIL);
                mExprStack.push(Component.getErrorTerm());
                return;
            }
            if (!component.isFlowComponent()) {
                ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER(), ErrorCode.LAYER_ERROR_FLOW);
                updateStatus(StatusCode.FAIL);
                mExprStack.push(Component.getErrorTerm());
                return;
            }
            int portNumber = component.nextOutput();
            if (portNumber == -1) {
                ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER(), ErrorCode.NO_VALID_PORTS);
                updateStatus(StatusCode.FAIL);
                mExprStack.push(Component.getErrorTerm());
                return;
            }
            String output = component.getMINTIdentifier() + " " + portNumber;
            if (ctx.valvePhase().IDENTIFIER() != null) {
                String ctlId = ctx.valvePhase().IDENTIFIER().getText();
                if (!mValveControllers.containsKey(ctlId)) {
                    ErrorHandler.printErrorMessage(mFilename, ctx.valvePhase().IDENTIFIER(), ErrorCode.UNDEFINED_SYMBOL);
                    updateStatus(StatusCode.FAIL);
                    mExprStack.push(Component.getErrorTerm());
                    return;
                }
                String valveId = mComponentNameGenerator.nextComponent("valve");
                String nodeId = mComponentNameGenerator.nextComponent("node");
                mModuleWriter.write("NODE " + nodeId, ModuleWriter.Target.FLOW_COMPONENT);
                String channelId = mComponentNameGenerator.nextChannel();
                String channel = "CHANNEL " + channelId;
                channel += " from " + output;
                channel += " to " + nodeId + " 1";
                channel += " w=" + mConfiguration.getDefaultChannelWidth();
                mModuleWriter.write(channel, ModuleWriter.Target.FLOW_CHANNEL);
                String valve = "VALVE " + valveId + " on " + channelId;
                valve += " w=" + mConfiguration.getDefaultValveWidth() + " l=" + mConfiguration.getDefaultValveLength();
                mModuleWriter.write(valve, ModuleWriter.Target.CONTROL_COMPONENT);
                String ctlChannel = "CHANNEL " + mComponentNameGenerator.nextChannel();
                ctlChannel += " from " + valveId + " 1";
                ctlChannel += " to " + mValveControllers.get(ctlId);
                ctlChannel += " w=" + mConfiguration.getDefaultChannelWidth();
                mModuleWriter.write(ctlChannel, ModuleWriter.Target.CONTROL_CHANNEL);
                mValveControllers.put(ctlId, valveId + " 2");
                output = nodeId + " 2";
            }
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
                ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER(), ErrorCode.INVALID_OPERATOR);
                updateStatus(StatusCode.FAIL);
                mExprStack.push(Component.getErrorTerm());
                return;
            }
            if (operator.getInputs() != inputs.size()) {
                ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER(), ErrorCode.OPERATOR_INPUTS_NOT_MATCH);
                updateStatus(StatusCode.FAIL);
                mExprStack.push(Component.getErrorTerm());
                return;
            }
            if (operator.getLayer() == Layer.FLOW) {
                String operatorComponent = mComponentNameGenerator.nextComponent(operator.getIdentifier());
                mModuleWriter.write(operator.getMINT(operatorComponent), ModuleWriter.Target.FLOW_COMPONENT);
                for (int i = 0; i < inputs.size(); ++i) {
                    String input = inputs.get(i);
                    int operatorPortNumber = operator.getInputTerms().get(i);
                    String channelBuffer;
                    channelBuffer = "CHANNEL " + mComponentNameGenerator.nextChannel();
                    channelBuffer += " from " + input;
                    channelBuffer += " to " + operatorComponent + " " + operatorPortNumber;
                    channelBuffer += " w=" + mConfiguration.getDefaultChannelWidth();
                    mModuleWriter.write(channelBuffer, ModuleWriter.Target.FLOW_CHANNEL);
                }
                List<String> outputs = new ArrayList<>();
                for (int outputTerm : operator.getOutputTerms()) {
                    outputs.add(operatorComponent + " " + outputTerm);
                }
                if (ctx.valvePhase().IDENTIFIER() != null) {
                    String ctlId = ctx.valvePhase().IDENTIFIER().getText();
                    if (!mValveControllers.containsKey(ctlId)) {
                        ErrorHandler.printErrorMessage(mFilename, ctx.valvePhase().IDENTIFIER(), ErrorCode.UNDEFINED_SYMBOL);
                        updateStatus(StatusCode.FAIL);
                        mExprStack.push(Component.getErrorTerm());
                        return;
                    }
                    for (int i = 0; i < outputs.size(); ++i) {
                        String valveId = mComponentNameGenerator.nextComponent("valve");
                        String nodeId = mComponentNameGenerator.nextComponent("node");
                        mModuleWriter.write("NODE " + nodeId, ModuleWriter.Target.FLOW_COMPONENT);
                        String channelId = mComponentNameGenerator.nextChannel();
                        String channel = "CHANNEL " + channelId;
                        channel += " from " + outputs.get(i);
                        channel += " to " + nodeId + " 1";
                        channel += " w=" + mConfiguration.getDefaultChannelWidth();
                        mModuleWriter.write(channel, ModuleWriter.Target.FLOW_CHANNEL);
                        String valve = "VALVE " + valveId + " on " + channelId;
                        valve += " w=" + mConfiguration.getDefaultValveWidth() + " l=" + mConfiguration.getDefaultValveLength();
                        mModuleWriter.write(valve, ModuleWriter.Target.CONTROL_COMPONENT);
                        String ctlChannel = "CHANNEL " + mComponentNameGenerator.nextChannel();
                        ctlChannel += " from " + valveId + " 1";
                        ctlChannel += " to " + mValveControllers.get(ctlId);
                        ctlChannel += " w=" + mConfiguration.getDefaultChannelWidth();
                        mModuleWriter.write(ctlChannel, ModuleWriter.Target.CONTROL_CHANNEL);
                        mValveControllers.put(ctlId, valveId + " 2");
                        outputs.set(i, nodeId + " 2");
                    }
                }
                for (String output : outputs) {
                    mExprStack.push(output);
                }
            } else {
                ErrorHandler.printErrorMessage(mFilename, ctx.IDENTIFIER(), ErrorCode.CONTROL_OPERATOR_NOT_SUPPORT);
                updateStatus(StatusCode.FAIL);
                mExprStack.push(Component.getErrorTerm());
            }
        }
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        super.visitErrorNode(node);
        System.exit(1);
    }
}
