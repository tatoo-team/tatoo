<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.ProductionDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.VariableDecl"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%!/*PARAM*/
Type pluginUtilsType;
Type P;
Iterable<ProductionDecl> productions;
%>
<%!/*COMMENT*/
java.io.PrintWriter out;
%>
<%!
  private int numberOfTerminal(ProductionDecl production) {
    int count=0;
    for(VariableDecl variable:production.getRight()) {
      if (variable.isTerminal())
        count++;
    }
    return count;
  } 
%>
package <%=pluginUtilsType.getPackageName()%>;

import java.util.Map;
import java.util.EnumMap;

import <%= P.getRawType() %>;

public class <%= pluginUtilsType.getSimpleName() %> {
  private <%= pluginUtilsType.getSimpleName() %>() {
    // helper class
  }
  
  /** Return a map that associates a production to its number of terminals.
   * @return a map that associates a production to its number of terminals.
   */
  public static Map<<%=P.getSimpleName() %>,Integer> createNumberOfTerminalMap() {
    EnumMap<<%=P.getSimpleName() %>,Integer> map=
      new EnumMap<<%=P.getSimpleName() %>,Integer>(<%=P.getSimpleName() %>.class);
<% for(ProductionDecl production:productions) { %>
    map.put(<%=P.getSimpleName() %>.<%= production.getId() %>, <%= numberOfTerminal(production) %>);   
<% } %>
    return map;
  }
}