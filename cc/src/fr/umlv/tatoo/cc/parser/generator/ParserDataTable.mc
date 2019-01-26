<%@page import="fr.umlv.tatoo.cc.parser.parser.BranchTableActionDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.parser.RegularTableActionDecl"%>
<%@page import="java.util.Iterator"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.VariableDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.table.StateMetadataDecl"%>
<%@page import="java.util.Set"%>
<%@page import="fr.umlv.tatoo.cc.parser.parser.ActionDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.parser.VersionedActionDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.parser.BranchActionDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.parser.ErrorActionDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.parser.EnterActionDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.parser.ShiftActionDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.parser.ReduceActionDecl"%>
<%@page import="java.util.Map"%>
<%@page import="fr.umlv.tatoo.cc.parser.table.ParserTableDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.ProductionDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.TerminalDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.VersionDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl"%>
<%@page import="java.util.Collection"%>
<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>

<%!/*PARAM*/
Type nonTerminalEnum;
Collection<? extends NonTerminalDecl> nonTerminals;
Type versionEnum;
Collection<? extends VersionDecl> versions;
Type terminalEnum;
Collection<? extends TerminalDecl> terminals;
Type productionEnum;
Collection<? extends ProductionDecl> productions;
Type parserDataTable;
ParserTableDecl table;
Set<StateMetadataDecl> metadataSet;
%>
package <%=parserDataTable.getPackageName()%>;

import <%=nonTerminalEnum.getName()%>;
import <%=productionEnum.getName()%>;
import <%=terminalEnum.getName()%>;
import fr.umlv.tatoo.runtime.parser.AcceptAction;
import fr.umlv.tatoo.runtime.parser.Action;
import fr.umlv.tatoo.runtime.parser.BranchAction;
<% if (table.getActionFactory().getEnters().size()>0)  { %>
import fr.umlv.tatoo.runtime.parser.EnterAction;
<% } %>
import fr.umlv.tatoo.runtime.parser.ErrorAction;
import fr.umlv.tatoo.runtime.parser.ExitAction;
import fr.umlv.tatoo.runtime.parser.ParserTable;
import fr.umlv.tatoo.runtime.parser.ReduceAction;
import fr.umlv.tatoo.runtime.parser.ShiftAction;
<% if (table.getActionFactory().getVersioneds().size()>0) { %>
import fr.umlv.tatoo.runtime.parser.VersionedAction;
<% } %>
import fr.umlv.tatoo.runtime.parser.StateMetadata;
import java.util.EnumMap;
<% if (table.getActionFactory().getVersioneds().size()>0) { %>
import java.util.EnumSet;
<% } %>

<% String termName=terminalEnum.getSimpleName();
   String prodName=productionEnum.getSimpleName();
   String versName=versionEnum.getSimpleName();
   String nontName=nonTerminalEnum.getSimpleName();
   String triplet="<"+termName+","+prodName+","+versName+">";
%>
/** 
 *  This class is generated - please do not edit it 
 */
