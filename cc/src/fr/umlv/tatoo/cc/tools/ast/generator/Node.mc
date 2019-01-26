<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Set"%>
<%!/*PARAM*/
Type abstractNode;
Type abstractInnerNode;
Type productionEnum;
Map<String,Type> attributeMap;
Set<Type> attributeImportSet;
%>
<%!/*COMMENT*/
java.io.PrintWriter out;
%><%!
  private static String capitalize(String name) {
    return Character.toUpperCase(name.charAt(0))+((name.length()!=0)?name.substring(1):"");
  }
%>

package <%=abstractNode.getPackageName()%>;

import java.util.List;

<%
  for(Type iMport : attributeImportSet) { %>
import <%=iMport.getRawName()%>;
<%
  }
%>

/** Common class for all node, specialized for the AST.
 *
 *  This class is generated - please do not edit it 
 */
public abstract class <%=abstractNode.getSimpleName()%> implements fr.umlv.tatoo.runtime.ast.Node {
  <%
     for(Map.Entry<String,Type> entry: attributeMap.entrySet()) {
       String name = entry.getKey();
       Type type = entry.getValue();
  %>
  private <%=type.getSimpleName()%> <%=name%>Attribute;
  
  public <%=type.getSimpleName()%> get<%=capitalize(name)%>Attribute() {
    return <%=name%>Attribute;
  }
  public void set<%=capitalize(name)%>Attribute(<%=type.getSimpleName()%> <%=name%>Attribute) {
    this.<%=name%>Attribute = <%=name%>Attribute;
  }
  <%     
     }
  %>

  <%=abstractInnerNode.getSimpleName()%> parent;

  public <%=abstractNode.getSimpleName()%> getParent() {
    return parent;
  }
  
  public abstract List<<%=abstractNode.getSimpleName()%>> nodeList();
  
  /** call a visitor method depending of the real type of the current node.
   * 
   * @param <_R> type of the return value.
   * @param <_P> type of the argument.
   * @param <_E> type of the exception possibly raised by a visit method.
   * @param visitor visitor to call.
   * @param param parameter send to the visitor.
   * @return the return value of the selected visitor method.
   * @throws _E could propagate a generic exception raised by the visitor.
   */
  public abstract <_R,_P,_E extends Exception> _R accept(Visitor<? extends _R, ? super _P, ? extends _E> visitor, _P param) throws _E;
}