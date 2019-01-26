package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.umlv.tatoo.cc.common.generator.Type;
import fr.umlv.tatoo.cc.ebnf.ast.analysis.Directive;
import fr.umlv.tatoo.cc.lexer.lexer.RuleDecl;
import fr.umlv.tatoo.cc.parser.grammar.NonTerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.Priority;
import fr.umlv.tatoo.cc.parser.grammar.ProductionDecl;
import fr.umlv.tatoo.cc.parser.grammar.TerminalDecl;
import fr.umlv.tatoo.cc.parser.grammar.VariableDecl;
import fr.umlv.tatoo.cc.parser.grammar.VersionDecl;
import fr.umlv.tatoo.runtime.node.Binding;
import fr.umlv.tatoo.runtime.node.BindingSite;

public class Bindings {
  static abstract class AbstractBinding<D extends BindingSite> implements Binding {
    private final D declaringSite;
    protected AbstractBinding(D declaringSite) {
      this.declaringSite=declaringSite;
    }
    
    public D getDeclaringSite() {
      return declaringSite;
    }
    @Override
    public List<? extends BindingSite> getRefereeList() {
      return Collections.emptyList();
    }
  }
  
  public interface ReferenceableBinding extends Binding {
    public List<? extends BindingSite> getRefereeList();
  }
  
  static abstract class AbstractReferenceableBinding<D extends BindingSite,R extends BindingSite> extends AbstractBinding<D> implements ReferenceableBinding{
    private final ArrayList<R> refereeList=
      new ArrayList<R>();
    protected AbstractReferenceableBinding(D declaringSite) {
      super(declaringSite);
    }
    
    public void addReferee(R referee) {
      refereeList.add(referee);
    }
    
    @Override
    public List<R> getRefereeList() {
      return refereeList;
    }
  }
  
  public static class RuleBinding extends AbstractBinding<RuleDefAST> {
    private final RuleDecl rule;
    public RuleBinding(RuleDefAST declaringSite,RuleDecl rule) {
      super(declaringSite);
      this.rule=rule;
    }
    public RuleDecl getDomainObject() {
      return rule;
    }
  }
  
  public static class DirectiveBinding extends AbstractBinding<DirectiveDefAST> {
    private final Directive directive;
    public DirectiveBinding(DirectiveDefAST declaringSite,Directive directive) {
      super(declaringSite);
      this.directive=directive;
    }
    public Directive getDomainObject() {
      return directive;
    }
  }
  
  public static abstract class VariableBinding<D extends BindingSite> extends AbstractReferenceableBinding<D,VariableVarAST> {
    protected VariableBinding(D declaringSite) {
      super(declaringSite);
    }
    public abstract VariableDecl getDomainObject();
  }
  
  public static class TerminalBinding extends VariableBinding<TerminalDefAST> {
    private final TerminalDecl terminal;
    public TerminalBinding(TerminalDefAST declaringSite,TerminalDecl terminal) {
      super(declaringSite);
      this.terminal=terminal;
    }
    @Override
    public TerminalDecl getDomainObject() {
      return terminal;
    }
  }
  
  public static class NonTerminalBinding extends VariableBinding<NonTerminalBinder> {
    private final NonTerminalDecl nonterminal;
    public NonTerminalBinding(NonTerminalBinder declaringSite,NonTerminalDecl nonterminal) {
      super(declaringSite);
      this.nonterminal=nonterminal;
    }
    @Override
    public NonTerminalDecl getDomainObject() {
      return nonterminal;
    }
  }
  
  public static class ProductionBinding  extends AbstractBinding<ProductionDefAST> {
    private final ProductionDecl production;
    public ProductionBinding(ProductionDefAST declaringSite,ProductionDecl production) {
      super(declaringSite);
      this.production=production;
    }
    public ProductionDecl getDomainObject() {
      return production;
    }
  }
  
  public static class VersionBinding extends AbstractReferenceableBinding<VersionDefAST,VersionVarAST> {
    private final VersionDecl version;
    public VersionBinding(VersionDefAST declaringSite,VersionDecl version) {
      super(declaringSite);
      this.version=version;
    }
    public VersionDecl getDomainObject() {
      return version;
    }
  }
  
  public static class PriorityBinding extends AbstractReferenceableBinding<PriorityDefAST,PriorityVarAST> {
    private final Priority priority;
    public PriorityBinding(PriorityDefAST declaringSite,Priority priority) {
      super(declaringSite);
      this.priority=priority;
    }
    public Priority getDomainObject() {
      return priority;
    }
  }
  
  public static class TypeBinding extends AbstractReferenceableBinding<TypeBinder,TypeVarAST> {
    private final Type type;
    public TypeBinding(TypeBinder declaringSite,Type type) {
      super(declaringSite);
      this.type=type;
    }
    public Type getDomainObject() {
      return type;
    }
  }
}
