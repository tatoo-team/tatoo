<%@page import="java.util.Iterator"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.VersionDecl"%>
<%@page import="java.util.Collection"%>
<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%!/*PARAM*/
Type versionEnum;
Collection<? extends VersionDecl> versions;
%>

package <%=versionEnum.getPackageName()%>;

/** 
 *  This class is generated - please do not edit it 
 */
public enum <%=versionEnum.getSimpleName()%> {
  <%
  Iterator<? extends VersionDecl> iterator = versions.iterator();
  out.print(iterator.next().getId());
  while(iterator.hasNext()) {
    out.println(',');
    out.print(iterator.next().getId());
  }
  out.println();
  %>;
}