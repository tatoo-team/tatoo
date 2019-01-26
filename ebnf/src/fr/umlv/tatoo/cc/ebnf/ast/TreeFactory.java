package fr.umlv.tatoo.cc.ebnf.ast;

import java.util.List;

import fr.umlv.tatoo.cc.ebnf.ast.TerminalDefAST.TerminalKind;
import fr.umlv.tatoo.cc.lexer.ebnf.parser.TerminalEnum;
import fr.umlv.tatoo.runtime.node.AST;
import fr.umlv.tatoo.runtime.node.AnnotationComputer;
import fr.umlv.tatoo.runtime.node.Node;
import fr.umlv.tatoo.runtime.node.NodeBuilder;

public class TreeFactory {
  AST ast;
  private final AnnotationComputer annotationComputer;
  
  public TreeFactory(AST ast,AnnotationComputer annotationComputer) {
    this.ast=ast;
    this.annotationComputer=annotationComputer;
  }
  
  public void reset(AST ast) {
    this.ast=ast;
  }
  
  public AnnotationComputer getAnnotationComputer() {
    return annotationComputer;
  }
  
  public void setRoot(Node root) {
    ast.setRoot(root);
  }
  
  public <V> TokenAST<V> createToken(TerminalEnum kind, V value) {
    TokenAST<V> token=new TokenAST<V>(ast,kind,value);
    annotationComputer.computeTokenAnnotation(token);
    return token;
  }
  
  public <V> NodeBuilder<SimpleNodeAST<V>> createSimpleNode(final Kind kind,final V value) {
    return new NodeBuilder<SimpleNodeAST<V>>(annotationComputer) {
      @Override
      protected SimpleNodeAST<V> createNode(List<Node> nodes) {
        return new SimpleNodeAST<V>(ast,kind,value,nodes);
      }
    };
  }
  
  public NodeBuilder<ProductionIdAndVersionDefAST> createProductionIdAndVersionDef(final TokenAST<String> name, final VersionVarAST version) {
    return new NodeBuilder<ProductionIdAndVersionDefAST>(annotationComputer) {
      @Override
      protected ProductionIdAndVersionDefAST createNode(List<Node> nodes) {
        return new ProductionIdAndVersionDefAST(ast,name,version,nodes);
      }
    };
  }
  
  public NodeBuilder<EnhancedDefAST> createEnhancedVariable(final EnhancedDefAST.Enhancement enhancement,final VariableVarAST element,final VariableVarAST separator,final List<Node> vargroup) {
    return new NodeBuilder<EnhancedDefAST>(annotationComputer) {
      @Override
      protected EnhancedDefAST createNode(List<Node> nodes) {
        return new EnhancedDefAST(ast,enhancement,element,separator,vargroup,nodes);
      }
    };
  }
  
  public NodeBuilder<ImportDefAST> createImportDef(final TokenAST<String> qualifiedId) {
    return new NodeBuilder<ImportDefAST>(annotationComputer) {
      @Override
      protected ImportDefAST createNode(List<Node> nodes) {
        return new ImportDefAST(ast,qualifiedId,nodes);
      }
    };
  }
  
  public NodeBuilder<MacroDefAST> createMacroDef(final TokenAST<String> name,final String regex) {
    return new NodeBuilder<MacroDefAST>(annotationComputer) {
      @Override
      protected MacroDefAST createNode(List<Node> nodes) {
        return new MacroDefAST(ast,name,regex,nodes);
      }
    };
  }
  
  public NodeBuilder<NonTerminalDefAST> createNonTerminalDef(final TokenAST<String> name,final TypeVarAST type,final boolean appendMode,final List<ProductionDefAST> productions) {
    return new NodeBuilder<NonTerminalDefAST>(annotationComputer) {
      @Override
      protected NonTerminalDefAST createNode(List<Node> nodes) {
        return new NonTerminalDefAST(ast,name,type,appendMode,productions,nodes);
      }
    };
  }
  
  public NodeBuilder<UnquotedIdVarAST> createUnquotedIdVar(final TokenAST<String> name) {
    return new NodeBuilder<UnquotedIdVarAST>(annotationComputer) {
      @Override
      protected UnquotedIdVarAST createNode(List<Node> nodes) {
        return new UnquotedIdVarAST(ast,name,nodes);
      }
    };
  }
  
  public NodeBuilder<PriorityDefAST> createPriorityDef(final TokenAST<String> name,final double number,final String assoc) {
    return new NodeBuilder<PriorityDefAST>(annotationComputer) {
      @Override
      protected PriorityDefAST createNode(List<Node> nodes) {
        return new PriorityDefAST(ast,name,number,assoc,nodes);
      }
    };
  }
  
  public NodeBuilder<PriorityVarAST> createPriorityVar(final TokenAST<String> name) {
    return new NodeBuilder<PriorityVarAST>(annotationComputer) {
      @Override
      protected PriorityVarAST createNode(List<Node> nodes) {
        return new PriorityVarAST(ast,name,nodes);
      }
    };
  }
  
