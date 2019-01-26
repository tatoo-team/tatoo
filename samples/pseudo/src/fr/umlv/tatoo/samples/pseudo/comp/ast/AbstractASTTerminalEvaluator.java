/**
 * 
 */
package fr.umlv.tatoo.samples.pseudo.comp.ast;

import fr.umlv.tatoo.samples.pseudo.ast.AngleBracketsToken;
import fr.umlv.tatoo.samples.pseudo.ast.BooleanConstToken;
import fr.umlv.tatoo.samples.pseudo.ast.IdToken;
import fr.umlv.tatoo.samples.pseudo.ast.Node;
import fr.umlv.tatoo.samples.pseudo.ast.StringConstToken;
import fr.umlv.tatoo.samples.pseudo.ast.ValueToken;
import fr.umlv.tatoo.samples.pseudo.tools.TerminalEvaluator;

public abstract class AbstractASTTerminalEvaluator implements TerminalEvaluator<CharSequence> {
  @Override
  public void comment(CharSequence data) {
    // comment, do nothing
  }
  
  @Override
  public AngleBracketsToken angle_brackets(CharSequence data) {
    AngleBracketsToken node=new AngleBracketsToken();
    computeAnnotation(node);
    return node;
  }

  @Override
  public BooleanConstToken boolean_const(CharSequence data) {
    BooleanConstToken node=new BooleanConstToken(Boolean.parseBoolean(data.toString()));
    computeAnnotation(node);
    return node;
  }

  @Override
  public IdToken id(CharSequence data) {
    IdToken node=new IdToken(data.toString());
    computeAnnotation(node);
    return node;
  }

  @Override
  public ValueToken value(CharSequence data) {
    ValueToken node=new ValueToken(Integer.parseInt(data.toString()));
    computeAnnotation(node);
    return node;
  }
  
  @Override
  public StringConstToken string_const(CharSequence data) {
    StringConstToken node=new StringConstToken(
      data.subSequence(1,data.length()-1).toString());
    computeAnnotation(node);
    return node;
  }
  
  protected abstract void computeAnnotation(Node node);
}