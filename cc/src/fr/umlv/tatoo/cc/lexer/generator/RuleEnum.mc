<%@ page import="fr.umlv.tatoo.cc.lexer.lexer.RuleDecl,
fr.umlv.tatoo.cc.common.generator.Type,
java.util.Iterator,
java.util.Collection"%>
<%!/*PARAM*/
Collection<RuleDecl> rules;
Type ruleEnum;
%>
package <%=ruleEnum.getPackageName()%>;

/** 
 *  This class is generated - please do not edit it 
 */
public enum <%=ruleEnum.getSimpleName()%> {
<%for(Iterator<? extends RuleDecl> r=rules.iterator();r.hasNext();) {
RuleDecl rule = r.next();
out.print(rule.getId());
if (r.hasNext())
  out.println(',');
}%>;
}
