<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%@page import="fr.umlv.tatoo.cc.tools.generator.ToolsGeneratorUtils"%>
<%@page import="fr.umlv.tatoo.cc.tools.ast.generator.ASTGeneratorUtils"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl"%>
<%@page import="fr.umlv.tatoo.cc.tools.generator.Naming"%>
<%@page import="java.util.Map"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.EBNFSupport"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.VariableDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.ProductionDecl"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Iterator"%>
<%!/*PARAM*/
Type astGrammarEvaluator;
Type grammarEvaluator;
Type abstractNode;
Collection<Type> variableImports;
Collection<? extends ProductionDecl> productions;
EBNFSupport ebnfSupport;
Map<VariableDecl, Type> variableTypeMap;
Collection<? extends NonTerminalDecl> starts;
Map<ProductionDecl,Type> productionTypeMap;%>
<%!/*COMMENT*/
java.io.PrintWriter out;%>
<%!
%>
package <%=astGrammarEvaluator.getPackageName()%>;

<%
  for(Type iMport : variableImports) { %>
import <%=iMport.getRawName()%>;
<%
  }
%>
import <%=grammarEvaluator.getRawName()%>;
import fr.umlv.tatoo.runtime.ast.RootNodeProvider;

/** 
 *  This class is generated - please do not edit it 
 */
public class <%=astGrammarEvaluator.getSimpleName()%> implements <%=grammarEvaluator.getSimpleName()%>, RootNodeProvider {
<%
  for(ProductionDecl production:productions) { 
     Type returnType = variableTypeMap.get(production.getLeft());
     if (!ebnfSupport.getEBNFTypeMap().containsKey(production) 
     && (returnType!=null
     || ToolsGeneratorUtils.notAllNull(production.getRight(),variableTypeMap))) {
%>
  /** This methods is called after the reduction of the non terminal <%=production.getLeft()%>
   *  by the grammar production <%=production.getId()%>.
   *  <%="<"%>code><%=production.getLeft().getId()%> ::=<%
  for(VariableDecl right : production.getRight())
   out.append(' ').append(right.getId());
%><%="<"%>/code>
   */
  public <%=returnType==null?"void":returnType.getSimpleName()%> <%=production.getId()%>(<%
      ToolsGeneratorUtils.foreachNonNull(production.getRight(),variableTypeMap, new ToolsGeneratorUtils.ParamClosure<Boolean>() {
        public Boolean apply(Type type,String name,Boolean initial) {
          %><%=(initial)?"":","%><%=type.getSimpleName()%> <%=name%><%
          return false;
        }
    },true);
 %>) {
 <%
     Type node=productionTypeMap.get(production);
%>
     <%=node.getSimpleName()%> <%=production.getId()%> = new <%=node.getSimpleName()%>(<%
            ToolsGeneratorUtils.foreachNonNull(production.getRight(),variableTypeMap, new ToolsGeneratorUtils.ParamClosure<Boolean>() {
              public Boolean apply(Type type,String name,Boolean initial) {
                %><%=(initial)?"":","%><%=name%><%     
                return false;
              }
            },true);
          %>);
     computeAnnotation(<%=production.getId()%>);
     return <%=production.getId()%>;
   }
<%
   }
   }
 %>

<%
  for(NonTerminalDecl nonTerminal:starts) { 
     Type type = variableTypeMap.get(nonTerminal);
     String acceptName=ToolsGeneratorUtils.toUpperCase(nonTerminal);
     String param= type==null||type.isVoid()?"":type.getSimpleName()+" "+nonTerminal.getId();
%>
  public void accept<%=acceptName%>(<%=param%>) {
<% if (type!=null && !type.isVoid()) { %>
     this.<%=nonTerminal.getId()%>=<%=nonTerminal.getId()%>;
  }
  public <%=type.getSimpleName()%> get<%=acceptName%>() {
     return <%=nonTerminal.getId()%>;
  }
  private <%=type.getSimpleName()%> <%=nonTerminal.getId()%>;
<% } else { %>
     // do nothing
  }
<% } 
  }
%>

  public <N> N getRootNode(Class<N> nonTerminalType) {
<%
   for(NonTerminalDecl nonTerminal:starts) { 
     Type type = variableTypeMap.get(nonTerminal);
     String acceptName=ToolsGeneratorUtils.toUpperCase(nonTerminal);
%>
    if (nonTerminalType==<%=type.getSimpleRawName()%>.class)
      return nonTerminalType.cast(get<%=acceptName%>());
<% }
%>
    throw new IllegalArgumentException("invalid type for a start non-terminal "+nonTerminalType);
  }

  /**  This method is called each time a node is created and can
   *   be overridden to compute values when constructing a node.
   *   This implementation does nothing.
   * @param node the created node.
   */
  protected void computeAnnotation(<%=abstractNode.getSimpleName()%> node) {
    // do nothing
  }
}
