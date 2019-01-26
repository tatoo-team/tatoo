/*
 * Created on Jun 2, 2003
 *
 */
package fr.umlv.tatoo.cc.parser.solver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author jcervell
 * @param <K> 
 * @param <V> 
 *
 */
public class Solver<K,V> {
  
  private final NodeFactory<K,V> factory;
  private final HashMap<K,NodeInfo<K,V>> infoNodes = 
    new HashMap<K,NodeInfo<K,V>>();
  
  public Solver(NodeFactory<K,V> factory) {
    this.factory=factory;
  }

  private static class NodeInfo<L,W> {
    private final NodeContent<L,W> node;
    // nodes which depend on the node (reverse of NodeContent.dependencies()) 
    private final HashSet<L> dependencyKeys;

    NodeInfo(NodeContent<L,W> node) {
      this.node = node;
      dependencyKeys = new HashSet<L>();
    }

    void addDependency(L key) {
      dependencyKeys.add(key);
    }

    HashSet<L> getDependencies() {
      return dependencyKeys;
    }

    NodeContent<L,W> getNode() {
      return node;
    }

  }

  /* modified contains all the nodes that are newly created or modifed */
  private NodeInfo<K,V> createNodeInfo(K key,HashMap<K,NodeInfo<K,V>> modified) {
		NodeContent<K, V> node = factory.getNode(key);   
        NodeInfo<K,V> info=new NodeInfo<K,V>(node);
        infoNodes.put(key,info);
        
		Set<K> set = node.dependencies();
    
		for(K targetKey: set) {
          NodeInfo<K,V> targetInfo = infoNodes.get(targetKey);
          if(targetInfo == null) {
            targetInfo = createNodeInfo(targetKey,modified);
          }
          modified.put(targetKey,targetInfo);
          targetInfo.addDependency(key);
		}
		return info;
  }
  
  public V getCurrent(K key) {
    NodeInfo<K,V> node = infoNodes.get(key);
    if (node==null)
      return null;
    return node.getNode().getCurrentResult();
  }
  
  public V solve(K key) {
    NodeInfo<K,V> info=infoNodes.get(key);

    if (info!=null) {
      return info.getNode().getResult();
    }
    HashMap<K, NodeInfo<K, V>> newNodes = new HashMap<K,NodeInfo<K,V>>();
    info=createNodeInfo(key,newNodes);

    
     HashMap<K, NodeInfo<K, V>> hasChanged = newNodes;	
     HashMap<K, NodeInfo<K, V>> buildingHasChanged = new HashMap<K, NodeInfo<K, V>>();
		
		while(! hasChanged.isEmpty()) {
			for(Map.Entry<K, NodeInfo<K, V>> entry : hasChanged.entrySet()) {
				K sourceKey=entry.getKey();
				NodeInfo<K,V> sourceInfo=entry.getValue();
				for (K targetKey : sourceInfo.getDependencies()) {
					NodeInfo<K,V> targetInfo=infoNodes.get(targetKey);
					if (targetInfo.getNode().hasChanged(sourceKey,sourceInfo.getNode(),this)) {
						buildingHasChanged.put(targetKey,targetInfo);
					}
				}
			}
			
			HashMap<K,NodeInfo<K,V>> tmpMap=hasChanged;
			hasChanged=buildingHasChanged;
			buildingHasChanged=tmpMap;
			buildingHasChanged.clear();


      
 		}
    
    return info.getNode().getResult();

  }
  

}
