<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%@page import="fr.umlv.tatoo.cc.lexer.lexer.RuleDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.ProductionDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.TerminalDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.VariableDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.EBNFSupport"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.EBNFSyntheticType"%>
<%@page import="fr.umlv.tatoo.cc.tools.tools.RuleInfo"%>
<%@page import="fr.umlv.tatoo.cc.tools.tools.ToolsFactory"%>
<%@page import="fr.umlv.tatoo.cc.tools.generator.ParamDecl"%>
<%@page import="fr.umlv.tatoo.cc.tools.generator.ToolsGeneratorUtils"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Set"%>
<%!/*PARAM*/
Set<Type> variableImports;
Type analyzerProcessor;
Type ruleEnum;
Type terminalEnum;
Type nonTerminalEnum;
Type productionEnum;
Type terminalEvaluator;
Type grammarEvaluator;
Collection<? extends RuleDecl> rules;
Collection<? extends ProductionDecl> productions;
Collection<? extends TerminalDecl> terminals;
Collection<? extends NonTerminalDecl> nonTerminals;
Collection<? extends NonTerminalDecl> starts;
ToolsFactory toolsFactory;

EBNFSupport ebnfSupport;
%>
<%!/*COMMENT*/
java.io.PrintWriter out;%>
<%!String printGrammarEvaluatorCall(ProductionDecl production) {
    StringBuilder builder=new StringBuilder();
    builder.append("grammarEvaluator.").
      append(production.getId()).append('(');
    ToolsGeneratorUtils.foreachNonNull(production.getRight(),toolsFactory.getVariableTypeMap(),
        new ToolsGeneratorUtils.ParamClosure<StringBuilder>() {
      public StringBuilder apply(Type type, String name, StringBuilder builder) {
        return builder.append(name).append(',');
      }
    },builder);
    if (builder.charAt(builder.length()-1)==',')
      builder.setLength(builder.length()-1);
    return builder.append(")").toString();
  }
  
  String insertCastIfNeeded(Type type) {
    if (type.isPrimitive())
      return "";
    return '('+type.getSimpleName()+')';
  }

  String popParam(Type type, boolean insertCast) {
    if (type==null || type.isVoid())
      return "";
    StringBuilder builder=new StringBuilder();
    if (insertCast)
      builder.append(insertCastIfNeeded(type));
    builder.append("stack.pop_").
      append(type.getVMTypeName()).
      append("()");
    return builder.toString();
  }
  String popParamAndAssign(ParamDecl element) {
    return popParamAndAssign(element,true);
  }
   String popParamAndAssign(ParamDecl element, boolean assign) {
     Type type=element.getType();
     return (assign?(type.getSimpleName()+" "+
       element.getName()+'='):"")+popParam(type,true)+';';
   }
   
   private boolean hasComment(Map<RuleDecl,RuleInfo> ruleInfoMap) {
     for(RuleDecl rule:rules) {
       RuleInfo ruleInfo=ruleInfoMap.get(rule);
       if (ruleInfo.isSpawnable() && ruleInfo.getTerminal()==null) {
         return true;
       }
     }
     return false;
   }
   
   private boolean isOneTypeParametrized(Collection<? extends Type> types) {
     for(Type type:types) {
       if (type!=null && type.hasTypeArguments()) {
         return true;
       }
     }
     return false;
   }
   
   private <E> boolean isOneTypeParametrized(Collection<? extends E> elements, Map<? super E,Type> typeMap) {
     for(E element:elements) {
       Type type=typeMap.get(element);
       if (type!=null && type.hasTypeArguments()) {
         return true;
       }
     }
     return false;
   }
   %>
<%
  Map<TerminalDecl, ? extends Set<? extends RuleDecl>> terminalRulesMap=
  toolsFactory.getTerminalRulesMap();
    Map<RuleDecl,Type> ruleTypeMap=toolsFactory.getRuleTypeMap();
    Map<RuleDecl,RuleInfo> ruleInfoMap=toolsFactory.getRuleInfoMap();
    Map<TerminalDecl,Type> terminalTypeMap=toolsFactory.getTerminalTypeMap();
    Map<VariableDecl,Type> variableTypeMap=toolsFactory.getVariableTypeMap();
