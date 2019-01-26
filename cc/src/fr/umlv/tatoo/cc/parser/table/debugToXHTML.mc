<%@page import="fr.umlv.tatoo.cc.parser.grammar.Grammar"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.GrammarSets"%>
<%@page import="fr.umlv.tatoo.cc.parser.table.NodeFactory"%>
<%@page import="java.util.Map"%>
<%@page import="fr.umlv.tatoo.cc.parser.table.NodeDecl"%>
<%@page import="fr.umlv.tatoo.cc.common.util.MultiMap"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.TerminalDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl"%>
<%@page import="java.util.List"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.ProductionDecl"%>
<%@page import="java.util.Iterator"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.VariableDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.VersionDecl"%>
<%@page import="java.util.Set"%>
<%@page import="fr.umlv.tatoo.cc.parser.parser.ActionDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.parser.RegularTableActionDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.parser.BranchTableActionDecl"%>
<%@page import="fr.umlv.tatoo.cc.parser.table.NodeItem"%>
<%@page import="fr.umlv.tatoo.cc.parser.lr.LR1Item"%>
<%@page import="fr.umlv.tatoo.cc.parser.parser.SimpleActionDeclVisitor"%>
<%@page import="fr.umlv.tatoo.cc.common.generator.ObjectId"%>
<%@page import="fr.umlv.tatoo.cc.common.util.Pair"%>
<%@page import="fr.umlv.tatoo.cc.parser.grammar.Priority"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.io.IOException"%>
<%!/*PARAM*/
Grammar grammar;
GrammarSets grammarSets;
NodeFactory<?> nodeFactory;
Map<NodeDecl<?>, ? extends Map<TerminalDecl,? extends Map<RegularTableActionDecl,Priority>>> table;
Map<NodeDecl<?>,? extends Map<BranchTableActionDecl,Priority>> branches;
Map<NodeDecl<?>, ? extends Map<NonTerminalDecl,NodeDecl<?>>> buildedGotos;
SimpleActionDeclVisitor<String> displayVisitor;
%>
<%!/*COMMENT*/
java.io.PrintWriter out;
%>
<%!
private static final HashMap<Character,String> XML_ENTITIES = new HashMap<Character,String>();
static {
  XML_ENTITIES.put('\'',"&apos;");
  XML_ENTITIES.put('&',"&amp;");
  XML_ENTITIES.put('"',"&quot;");
  XML_ENTITIES.put('<',"&lt;");
  XML_ENTITIES.put('>',"&gt;");
}



public void escapeXML(CharSequence s, Appendable builder) {
  try {
    for(int i=0;i<s.length();i++) {
      char c = s.charAt(i);
      String value = XML_ENTITIES.get(c);
      if (value==null)
        builder.append(c);
      else
        builder.append(value);
    }
  }
  catch (IOException e) {
    throw new IllegalStateException(e);
  }
}

public String escapeXML(CharSequence s) {
  StringBuilder builder = new StringBuilder();
  escapeXML(s,builder);
  return builder.toString();
}  

public void printTerminalSet(String fmt,Set<? extends TerminalDecl> set) {
  for(TerminalDecl o : set) {
    out.append("<tr><td>");
    out.append(String.format(fmt,escapeXML(o.getName())));
    out.append("</td></tr>");
  }
}

public void printSet(String fmt,Set<? extends ObjectId> set) {
  for(ObjectId o : set) {
    out.append("<tr><td>");
    out.append(String.format(fmt,o.getId()));
    out.append("</td></tr>");
  }
}

public String join(Set<? extends ProductionDecl> set) {
  if (set.isEmpty())
    return "";
  StringBuilder builder = new StringBuilder();
  for(ProductionDecl prod:set) {
    appendProduction(prod,builder);
    builder.append(", ");
  }
  builder.setLength(builder.length()-2);
  return builder.toString();
}

public void printSet(String fmt,MultiMap<? extends ObjectId,? extends ProductionDecl> set) {
  for(Map.Entry<? extends ObjectId, ? extends Set<? extends ProductionDecl>> o : set.entrySet()) {
    out.append(String.format("<tr><td><span title='%s'>",join(o.getValue())));
    out.append(String.format(fmt,o.getKey().getId()));
    out.append("</span></td></tr>");
  }
}

public void printTerminalSet(String fmt,MultiMap<? extends TerminalDecl,? extends ProductionDecl> set) {
  for(Map.Entry<? extends TerminalDecl, ? extends Set<? extends ProductionDecl>> o : set.entrySet()) {
    out.append(String.format("<tr><td><span title='%s'>",join(o.getValue())));
    out.append(String.format(fmt,escapeXML(o.getKey().getName())));
    out.append("</span></td></tr>");
  }
}

