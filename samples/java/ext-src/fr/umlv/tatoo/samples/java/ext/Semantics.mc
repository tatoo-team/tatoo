<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.ProductionDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.VariableDecl"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%!/*PARAM*/
Type semanticsType;
Type P;
Iterable<ProductionDecl> productions;
Map<VariableDecl,Type> variableTypeMap;
%>
<%!/*COMMENT*/
java.io.PrintWriter out;
%>
<%!
  private int numberOfTypedVariable(ProductionDecl production) {
    int count=0;
    for(VariableDecl variable:production.getRight()) {
      Type type=variableTypeMap.get(variable);
      if (type!=null && !type.isVoid())
        count++;
    }
    return count;
  } 
%>
package <%=semanticsType.getPackageName()%>;

import java.util.Map;
import java.util.EnumMap;

import <%= P.getRawType() %>;

public class <%= semanticsType.getSimpleName() %> {
  private <%= semanticsType.getSimpleName() %>() {
    // helper class
  }
  
  /** Return a map that associates a production to its number of typed variables.
   * @return a map that associates a production to its number of typed variables.
   */
  public static Map<<%=P.getSimpleName() %>,Integer> createNumberOfTypedVariableMap() {
    EnumMap<<%=P.getSimpleName() %>,Integer> map=
      new EnumMap<<%=P.getSimpleName() %>,Integer>(<%=P.getSimpleName() %>.class);
<% for(ProductionDecl production:productions) { %>
    map.put(<%=P.getSimpleName() %>.<%= production.getId() %>, <%= numberOfTypedVariable(production) %>);   
<% } %>
    return map;
  }
}