%>
package <%=analyzerProcessor.getPackageName()%>;

<%
  for(Type type:variableImports) {
%>
  import <%=type.getRawName()%>;
  <%
  }
%>

import <%=ruleEnum.getRawName()%>;
import <%=terminalEnum.getRawName()%>;
import <%=nonTerminalEnum.getRawName()%>;
import <%=productionEnum.getRawName()%>;
import <%=terminalEvaluator.getRawName()%>;
import <%=grammarEvaluator.getRawName()%>;

import fr.umlv.tatoo.runtime.buffer.LexerBuffer;
import fr.umlv.tatoo.runtime.tools.AnalyzerListener;
import fr.umlv.tatoo.runtime.tools.DataViewer;
import fr.umlv.tatoo.runtime.tools.SemanticStack;

/**  This class is called by the parser when
 *  <%="<"%>ol>
 *    <%="<"%>li>a terminal is shifted<%="<"%>/li>
 *    <%="<"%>li>a non terminal is reduced<%="<"%>/li>
 *    <%="<"%>li>a non terminal is accepted<%="<"%>/li>
 *   <%="<"%>/ol>
 *   In that case, depending on the information of the .xtls, terminal and non-terminal
 *   values are pushed/pop from a semantic stack.
 *   
 *   Furthermore, in case of error recovery, values of the stack can be pop out
 *   depending if the last recognized element is a terminal or a non-terminal.
 * 
 *  This class is generated - please do not edit it 
 */