public void printPathInfo(NodeDecl<?> state) {
    out.append("state ");
    out.print(state.getStartPathState());
    out.append(":");
    for(TerminalDecl terminal:state.getShortestPath()) {
      out.append(" ");
      escapeXML(terminal.getName(),out);
    }
  }

public void printWord(boolean separateFirst, VariableDecl mainId,List<? extends VariableDecl> word) {
  boolean first=true;
  for(VariableDecl id:word) {
    if (!first)
      out.append("&nbsp;");
    else {
      first=false;
      if (separateFirst) {
        print(id);
        out.append(":&nbsp;");
        continue;
      }
    }
    if (id==mainId)
      out.append("<em>");
    print(id);
    if (id==mainId)
      out.append("</em>");
  }
}

public void print(VariableDecl variable) {
  if (variable.isTerminal()) {
    out.print("<span class=\"terminal\">");
    escapeXML(((TerminalDecl)variable).getName(),out);
    out.print("</span>");
  }
  else {
    String id = variable.getId();
    out.print("<a href=\"#"+id+"\">"+id+"</a>");
  }
}

public void appendProduction(ProductionDecl prod,StringBuilder builder) {
  escapeXML(prod.getLeft().getId(),builder);
  builder.append("&#160;::=");
  List<? extends VariableDecl> rights = prod.getRight();
  if (rights.isEmpty()) {
    builder.append("&#160;&#949;");
    return;
  }
  for(VariableDecl var:rights) {
    builder.append("&#160;");
    escapeXML(var.toString(),builder);
  }
}

public void print(NodeItem<?> item) {
  print(item.getLeft());
  out.print("&#160;::=&#160;");
  int count=-1;
  for(VariableDecl var : item.getRight()) {
    count++;
    if(count>0)
      out.print("&#160;");
    if (count==item.getDotPlace())
      out.print("&#8226;&#160;");
    print(var);
  }
  if (count+1==item.getDotPlace())
    out.print("&#160;&#8226;");
  if(((Object) item) instanceof LR1Item) {
    out.print("&#160;/&#160;"+((LR1Item)(Object)item).getLookahead().getId());
  }
}
%>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
    "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">



