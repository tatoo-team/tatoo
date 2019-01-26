<%-- Motocity file for LexerDataTable.java --%>
<%@
page import="fr.umlv.tatoo.cc.lexer.lexer.RuleDecl,
fr.umlv.tatoo.cc.lexer.lexer.LexerMap.Table,
fr.umlv.tatoo.cc.lexer.lexer.TableAutomata,
fr.umlv.tatoo.cc.common.generator.Type,
java.util.Iterator,
java.util.Collection,
java.util.Map,
java.io.PrintWriter,
fr.umlv.tatoo.cc.lexer.regex.AutomatonDecl"%>
<%!/*COMMENT*/
java.io.PrintWriter out;
%>
<%!/*PARAM*/
Collection<RuleDecl> rules;
Table lexerMapTable;
Type ruleEnum;
Type lexerDataTable;
%>

<%!
private void print(boolean[] array) {
  for(int i=0;i<array.length-1;i++) {
    out.print(array[i]);
    out.print(',');
  }
  out.print(array[array.length-1]);
} 
private void print(int[] array) {
  for(int i=0;i<array.length-1;i++) {
    out.print(array[i]);
    out.print(',');
  }
  out.print(array[array.length-1]);
}
private void print(int[][] array) {
  out.print('{');
  for(int i=0;i<array.length-1;i++) {
    print(array[i]);
    out.print("},{");
  }
  print(array[array.length-1]);
  out.print('}');
}  
%>
package <%=lexerDataTable.getPackageName()%>;

import <%=ruleEnum.getName()%>;
import fr.umlv.tatoo.runtime.lexer.LexerTable;
import fr.umlv.tatoo.runtime.lexer.rules.RuleData;
import fr.umlv.tatoo.runtime.regex.CharRegexTable;
import java.util.EnumMap;
<%
String tableType=lexerDataTable.getSimpleName();
String enumType=ruleEnum.getSimpleName();
%>
/** 
 *  This class is generated - please do not edit it 
 */
public class <%=tableType%> {

  public static LexerTable<<%=enumType%>> createTable() {
    return new <%=tableType%>().table;
  }

  private <%=tableType%>() {
<% for(Map.Entry<RuleDecl,TableAutomata> entry:lexerMapTable.getAutomataMap().entrySet()) {
      RuleDecl rule=entry.getKey();
      TableAutomata automata=entry.getValue();
      String id=rule.getId(); %>
    init<%=id%>MainAccepts();
    init<%=id%>MainTransitions();
    CharRegexTable <%=id%>Main = new CharRegexTable(<%=automata.getMainAutomaton().getFirstState()%>, <%=id%>MainTransitions, <%=id%>MainAccepts);
<% AutomatonDecl follow=automata.getFollowAutomaton();
    if (follow != null) { /* if_1*/%>
    init<%=id%>FollowAccepts();
    init<%=id%>FollowTransitions();
    CharRegexTable <%=id%>Follow = new CharRegexTable(<%=follow.getFirstState()%>, <%=id%>FollowTransitions, <%=id%>FollowAccepts);
    RuleData <%=id%> = new RuleData(<%=id%>Main, <%=id%>Follow, <%=rule.getPriority()%>, <%=rule.isBeginningOfLineRequired()%>);
<% } else { /* if_1 */%>
    RuleData <%=id%> = new RuleData(<%=id%>Main, null, <%=rule.getPriority()%>, <%=rule.isBeginningOfLineRequired()%>);
<% } /* if_1 */
    } /* for */ %>

    EnumMap<<%=enumType%>,RuleData> datas = new EnumMap<<%=enumType%>,RuleData>(<%=enumType%>.class);
<% for(RuleDecl rule:rules) { %>
    datas.put(<%=enumType%>.<%=rule.getId()%>, <%=rule.getId()%>);
<% } /* for */ %>
    table = new LexerTable<<%=enumType%>>(datas);
  }

<% for(Map.Entry<RuleDecl,TableAutomata> entry:lexerMapTable.getAutomataMap().entrySet()) {
      RuleDecl rule=entry.getKey();
      TableAutomata automata=entry.getValue();
      String id=rule.getId(); %>  
  private boolean[] <%=id%>MainAccepts;
  private void init<%=id%>MainAccepts() {
    <%=id%>MainAccepts = new boolean[] {<%
      print(automata.getMainAutomaton().getAccepts());
    %>};
  }
    
  private int[][] <%=id%>MainTransitions;
  private void init<%=id%>MainTransitions() {
    <%=id%>MainTransitions = new int[][] {<%
      print(automata.getMainAutomaton().getTransitions());
    %>};
  }
<% AutomatonDecl follow=automata.getFollowAutomaton();
    if (follow != null) { %>
  private boolean[] <%=id%>FollowAccepts;
  private void init<%=id%>FollowAccepts() {
    <%=id%>FollowAccepts = new boolean[] {<%
      print(follow.getAccepts());
    %>};
  }
    
  private int[][] <%=id%>FollowTransitions;
  private void init<%=id%>FollowTransitions() {
    <%=id%>FollowTransitions = new int[][] {<%
      print(follow.getTransitions());
    %>};
  }
<% } %>
<% } %>
  
  private final LexerTable<<%=enumType%>> table;
}