public class <%=analyzerProcessor.getSimpleName()%><%="<B extends LexerBuffer,D>"%>
  implements AnalyzerListener<%="<"%><%=ruleEnum.getSimpleName()%>,B,<%=terminalEnum.getSimpleName()%>,<%=nonTerminalEnum.getSimpleName()%>,<%=productionEnum.getSimpleName()%><%=">"%> {
          
  private final <%=grammarEvaluator.getSimpleName()%> grammarEvaluator;
  private final <%=terminalEvaluator.getSimpleName()%><%="<? super D>"%> terminalEvaluator;
  private final DataViewer<%="<? super B,? extends D>"%> dataViewer;
  private final SemanticStack stack;
  
  protected <%=analyzerProcessor.getSimpleName()%>(<%=terminalEvaluator.getSimpleName()%><%="<? super D>"%> terminalEvaluator, <%=grammarEvaluator.getSimpleName()%> grammarEvaluator, DataViewer<%="<? super B,? extends D>"%> dataViewer, SemanticStack stack) {
    this.terminalEvaluator=terminalEvaluator;
    this.grammarEvaluator=grammarEvaluator;
    this.dataViewer=dataViewer;
    this.stack=stack;
  }
  
  /** Creates a tools listener that redirect accept/shift/reduce and comment to the terminal Evaluator
      and the grammar evaluator..
      This constructor allows to share the same stack between more
      than one parser processor.
      @param terminalEvaluator the terminal evaluator.
      @param grammarEvaluator the grammar evaluator.
      @param stack the stack used by the processor
   */
  public static <%="<B extends LexerBuffer,D>"%> <%=analyzerProcessor.getSimpleName()%><%="<B,D>"%>
    create<%=analyzerProcessor.getSimpleName()%>(<%=terminalEvaluator.getSimpleName()%><%="<? super D>"%> terminalEvaluator, <%=grammarEvaluator.getSimpleName()%> grammarEvaluator, DataViewer<%="<? super B,? extends D>"%> dataViewer, SemanticStack stack) {
    
    return new <%=analyzerProcessor.getSimpleName()%><%="<B,D>"%>(terminalEvaluator,grammarEvaluator,dataViewer,stack);
  }
  
  public void comment(<%=ruleEnum.getSimpleName()%> rule, B buffer) {
<% if (hasComment(ruleInfoMap)) {%>
    D data;
    switch(rule) { <%
   for(RuleDecl rule:rules) {
     RuleInfo ruleInfo=ruleInfoMap.get(rule);
     if (ruleInfo.isSpawnable() && ruleInfo.getTerminal()==null) {
 %>
          case <%=rule.getId()%>:
            data=dataViewer.view(buffer);
            terminalEvaluator.<%=rule.getId()%>(data);
            return;
        <%
   } //endif
   }
 %>
      default:
    }
<% } %>
    throw new AssertionError("unknown rule "+rule);
  }
 
   public void shift(<%=terminalEnum.getSimpleName()%> terminal, <%=ruleEnum.getSimpleName()%> rule, B buffer) {
     D data;
     switch(terminal) { <%
   for(TerminalDecl terminal:terminals) {
     Type type=terminalTypeMap.get(terminal);
     Set<? extends RuleDecl> ruleSet = terminalRulesMap.get(terminal);
 %>
     case <%=terminal.getId()%>: {
         <%
         if (type!=null) {
          %>data=dataViewer.view(buffer);
          <%
         } //endif
             
             if (ruleSet!=null) {
               if (ruleSet.size()==1) {
                 RuleDecl rule=ruleSet.iterator().next();
                 if (type!=null) {
                   if (!type.isVoid()) { %>
                        <%=type%> <%=terminal.getId()%>=<%
                   } //endif
                   %>terminalEvaluator.<%=rule.getId()%>(data);
                   <%
                 } //endif
               } else { //ruleset size greater than one
                 if (type!=null && !type.isVoid()) {
                    %><%=type%> <%=terminal.getId()%>;
                    <%
                 } //endif
                  %>
                  switch(rule) {
                    <%
                    for(RuleDecl rule:ruleSet) { %>
                      case <%=rule.getId()%>:
                      <%
                        if (type!=null) {
                          if(!type.isVoid()) { %>
                           <%=terminal.getId()%>=<%
                          } //endif
                           %>terminalEvaluator.<%=rule.getId()%>(data);
                           <%
                         } //endif
                       %>break;
                        <%
                     } //endfor
                     %>
                     default:
                          throw new AssertionError("Unknown rule " +rule);
                    }
                  <%
                  } //endif
                  if (type!=null && !type.isVoid()) {
                   %>
                   stack.push_<%=type.getVMTypeName()%>(<%=terminal.getId()%>);
                   <%
                  } //endif
                } //endif
              %>
              return;
           }
            <%
        } //endfor
             %>
     }
     throw new AssertionError("unknown terminal "+terminal);
   }
    
    
<% if (isOneTypeParametrized(variableTypeMap.values())) {%>
    @SuppressWarnings("unchecked")
<% } %>
    public void reduce(<%=productionEnum.getSimpleName()%> production) {
      switch(production) { <%
                           for(ProductionDecl production:productions) { 
                                Type type=variableTypeMap.get(production.getLeft());
                                EBNFSyntheticType ebnfType=ebnfSupport.getEBNFTypeMap().get(production);
                         %>
          case <%=production.getId()%>: { // <%= (ebnfType==null)?"not synthetic":ebnfType.name() %>
          <% //if it's not a star/optional production
                    if (ebnfType==null) {
                      boolean notAllNull = ToolsGeneratorUtils.notAllNull(production.getRight(),variableTypeMap);
                      for(ParamDecl param:ToolsGeneratorUtils.getNonNullParameterReverseList(production.getRight(),variableTypeMap)) { %>
                       <%=popParamAndAssign(param,type!=null||notAllNull)%>
                   <% } // endfor           
                      if (type!=null) {
                        if  (type.isVoid()) { %>
                            <%=printGrammarEvaluatorCall(production)%>;
                    <%  } else { %>
                             stack.push_<%=type.getVMTypeName()%>(<%=printGrammarEvaluatorCall(production)%>);
                    <%  } //endif
                      } else {
                        if (notAllNull) { %>
                        <%=printGrammarEvaluatorCall(production)%>;
                    <%  }
                      }
                    } else {          
                                  switch(ebnfType) {
                                     case OPTIONAL_EMPTY:
                                       if (type!=null && !type.isVoid()) {
                         %>
                    stack.push_<%=type.getVMTypeName()%>(<%=type.getDefaultValue()%>);
                  <%
                           }
                                       break;
                                     case OPTIONAL_SINGLETON:
                                       break;
                                     case STAR_EMPTY:
                                       if (type!=null && !type.isVoid()) {
                         %>
                  stack.push_Object(new java.util.ArrayList<%="<Object>"%>());
                <%
                             }
                                                              break;
                                                            case STAR_SINGLETON:
                                                              if (type!=null && !type.isVoid()) { 
                                                                Type elementType=ToolsGeneratorUtils.getNonNullParameterList(production.getRight(),variableTypeMap).get(0).getType();
                           %>
                   java.util.ArrayList<%="<"+elementType.boxIfPrimitive().getSimpleName()+">"%> list=
                     new java.util.ArrayList<%="<"+elementType.boxIfPrimitive().getSimpleName()+">"%>();
                   list.add(<%=popParam(elementType,true)%>);
                   stack.push_<%=type.getVMTypeName()%>(list);
                  <%
                             } //endif
                                                              break;
                                                            case STAR_RECURSIVE_LEFT:
                                                              if (type!=null && !type.isVoid()) {
                                                                ParamDecl list=ToolsGeneratorUtils.getNonNullParameterList(production.getRight(),variableTypeMap).get(0);
                                                                ParamDecl element=ToolsGeneratorUtils.getNonNullParameterList(production.getRight(),variableTypeMap).get(1);
                           %>                  
                    <%=popParamAndAssign(element)%>
                    <%=popParamAndAssign(list)%>
                     <%=list.getName()%>.add(<%=element.getName()%>);
                     stack.push_<%=type.getVMTypeName()%>(<%=list.getName()%>);
                     <%
                       } //endif
                                                    break;
                                                  case STAR_RECURSIVE_RIGHT:
                                                    if (type!=null && !type.isVoid()) {
                                                      ParamDecl list=ToolsGeneratorUtils.getNonNullParameterList(production.getRight(),variableTypeMap).get(1);
                                                      ParamDecl element=ToolsGeneratorUtils.getNonNullParameterList(production.getRight(),variableTypeMap).get(0);
                     %>                  
                    <%=popParamAndAssign(list)%>
                    <%=popParamAndAssign(element)%>
                     <%=list.getName()%>.add(<%=element.getName()%>);
                     stack.push_<%=type.getVMTypeName()%>(<%=list.getName()%>);
                     <%
                       } //endif
                                   break;
                                 case STAR_PASS_THROUGH:
                                   
                                 case ANONYMOUS: 
                                   // nothing
                              } //endswitch
                           } //endif
                     %>  
          }
          return;
          <%
                       } //endfor
                     %>
          default:
             throw new AssertionError("unknown production "+production);
       }
    }

<% if (isOneTypeParametrized(starts,variableTypeMap)) {%>
    @SuppressWarnings("unchecked")
<% } %>
     public void accept(<%=nonTerminalEnum.getSimpleName()%> nonterminal) {
       switch(nonterminal) { <%
                       for(NonTerminalDecl nonTerminal:starts) {
                              Type type=variableTypeMap.get(nonTerminal);
                              String acceptName=ToolsGeneratorUtils.toUpperCase(nonTerminal);
                     %>
           case <%=nonTerminal.getId() %>:
             grammarEvaluator.accept<%= acceptName%>(<%=popParam(type,true) %>);
             return;
           <%
         }
         %>
          default:
       }
        throw new AssertionError("unknown start nonterminal "+nonterminal);
     }

      public void popTerminalOnError(<%=terminalEnum.getSimpleName()%> terminal) {
        switch(terminal) { <% 
          for(TerminalDecl terminal:terminals) {
            Type type=terminalTypeMap.get(terminal);
            %>
            case <%=terminal.getId() %>:
              <%= popParam(type,false) %><%=(type==null || type.isVoid())?"":";"%>
              return;
             <%
          }
        %>
        }
        throw new AssertionError("unknown terminal "+terminal);
      }
 
      public void popNonTerminalOnError(<%=nonTerminalEnum.getSimpleName()%> nonTerminal) {
        switch(nonTerminal) { <% 
          for(NonTerminalDecl nonTerminal:nonTerminals) {
            Type type=variableTypeMap.get(nonTerminal);
            %>
            case <%=nonTerminal.getId() %>:
              <%= popParam(type,false) %><%=(type==null || type.isVoid())?"":";"%>
              return;
             <%
          }
        %>
        }
        throw new AssertionError("unknown nonterminal "+nonTerminal);
      }
}