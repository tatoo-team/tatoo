<%-- Motocity file for ParserDataTable using switches --%>
<%@ page import="java.util.Collection,
java.util.List,
fr.umlv.tatoo.cc.lexer.lexer.RuleDecl,
fr.umlv.tatoo.cc.lexer.lexer.LexerMap.Switch,
fr.umlv.tatoo.cc.lexer.lexer.SwitchAutomata,
fr.umlv.tatoo.cc.lexer.regex.RegexSwitch,
fr.umlv.tatoo.cc.common.generator.Type,
fr.umlv.tatoo.cc.lexer.generator.SwitchCase"%>
<%!/*PARAM*/
Collection<RuleDecl> rules;
Switch lexerMapSwitch;
Type ruleEnum;
Type lexerSwitch;
%>
<%
String enumType=ruleEnum.getSimpleName();
String switchType=lexerSwitch.getSimpleName();
%>
package <%=lexerSwitch.getPackageName()%>;

import <%=ruleEnum.getName()%>;
import fr.umlv.tatoo.runtime.lexer.LexerTable;
import fr.umlv.tatoo.runtime.lexer.rules.RuleData;
import fr.umlv.tatoo.runtime.lexer.rules.RegexTable;
import java.util.EnumMap;

/** 
 *  This class is generated - please do not edit it 
 */
public class <%=switchType%> {

  public static LexerTable<<%=enumType%>> createTable() {
    return new <%=switchType%>().table;
  }

  private <%=switchType%>() {
<% for(Map.Entry<RuleDecl,SwitchAutomata> entry:lexerMapSwitch.getAutomataMap().entrySet()) { %>
    RuleDecl rule=entry.getKey();
    SwitchAutomata automata=entry.getValue();
    RegexTable <%=rule.getId()%>Main = new RegexTable() {
      public int getTransition(int state,int next) {
      	switch(state) {
<%   for(int state=0;state<automata.getMainRegexSwitch().getStateNb();state++) { %>
          case <%=state%>:
            switch(next) {
<%     for(SwitchCase transition: automata.getMainRegexSwitch().getTransitions(state))  { 
          if (transition.isDefault()) { %>
              default:
                return <%=transition.getState()%>;
<%       } else { 
            for(int label:transition.getLabels()) { %>
              case <%=label%>:
<%         } %>
                return <%=transition.getState()%>;
<%        }
        } %>
            }
<% }%>
        }
      	return -1;
      }
      public boolean accept(int i) {
        switch(i) {
<% boolean[] accepts = automata.getMainRegexSwitch().getAccepts();
    for(int v=0;v<accepts.length;v++) { 
      if (accepts[v]) {%>
          case <%=v%>:
<%   }
    } %>
            return true;
          default:
            return false;
        }
      }
      public int getStateNumber() {
        return <%=automata.getMainRegexSwitch().getStateNb()%>;
      }
      public boolean noOut(int currentState) {
        switch(currentState) {
<% List<Integer> noOut = automata.getMainRegexSwitch().getNoOut();
    if (noOut.size()!=0) {
      for(int nout:noOut) { %>
         case <%=nout%>:
<%   } %>
           return true;
<% } %> 
         default:
            return false;
        }
      }
      public int getStart() {
        return <%=automata.getMainRegexSwitch().getFirstState()%>;
      }
    };
<% RegexSwitch follow = automata.getFollowRegexSwitch();
    if (follow != null) { %>
    RegexTable <%=rule.getId()%>Follow = new RegexTable() {
      public int getTransition(int state,int next) {
      	switch(state) {
<%   for(int state=0;state<follow.getStateNb();state++) { %>
          case <%=state%>:
            switch(next) {
<%     for(SwitchCase transition: follow.getTransitions(state))  { 
          if (transition.isDefault()) { %>
              default:
                return <%=transition.getState()%>;
<%       } else { 
            for(int label:transition.getLabels()) { %>
              case <%=label%>:
<%         } %>
                return <%=transition.getState()%>;
<%       }
        } %>
            }
<% } %>
        }
      	return -1;
      }
      public boolean accept(int i) {
        switch(i) {
<% boolean[] followAccepts = follow.getAccepts();
    for(int v=0;v<followAccepts.length;v++) { 
      if (accepts[v]) {%>
          case <%=v%>:
<%   }
    } %>
            return true;
          default:
            return false;
        }
      }
      public int getStateNumber() {
        return <%=follow.getStateNb()%>;
      }
      public boolean noOut(int currentState) {
        switch(currentState) {
<% List<Integer> followNoOut = follow.getNoOut();
    if (followNoOut.size()!=0) {
      for(int nout:followNoOut) { %>
         case <%=nout%>:
<%   } %>
           return true;
<% } %> 
         default:
            return false;
        }
      }
      public int getStart() {
        return <%=follow.getFirstState()%>;
      }
    };

    RuleData <%=rule.getId()%> = new RuleData(<%=rule.getId()%>Main, <%=rule.getId()%>Follow, <%=rule.getPriority()%>, <%=rule.isBeginningOfLineRequired()%>);
<% } else { /* if (follow!=null) */ %> 
    RuleData <%=rule.getId()%> = new RuleData(<%=rule.getId()%>Main, null, <%=rule.getPriority()%>, <%=rule.isBeginningOfLineRequired()%>);
<%   } %>
<%   } %>

    EnumMap<<%=enumType%>,RuleData> datas = new EnumMap<<%=enumType%>,RuleData>(<%=enumType%>.class);
<% for(RuleDecl rule:rules) { %>
    datas.put(<%=ruleEnum.getSimpleName()%>.<%=rule.getId()%>, <%=rule.getId()%>);
<% } %>
    table = new LexerTable<<%=enumType%>>(datas);
  }  
  private final LexerTable<<%=enumType%>> table;
}
