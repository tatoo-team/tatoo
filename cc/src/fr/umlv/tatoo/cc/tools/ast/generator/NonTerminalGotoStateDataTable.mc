<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.table.ParserTableDecl"%>
<%@page import="java.util.Map.Entry"%>
<%!/*PARAM*/
Type nonTerminalGotoStateDataTable;
ParserTableDecl parserTable;
Type N;
%>
package <%=nonTerminalGotoStateDataTable.getPackageName()%>;

import <%=N.getName()%>;

import fr.umlv.tatoo.runtime.ast.NonTerminalGotoStateTable;

public class <%= nonTerminalGotoStateDataTable.getSimpleName() %> {
  private <%= nonTerminalGotoStateDataTable.getSimpleName() %>() {
    // helper class
  }
  
<%
  int stateCount=parserTable.getStateCount();
  for(Entry<NonTerminalDecl, int[]> entry:parserTable.getGotoes().entrySet()) {
    NonTerminalDecl nonTerminal=entry.getKey(); 
    int[] gotoes=entry.getValue(); %>
    
  private static void init_<%=nonTerminal.getId()%>(NonTerminalGotoStateTable<<%=N.getSimpleName()%>> nonTerminalGotoStateTable) {
 <%
    for(int i=0;i<stateCount;i++) { 
      if (gotoes[i]!=-1) { %>
      nonTerminalGotoStateTable.setGotoState(<%=i%>, <%=N.getSimpleName()%>.<%=nonTerminal.getId()%>, <%=gotoes[i]%>);
<%
      } 
    } %>
  }
  
<%
  }
%>
  
  public static NonTerminalGotoStateTable<<%=N.getSimpleName()%>> create() {
     NonTerminalGotoStateTable<<%=N.getSimpleName()%>> nonTerminalGotoStateTable=
       new NonTerminalGotoStateTable<<%=N.getSimpleName()%>>(<%=parserTable.getStateCount()%>);
<%
     
  for(NonTerminalDecl terminal:parserTable.getGotoes().keySet()) { %>
     init_<%=terminal.getId()%>(nonTerminalGotoStateTable);
    <%
  }
%>
     return nonTerminalGotoStateTable;
  } 
}