<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%!/*PARAM*/
Type abstractToken;
Type abstractNode;
Type terminalEnum;
%>
<%!/*COMMENT*/
java.io.PrintWriter out;
%>

package <%=abstractToken.getPackageName()%>;

import java.util.Collections;
import java.util.List;

import fr.umlv.tatoo.runtime.ast.Token;
import <%=terminalEnum.getRawName()%>;

/** Common abstract class of all tokens.
 *
 *  This class is generated - please do not edit it 
 */
public abstract class <%=abstractToken.getSimpleName()%><V> extends <%=abstractNode.getSimpleName()%> implements Token {
  @Override
  public boolean isToken() {
    return true;
  }
  
  public abstract V getValue();
  
  public abstract <%=terminalEnum.getSimpleName()%> getKind();
  
  @Override
  public String toString() {
    return "["+getKind()+' '+getValue()+']';
  }
  
  @Override
  public List<<%=abstractNode.getSimpleName()%>> nodeList() {
    return Collections.emptyList();
  }
}
