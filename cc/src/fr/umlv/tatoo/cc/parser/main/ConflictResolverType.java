package fr.umlv.tatoo.cc.parser.main;

import fr.umlv.tatoo.cc.parser.table.ConflictResolverPolicy;
import fr.umlv.tatoo.cc.parser.table.DefaultConflictResolverPolicy;

public enum ConflictResolverType {
  DEFAULT(new DefaultConflictResolverPolicy());
  
  private ConflictResolverType(ConflictResolverPolicy conflictResolver) {
    this.conflictResolver=conflictResolver;
  }
  public ConflictResolverPolicy getConflictResolver() {
    return conflictResolver; 
  }
  private final ConflictResolverPolicy conflictResolver;
}
