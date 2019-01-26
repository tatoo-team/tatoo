<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.TerminalDecl"%>
<%!/*PARAM*/
Type terminalNode;
Type valueType;
Type terminalEnum;
TerminalDecl terminal;
Type abstractToken;
Type parent;
%>
<%!/*COMMENT*/
java.io.PrintWriter out;
%>
package <%=terminalNode.getPackageName()%>;

import <%=abstractToken.getRawName()%>;
import <%=terminalEnum.getRawName()%>;

<% if (valueType.isQualifiedType()) { %>
import <%=valueType.getRawName()%>;
<% } %>

/**
 *
 *  This class is generated - please do not edit it 
 */
public class <%=terminalNode.getSimpleName()%> extends <%=abstractToken.getSimpleRawName()%><<%=valueType.boxIfPrimitive().getSimpleName()%>> {
<% if (!valueType.isVoid()) {%>
    private final <%=valueType.getSimpleName()%> <%=terminal.getId()%>;
    
    public <%=terminalNode.getSimpleName()%>(<%=valueType.getSimpleName()%> <%=terminal.getId()%>) {
      this.<%=terminal.getId()%>=<%=terminal.getId()%>;
    } 
<% } else { %>    
    public <%=terminalNode.getSimpleName()%>() {
      // default constructor
    } 
<% } %>

    @Override
    public <%=valueType.boxIfPrimitive().getSimpleName()%> getValue() {
<% if (!valueType.isVoid()) { %>
      return <%=terminal.getId()%>;
<% } else { %>
      return null;
<% } %>
    }
    
    @Override
    public <%=terminalEnum.getSimpleName()%> getKind() {
      return <%=terminalEnum.getSimpleName()%>.<%=terminal.getId()%>;
    }
    
<% if(parent!=null) { %>
    @Override
    public <%=parent.getSimpleName()%> getParent() {
      return (<%=parent.getSimpleName()%>)super.getParent();
    }
<%}%>

   @Override
   public <_R,_P,_E extends Exception> _R accept(Visitor<? extends _R, ? super _P, ? extends _E> visitor, _P param) throws _E {
     return visitor.visit(this,param);
   }
   /*
   @Override
   public String toString() {
     return getValue().toString();
   }*/
}
