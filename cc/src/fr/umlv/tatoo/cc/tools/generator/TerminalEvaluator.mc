<%@page import="fr.umlv.tatoo.cc.tools.tools.RuleInfo"%>
<%@page import="fr.umlv.tatoo.cc.lexer.lexer.RuleDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.TerminalDecl"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Collection"%>
<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%!/*PARAM*/
Type terminalEvaluator;
Collection<Type> terminalImports;
Map<RuleDecl,RuleInfo> ruleInfoMap;
Map<RuleDecl,Type> ruleTypeMap;
%>
<%!/*COMMENT*/
java.io.PrintWriter out;
%>package <%=terminalEvaluator.getPackageName()%>;

<%  for(Type iMport:terminalImports) {%>
import <%=iMport.getRawName()%>;
<%} %>

/** 
 *  @param <%="<"%>D> data type passed by the lexer listener.
 *
 *  This class is generated - please do not edit it 
 */
public interface <%=terminalEvaluator.getSimpleName()%><%="<"%>D> {
<% for (Map.Entry<RuleDecl,RuleInfo> entry: ruleInfoMap.entrySet()) {
     RuleInfo ruleInfo=entry.getValue();
     if (ruleInfo.isSpawnable()) {
       Type type;
       if (ruleInfo.getTerminal()==null) {
         type=Type.VOID; // comment, type is necessary void
       } else {
         type=ruleTypeMap.get(entry.getKey());
       }
       if (type!=null) {%>
  /** This method is called when the rule <%="<"%>code><%=entry.getKey().getId()%><%="<"%>/code> is recognized by the lexer.
   *  @param data the data sent by the lexer, in general, the
   *         {@link fr.umlv.tatoo.runtime.buffer.TokenBuffer#view a view of the token buffer} or the buffer itself.
<%       if(!type.isVoid()) { %>  
   *  @return the value associated with the terminal spawn for the rule.
<%       } %>
   */
  public <%=type.getSimpleName()%> <%=entry.getKey().getId()%>(D data);
<%     }
     }
   }%>
}
