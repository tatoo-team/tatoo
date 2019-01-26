//FIXME Remi when AST is back

package fr.umlv.tatoo.samples.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import fr.umlv.tatoo.samples.test.tools.TerminalEvaluator;
import fr.umlv.tatoo.samples.test.tools.tree.GrammarEvaluator;
import fr.umlv.tatoo.samples.test.tools.tree.TreeAnalyzers;


public class TreeMain {
  public static void main(String[] args) throws FileNotFoundException {
    Reader reader;
    if (args.length>0)
      reader=new FileReader(args[0]);
    else
      reader=new InputStreamReader(System.in);

    TerminalEvaluator<CharSequence> attributeEvaluator=new IntAttributeEvaluator();
    ArrayList<Expr> exprs=new ArrayList<Expr>();
    GrammarEvaluator grammarEvaluator=new TreeGrammarEvaluator(exprs);
    
    TreeAnalyzers.run(reader,attributeEvaluator,grammarEvaluator,null,null);
    for(Expr expr:exprs)
      System.out.println(expr+"="+expr.eval());
  }
}
