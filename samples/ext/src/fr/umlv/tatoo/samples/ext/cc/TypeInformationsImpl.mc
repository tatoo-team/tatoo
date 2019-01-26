<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%@page import="fr.umlv.tatoo.cc.lexer.lexer.RuleDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.TerminalDecl"%>
<%@page import="java.util.Map"%>
<%!/*PARAM*/
Type currentType;
Type terminalEnum;
Map<TerminalDecl,Type> terminalTypeMap;
%>
<%!/*COMMENT*/
java.io.PrintWriter out;
%>
<%!

%>
package <%=currentType.getPackageName()%>;

import <%=terminalEnum.getRawName()%>;
import fr.umlv.tatoo.samples.ext.runtime.TypeInformations;

public class TypeInformationsImpl implements TypeInformations<%="<"%><%=terminalEnum.getSimpleName()%>> {
  public Class<?> getTerminalType(<%=terminalEnum.getSimpleName()%> terminal) {
    switch(terminal) {
<%
  for(Map.Entry<TerminalDecl,Type> entry:terminalTypeMap.entrySet()) {
%>      case <%=entry.getKey().getId()%>:
        return <%=entry.getValue()%>.class;
<% } %>
      default:
    }
    return null;
  }
}