<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%@page import="fr.umlv.tatoo.cc.lexer.lexer.RuleDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.TerminalDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.ProductionDecl"%>
<%@page import="fr.umlv.tatoo.cc.plugin.StateProposalDecl"%>
<%@page import="fr.umlv.tatoo.cc.plugin.ContextDecl"%>
<%@page import="fr.umlv.tatoo.cc.plugin.PluginGeneratorUtils"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%!/*PARAM*/
Type pluginProposalType;
Type P;
Type T;
Integer stateCount;
StateProposalDecl[] stateProposalArray;
%>
<%!/*COMMENT*/
java.io.PrintWriter out;
%>
<%!

%>
package <%=pluginProposalType.getPackageName()%>;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import fr.umlv.tatoo.runtime.plugin.Context;
import fr.umlv.tatoo.runtime.plugin.StateProposal;

public class <%= pluginProposalType.getSimpleName() %> {
  public static StateProposal<<%=T%>,<%=P%>>[] createStateProposalArray() {
    @SuppressWarnings("unchecked")
    StateProposal<<%=T%>,<%=P%>>[] stateProposalArray=new StateProposal[<%=stateCount%>];
<% for(int i=0;i<stateCount;i++) { %>
    stateProposalArray[<%= i %>]=createStateProposal<%= i %>();
<% } %>
    return stateProposalArray;
  }
<% for(int i=0;i<stateCount;i++) {
         StateProposalDecl stateProposal=stateProposalArray[i]; %>
  private static StateProposal<<%=T%>,<%=P%>> createStateProposal<%= i %> () {
    return new StateProposal<<%=T%>,<%=P%>>(
      createWordProposalList<%= i %>(),
      createVariantContextMap<%= i %>()); 
  }
  
  private static List<String> createWordProposalList<%= i %> () {
<%   if (stateProposal.getWordProposalSet().isEmpty()) { %>
    return java.util.Collections.<String>emptyList();
<%   } else { %>
    return java.util.Arrays.asList(<%= PluginGeneratorUtils.join(", ",stateProposal.getWordProposalSet()) %>);
<%   } %>
  }
  
  private static Map<<%=T%>,List<Context<<%=P%>>>> createVariantContextMap<%= i %> () {
<%   if (stateProposal.getVariantContextMap().isEmpty()) {%>
     return java.util.Collections.<<%=T%>,List<Context<<%=P%>>>>emptyMap();
<%   } else { %>
    HashMap<<%=T%>,List<Context<<%=P%>>>> map=
      new HashMap<<%=T%>,List<Context<<%=P%>>>>();
<%     for(TerminalDecl terminal:stateProposal.getVariantContextMap().keySet()) { %>
    map.put(<%=T%>.<%=terminal.getId()%>, createContextVariant_<%= i %><%= terminal.getId()%>List());
<%     } %>
    return map;
<%   } %>
  }
  
<%   for(Map.Entry<TerminalDecl,List<ContextDecl>> entry:stateProposal.getVariantContextMap().entrySet()) {
           TerminalDecl terminal=entry.getKey(); 
           List<ContextDecl> contextList=entry.getValue(); 
           Iterator<ContextDecl> it=contextList.iterator(); %>
  private static List<Context<<%=P%>>> createContextVariant_<%= i %><%= terminal.getId()%>List() {
<%     if (!it.hasNext()) { %>
    return java.util.Collections.<Context<<%=P%>>>emptyList();        
<%     } else { %>
    @SuppressWarnings("unchecked") 
    List<Context<<%=P%>>> contextList=java.util.Arrays.<Context<<%=P%>>>asList(
<%       while(it.hasNext()) {
               ContextDecl context=it.next(); 
               if (context instanceof ContextDecl.ShiftDecl) { 
                  ContextDecl.ShiftDecl shiftContext=(ContextDecl.ShiftDecl)context; %>
      new Context.Shift<<%=P%>>(<%=P%>.<%=shiftContext.getProduction().getId()%>, <%=shiftContext.getDot()%>)<%
               } else { 
                  ContextDecl.ReduceDecl reduceContext=(ContextDecl.ReduceDecl)context; %>
      new Context.Reduce<<%=P%>>(<%=P%>.<%=reduceContext.getProduction().getId()%>)<%
               } 
               if (it.hasNext()) { %>,
<%         } %>                      
<%       } %>
    );
    return contextList;
<%     } %>
  }
<%   } %>
<% } %>
}