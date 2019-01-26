<%@page import="fr.umlv.tatoo.cc.common.generator.Type"%>
<%!/*PARAM*/
Type abstractInnerNode;
Type abstractNode;
Type productionEnum;
%>
<%!/*COMMENT*/
java.io.PrintWriter out;
%>

package <%=abstractInnerNode.getPackageName()%>;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

/** Common abstract class of all nodes that aren't tokens.
 *
 *  This class is generated - please do not edit it 
 */
abstract class <%=abstractInnerNode.getSimpleName()%> extends <%=abstractNode.getSimpleName()%> {
  private final SubNodeList subNodeList=new SubNodeList();
   
  public boolean isToken() {
    return false;
  }
  
  /** This method changes the parent of the current node.
   *  
   * @param oldNode the old child node.
   * @param newNode the new child node.
   * @return the new child node.
   * 
   * @param <N> a subtype of <%=abstractNode.getSimpleName()%> 
   */
  <N extends <%=abstractNode.getSimpleName()%>> N reparent(N oldNode,N newNode) {
    if (oldNode==newNode)
      return newNode;
    if (oldNode!=null)
      oldNode.parent=null;
    if (newNode!=null) 
      newNode.parent=this;
    return newNode;
  }
  
  private static void setListParent(List<? extends <%=abstractNode.getSimpleName()%>> nodeList, <%=abstractInnerNode.getSimpleName()%> parent) {
    for(<%=abstractNode.getSimpleName()%> node:nodeList) {
      if (node != null) {
        node.parent=parent;
      }
    }
  }
  
  //Perhaps Need optimization
  <N extends <%=abstractNode.getSimpleName()%>> List<N> reparent(List<N> oldNodeList,List<N> newNodeList) {
    if (oldNodeList==newNodeList)
      return newNodeList;
    if (oldNodeList!=null)
      setListParent(oldNodeList,null);
    if (newNodeList!=null) {
      setListParent(newNodeList,this);
      newNodeList=new NodeList<N>(newNodeList);
    }
    return newNodeList;
  }
  
  class NodeList<N extends <%=abstractNode.getSimpleName()%>> extends AbstractList<N> implements RandomAccess {
    private final ArrayList<N> list=new ArrayList<N>();
    NodeList(List<? extends N> nodes) {
      list.addAll(nodes);
    }
    @Override
    public int size() {
      return list.size();
    }
    @Override
    public N get(int index) {
      return list.get(index);
    }
    @Override
    public N set(int index, N newNode) {
      N oldNode=list.get(index);
      reparent(oldNode,newNode);
      list.set(index,newNode);
      return oldNode;
    }
    @Override
    public void add(int index, N newNode) {
      reparent(null,newNode);
      list.add(index, newNode);
    }
    @Override
    public N remove(int index) {
      N oldNode=list.remove(index);
      reparent(oldNode,null);
      return oldNode;
    }
  }
  
  
  @Override
  public String toString() {
    List<? extends Node> nodeList=nodeList();
    if (nodeList.isEmpty())
      return "["+getKind()+']';
    StringBuilder builder=new StringBuilder();
    builder.append('[').append(getKind()).append(' ');
    for(Node node:nodeList)
      builder.append(node).append(',');
    builder.setLength(builder.length()-1);
    return builder.append(']').toString();
  }
  
   class SubNodeList extends AbstractList<<%=abstractNode.getSimpleName()%>> implements RandomAccess {
     @Override
     public int size() {
       return subNodesSize();
     }
     
     @Override
     public <%=abstractNode.getSimpleName()%> get(int index) {
       if (index<0)
         throw new IndexOutOfBoundsException("negative index "+index);
       return subNodeAt(index);
     }
     
     @Override
     public <%=abstractNode.getSimpleName()%> set(int index, <%=abstractNode.getSimpleName()%> node) {
       if (index<0)
         throw new IndexOutOfBoundsException("negative index "+index);
       return subNodeAt(index, node);
     }
   }
   
   protected abstract <%=abstractNode.getSimpleName()%> subNodeAt(int index);
   protected abstract <%=abstractNode.getSimpleName()%> subNodeAt(int index, <%=abstractNode.getSimpleName()%> node);
   protected abstract int subNodesSize();
   
   @Override
   public List<<%=abstractNode.getSimpleName()%>> nodeList() {
     return subNodeList;
   }
}