  public NodeBuilder<ProductionDefAST> createProductionDef(
      final List<Node> varlist,final PriorityVarAST priority,final ProductionIdAndVersionDefAST idAndVersion) {
    return new NodeBuilder<ProductionDefAST>(annotationComputer) {
      @Override
      protected ProductionDefAST createNode(List<Node> trees) {
        return new ProductionDefAST(ast,varlist,priority,idAndVersion,trees);
      }
    };
  }
  
  public NodeBuilder<DirectiveDefAST> createDirectiveDef(final TokenAST<String> name) {
    return new NodeBuilder<DirectiveDefAST>(annotationComputer) {
      @Override
      protected DirectiveDefAST createNode(List<Node> trees) {
        return new DirectiveDefAST(ast,name,trees);
      }
    };
  }
  
  public NodeBuilder<RuleDefAST> createRuleDef(final TokenAST<String> name,final String regex) {
    return new NodeBuilder<RuleDefAST>(annotationComputer) {
      @Override
      protected RuleDefAST createNode(List<Node> trees) {
        return new RuleDefAST(ast,name,regex,trees);
      }
    };
  }
  
  public NodeBuilder<RootDefAST> createRootDef(final StartNonTerminalSetDefAST startNonTerminalSetDef) {
    return new NodeBuilder<RootDefAST>(annotationComputer) {
      @Override
      protected RootDefAST createNode(List<Node> trees) {
        return new RootDefAST(ast,startNonTerminalSetDef,trees);
      }
    };
  }
  
  public NodeBuilder<StartNonTerminalSetDefAST> createStartNonTerminalSetDef(final List<UnquotedIdVarAST> startNonTerminalList) {
    return new NodeBuilder<StartNonTerminalSetDefAST>(annotationComputer) {
      @Override
      protected StartNonTerminalSetDefAST createNode(List<Node> trees) {
        return new StartNonTerminalSetDefAST(ast,startNonTerminalList,trees);
      }
    };
  }
  
  public NodeBuilder<TerminalDefAST> createTerminalDef(final TerminalKind kind,final TokenAST<String> name,final AliasDefAST alias,
      final TypeVarAST type, final RuleDefAST rule, final PriorityVarAST priority) {
    return new NodeBuilder<TerminalDefAST>(annotationComputer) {
      @Override
      protected TerminalDefAST createNode(List<Node> trees) {
        return new TerminalDefAST(ast,kind,alias,name,type,rule,priority,trees);
      }
    };
  }
  
  public NodeBuilder<QuotedIdVarAST> createQuotedIdVar(final TokenAST<String> name) {
    return new NodeBuilder<QuotedIdVarAST>(annotationComputer) {
      @Override
      protected QuotedIdVarAST createNode(List<Node> trees) {
        return new QuotedIdVarAST(ast,name,trees);
      }
    };
  }
  
  public NodeBuilder<TypeVarAST> createTypeVar(final TokenAST<String> qualifiedid) {
    return new NodeBuilder<TypeVarAST>(annotationComputer) {
      @Override
      protected TypeVarAST createNode(List<Node> nodes) {
        return new TypeVarAST(ast,qualifiedid,nodes);
      }
    };
  }
  
  public NodeBuilder<AttributeDefAST> createAttributeDef(final TokenAST<String> id,final TypeVarAST type) {
    return new NodeBuilder<AttributeDefAST>(annotationComputer) {
      @Override
      protected AttributeDefAST createNode(List<Node> nodes) {
        return new AttributeDefAST(ast,id,type,nodes);
      }
    };
  }
  
  public NodeBuilder<VersionDefAST> createVersionDef(final TokenAST<String> name,final VersionVarAST parent) {
    return new NodeBuilder<VersionDefAST>(annotationComputer) {
      @Override
      protected VersionDefAST createNode(List<Node> nodes) {
        return new VersionDefAST(ast,name,parent,nodes);
      }
    };
  }

  public NodeBuilder<AliasDefAST> createAliasDef(final TokenAST<String> name) {
    return new NodeBuilder<AliasDefAST>(annotationComputer) {
      @Override
      protected AliasDefAST createNode(List<Node> nodes) {
        return new AliasDefAST(ast,name,nodes);
      }
    };
  }

  
  public NodeBuilder<VariableTypeDefAST> createVariableTypeDef(final VariableVarAST variable,final TypeVarAST type) {
    return new NodeBuilder<VariableTypeDefAST>(annotationComputer) {
      @Override
      protected VariableTypeDefAST createNode(List<Node> nodes) {
        return new VariableTypeDefAST(ast,variable,type,nodes);
      }
    };
  }
  
  public NodeBuilder<VersionVarAST> createVersionVar(final TokenAST<String> name) {
    return new NodeBuilder<VersionVarAST>(annotationComputer) {
      @Override
      protected VersionVarAST createNode(List<Node> nodes) {
        return new VersionVarAST(ast,name,nodes);
      }
    };
  }
}