public class <%=parserDataTable.getSimpleName()%> {
  private <%=parserDataTable.getSimpleName()%>() {
   accept = AcceptAction.<%=triplet%>getInstance();
   exit = ExitAction.<%=triplet%>getInstance();
<% for(NonTerminalDecl nt: table.getGotoes().keySet()) { %>
    init<%=nt.getId()%>Gotoes();
<% } %>
<% for(ReduceActionDecl reduce : table.getActionFactory().getReduces()) { %>
    <%=reduce.getId()%> = new ReduceAction<%=triplet%>(<%=prodName%>.<%=reduce.getProduction().getId()%>,<%=reduce.getProduction().getRight().size()%>,<%=reduce.getProduction().getLeft().getId()%>Gotoes);
<% } %>
<% for(ShiftActionDecl shift : table.getActionFactory().getShifts()) { %>
    <%=shift.getId()%> = new ShiftAction<%=triplet%>(<%=shift.getState().getStateNo()%>);
<% } %>
<% for(EnterActionDecl enter : table.getActionFactory().getEnters()) { %>
    <%=enter.getId()%> = new EnterAction<%=triplet%>(<%=termName%>.<%=enter.getBranchingTerminal().getId()%>,<%=enter.getState().getStateNo()%>);
<% } %>
<% for(ErrorActionDecl error : table.getActionFactory().getErrors()) { %>
    <%=error.getId()%> = new ErrorAction<%=triplet%>("<%=error.getMessage().replace("\"","\\\"")%>");
<% } %>
<% for(BranchActionDecl branch:table.getActionFactory().getBranchs()) { %>
    <%=branch.getId() %> = new BranchAction<%=triplet%>("<%=branch.getMessage().replace("\"","\\\"")%>");
<% } %>
<% for(VersionedActionDecl versioned : table.getActionFactory().getVersioneds()) { %>

    EnumMap<%="<"%><%=versName %>,Action<%=triplet%>> <%=versioned.getId()%>Map=
      new EnumMap<%="<"%><%=versName %>,Action<%=triplet%>>(<%=versName %>.class);
<%   for(Map.Entry<VersionDecl,? extends ActionDecl> entry:versioned.getActionMap().entrySet()) {%>
    <%=versioned.getId()%>Map.put(<%=versName%>.<%=entry.getKey().getId()%>,<%=entry.getValue().getId()%>);
<% } %>    
    <%=versioned.getId()%>=new VersionedAction<%=triplet%>(<%=versioned.getId()%>Map);
<% } %>
<% for(TerminalDecl terminal : table.getActionMap().keySet()) { %>
    init<%=terminal.getId()%>Array();
<% } %>
    EnumMap<%="<"%><%=termName%>,Action<%=triplet%>[]> tableMap =
      new EnumMap<%="<"%><%=termName%>,Action<%=triplet%>[]>(<%=termName%>.class);
      
<% for(TerminalDecl terminal : table.getActionMap().keySet()) { %>
    tableMap.put(<%=termName%>.<%=terminal.getId()%>,<%=terminal.getId()%>Array);
<% } %>
    initBranchArrayTable();
    
<% String metaTriplet = "<"+termName+","+nontName+","+prodName+","+versName+">"; %>
    StateMetadata<%=metaTriplet%>[] tableMetadata = createStateMetadataTable();
    
    EnumMap<%="<"%><%=nontName%>,Integer> tableStarts =
      new EnumMap<%="<"%><%=nontName%>,Integer>(<%=nontName%>.class);
<% for(Map.Entry<NonTerminalDecl,Integer> entry:table.getStartStateMap().entrySet()) { %>
    tableStarts.put(<%=nontName%>.<%=entry.getKey().getId()%>,<%=entry.getValue()%>);
<%} %>
    table = new ParserTable<%="<"%><%=termName%>,<%=nontName%>,<%=prodName%>,<%=versName%>>(tableMap,branchArrayTable,tableMetadata,tableStarts,<%=versName%>.values(),<%=table.getStateCount()%>,<%=termName%>.<%=table.getEof().getId()%>,<%=table.getError()==null?"null":termName+"."+table.getError().getId()%>);
  } 

