package fr.umlv.tatoo.cc.parser.table;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import fr.umlv.tatoo.cc.common.generator.impl.Motocity;
import fr.umlv.tatoo.cc.common.log.Info;
import fr.umlv.tatoo.cc.parser.grammar.Grammar;
import fr.umlv.tatoo.cc.parser.grammar.GrammarSets;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.Priority;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.parser.parser.AcceptActionDecl;
import fr.umlv.tatoo.cc.parser.parser.BranchActionDecl;
import fr.umlv.tatoo.cc.parser.parser.BranchTableActionDecl;
import fr.umlv.tatoo.cc.parser.parser.EnterActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ErrorActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ExitActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ReduceActionDecl;
import fr.umlv.tatoo.cc.parser.parser.RegularTableActionDecl;
import fr.umlv.tatoo.cc.parser.parser.ShiftActionDecl;
import fr.umlv.tatoo.cc.parser.parser.SimpleActionDeclVisitor;
import fr.umlv.tatoo.cc.parser.parser.VersionedActionDecl;

/**
 * @author Gilles
 */
public class TableWriter {
  public static <I extends NodeItem<I>> void dumpTable(File log,
      Grammar grammar,
      GrammarSets grammarSets,
      NodeFactory<I> factory,
      HashMap<NodeDecl<I>, HashMap<TerminalDecl,HashMap<RegularTableActionDecl,Priority>>> tmpTable,
      HashMap<NodeDecl<I>,HashMap<BranchTableActionDecl,Priority>> tmpBranch,
      HashMap<NodeDecl<I>, HashMap<NonTerminalDecl, NodeDecl<I>>> buildedGotos) {
    HashMap<String,Object> root = new HashMap<String, Object>();
    root.put("grammar", grammar);
    root.put("grammarSets",grammarSets);
    root.put("nodeFactory", factory);
    root.put("table",tmpTable);
    root.put("branches",tmpBranch);
    root.put("buildedGotos", buildedGotos);
    root.put("displayVisitor", new SimpleActionDeclVisitor<String>() {
      @Override
      public String visit(ErrorActionDecl error) {
        return "error\u00a0"+error.getMessage();
      }
      @Override
      public String visit(BranchActionDecl error) {
        return "branch\u00a0"+error.getMessage();
      }
      @Override
      public String visit(ReduceActionDecl reduce) {
        ProductionDecl prod = reduce.getProduction();
        StringBuilder b = new StringBuilder("<a href='javascript:history.go(-"+prod.getRight().size()+");'>reduce</a>\u00a0by\u00a0");
        NonTerminalDecl nonTerminal = prod.getLeft();
        b.append("<a href='#").append(nonTerminal).
          append("'>").append(nonTerminal).append("</a>\u00a0::=\u00a0");
        if (prod.getRight().isEmpty())
          b.append("\u025b");
        else {
          for(VariableDecl v:prod.getRight()) {
            if (v.isTerminal()) {
              b.append("<span class=\"terminal\">");
              escapeXML(((TerminalDecl)v).getName(),b);
              b.append("</span>\u00a0");
            } else {
              b.append("<a href=\"#").append(v).
              append("\">").append(v).append("</a>\u00a0");
            }
          }
          b.setLength(b.length()-1);
        }
        return b.toString();
      }
      @Override
      public String visit(ShiftActionDecl shift) {
        return String.format("shift\u00a0to\u00a0<a href=\"#%1$s\">%1$s</a>",shift.getState());
      }
      @Override
      public String visit(AcceptActionDecl accept) {
        return "accept";
      }
      @Override
      public String visit(VersionedActionDecl versioned) {
        throw new UnsupportedOperationException("invalid action type");
      }
      @Override
      public String visit(EnterActionDecl enter) {
        return "enter\u00a0"+enter.getBranchingTerminal().getId();
      }
      @Override
      public String visit(ExitActionDecl exit) {
        return "exit\u00a0"+exit.getId();
      }
    });
    try {
      Motocity.INSTANCE.generate(root,"debugToXHTML",log,TableWriter.class,null,null);
      Info.info("Grammar table wrote to log file: %s",log).report();
    } catch(Exception e) {
      Info.error("error while writing table to log file: %s",log).
        cause(e).report();
    }
  }
  
  private static final HashMap<Character,String> XML_ENTITIES = new HashMap<Character,String>();
  static {
    XML_ENTITIES.put('\'',"&apos;");
    XML_ENTITIES.put('&',"&amp;");
    XML_ENTITIES.put('"',"&quot;");
    XML_ENTITIES.put('<',"&lt;");
    XML_ENTITIES.put('>',"&gt;");
  }



  public static void escapeXML(CharSequence s, Appendable builder) {
    try {
      for(int i=0;i<s.length();i++) {
        char c = s.charAt(i);
        String value = XML_ENTITIES.get(c);
        if (value==null)
          builder.append(c);
        else
          builder.append(value);
      }
    }
    catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

}