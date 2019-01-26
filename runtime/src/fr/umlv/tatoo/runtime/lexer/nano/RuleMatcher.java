package fr.umlv.tatoo.runtime.lexer.nano;

import fr.umlv.tatoo.runtime.lexer.rules.ProcessReturn;

public interface RuleMatcher {
  /**
   * Processes available characters from the input stream.
   * @return {@link ProcessReturn#MORE MORE} if more characters are needed to perform the match,
   * {@link ProcessReturn#ERROR ERROR}, if an error occurred
   * and {@link ProcessReturn#TOKEN TOKEN} if a new token is spawned.
   */
  public abstract ProcessReturn step(int letter);
  
  /** Returns -1 if the entry is not recognized.
   * @return -1 if the entry is not recognized, any other value otherwise.
   */
  public abstract int lastMatch();
  
  /*
   * TODO in order to write policy, this kind of method would be appreciable
  public abstract Branchable<?,?> branchable();
  */
  
  /* added by branchable with arguments of initialRule, fill and fillUnbranched */
  //public abstract void addAttachment(Object o, int depth);
  
  /* if true, one attachement must be chosen by the policy */
  //public abstract boolean multipleAttachments();
  //public abstract Collection<?> attachements();
  //public abstract void setAttachment(Object o, int depth);
  
  /* policy must choose between multiple attachments before these methods are called */
  public abstract Object attachment();
  public abstract int depth();
}