<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%@page import="fr.umlv.tatoo.cc.tools.generator.ToolsGeneratorUtils"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Map"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.VariableDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.ProductionDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.TerminalDecl"%>
<%@page import="fr.umlv.tatoo.cc.tools.ast.generator.NonTerminalKind"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.EBNFSupport"%>

<%!/*PARAM*/
Type visitorCopierNode;
Type visitorNode;
Type abstractNode;
Collection<? extends ProductionDecl> productions;
Collection<? extends NonTerminalDecl> nonTerminals;
Collection<? extends TerminalDecl> terminals;
Map<VariableDecl, Type> variableTypeMap;
Map<TerminalDecl,Type> terminalValueTypeMap;
Map<NonTerminalDecl,NonTerminalKind> nonTerminalKindMap;
Map<ProductionDecl,Type> productionTypeMap;
EBNFSupport ebnfSupport;
%>
<%!/*COMMENT*/
java.io.PrintWriter out;
%><%!
  private static String capitalize(String name) {
    String[] subNames=name.split("_");
    StringBuilder builder=new StringBuilder();
    for(String subName:subNames) {
      switch(subName.length()) {
        case 0:
          break;
        case 1:
          builder.append(Character.toUpperCase(subName.charAt(0)));
          break;
        default:
          builder.append(Character.toUpperCase(subName.charAt(0))).append(subName.substring(1));    
      }
    }
    return builder.toString();
  }
%>

package <%=visitorCopierNode.getPackageName()%>;
<% if (!ebnfSupport.getStarNonTerminals().isEmpty()) { %>
import java.util.ArrayList;
import java.util.Iterator;
<% } %>

/**
 *
 *  This class is generated - please do not edit it 
 */
public class <%=visitorCopierNode.getSimpleName()%><_P,_E extends Exception> extends <%=visitorNode.getSimpleName()%><<%=abstractNode.getSimpleName()%>,_P,_E> {
  <%
    for(final ProductionDecl production:productions) {
      NonTerminalDecl nonTerminal=production.getLeft();
      NonTerminalKind nonTerminalKind=nonTerminalKindMap.get(nonTerminal);
      if (nonTerminalKind==NonTerminalKind.EBNF_DEFINED)
        continue;
      final Type productionType=productionTypeMap.get(production);
%>
   @Override
   public <%=abstractNode.getSimpleName()%> visit(<%=productionType.getSimpleName()%> <%=production.getId()%>,_P _param) throws _E {
     <%     
     ToolsGeneratorUtils.foreachNonNull(production.getRight(),variableTypeMap,
        new ToolsGeneratorUtils.VarParamClosure<Void>(ebnfSupport) {
          public Void apply(Type type,VariableDecl variable,String name,Void notUsed) {
            if (ebnfSupport.getStarNonTerminals().contains(variable)) {%>
     ArrayList<<%=type.getTypeArguments().get(0).getSimpleName()%>> <%=name%>=new ArrayList<<%=type.getTypeArguments().get(0).getSimpleName()%>>();
     for(Iterator<<%=type.getTypeArguments().get(0).getSimpleName()%>> it=<%=production.getId()%>.get<%=capitalize(name)%>().iterator(); it.hasNext();) {
       <%=name%>.add((<%=type.getTypeArguments().get(0).getSimpleName()%>)it.next().accept(this, _param));
     }
     <%
            } else {%>
     <%=type.getSimpleName()%> <%=name%>=(<%=type.getSimpleName()%>)<%=production.getId()%>.get<%=capitalize(name)%>().accept(this, _param);
     <%
            }
            return null;
          }
        }, null);%>     
     return new <%=productionType.getSimpleName()%>(<%=
       ToolsGeneratorUtils.foreachNonNull(production.getRight(),variableTypeMap,
         new ToolsGeneratorUtils.VarParamClosure<StringBuilder>(ebnfSupport) {
           public StringBuilder apply(Type type,VariableDecl variable,String name,StringBuilder builder) {
             if (builder.length()!=0)
               builder.append(", ");
             return builder.append(name);
           }
         }, new StringBuilder())
     %>);
   }
   <%
  }
  %>
  <%
    for(TerminalDecl terminal:terminals) {
      Type terminalNodeType=variableTypeMap.get(terminal);
      if (terminalNodeType==null)
        continue;
      Type terminalType=terminalValueTypeMap.get(terminal);
      %>
   @Override
   public <%=abstractNode.getSimpleName()%> visit(<%=terminalNodeType.getSimpleName()%> <%=terminal.getId()%>,_P _param) throws _E {<%
     if (terminalType.isVoid()) {%>
     return new <%=terminalNodeType.getSimpleName()%>();<%
     } else { %>
     return new <%=terminalNodeType.getSimpleName()%>(<%=terminal.getId()%>.getValue());<%
     }%>
   }
   <%
  }
  %>
}
