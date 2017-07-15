package com.github.ilms49898723.minttranslator.lfr;

import com.github.ilms49898723.minttranslator.antlr.LFRBaseListener;
import com.github.ilms49898723.minttranslator.antlr.LFRParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * Created by littlebird on 2017/07/15.
 */
public class LFRProcessor extends LFRBaseListener {
    @Override
    public void enterLfr(LFRParser.LfrContext ctx) {
        super.enterLfr(ctx);
    }

    @Override
    public void exitLfr(LFRParser.LfrContext ctx) {
        super.exitLfr(ctx);
    }

    @Override
    public void enterVerilog_modules(LFRParser.Verilog_modulesContext ctx) {
        super.enterVerilog_modules(ctx);
        System.out.println("enter module with name " + ctx.IDENTIFIER().getText());
    }

    @Override
    public void exitVerilog_modules(LFRParser.Verilog_modulesContext ctx) {
        super.exitVerilog_modules(ctx);
    }

    @Override
    public void enterVerilog_stmts(LFRParser.Verilog_stmtsContext ctx) {
        super.enterVerilog_stmts(ctx);
    }

    @Override
    public void exitVerilog_stmts(LFRParser.Verilog_stmtsContext ctx) {
        super.exitVerilog_stmts(ctx);
    }

    @Override
    public void enterVerilog_stmt(LFRParser.Verilog_stmtContext ctx) {
        super.enterVerilog_stmt(ctx);
    }

    @Override
    public void exitVerilog_stmt(LFRParser.Verilog_stmtContext ctx) {
        super.exitVerilog_stmt(ctx);
    }

    @Override
    public void enterFlow_input_decl(LFRParser.Flow_input_declContext ctx) {
        super.enterFlow_input_decl(ctx);
    }

    @Override
    public void exitFlow_input_decl(LFRParser.Flow_input_declContext ctx) {
        super.exitFlow_input_decl(ctx);
    }

    @Override
    public void enterFlow_output_decl(LFRParser.Flow_output_declContext ctx) {
        super.enterFlow_output_decl(ctx);
    }

    @Override
    public void exitFlow_output_decl(LFRParser.Flow_output_declContext ctx) {
        super.exitFlow_output_decl(ctx);
    }

    @Override
    public void enterControl_input_decl(LFRParser.Control_input_declContext ctx) {
        super.enterControl_input_decl(ctx);
    }

    @Override
    public void exitControl_input_decl(LFRParser.Control_input_declContext ctx) {
        super.exitControl_input_decl(ctx);
    }

    @Override
    public void enterChannel_decl(LFRParser.Channel_declContext ctx) {
        super.enterChannel_decl(ctx);
    }

    @Override
    public void exitChannel_decl(LFRParser.Channel_declContext ctx) {
        super.exitChannel_decl(ctx);
    }

    @Override
    public void enterAssign_stmt(LFRParser.Assign_stmtContext ctx) {
        super.enterAssign_stmt(ctx);
    }

    @Override
    public void exitAssign_stmt(LFRParser.Assign_stmtContext ctx) {
        super.exitAssign_stmt(ctx);
    }

    @Override
    public void enterExpr(LFRParser.ExprContext ctx) {
        super.enterExpr(ctx);
        System.out.println("enter expr " + ctx.getText());
        for (LFRParser.ExprContext context : ctx.expr()) {
            System.out.println("    " + context.getText());
        }
    }

    @Override
    public void exitExpr(LFRParser.ExprContext ctx) {
        super.exitExpr(ctx);
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        super.enterEveryRule(ctx);
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        super.exitEveryRule(ctx);
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        super.visitTerminal(node);
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        super.visitErrorNode(node);
    }
}
