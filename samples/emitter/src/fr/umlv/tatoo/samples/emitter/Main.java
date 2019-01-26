package fr.umlv.tatoo.samples.emitter;

import java.io.InputStreamReader;
import java.util.List;

import fr.umlv.tatoo.samples.emitter.calc.ast.Expr;
import fr.umlv.tatoo.samples.emitter.calc.ast.ExprDiv;
import fr.umlv.tatoo.samples.emitter.calc.ast.ExprMinus;
import fr.umlv.tatoo.samples.emitter.calc.ast.ExprPlus;
import fr.umlv.tatoo.samples.emitter.calc.ast.ExprStar;
import fr.umlv.tatoo.samples.emitter.calc.ast.Line;
import fr.umlv.tatoo.samples.emitter.calc.ast.Node;
import fr.umlv.tatoo.samples.emitter.calc.ast.Start;
import fr.umlv.tatoo.samples.emitter.calc.tools.Analyzers;
import fr.umlv.tatoo.samples.emitter.exprcall.ast.Expression;
import fr.umlv.tatoo.samples.emitter.exprcall.ast.IdToken;
import fr.umlv.tatoo.samples.emitter.exprcall.ast.ValueToken;

public class Main {
  public static void main(String[] args) {
    fr.umlv.tatoo.samples.emitter.calc.tools.TerminalEvaluator<CharSequence> calcTerminalEvaluator=
      new fr.umlv.tatoo.samples.emitter.calc.tools.TerminalEvaluator<CharSequence>() {
      @Override
      public fr.umlv.tatoo.samples.emitter.calc.ast.ValueToken value(CharSequence data) {
        return new fr.umlv.tatoo.samples.emitter.calc.ast.ValueToken(Integer.parseInt(data.toString()));
      }
      @Override
      public void comment(CharSequence data) {
        // do nothing
      }
    };
    fr.umlv.tatoo.samples.emitter.calc.ast.ASTGrammarEvaluator calcASTGrammarEvaluator=
      new fr.umlv.tatoo.samples.emitter.calc.ast.ASTGrammarEvaluator();
    Analyzers.run(new InputStreamReader(System.in), calcTerminalEvaluator, calcASTGrammarEvaluator, null, null);
    
    fr.umlv.tatoo.samples.emitter.exprcall.ast.ASTGrammarEvaluator exprCallGrammarEvaluator=
      new fr.umlv.tatoo.samples.emitter.exprcall.ast.ASTGrammarEvaluator();
    fr.umlv.tatoo.samples.emitter.exprcall.tools.TerminalEvaluator<CharSequence> exprCallTerminalEvaluator=
      new fr.umlv.tatoo.samples.emitter.exprcall.tools.TerminalEvaluator<CharSequence>(){
      @Override
      public ValueToken value(CharSequence data) {
        return new ValueToken(Integer.parseInt(data.toString()));
      }
      @Override
      public IdToken id(CharSequence data) {
        return new IdToken(data.toString());
      }
    };
    
    final fr.umlv.tatoo.samples.emitter.exprcall.ast.ASTEmitter emitter=
      fr.umlv.tatoo.samples.emitter.exprcall.ast.ASTEmitter.create(exprCallTerminalEvaluator, exprCallGrammarEvaluator);
    
    fr.umlv.tatoo.samples.emitter.calc.ast.Visitor<Expression,Void,RuntimeException> rewriter=
      new fr.umlv.tatoo.samples.emitter.calc.ast.Visitor<Expression,Void,RuntimeException>() {
      
      @Override
      protected Expression visit(Node node, Void notUsed) {
        List<Node> nodeList = node.nodeList();
        if (nodeList.size()!=1)
          throw new AssertionError("node list has more than one node");
        return nodeList.get(0).accept(this, null);
      }
      
      private Expression visitBinary(String name, Expr e1, Expr e2) {
        Expression expression1=e1.accept(this, null);
        Expression expression2=e2.accept(this, null);
        return emitter.toExpression().parse(name+"(").Expression(expression1).parse(",").Expression(expression2).parse(")").emit();
      }
      
      @Override
      public Expression visit(ExprPlus exprPlus, Void notUsed) {
        return visitBinary("add", exprPlus.getExpr(), exprPlus.getExpr2());
      }
      @Override
      public Expression visit(ExprMinus exprMinus, Void param) {
        return visitBinary("minus", exprMinus.getExpr(), exprMinus.getExpr2());
      }
      @Override
      public Expression visit(ExprStar exprStar, Void param) {
        return visitBinary("mult", exprStar.getExpr(), exprStar.getExpr2());
      }
      @Override
      public Expression visit(ExprDiv exprDiv, Void param) {
        return visitBinary("div", exprDiv.getExpr(), exprDiv.getExpr2());
      }
      @Override
      public Expression visit(fr.umlv.tatoo.samples.emitter.calc.ast.ValueToken value, Void notUsed) {
        return emitter.toExpression().parse(value.getValue().toString()).emit();    
      }
    };
    
    Start start=calcASTGrammarEvaluator.getStart();
    for(Line line:start.getLineStar()) {
      System.out.println(line.getExpr().accept(rewriter, null));
    }
  }
}