  // metadata aren't stored in local vars because it freak-out the register allocator of android
  @SuppressWarnings("unchecked")
  private StateMetadata<%=metaTriplet%>[] createStateMetadataTable() {
    <% for(StateMetadataDecl metadata : metadataSet) { 
       VariableDecl assoc = metadata.getAssociated();
       String terminal;
       String assocId;
       if (assoc==null) {         
         terminal="Non";
         assocId="null";
       } else if (assoc.isTerminal()) {
         assocId=termName+"."+assoc.getId();
         terminal="";
       } else {
         assocId=nontName+"."+assoc.getId();
         terminal="Non";
       }
       String methodName;
       String extraArg;
       if (metadata.isFullversion()) {
         methodName="createAllVersionWith"+terminal+"Terminal";
         extraArg="";
       } else {
         methodName="createWith"+terminal+"Terminal";
         StringBuilder builder = new StringBuilder("EnumSet.of(");
         Iterator<? extends VersionDecl> iterator = metadata.getCompatibleVersions().iterator();
         builder.append(versName).append('.').append(iterator.next().getId());
         while(iterator.hasNext()) {
           builder.append(',').append(versName).append('.').append(iterator.next().getId());
         }
         extraArg=builder.append("),").toString();
       }
       ActionDecl reduce = metadata.getDefaultReduce();
%>
    <%=metadata.getId()%> = StateMetadata.<%=metaTriplet%><%=methodName%>(<%=extraArg%><%=assocId%>,<%=reduce==null?"null":reduce.getId() %>);
<% } %>

    return (StateMetadata<%=metaTriplet%>[])new StateMetadata<?,?,?,?>[]{<%
    StateMetadataDecl[] metadatas = table.getMetadata();
    out.print(metadatas[0].getId());
    for(int i=1;i<metadatas.length;i++) {
      out.print(',');
      out.print(metadatas[i].getId());
    }
    %>};
  }

<% for(Map.Entry <NonTerminalDecl,int[]> entry:table.getGotoes().entrySet()) { %>  
  private int[] <%=entry.getKey().getId()%>Gotoes;

  private void init<%=entry.getKey().getId()%>Gotoes() {
    <%=entry.getKey().getId()%>Gotoes = 
      new int[]{<%
        int[] values = entry.getValue();
        out.print(values[0]);
        for(int i=1;i<values.length;i++) {
          out.print(',');
          out.print(values[i]);
        }
    %>};
  }
<% } %>

<% for(Map.Entry<TerminalDecl,RegularTableActionDecl[]> entry: table.getActionMap().entrySet()) { %>
  private Action<%=triplet%>[] <%=entry.getKey().getId()%>Array;
  @SuppressWarnings("unchecked")
  private void init<%=entry.getKey().getId()%>Array() {
    <%=entry.getKey().getId()%>Array=(Action<%=triplet%>[])new Action<?,?,?>[]{<%
        RegularTableActionDecl[] values = entry.getValue();
        out.print(values[0].getId());
        for(int i=1;i<values.length;i++) {
          out.print(',');
          out.print(values[i].getId());
        }
        %>};
  }
<% } %>

  private Action<%=triplet%>[] branchArrayTable;
  @SuppressWarnings("unchecked")
  private void initBranchArrayTable() {
    branchArrayTable=(Action<%=triplet%>[])new Action<?,?,?>[]{<%
        BranchTableActionDecl[] branches = table.getBranchArray();
        out.print(branches[0].getId());
        for(int i=1;i<branches.length;i++) {
          out.print(',');
          out.print(branches[i].getId());
        }
      %>};
  }

  private final ParserTable<%="<"%><%=termName%>,<%=nontName%>,<%=prodName%>,<%=versName%>> table;
  
  public static final ParserTable<%="<"%><%=termName%>,<%=nontName%>,<%=prodName%>,<%=versName%>> createTable() {
    return new <%=parserDataTable.getSimpleName()%>().table;
  }

  private final AcceptAction<%=triplet%> accept;
  private final ExitAction<%=triplet%> exit;

<% for(ReduceActionDecl reduce:table.getActionFactory().getReduces()) { %>
  private final ReduceAction<%=triplet%> <%=reduce.getId()%>;
<% } %>

<% for(ShiftActionDecl shift:table.getActionFactory().getShifts()) { %>
  private final ShiftAction<%=triplet%> <%=shift.getId()%>;
<% } %>

<% for (EnterActionDecl enter:table.getActionFactory().getEnters()) {%>
  private final EnterAction<%=triplet%> <%=enter.getId()%>;
<% } %>

<% for (ErrorActionDecl error:table.getActionFactory().getErrors()) {%>
  private final ErrorAction<%=triplet%> <%=error.getId()%>;
<% } %>

<% for (BranchActionDecl branch:table.getActionFactory().getBranchs()) { %>
  private final BranchAction<%=triplet%> <%=branch.getId()%>;
<% } %>

<% for(VersionedActionDecl versioned:table.getActionFactory().getVersioneds()) { %>
  private final VersionedAction<%=triplet%> <%=versioned.getId()%>;
<% } %>

<% for(StateMetadataDecl metadata : metadataSet) { %>
  private StateMetadata<%=metaTriplet%> <%=metadata.getId()%>;
<% } %>
}
