<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%@page import="fr.umlv.tatoo.cc.tools.tools.RuleInfo"%>
<%@page import="fr.umlv.tatoo.cc.lexer.lexer.RuleDecl"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.Iterator"%>
<%@page import="fr.umlv.tatoo.cc.common.generator.ObjectId"%>
<%!/*PARAM*/
Map<RuleDecl,RuleInfo> ruleInfoMap;
Type toolsDataTable;
Type ruleEnum;
Type terminalEnum;
%>
<%!/*COMMENT*/
java.io.PrintWriter out;
%>
<%!
HashSet<RuleDecl> spawns, discards, unconditionals;
void buildSets() {
  spawns = new HashSet<RuleDecl>();
  discards = new HashSet<RuleDecl>();
  unconditionals = new HashSet<RuleDecl>();
  for(Map.Entry<RuleDecl,RuleInfo> entry:ruleInfoMap.entrySet()) {
    if (entry.getValue().isDiscardable())
      discards.add(entry.getKey());
    if (entry.getValue().isSpawnable())
      spawns.add(entry.getKey());
    if (entry.getValue().isAlwaysActive())
      unconditionals.add(entry.getKey());
  }
}

void enumSet(HashSet<? extends ObjectId> set) {
  String name=ruleEnum.getSimpleName();
  if (set.isEmpty()) {
    out.printf("EnumSet.noneOf(%s.class)",name);
  }
  else if (set.equals(ruleInfoMap.keySet())) {
    out.printf("EnumSet.allOf(%s.class)",name);
  }
  else {
    Iterator<? extends ObjectId> i = set.iterator();
    out.printf("EnumSet.of(%s.%s",name,i.next().getId());
    while(i.hasNext()) {
      out.printf(",%s.%s",name,i.next().getId());
    }
    out.print(")");
  } 
}
%>
<% buildSets();
String ruleSetType="EnumSet<"+ruleEnum.getSimpleName()+">";
String ruleTermTmpl="<"+ruleEnum.getSimpleName()+","+terminalEnum.getSimpleName()+">";
String mapType="EnumMap"+ruleTermTmpl;
%>
package <%=toolsDataTable.getPackageName()%>;

import fr.umlv.tatoo.runtime.tools.ToolsTable;

import java.util.EnumMap;
import java.util.EnumSet;

import <%=ruleEnum.getName()%>;
import <%=terminalEnum.getName()%>;

public class <%=toolsDataTable.getSimpleName()%> {
  public static ToolsTable<%=ruleTermTmpl%> createToolsTable() {
      <%=ruleSetType%> spawns = <%enumSet(spawns);%>;
      <%=ruleSetType%> discards = <%enumSet(discards);%>;
      <%=mapType%> terminal = new <%=mapType%>(<%=ruleEnum.getSimpleName()%>.class);
      <% for(Map.Entry<RuleDecl,RuleInfo> entry:ruleInfoMap.entrySet()) {
      if (entry.getValue().getTerminal()!=null) {%>
        terminal.put(<%=ruleEnum.getSimpleName()%>.<%=entry.getKey().getId()%>,<%=terminalEnum.getSimpleName()%>.<%=entry.getValue().getTerminal().getId()%>);
      <% }
      } %>
      <%=ruleSetType%> unconditionals = <%enumSet(unconditionals);%>;
      return new ToolsTable<%=ruleTermTmpl%>(spawns,discards,unconditionals,terminal);
  }
}