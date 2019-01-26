<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%@page import="fr.umlv.tatoo.cc.tools.generator.ToolsGeneratorUtils"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashSet"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.VariableDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.ProductionDecl"%>
<%@page import="fr.umlv.tatoo.cc.tools.ast.generator.ASTGeneratorUtils"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.EBNFSupport"%>

<%!/*PARAM*/
Type productionNode;
Type nonTerminalNode;
ProductionDecl production;
Map<VariableDecl, Type> variableTypeMap;
Type productionEnum;
Type abstractInnerNode;
Type abstractNode;
Type parent;
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
  
  private boolean hasStar(ProductionDecl production) {
    for(VariableDecl variable:production.getRight()) {
      if (ebnfSupport.getStarNonTerminals().contains(variable))
        return true;
    }
    return false;
  } 
%>

package <%=productionNode.getPackageName()%>;

<% if (nonTerminalNode==null) {%>
import <%=abstractInnerNode.getRawName()%>;
<%}%>
import <%=productionEnum.getRawName()%>;
<% if (hasStar(production)) {%>
import java.util.List;
<% } %>

/**
 *
 *  This class is generated - please do not edit it 
 */
public class <%=productionNode.getSimpleName()%> extends <%=((nonTerminalNode==null)?abstractInnerNode:nonTerminalNode).getSimpleName()%> {
   <%
    // fields
    ToolsGeneratorUtils.foreachNonNull(production.getRight(),variableTypeMap, new ToolsGeneratorUtils.VarParamClosure<Void>(ebnfSupport) {
        public Void apply(Type type,String name,Void acc) {
          %>private <%=type.getSimpleName()%> <%=name%>;
         <%     
          return null;
        }
    },null);
    %>
    <%
    //constructor
    %>public <%=productionNode.getSimpleName()%>(<%
    ToolsGeneratorUtils.foreachNonNull(production.getRight(),variableTypeMap, new ToolsGeneratorUtils.VarParamClosure<Boolean>(ebnfSupport) {
        public Boolean apply(Type type,String name,Boolean initial) {
          %><%=(initial)?"":","%><%=type.getSimpleName()%> <%=name%><%     
          return false;
        }
    },true);
    %>) {
    <%
    boolean hasField=ToolsGeneratorUtils.foreachNonNull(production.getRight(),variableTypeMap, new ToolsGeneratorUtils.VarParamClosure<Boolean>(ebnfSupport) {
        public Boolean apply(Type type,String name,Boolean notUsed) {
          %>this.<%=name%>=reparent(null,<%=name%>);
          <%     
          return true;
        }
    },false);
    if (!hasField)  {
%>
      //no field to initialize
<% } %>
    }
    
    @Override
    public <%=productionEnum.getSimpleName()%> getKind() {
      return <%=productionEnum.getSimpleName()%>.<%=production.getId()%>;
    }
    
<% if(nonTerminalNode==null && parent!=null) { %>
    @Override
    public <%=parent.getSimpleName()%> getParent() {
      return (<%=parent.getSimpleName()%>)super.getParent();
    }
<%}%>
    
    <%    
    // getter+setter    
    ToolsGeneratorUtils.foreachNonNull(production.getRight(),variableTypeMap, new ToolsGeneratorUtils.VarParamClosure<Void>(ebnfSupport) {
        public Void apply(Type type,String name,Void acc) {%>
          public <%=type.getSimpleName()%> get<%=capitalize(name)%>() {
            return <%=name%>;
          }
          public void set<%=capitalize(name)%>(<%=type.getSimpleName()%> <%=name%>) {
            this.<%=name%>=reparent(this.<%=name%>,<%=name%>);
          }
          <%     
          return null;
        }
    },null);
    %>
    
  @Override
  public <_R,_P,_E extends Exception> _R accept(Visitor<? extends _R, ? super _P, ? extends _E> visitor, _P param) throws _E {
    return visitor.visit(this,param);
  }
   
