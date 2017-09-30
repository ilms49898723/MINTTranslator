package com.github.ilms49898723.minttranslator.errorhandling;

import org.antlr.v4.runtime.tree.TerminalNode;

public class ErrorHandler {
    public static void printErrorMessageAndExit(String filename, TerminalNode node, ErrorCode code) {
        if (node == null) {
            System.err.println("Fatal Error: at printErrorMessageAndExit():");
            System.err.println("A null node was passed as a parameter.");
            System.exit(1);
        }
        printErrorMessage(filename, node, code);
        System.exit(1);
    }

    public static void printErrorMessage(String filename, TerminalNode node, ErrorCode code) {
        switch (code) {
            case INVALID_UCF_FILE:
                System.err.println(filename + ": invalid UCF file.");
                break;
            case INVALID_IDENTIFIER:
                printHeader(filename, node);
                System.err.println("Invalid symbol " + node.getSymbol().getText() + ": already defined?");
                break;
            case INVALID_OPERATOR:
                printHeader(filename, node);
                System.err.println("Invalid operator " + node.getSymbol().getText());
                break;
            case UNDEFINED_MODULE:
                printHeader(filename, node);
                System.err.println("Unrecognized module " + node.getSymbol().getText());
                break;
            case UNDEFINED_SYMBOL:
                printHeader(filename, node);
                System.err.println("Unrecognized symbol " + node.getSymbol().getText());
                break;
            case PORT_NAME_NOT_MATCH:
                printHeader(filename, node);
                System.err.println("Target module has no port named " + node.getSymbol().getText());
                break;
            case LAYER_ERROR_FLOW:
                printHeader(filename, node);
                System.err.println("Component " + node.getSymbol().getText() + " is not in flow layer.");
                break;
            case LAYER_ERROR_CONTROL:
                printHeader(filename, node);
                System.err.println("Component " + node.getSymbol().getText() + " is not in control layer.");
                break;
            case ASSIGN_PORTS_NOT_MATCH:
                printHeader(filename, node);
                System.err.println("Number of assign targets is different from number of the expression result.");
                break;
            case NO_VALID_PORTS:
                printHeader(filename, node);
                System.err.println("Component " + node.getSymbol().getText() + " has no ports available.");
                break;
            case NOT_BINARY_UNARY_OPERATOR:
                printHeader(filename, node);
                System.err.println("Operator " + node.getSymbol().getText() + " is not a unary or binary operator.");
                break;
            case OPERATOR_INPUTS_NOT_MATCH:
                printHeader(filename, node);
                System.err.println("Number of inputs not matched for operator " + node.getSymbol().getText());
                break;
            case CONTROL_OPERATOR_NOT_SUPPORT:
                printHeader(filename, node);
                System.err.println(node.getSymbol().getText() + ": control operator is not supported currently.");
                break;
            case PORT_NOT_LISTED:
                printHeader(filename, node);
                System.err.println(node.getSymbol().getText() + ": all input and output ports should be listed in \'()\' after module name.");
                break;
        }
    }

    private static void printHeader(String filename, TerminalNode node) {
        System.err.println("In file " + filename);
        System.err.println("At line " + node.getSymbol().getLine());
    }
}
