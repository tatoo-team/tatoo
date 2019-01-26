/*
 * Created on 14 janv. 2006
 *
 */
package fr.umlv.tatoo.samples.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GrammarElement {
  public GrammarElement(Enum<?> production,int start,int end) {
    this.production=production;
    this.start=start;
    this.end=end;
  }
  
  public Enum<?> getProduction() {
    return production;
  }
  
  public GrammarElement getParent() {
    return parent;
  }

  public void setParent(GrammarElement parent) {
    this.parent = parent;
  }
  
  public int getStartOffset() {
    if (start==-1 && elements!=null)
      return elements.get(0).getStartOffset();
    return start;
  }

  public int getEndOffset() {
    if (end==-1 && elements!=null)
      return elements.get(elements.size()-1).getEndOffset();
    return end;
  }
  
  public List<? extends GrammarElement> elements() {
    if (elements==null)
      return Collections.<GrammarElement>emptyList();
    return unmodifiableElements;
  }
  
  public void add(GrammarElement element) {
    if (elements==null) {
      elements=new ArrayList<GrammarElement>();
      unmodifiableElements=Collections.unmodifiableList(elements);
    }
    elements.add(element);
    element.setParent(this);
  }
  
  public GrammarElement getElementAtOffset(int offset) {
    for(GrammarElement element:elements()) {
      if (offset>=element.getStartOffset() && offset<element.getEndOffset())
        return element.getElementAtOffset(offset);
    }
    return this;
  }
  
  @Override
  public String toString() {
    return production+"("+getStartOffset()+','+getEndOffset()+") ";
  }
  
  private GrammarElement parent;
  private final int start,end;
  private final Enum<?> production;
  private ArrayList<GrammarElement> elements;
  private List<GrammarElement> unmodifiableElements;
}