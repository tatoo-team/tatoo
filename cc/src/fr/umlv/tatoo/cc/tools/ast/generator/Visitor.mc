<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%@page import="fr.umlv.tatoo.cc.tools.generator.ToolsGeneratorUtils"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Map"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.VariableDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.ProductionDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.TerminalDecl"%>
<%@page import="fr.umlv.tatoo.cc.tools.ast.generator.NonTerminalKind"%>

<%!/*PARAM*/
Type visitorNode;
Type abstractNode;
Collection<? extends ProductionDecl> productions;
Collection<? extends NonTerminalDecl> nonTerminals;
Collection<? extends TerminalDecl> terminals;
Map<VariableDecl, Type> variableTypeMap;
Map<NonTerminalDecl,NonTerminalKind> nonTerminalKindMap;
Map<ProductionDecl,Type> productionTypeMap;
%>
<%!/*COMMENT*/
java.io.PrintWriter out;
%><%!
%>

package <%=visitorNode.getPackageName()%>;

/**
 *
 *  This class is generated - please do not edit it 
 */
public class <%=visitorNode.getSimpleName()%><_R,_P,_E extends Exception> {
  <%
    for(ProductionDecl production:productions) {
      NonTerminalDecl nonTerminal=production.getLeft();
      NonTerminalKind nonTerminalKind=nonTerminalKindMap.get(nonTerminal);
      if (nonTerminalKind==NonTerminalKind.EBNF_DEFINED)
        continue;
      String subType=(nonTerminalKind==NonTerminalKind.HAS_ONE_PRODUCTION)?
          "Node":variableTypeMap.get(nonTerminal).getSimpleName();
      Type productionType=productionTypeMap.get(production);
      %>
   public _R visit(<%=productionType.getSimpleName()%> <%=production.getId()%>,_P _param) throws _E {
     return visit((<%=subType%>)<%=production.getId()%>,_param);
   }
   <%
  }
  %>
  <%
    for(NonTerminalDecl nonTerminal:nonTerminals) {
      NonTerminalKind nonTerminalKind=nonTerminalKindMap.get(nonTerminal);
      if (nonTerminalKind==NonTerminalKind.EBNF_DEFINED ||
          nonTerminalKind==NonTerminalKind.HAS_ONE_PRODUCTION)
        continue;
      Type nonTerminalType=variableTypeMap.get(nonTerminal);
      %>
   protected _R visit(<%=nonTerminalType.getSimpleName()%> <%=nonTerminal.getId()%>,_P _param) throws _E {
     return visit((<%=abstractNode.getSimpleName()%>)<%=nonTerminal.getId()%>,_param);
   }
   <%
  }
  %>
  <%
    for(TerminalDecl terminal:terminals) {
      Type terminalType=variableTypeMap.get(terminal);
      if (terminalType==null)
        continue;
      %>
   public _R visit(<%=terminalType.getSimpleName()%> <%=terminal.getId()%>,_P _param) throws _E {
     return visit((<%=abstractNode.getSimpleName()%>)<%=terminal.getId()%>,_param);
   }
   <%
  }
  %>
  /** Default type visitor.
     *  The implementation always throws an {@link AssertionError}.
     * 
     * @param node the visited node.
     * @param _param a parameter value.
     * @return a return value.
     * @throws _E an exception (runtime or not).
     */
  protected _R visit(<%=abstractNode.getSimpleName()%> node,_P _param) throws _E {
     throw new AssertionError("default visit not defined");
  }
}