   @Override
   protected final <%=abstractNode.getSimpleName()%> subNodeAt(int index) {
<% for(VariableDecl variable:production.getRight()) {
         Type type=variableTypeMap.get(variable);
         if (type!=null && !type.isVoid()) {
%>
     int _index=index;
<% 
            break;
         } 
    }
%><%
      boolean inSwitch=ToolsGeneratorUtils.foreachNonNull(production.getRight(),variableTypeMap,
        new ToolsGeneratorUtils.VarParamClosure<Boolean>(ebnfSupport) {
        boolean inSwitch; // last was a case of a switch
        boolean afterList; // last was a list
        int caseCounter;  // case number
        String varName;  // last variable name
        public Boolean apply(Type type,VariableDecl variable,String name,Boolean notUsed) {
          if (afterList) {%>
            _index -= <%=varName%>.size();
          <%
          }
          varName=name;
          afterList=false;
          boolean isStar=ebnfSupport.getStarNonTerminals().contains(variable);
          boolean isOptional=ebnfSupport.getOptionalNonTerminals().contains(variable);
          if (isStar || isOptional) {
            if (inSwitch) {
              // close switch
              %> } 
               _index -= <%=caseCounter%>;
            <%
                 caseCounter=0;
                 inSwitch=false;
            }
          }
          if (isStar) {
              %>
               if (_index < <%=varName%>.size()) {
                 return <%=varName%>.get(_index);
               }
            <%
            afterList=true;
          } else
          if (isOptional) {
            %>
            if (_index == 0 && <%=varName%> != null) {
              return <%=varName%>;
            }
            if (<%=varName%> != null)
              _index--;
            <%
            afterList=false;
          } else { 
            if (!inSwitch) {
            %>
              switch(_index) {
            <%
              inSwitch=true;
            }
          %>
            case <%=caseCounter%>:
              return <%=varName%>;
          <% 
            caseCounter++;    
          }
          return inSwitch;
        }
      },false);
      
      // needs to close the switch ?
      if (inSwitch) {
        %> }
        <%
      }
      %>
      throw new IndexOutOfBoundsException("invalid index "+index);
   }
   
   @Override
   protected final <%=abstractNode.getSimpleName()%> subNodeAt(int index, <%=abstractNode.getSimpleName()%> node) {
<% for(VariableDecl variable:production.getRight()) {
         Type type=variableTypeMap.get(variable);
         if (type!=null && !type.isVoid()) {
%>
     int _index=index;
<% 
            break;
         } 
    }
%><%
      inSwitch=ToolsGeneratorUtils.foreachNonNull(production.getRight(),variableTypeMap,
        new ToolsGeneratorUtils.VarParamClosure<Boolean>(ebnfSupport) {
        boolean inSwitch; // last was a case of a switch
        boolean afterList; // last was a list
        int caseCounter;  // case number
        String varName;  // last variable name
        public Boolean apply(Type type,VariableDecl variable,String name,Boolean notUsed) {
          if (afterList) {%>
            _index -= <%=varName%>.size();
          <%
          }
          varName=name;
          afterList=false;
          boolean isStar=ebnfSupport.getStarNonTerminals().contains(variable);
          boolean isOptional=ebnfSupport.getOptionalNonTerminals().contains(variable);
          if (isStar || isOptional) {
            if (inSwitch) {
              // close switch
              %> } 
               _index -= <%=caseCounter%>;
            <%
                 caseCounter=0;
                 inSwitch=false;
            }
          }
          if (isStar) {
              %>
               if (_index < <%=varName%>.size()) {
                 return <%=varName%>.set(_index, (<%= type.getTypeArguments().get(0) %>)node);
               }
            <%
            afterList=true;
          } else
          if (isOptional) {
            %>
            if (_index == 0 && <%=varName%> != null) {
              <%= type.getSimpleName() %> old=<%=varName%>;
              this.<%=varName%>=(<%= type.getSimpleName() %>)node;
              return old;
            }
            if (<%=varName%> != null)
              _index--;
            <%
            afterList=false;
          } else { 
            if (!inSwitch) {
            %>
              switch(_index) {
            <%
              inSwitch=true;
            }
          %>
            case <%=caseCounter%>: {
              <%= type.getSimpleName() %> old=<%=varName%>;
              this.<%=varName%>=(<%= type.getSimpleName() %>)node;
              return old;
            }
          <% 
            caseCounter++;    
          }
          return inSwitch;
        }
      },false);
      
      // needs to close the switch ?
      if (inSwitch) {
        %> }
        <%
      }
      %>
      throw new IndexOutOfBoundsException("invalid index "+index);
   }   
   
   @Override
   protected final int subNodesSize() {
     <%
      final StringBuilder varBuilder=new StringBuilder();
      int constant=ToolsGeneratorUtils.foreachNonNull(production.getRight(),variableTypeMap,
        new ToolsGeneratorUtils.VarParamClosure<Integer>(ebnfSupport) {
        public Integer apply(Type type,VariableDecl variable,String varName,Integer constant) {
          if (ebnfSupport.getStarNonTerminals().contains(variable)) {
              if (varBuilder.length()>0)
                varBuilder.append('+');
              varBuilder.append(varName).append(".size()");
              return constant;
         } else
          if (ebnfSupport.getOptionalNonTerminals().contains(variable)) {
              if (varBuilder.length()>0)
                varBuilder.append('+');
              varBuilder.append("((").append(varName).append("==null)?0:1)");
              return constant;
          } else   
          {
              return constant+1;
          }
        }
    },0);
      %>
      return <%=constant%><%
        if (varBuilder.length()!=0) {
          %>+<%=varBuilder%><%
        }
      %>;
   }
   
   /*
   @Override
   public String toString() { 
     return <%=
     ToolsGeneratorUtils.foreach(production.getRight(),variableTypeMap, new ToolsGeneratorUtils.VarParamClosure<String>(ebnfSupport) {
        public String apply(Type type,String name,String acc) {
          if (type==null) {
            if (acc.length()==0) {
              return '"'+name+'"';
            } else {
              return acc+" + "+"\" \""+'"'+name+'"';
            }
          } else {
            if (acc.length()==0) {
              return "(("+name+"==null)?\"\":"+name+".toString())"; 
            } else {
              return acc+" + "+"\" \""+"(("+name+"==null)?\"\":"+name+".toString())";
            }
          }   
       }
     },"")
     
     %>;
   }*/
}
