<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%!/*PARAM*/
Type nonTerminalNode;
Type productionEnum;
Type abstractInnerNode;
Type parent;
%>
<%!/*COMMENT*/
java.io.PrintWriter out;
%>

package <%=nonTerminalNode.getPackageName()%>;

import <%=abstractInnerNode.getRawName()%>;
import <%=productionEnum.getRawName()%>;

/**
 *
 *  This class is generated - please do not edit it 
 */
public abstract class <%=nonTerminalNode.getSimpleName()%> extends <%=abstractInnerNode.getSimpleName()%> {
  public abstract <%=productionEnum.getSimpleName()%> getKind();
  
  <% if(parent!=null) { %>
  @Override
  public <%=parent.getSimpleName()%> getParent() {
    return (<%=parent.getSimpleName()%>)super.getParent();
  }
  <%}%>
}