<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="fr">
<head>
	<title>Parser Table</title>
	<style type="text/css">
		body {font-family: sans-serif; background:white}
		h1 {border-style: solid;border-width: 1px;background: #E0E0FF;padding: 0.5cm;font: 180% sans-serif;text-align: center;}
		h2 {font: 150% sans-serif;text-decoration: underline;}
		h3 {font: 130% sans-serif;}td{background: #EEEEEE;}
		td.g {background:white}
		span.terminal { font-style:italic; color:#881122; }
	</style>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body>
<h1>Parser Table</h1>
<h2>Grammar</h2>
<% 
Set<? extends NonTerminalDecl> starts = grammar.getStarts(); 
for(Map.Entry<NonTerminalDecl,? extends List<ProductionDecl>> entry:
       grammar.getProductions().entrySet()) { %>
<table border="0" style="padding: 0.2cm">
<tr>
<%
String id;
if (!starts.contains(entry.getKey()))
  id=" id=\""+entry.getKey().getId()+"\"";
else
  id="";
%>
<td class="g"<%=id%>><%=entry.getKey().getId()%></td> <td class="g" align="center">::=</td>
<% 
  boolean first=true;
  for(ProductionDecl production:entry.getValue()) {
    if(!first) {
%>
<tr><td class="g"></td><td class="g" align="center">|</td>
<%  } else
    first=false;
%>
<td class="g">
<%  if (production.getRight().size()==0)
      out.print("<i>*empty*</i>");
    else {
      Iterator<? extends VariableDecl> iterator = production.getRight().iterator();
      print(iterator.next());
      while(iterator.hasNext()) {
        out.print("&#160;");
        print(iterator.next());
      }
    }
%>
</td>
<% VersionDecl version = production.getVersion(); %>
<td class="g"><%= version==null?"<i>all</i>":version.getId() %></td>
<td class="g"><% 
Pair<NonTerminalDecl,List<? extends TerminalDecl>> pair = grammarSets.wordUsingProduction(production);
out.append(pair.getFirst().getId());
out.append(":&nbsp;");
printWord(false,null,pair.getSecond());
%></td>
</tr>
<% } %>
</table>
<%
}%>


<h2>Non Terminals</h2>
<table border="1">
<tr><th>Non terminal</th><th>Epsilon</th><th>First</th><th>Result</th><th>Last</th><th>Result</th><th>Follow</th><th>Result</th></tr>
<% for(NonTerminalDecl nt : grammar.getNonTerminals()) {
%>
<tr id="NT:<%= nt.getId()%>"><td><%=nt.getId()%></td><td><%=grammarSets.derivesToEpsilon(nt)%></td>
<td><table border="0">
<%   printSet("First(<a href='#NT:%1$s'>%1$s</a>)",grammarSets.firstFirstDependencies(nt));
     printTerminalSet("%s",grammarSets.firstTerminalDependencies(nt)); %>
</table></td><td><table border="0">
<%   printTerminalSet("%s",grammarSets.first(nt)); %>
</table></td>

<td><table border="0">
<%   printSet("Last(<a href='#NT+%1$s'>%1$s</a>)",grammarSets.lastLastDependencies(nt));
     printTerminalSet("%s",grammarSets.lastTerminalDependencies(nt)); %>
</table></td><td><table border="0">
<%   printTerminalSet("%s",grammarSets.last(nt)); %>
</table></td>

<%  if(starts.contains(nt)) { %>
<td colspan="2" class="g"/>
<%  }
    else { %>
<td><table border="0">
<%   printSet("Follow(<a href='#NT+%1$s'>%1$s</a>)",grammarSets.followFollowDependencies(nt));
     printSet("First(<a href='#NT+%1$s'>%1$s</a>)",grammarSets.followFirstDependencies(nt));
     printSet("%s",grammarSets.followTerminalDependencies(nt)); %>
</table></td><td><table border="0">
<%   printSet("%s",grammarSets.follow(nt));  %>
</table></td>
<%  } %>
<%-- <td><% printWord(false,null,grammarSets.shortestWord(nt)); %></td>
<td><% printWord(true,nt,grammarSets.shortestLeadingWord(nt)); %></td> --%>
</tr>
<% } %>
</table>

<h2>Start States</h2>
<table border="1">
<tr><th>Non terminal</th><th>Start State</th></tr>
<% for(Map.Entry<NonTerminalDecl,? extends NodeDecl<?>> entry: nodeFactory.getStartStateMap().entrySet()) { 
     String state="state"+entry.getValue().getStateNo();
%>
<tr><td><%=entry.getKey().getId()%></td><td><a href="<%="#"+state%>"><%=state%></a></td></tr>
<% } %>
</table>

<h2>States</h2>
<% for(NodeDecl<?> node: nodeFactory.getNodes()) { 
     String state = "state"+node.getStateNo();
%>
<h3 id="<%=state%>"><%=state%> - <%printPathInfo(node);%></h3>
<%
Map<TerminalDecl,? extends Map<RegularTableActionDecl,Priority>> actionMap = table.get(node);
Map<NonTerminalDecl,NodeDecl<?>> gotos = buildedGotos.get(node);
Map<BranchTableActionDecl,Priority> branch = branches.get(node);
%>
<p>Compatible versions :
<% for(VersionDecl version:node.getCompatibleVersion()) { %>
 <%=version==null?"null":version.getId() %>
<% } %>
</p>

<table border="1">
<tr><th>Kernel items</th><th>Actions</th>
<% if (!gotos.isEmpty()) { %>
<th>Gotoes</th>
<% } %></tr>
<tr><td>
<table border="0">
<% for (NodeItem<?> item:node.getKernelItems()) { %>
<tr><td><b><% print(item); %></b></td></tr>
<% } %>
</table></td>
<td><table>
<% for(Map.Entry<TerminalDecl,? extends Map<RegularTableActionDecl,Priority>> entry : actionMap.entrySet()) { %>
<tr>
<td><%=entry.getKey().getId()%></td><td>:</td>
<td>
<%   Iterator<Map.Entry<RegularTableActionDecl,Priority>> iterator = entry.getValue().entrySet().iterator();
     out.print(iterator.next().getKey().accept(displayVisitor));
     while(iterator.hasNext()) {
       out.print(",");
       out.print(iterator.next().getKey().accept(displayVisitor));
     }
%>
</td></tr>
<% } 
   if (branch.size()!=0) {
%>
<tr><td><i>branch</i></td><td>:</td>
<td>
<%   Iterator<Map.Entry<BranchTableActionDecl,Priority>> iterator = branch.entrySet().iterator();
     out.print(iterator.next().getKey().accept(displayVisitor));
     while(iterator.hasNext()) {
       out.print(",");
       out.print(iterator.next().getKey().accept(displayVisitor));
     } %>
</td></tr>
<%  } %>
</table></td>
<% if (!gotos.isEmpty()) { %>
<td><table border="0">
<% for(Map.Entry<NonTerminalDecl,NodeDecl<?>> entry:gotos.entrySet()) {
     String stateName = "state"+entry.getValue().getStateNo();
%>
<tr><td><%=entry.getKey().getId()%></td><td>:</td><td><a href="<%="#"+stateName%>"><%=stateName %></a></td></tr>
<% } %>
</table></td>
<% } %> 
</tr>
</table>
<% } %>
  <p style="margin-bottom: 100%">
    <a href="http://validator.w3.org"><img
        src="http://www.w3.org/Icons/valid-xhtml11"
        alt="Valid XHTML 1.1" height="31" width="88" /></a>
  </p>
</body>
</html>
