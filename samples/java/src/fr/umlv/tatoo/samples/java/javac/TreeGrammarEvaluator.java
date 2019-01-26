package fr.umlv.tatoo.samples.java.javac;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCArrayAccess;
import com.sun.tools.javac.tree.JCTree.JCArrayTypeTree;
import com.sun.tools.javac.tree.JCTree.JCAssert;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCBreak;
import com.sun.tools.javac.tree.JCTree.JCCase;
import com.sun.tools.javac.tree.JCTree.JCCatch;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCContinue;
import com.sun.tools.javac.tree.JCTree.JCDoWhileLoop;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCIf;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.JCTree.JCNewArray;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCPrimitiveTypeTree;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCSwitch;
import com.sun.tools.javac.tree.JCTree.JCTry;
import com.sun.tools.javac.tree.JCTree.JCTypeCast;
import com.sun.tools.javac.tree.JCTree.JCTypeParameter;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.JCTree.JCWhileLoop;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Position;

import fr.umlv.tatoo.samples.java.tools.GrammarEvaluator;

public class TreeGrammarEvaluator implements GrammarEvaluator {
  private final TreeMaker maker;
  private final Names names;
  
  private final PositionStack positionStack;
  private final Log log;
  
  private JCCompilationUnit compilationUnitRoot;
  private JCStatement statementRoot;
  private JCExpression expressionRoot;
  private JCExpression typeRoot;
  
  public TreeGrammarEvaluator(TreeMaker maker, Names names, PositionStack positionStack, Log log) {
    this.maker = maker;
    this.names = names;
    this.positionStack = positionStack;
    this.log = log;
  }
  
  
  // helper methods that tracks start/end positions
  
  protected final int currentStartOffset() {
    return positionStack.currentStartOffset();
  }
  
  protected int currentEndOffset() {
    return Position.NOPOS;
  }
  
  protected TreeMaker maker(int startOffset) {
    return maker.at(startOffset);
  }
  
  protected final TreeMaker maker() {
    return maker(currentStartOffset());
  }
  
  protected <T extends JCTree> T endPos(@SuppressWarnings("unused") int endOffset, T tree) {
    return tree;
  }
  
  protected final <T extends JCTree> T endPos(T tree) {
    return endPos(currentEndOffset(), tree);
  }
  
  
  // generic helpers
  
  //TODO remi perhaps check position
  private JCExpression createTypeArray(JCExpression type, int dimension) {
    for(int i=0;i<dimension;i++) {
      type=/*endPos(*/maker().TypeArray(type)/*)*/;
    }
    return type;
  }
  
  // additional checks
  
  private void checkFieldInit(ListBuffer<JCTree> fieldDeclaration) {
    for(JCTree tree:fieldDeclaration) {
      if (((JCVariableDecl)tree).init==null) {
        log.error(tree.pos, "expected", "=");
      }
    }
  }
  
  // get/accept roots of the parse tree
  
  public JCCompilationUnit getCompilationUnitRoot() {
    return compilationUnitRoot;
  }
  
  @Override
  public void acceptCompilationUnit(JCCompilationUnit compilationUnitRoot) {
    this.compilationUnitRoot=compilationUnitRoot; 
  }

  public JCExpression getExpressionRoot() {
    return expressionRoot;
  }
  @Override
  public void acceptExpression(JCExpression expressionRoot) {
    this.expressionRoot=expressionRoot;
  }

  public JCStatement getStatementRoot() {
    return statementRoot;
  }
  @Override
  public void acceptStatement(JCStatement statementRoot) {
    this.statementRoot=statementRoot;
  }

  public JCExpression getTypeRoot() {
    return typeRoot;
  }
  @Override
  public void acceptType(JCExpression typeRoot) {
    this.typeRoot=typeRoot;
  }
  
  
  // literals ---
  
  @Override
  public JCExpression literal_boolean(boolean booleanlit) {
    return endPos(maker().Literal(TypeTags.BOOLEAN, booleanlit?1:0));
  }
  @Override
  public JCExpression literal_character(char characterlit) {
    return endPos(maker().Literal(TypeTags.CHAR, characterlit));
  }
  @Override
  public JCExpression literal_integer(String integerlit) {
    int value;
    try {
      value=Integer.parseInt(integerlit); 
    } catch(NumberFormatException e) {
      return endPos(maker().Erroneous());
    }
    return endPos(maker().Literal(TypeTags.INT, value));
  }
  @Override
  public JCExpression literal_floatingpoint(String floatingpointlit) {
    double value;
    try {
      value=Double.parseDouble(floatingpointlit); 
    } catch(NumberFormatException e) {
      return endPos(maker().Erroneous());
    }
    return endPos(maker().Literal(TypeTags.DOUBLE, value));
  }
  @Override
  public JCExpression literal_string(String stringlit) {
    return endPos(maker().Literal(TypeTags.CLASS, stringlit));
  }
  @Override
  public JCExpression literal_null() {
    return endPos(maker().Literal(TypeTags.BOT, null));
  }
  
  // names ---
  
  @Override
  public JCIdent simple_name_identifier(String identifier) {
    Name id=names.fromString(identifier);
    return endPos(maker().Ident(id));
  }
  
  @Override
  public JCExpression name_simple(JCIdent simpleName) {
    return simpleName;
  }
  
  @Override
  public JCFieldAccess qualified_name_identifier(JCExpression name, String identifier) {
    Name selector=names.fromString(identifier);
    return endPos(maker().Select(name,selector));
  }
  
  @Override
  public JCExpression name_qualified(JCFieldAccess qualifiedName) {
    return qualifiedName;
  }
  
  
  // types ---
  
  @Override
  public JCExpression type_primitive(JCPrimitiveTypeTree primitiveType) {
    return primitiveType;
  }
  @Override
  public JCExpression type_reference(JCExpression referenceType) {
    return referenceType;
  }
  
  private JCPrimitiveTypeTree primitive_type(int typeTags) {
    return endPos(maker().TypeIdent(typeTags));
  }
  @Override
  public JCPrimitiveTypeTree primitive_type_boolean() {
    return primitive_type(TypeTags.BOOLEAN);
  }
  @Override
  public JCPrimitiveTypeTree primitive_type_byte() {
    return primitive_type(TypeTags.BYTE);
  }
  @Override
  public JCPrimitiveTypeTree primitive_type_char() {
    return primitive_type(TypeTags.CHAR);
  }
  @Override
  public JCPrimitiveTypeTree primitive_type_short() {
    return primitive_type(TypeTags.SHORT);
  }
  public JCPrimitiveTypeTree primitive_type_int() {
    return primitive_type(TypeTags.INT);
  }
  @Override
  public JCPrimitiveTypeTree primitive_type_long() {
    return primitive_type(TypeTags.LONG);
  }
  @Override
  public JCPrimitiveTypeTree primitive_type_float() {
    return primitive_type(TypeTags.FLOAT);
  }
  @Override
  public JCPrimitiveTypeTree primitive_type_double() {
    return primitive_type(TypeTags.DOUBLE);
  }
  
  
  @Override
  public JCExpression reference_type_class_type(JCExpression classType) {
    return classType;
  }
  @Override
  public JCExpression reference_type_array_type(JCArrayTypeTree arrayType) {
    return arrayType;
  }
  
  @Override
  public JCExpression class_type(JCExpression name) {
    return name;
  }
  
  
  // array types ---
  
  private JCArrayTypeTree array_type(JCExpression expression, int dimension) {
    // here dimension can't be zero
    JCArrayTypeTree arrayType=(JCArrayTypeTree)createTypeArray(expression, dimension);
    arrayType.pos=currentStartOffset();
    return endPos(arrayType);
  }
  @Override
  public JCArrayTypeTree array_type_name(JCExpression name,int dimension) {
    return array_type(name,dimension);
  }
  @Override
  public JCArrayTypeTree array_type_primitive(JCPrimitiveTypeTree primitiveType,int dimension) {
    return array_type(primitiveType,dimension);
  }
  
  
  // compilation unit --
  
  private JCCompilationUnit compilation_unit(JCModifiers packageModifiers, JCExpression packageDeclaration, List<JCImport> imports, List<JCClassDecl> typeDeclarations) {
    // package can't have modifiers but only annotations
    List<JCAnnotation> packagesAnnotations;
    if (packageDeclaration!=null) {
      long flags=packageModifiers.flags;
      if (flags!=0) {
        long lowestFlag = flags & -flags;
        log.error(packageModifiers.pos(), "mod.not.allowed.here", Flags.asFlagSet(lowestFlag));
      }
      packagesAnnotations=packageModifiers.annotations;
    } else {
      packagesAnnotations=List.<JCAnnotation>nil();
    }
    
    ListBuffer<JCTree> defs=ListBuffer.lb();
    defs.addAll(imports);
    defs.addAll(typeDeclarations);
    return endPos(maker().TopLevel(packagesAnnotations, packageDeclaration, defs.toList()));
  }
  
  @Override
  public JCCompilationUnit compilation_unit_empty() {
    return compilation_unit(null, null, List.<JCImport>nil(), List.<JCClassDecl>nil());
  }
  
  @Override
  public JCCompilationUnit compilation_unit_package(JCModifiers modifiers, JCExpression packageDeclaration) {
    return compilation_unit(modifiers, packageDeclaration, List.<JCImport>nil(), List.<JCClassDecl>nil());
  }
  @Override
  public JCCompilationUnit compilation_unit_imports(ListBuffer<JCImport> importDeclarations) {
    return compilation_unit(null, null, importDeclarations.toList(), List.<JCClassDecl>nil());
  }
  @Override
  public JCCompilationUnit compilation_unit_package_imports(JCModifiers modifiers, JCExpression packageDeclaration, ListBuffer<JCImport> importDeclarations) {
    return compilation_unit(modifiers, packageDeclaration, importDeclarations.toList(), List.<JCClassDecl>nil());
  }
  @Override
  public JCCompilationUnit compilation_unit_package_type_declaration(JCModifiers modifiers, JCExpression packageDeclaration, ListBuffer<JCClassDecl> typeDeclarations) {
    return compilation_unit(modifiers, packageDeclaration, List.<JCImport>nil(), typeDeclarations.toList());
  }
  @Override
  public JCCompilationUnit compilation_unit_type_declaration(ListBuffer<JCClassDecl> typeDeclarations) {
    return compilation_unit(null, null, List.<JCImport>nil(), typeDeclarations.toList());
  }
  @Override
  public JCCompilationUnit compilation_unit_imports_type_declaration(ListBuffer<JCImport> importDeclarations, ListBuffer<JCClassDecl> typeDeclarations) {
    return compilation_unit(null, null, importDeclarations.toList(), typeDeclarations.toList());
  }
  @Override
  public JCCompilationUnit compilation_unit_package_imports_type_declaration(JCModifiers modifiers, JCExpression packageDeclaration, ListBuffer<JCImport> importDeclarations, ListBuffer<JCClassDecl> typeDeclarations) {
    return compilation_unit(modifiers, packageDeclaration, importDeclarations.toList(), typeDeclarations.toList());
  }
  
  
  @Override
  public JCExpression package_declaration(JCExpression name) {
    return name;
  }
  
  @Override
  public JCImport import_declaration(JCExpression name) {
    return endPos(maker().Import(name,false));
  }
  @Override
  public JCImport import_declaration_all(JCExpression name) {
    name=endPos(maker().Select(name, names.asterisk));
    return endPos(maker().Import(name,false));
  }
  
  @Override
  public ListBuffer<JCImport> import_declarations_import_declaration(JCImport importDeclaration) {
    return ListBuffer.of(importDeclaration);
  }
  @Override
  public ListBuffer<JCImport> import_declarations_import_declarations_import_declaration(ListBuffer<JCImport> importDeclarations, JCImport importDeclaration) {
    return importDeclarations.append(importDeclaration);
  }
  
  @Override
  public JCClassDecl type_declaration_class_declaration(JCClassDecl classDeclaration) {
    return classDeclaration;
  }
  @Override
  public JCClassDecl type_declaration_interface_declaration(JCClassDecl interfaceDeclaration) {
    return interfaceDeclaration;
  }
  @Override
  public JCClassDecl type_declaration_enum_declaration(JCClassDecl enumDeclaration) {
    return enumDeclaration;
  }
  @Override
  public JCClassDecl type_declaration_annotation_type_declaration(JCClassDecl annotationTypeDeclaration) {
    return annotationTypeDeclaration;
  }
  @Override
  public JCClassDecl type_declaration_semicolon() {
    //It will be remove in type_declarations
    return null;
  }
  
  @Override
  public ListBuffer<JCClassDecl> type_declarations_type_declaration(JCClassDecl typeDeclaration) {
    if (typeDeclaration==null)
      return ListBuffer.lb();
    return ListBuffer.of(typeDeclaration);
  }
  @Override
  public ListBuffer<JCClassDecl> type_declarations_type_declarations_type_declaration(ListBuffer<JCClassDecl> typeDeclarations, JCClassDecl typeDeclaration) {
    if (typeDeclaration==null)
      return typeDeclarations;
    return typeDeclarations.append(typeDeclaration);
  }
  
  
  // modifiers ---
  @Override
  public JCModifiers modifiers_opt_empty() {
    return maker(Position.NOPOS).Modifiers(0);
  }
  @Override
  public JCModifiers modifiers_opt_modifiers(JCModifiers modifiers) {
    return modifiers;
  }
  @Override
  public JCModifiers modifiers_singleton(Object modifier) {
    // a modifier can be a flag (a Long) or an annotation (a JCAnnotation)
    if (modifier instanceof Integer) {
      return maker().Modifiers((Integer)modifier);
    } else {
      return maker().Modifiers(0, List.of((JCAnnotation)modifier));
    }
  }
  @Override
  public JCModifiers modifiers_recursive(JCModifiers modifiers, Object modifier) {
    // a modifier can be a flag (a Long) or an annotation (a JCAnnotation)
    if (modifier instanceof Integer) {
      long flag=(Integer)modifier;
      if ((modifiers.flags & flag) !=0 ) {
        log.rawError(currentStartOffset(), "repeated.modifier");
      }
      modifiers.flags |= flag;
    } else {
      modifiers.annotations=modifiers.annotations.prepend((JCAnnotation)modifier);
    }
    return modifiers;
  }
  
  
  @Override
  public Object modifier_annotation(JCAnnotation annotation) {
    return annotation;
  }
  
  @Override
  public Integer modifier_abstract() {
    return Flags.ABSTRACT;
  }
  @Override
  public Integer modifier_final() {
    return Flags.FINAL;
  }
  @Override
  public Integer modifier_native() {
    return Flags.NATIVE;
  }
  @Override
  public Integer modifier_private() {
    return Flags.PRIVATE;
  }
  @Override
  public Integer modifier_protected() {
    return Flags.PROTECTED;
  }
  @Override
  public Integer modifier_public() {
    return Flags.PUBLIC;
  }
  @Override
  public Integer modifier_static() {
    return Flags.STATIC;
  }
  @Override
  public Integer modifier_strictfp() {
    return Flags.STRICTFP;
  }
  @Override
  public Integer modifier_synchronized() {
    return Flags.SYNCHRONIZED;
  }
  @Override
  public Integer modifier_transient() {
    return Flags.TRANSIENT;
  }
  @Override
  public Integer modifier_volatile() {
    return Flags.VOLATILE;
  }
  
  
  // annotations ---
  
  @Override
  public List<JCAnnotation> annotations_opt_empty() {
    return List.nil();
  }
  @Override
  public List<JCAnnotation> annotations_opt_annotations(ListBuffer<JCAnnotation> annotations) {
    return annotations.toList();
  }
  @Override
  public ListBuffer<JCAnnotation> annotations_singleton(JCAnnotation annotation) {
    return ListBuffer.of(annotation);
  }
  @Override
  public ListBuffer<JCAnnotation> annotations_recursive(ListBuffer<JCAnnotation> annotations, JCAnnotation annotation) {
    return annotations.append(annotation);
  }
  
  @Override
  public JCAnnotation annotation_marker(JCExpression name) {
    return endPos(maker.Annotation(name,List.<JCExpression>nil()));
  }
  @Override
  public JCAnnotation annotation_element(JCExpression name, JCExpression elementValue) {
    return endPos(maker.Annotation(name,List.of(elementValue)));
  }
  @Override
  public JCAnnotation annotation_pairs(JCExpression name, ListBuffer<JCExpression> elementValuePairs) {
    return endPos(maker.Annotation(name,elementValuePairs.toList()));
  }
  
  @Override
  public ListBuffer<JCExpression> element_value_pairs_singleton(JCAssign elementValuePair) {
    return ListBuffer.<JCExpression>of(elementValuePair);
  }
  @Override
  public ListBuffer<JCExpression> element_value_pairs_recursive(ListBuffer<JCExpression> elementValuePairs, JCAssign elementValuePair) {
    return elementValuePairs.append(elementValuePair);
  }
  
  @Override
  public JCAssign element_value_pair(String identifier, JCExpression elementValue) {
    Name name=names.fromString(identifier);
    JCIdent ident=maker().Ident(name);
    return endPos(maker().Assign(ident,elementValue));
  }
  
  @Override
  public JCExpression element_value_expression(JCExpression constExpression) {
    return constExpression;
  }
  @Override
  public JCExpression element_value_annotation(JCAnnotation annotation) {
    return annotation;
  }
  @Override
  public JCExpression element_value_array_initializer(JCNewArray elementValueArrayInitializer) {
    return elementValueArrayInitializer;
  }
  
  private JCNewArray element_value_array(List<JCExpression> elements) {
    return endPos(maker().NewArray(null,List.<JCExpression>nil(),elements));
  }
  @Override
  public JCNewArray element_value_array_initializer_empty() {
    return element_value_array(List.<JCExpression>nil());
  }
  @Override
  public JCNewArray element_value_array_initializer_element_values(ListBuffer<JCExpression> elementValues) {
    return element_value_array(elementValues.toList());
  }
  
  @Override
  public ListBuffer<JCExpression> element_values_singleton(JCExpression elementValue) {
    return ListBuffer.of(elementValue);
  }
  @Override
  public ListBuffer<JCExpression> element_values_recursive(ListBuffer<JCExpression> elementValues, JCExpression elementValue) {
    return elementValues.append(elementValue);
  }
  
  // class declaration ---
  @Override
  public JCClassDecl class_declaration(JCModifiers modifiers, String identifier, JCTree superClass, List<JCExpression> implementsInterfaces, List<JCTree> classBody) {
    Name name=names.fromString(identifier);
    
    // check constructor name
    for(JCTree tree:classBody) {
      if (tree instanceof JCMethodDecl) {
        JCMethodDecl method=(JCMethodDecl)tree;
        // if it's a constructor
        if (method.restype==null) {
          if (name != method.name)
            log.rawError(method.pos, "invalid.meth.decl.ret.type.req");
          method.name=names.init;
        }
      }
    }
    
    return endPos(maker().ClassDef(modifiers, name,
      List.<JCTypeParameter>nil(),
      superClass, implementsInterfaces,
      classBody));
  }
  
  @Override
  public JCTree super_class_empty() {
    return null;
  }
  @Override
  public JCTree super_class_type(JCExpression classType) {
    return classType;
  }
  
  @Override
  public List<JCExpression> implements_class_type_list(ListBuffer<JCExpression> classTypeList) {
    return classTypeList.toList();
  }
  @Override
  public List<JCExpression> implements_empty() {
    return List.nil();
  }
  
  @Override
  public ListBuffer<JCExpression> class_type_list_class_type(JCExpression classType) {
    return ListBuffer.of(classType);
  }
  @Override
  public ListBuffer<JCExpression> class_type_list_class_type_list_class_type(ListBuffer<JCExpression> classTypeList, JCExpression classType) {
    return classTypeList.append(classType);
  }
  
  
  // class body ---
  
  @Override
  public List<JCTree> class_body_empty() {
    return List.nil();
  }
  @Override
  public List<JCTree> class_body_class_body_declarations(ListBuffer<JCTree> classBodyDeclarations) {
    return classBodyDeclarations.toList();
  }
  
  @Override
  public ListBuffer<JCTree> class_body_declarations_class_body_declaration(ListBuffer<JCTree> classBodyDeclaration) {
    return classBodyDeclaration;
  }
  @Override
  public ListBuffer<JCTree> class_body_declarations_class_body_declarations_class_body_declaration(ListBuffer<JCTree> classBodyDeclarations, ListBuffer<JCTree> classBodyDeclaration) {
    classBodyDeclarations.addAll(classBodyDeclaration);
    return classBodyDeclarations;
  }
  
  @Override
  public ListBuffer<JCTree> class_body_declaration_static_block(JCBlock block) {
    block.flags|=Flags.STATIC;
    return ListBuffer.<JCTree>of(block);
  }
  @Override
  public ListBuffer<JCTree> class_body_declaration_init_block(JCBlock block) {
    return ListBuffer.<JCTree>of(block);
  }
  @Override
  public ListBuffer<JCTree> class_body_declaration_class_member(ListBuffer<JCTree> classMemberDeclaration) {
    return classMemberDeclaration;
  }
  
  @Override
  public ListBuffer<JCTree> class_member_declaration_field(ListBuffer<JCTree> fieldDeclaration) {
    return fieldDeclaration;
  }
  @Override
  public ListBuffer<JCTree> class_member_declaration_method(JCMethodDecl methodDeclaration) {
    return ListBuffer.<JCTree>of(methodDeclaration);
  }
  @Override
  public ListBuffer<JCTree> class_member_declaration_constructor(JCMethodDecl constructorDeclaration) {
    return ListBuffer.<JCTree>of(constructorDeclaration);
  }
  @Override
  public ListBuffer<JCTree> class_member_declaration_inner(JCClassDecl innerMemberDeclaration) {
    return ListBuffer.<JCTree>of(innerMemberDeclaration);
  }
  @Override
  public JCClassDecl inner_member_declaration_class(JCClassDecl classDeclaration) {
    return classDeclaration;
  }
  @Override
  public JCClassDecl inner_member_declaration_interface(JCClassDecl interfaceDeclaration) {
    return interfaceDeclaration;
  }
  @Override
  public JCClassDecl inner_member_declaration_enum(JCClassDecl enumDeclaration) {
    return enumDeclaration;
  }
  @Override
  public JCClassDecl inner_member_declaration_annotation(JCClassDecl annotationTypeDeclaration) {
    return annotationTypeDeclaration;
  }
  
  // method declaration
  
  @Override
  public JCMethodDecl method_declaration(JCMethodDecl methodHeader, JCBlock methodBody) {
    methodHeader.body = methodBody;
    return methodHeader;
  }
  
  private JCMethodDecl method_header(JCModifiers modifiers, JCExpression type, MethodDeclarator methodDeclarator, List<JCExpression> throwsList) {
    return endPos(maker().MethodDef(modifiers,  // can be changed in #class_body_declaration_class_member_modifiers 
      methodDeclarator.getIdentifier(),
      type,
      List.<JCTypeParameter>nil(),
      methodDeclarator.getParameters(),
      (throwsList==null)?List.<JCExpression>nil():throwsList,
      null,                                      // can be changed in #method_declaration
      null));
  }
  @Override
  public JCMethodDecl method_header_type(JCModifiers modifiers, JCExpression type, MethodDeclarator methodDeclarator, List<JCExpression> throwsList) {
    return method_header(modifiers, type, methodDeclarator, throwsList);
  }
  @Override
  public JCMethodDecl method_header_void(JCModifiers modifiers, MethodDeclarator methodDeclarator, List<JCExpression> throwsList) {
    JCPrimitiveTypeTree voidType = maker(Position.NOPOS).TypeIdent(TypeTags.VOID);
    return method_header(modifiers, voidType, methodDeclarator, throwsList);
  }
  
  @Override
  public MethodDeclarator method_declarator(String identifier, List<JCVariableDecl> formalParameterList, int dimsOpt) {
    Name name=names.fromString(identifier);
    return new MethodDeclarator(name, formalParameterList, dimsOpt);
  }
  
  @Override
  public List<JCVariableDecl> formal_parameter_list_empty() {
    return List.nil();
  }
  @Override
  public List<JCVariableDecl> formal_parameter_list_rest(ListBuffer<JCVariableDecl> formalParameterListRest) {
    return formalParameterListRest.toList();
  }
  
  @Override
  public ListBuffer<JCVariableDecl> formal_parameter_list_rest_formal_parameter(JCVariableDecl formalParameter) {
    return ListBuffer.of(formalParameter);
  }
  @Override
  public ListBuffer<JCVariableDecl> formal_parameter_list_rest_formal_parameter_list_rest_formal_parameter(ListBuffer<JCVariableDecl> formalParameterListRest, JCVariableDecl formalParameter) {
    return formalParameterListRest.append(formalParameter);
  }
  
  @Override
  public JCVariableDecl formal_parameter(List<JCAnnotation> annotationsOpt, JCExpression type, String identifier, int dimsOpt) {
    Name name=names.fromString(identifier);
    int modifiersPos=Position.NOPOS;
    if (!annotationsOpt.isEmpty()) {
      modifiersPos=annotationsOpt.head.pos;
    }
    JCModifiers noModifiers=maker(modifiersPos).Modifiers(0,annotationsOpt);
    type = createTypeArray(type, dimsOpt);
    return endPos(maker().VarDef(noModifiers, name, type, null));
  }
  
  @Override
  public List<JCExpression> throws_empty() {
    return List.nil();
  }
  @Override
  public List<JCExpression> throws_class_type_list(ListBuffer<JCExpression> classTypeList) {
    return classTypeList.toList();
  }
  
  @Override
  public JCBlock method_body_empty() {
    return null;
  }
  @Override
  public JCBlock method_body_block(JCBlock block) {
    return block;
  }
  
  
  // constructor declaration
  @Override
  public JCMethodDecl constructor_declaration(JCModifiers modifiers, MethodDeclarator constructorDeclarator, List<JCExpression> throwsList, JCBlock constructorBody) {
    return endPos(maker().MethodDef(modifiers,
        constructorDeclarator.getIdentifier(),
        null,
        List.<JCTypeParameter>nil(),
        constructorDeclarator.getParameters(),
        throwsList,
        constructorBody,
        null));
  }
  
  @Override
  public MethodDeclarator constructor_declarator(String identifier, List<JCVariableDecl> formalParameterList) {
    Name name=names.fromString(identifier);
    return new MethodDeclarator(name, formalParameterList, 0);
  }
  
  private JCBlock constructor_body(List<JCStatement> blockStatements) {
    return endPos(maker().Block(0, blockStatements));
  }
  @Override
  public JCBlock constructor_body_empty() {
    return constructor_body(List.<JCStatement>nil());
  }
  @Override
  public JCBlock constructor_body_block(ListBuffer<JCStatement> blockStatements) {
    return constructor_body(blockStatements.toList());
  }
  @Override
  public JCBlock constructor_body_super(JCStatement explicitConstructorInvocation) {
    return constructor_body(List.of(explicitConstructorInvocation));
  }
  @Override
  public JCBlock constructor_body_super_block(JCStatement explicitConstructorInvocation, ListBuffer<JCStatement> blockStatements) {
    blockStatements.prepend(explicitConstructorInvocation);
    return constructor_body(blockStatements.toList());
  }
  
  private JCStatement explicit_constructor(JCExpression expression, List<JCExpression> argumentList) {
    JCMethodInvocation constructionCall=endPos(maker().Apply(List.<JCExpression>nil(), expression, argumentList));
    return endPos(maker().Exec(constructionCall));
  }
  private JCStatement explicit_constructor(Name name, List<JCExpression> argumentList) {
    JCIdent ident=endPos(currentStartOffset() + name.length(), maker().Ident(name));
    return explicit_constructor(ident, argumentList);
  }
  @Override
  public JCStatement explicit_constructor_invocation_super(List<JCExpression> argumentList) {
    return explicit_constructor(names._super, argumentList);
  }
  @Override
  public JCStatement explicit_constructor_invocation_this(List<JCExpression> argumentList) {
    return explicit_constructor(names._this, argumentList);
  }
  @Override
  public JCStatement explicit_constructor_invocation_primary_super(JCExpression primary, List<JCExpression> argumentList) {
    JCFieldAccess select = maker().Select(primary, names._super);
    return explicit_constructor(select, argumentList);
  }
  @Override
  public JCStatement explicit_constructor_invocation_primary_this(JCExpression primary, List<JCExpression> argumentList) {
    JCFieldAccess select = maker().Select(primary, names._this);
    return explicit_constructor(select, argumentList);
  }
  
  
  // interface declaration ---
  @Override
  public JCClassDecl interface_declaration(JCModifiers modifiers, String identifier, List<JCExpression> extendsInterfaces, List<JCTree> interfaceBody) {
    Name name=names.fromString(identifier);
    modifiers.flags|=Flags.INTERFACE;
    return endPos(maker().ClassDef(modifiers, name,
      List.<JCTypeParameter>nil(),
      null, 
      extendsInterfaces,
      interfaceBody));
  }
  
  @Override
  public List<JCExpression> extends_interfaces_empty() {
    return List.nil();
  }
  @Override
  public List<JCExpression> extends_interfaces_class_type_list(ListBuffer<JCExpression> classTypeList) {
    return classTypeList.toList();
  }
  
  @Override
  public List<JCTree> interface_body_empty() {
    return List.nil();
  }
  @Override
  public List<JCTree> interface_body_declarations(ListBuffer<JCTree> interfaceBodyDeclarations) {
    return interfaceBodyDeclarations.toList();
  }
  
  @Override
  public ListBuffer<JCTree> interface_body_declarations_interface_body_declaration(ListBuffer<JCTree> interfaceBodyDeclaration) {
    return interfaceBodyDeclaration;
  }
  @Override
  public ListBuffer<JCTree> interface_body_declarations_interface_body_declarations_interface_body_declaration(
      ListBuffer<JCTree> interfaceBodyDeclarations, ListBuffer<JCTree> interfaceBodyDeclaration) {
    return interfaceBodyDeclarations.appendList(interfaceBodyDeclaration);
  }
  
  @Override
  public ListBuffer<JCTree> interface_member_declaration_constant(ListBuffer<JCTree> fieldDeclaration) {
    checkFieldInit(fieldDeclaration);
    return fieldDeclaration;
  }
  @Override
  public ListBuffer<JCTree> interface_member_declaration_abstract_method(JCMethodDecl abstractMethodDeclaration) {
    return ListBuffer.<JCTree>of(abstractMethodDeclaration);
  }
  @Override
  public ListBuffer<JCTree> interface_member_declaration_inner(JCClassDecl innerMemberDeclaration) {
    return ListBuffer.<JCTree>of(innerMemberDeclaration);
  }
  
  @Override
  public JCMethodDecl abstract_method_declaration(JCMethodDecl methodHeader) {
    return methodHeader;
  }
  
  
  // enum declaration ---
  
  public JCClassDecl enum_declaration(JCModifiers modifiers, String identifier, List<JCExpression> implementsInterfaces, List<JCTree> enumBodyDefs) {
    Name enumName=names.fromString(identifier);
    
    // insert enum type to enum constant (variable and constructor call)
    for(JCTree tree:enumBodyDefs) {  
      if (tree instanceof JCVariableDecl) {
        JCVariableDecl variable=(JCVariableDecl)tree;
        if ((variable.mods.flags & Flags.ENUM)!=0) {
          variable.vartype=maker().Ident(enumName);
          ((JCNewClass)variable.init).clazz=maker().Ident(enumName);
        }
      }
    }
    
    modifiers.flags|=Flags.ENUM;
    return endPos(maker().ClassDef(modifiers,enumName,List.<JCTypeParameter>nil(),null,implementsInterfaces,enumBodyDefs));
  }
  
  @Override
  public List<JCTree> enum_constants_opt_empty() {
    return List.nil();
  }
  @Override
  public List<JCTree> enum_constants_opt_enum_constants(ListBuffer<JCTree> enumConstants) {
    return enumConstants.toList();
  }
  @Override
  public List<JCTree> enum_constants_opt_enum_constants_trailing_comma(ListBuffer<JCTree> enumConstants) {
    return enumConstants.toList();
  }
  
  @Override
  public ListBuffer<JCTree> enum_constants_singleton(JCVariableDecl enumConstant) {
    return ListBuffer.<JCTree>of(enumConstant);
  }
  @Override
  public ListBuffer<JCTree> enum_constants_recursive(ListBuffer<JCTree> enumConstants, JCVariableDecl enumConstant) {
    return enumConstants.append(enumConstant);
  }
  
  @Override
  public List<JCTree> enum_body(List<JCTree> enumConstantsOpt, ListBuffer<JCTree> enumBodyDeclarations) {
    return enumConstantsOpt.appendList(enumBodyDeclarations);
  }
  
  @Override
  public ListBuffer<JCTree> enum_body_declarations_empty() {
    return ListBuffer.lb();
  }
  @Override
  public ListBuffer<JCTree> enum_body_declarations_body(ListBuffer<JCTree> classBodyDeclarations) {
    return classBodyDeclarations;
  }
  
  
  
  private JCVariableDecl enum_constant(List<JCAnnotation> annotationsOpt, String identifier, List<JCExpression> argumentList, List<JCTree> defs) {
    JCClassDecl body = null;
    if (defs!=null) {
      JCModifiers bodyModifiers=maker(Position.NOPOS).Modifiers(Flags.ENUM|Flags.STATIC);
      body=maker(Position.NOPOS).AnonymousClassDef(bodyModifiers, defs);
    }
    
    // type is null here, it will be patched after with the enum name
    JCNewClass init =maker(Position.NOPOS).NewClass(null, List.<JCExpression>nil(), null, argumentList, body);
    
    int modifiersPos=Position.NOPOS;
    if (!annotationsOpt.isEmpty()) {
      modifiersPos=annotationsOpt.head.pos;
    }
    JCModifiers modifiers=maker(modifiersPos).Modifiers(
        Flags.PUBLIC|Flags.STATIC|Flags.FINAL|Flags.ENUM);
    Name varName=names.fromString(identifier);
    
    // type is null here, it will be patched after with the enum name
    return endPos(maker().VarDef(modifiers, varName, null, init));
  }
  
  @Override
  public JCVariableDecl enum_constant_identifier(List<JCAnnotation> annotationsOpt, String identifier) {
    return enum_constant(annotationsOpt, identifier, List.<JCExpression>nil(), null);
  }
  @Override
  public JCVariableDecl enum_constant_identifier_anonymous(List<JCAnnotation> annotationsOpt, String identifier, List<JCTree> defs) {
    return enum_constant(annotationsOpt, identifier, List.<JCExpression>nil(), null);
  }
  @Override
  public JCVariableDecl enum_constant_constructor_invocation(List<JCAnnotation> annotationsOpt, String identifier, List<JCExpression> argumentList) {
    return enum_constant(annotationsOpt, identifier, argumentList, null);
  }
  @Override
  public JCVariableDecl enum_constant_constructor_invocation_anonymous(List<JCAnnotation> annotationsOpt, String identifier, List<JCExpression> argumentList, List<JCTree> defs) {
    return enum_constant(annotationsOpt, identifier, argumentList, defs);
  }
  
  
  // annotation type ---
  private JCClassDecl annotation_type_declaration(JCModifiers modifiers, String identifier, List<JCTree> annotationTypeBody) {
    Name name=names.fromString(identifier);
    modifiers.flags|=Flags.ANNOTATION|Flags.INTERFACE;
    return endPos(maker().ClassDef(modifiers, name, List.<JCTypeParameter>nil(), null, List.<JCExpression>nil(), annotationTypeBody));
  }
  @Override
  public JCClassDecl annotation_type_declaration_default(String identifier, List<JCTree> annotationTypeBody) {
    JCModifiers noModifiers=maker(Position.NOPOS).Modifiers(0);
    return annotation_type_declaration(noModifiers, identifier, annotationTypeBody);
  }
  @Override
  public JCClassDecl annotation_type_declaration_modifiers(JCModifiers modifiers, String identifier, List<JCTree> annotationTypeBody) {
    return annotation_type_declaration(modifiers, identifier, annotationTypeBody);
  }
  
  @Override
  public List<JCTree> annotation_type_body_empty() {
    return List.nil();
  }
  @Override
  public List<JCTree> annotation_type_body_annotation_member_type_declarations(ListBuffer<JCTree> annotationTypeMemberDeclarations) {
    return annotationTypeMemberDeclarations.toList();
  }
  
  @Override
  public ListBuffer<JCTree> annotation_type_member_declarations_singleton(ListBuffer<JCTree> annotationTypeMemberDeclaration) {
    return annotationTypeMemberDeclaration;
  }
  @Override
  public ListBuffer<JCTree> annotation_type_member_declarations_recursive(ListBuffer<JCTree> annotationTypeMemberDeclarations, ListBuffer<JCTree> annotationTypeMemberDeclaration) {
    return annotationTypeMemberDeclarations.appendList(annotationTypeMemberDeclaration);
  }
  
  @Override
  public ListBuffer<JCTree> annotation_type_member_declaration_field(ListBuffer<JCTree> fieldDeclaration) {
    checkFieldInit(fieldDeclaration);
    return fieldDeclaration;
  }
  @Override
  public ListBuffer<JCTree> annotation_type_member_declaration_annotation_method(JCMethodDecl annotationMethodDeclaration) {
    return ListBuffer.<JCTree>of(annotationMethodDeclaration);
  }
  @Override
  public ListBuffer<JCTree> annotation_type_member_declaration_inner(JCClassDecl innerMemberDeclaration) {
    return ListBuffer.<JCTree>of(innerMemberDeclaration);
  }
  
  @Override
  public JCMethodDecl annotation_method_declaration_type(JCModifiers modifiers, JCExpression returnType, String identifier, int dimsOpt, JCExpression defaultValue) {
    Name name=names.fromString(identifier);
    returnType=createTypeArray(returnType, dimsOpt);
    return endPos(maker().MethodDef(modifiers, name, returnType, List.<JCTypeParameter>nil(), List.<JCVariableDecl>nil(), List.<JCExpression>nil(), null, defaultValue));
  }
  
  @Override
  public JCExpression default_value_empty() {
    return null;
  }
  @Override
  public JCExpression default_value_element_value(JCExpression elementValue) {
    return elementValue;
  }
  
  
  // block ---
  
  private JCBlock block(List<JCStatement> statements) {
    return endPos(maker().Block(0, statements));
  }
  
  @Override
  public JCBlock block_empty() {
    return block(List.<JCStatement>nil());
  }
  
  
  @Override
  public JCBlock block_statements(ListBuffer<JCStatement> blockStatements) {
    return block(blockStatements.toList());
  }
  
  @Override
  public ListBuffer<JCStatement> block_statements_block_statement(List<JCStatement> blockStatement) {
    return ListBuffer.<JCStatement>lb().appendList(blockStatement);
  }
  @Override
  public ListBuffer<JCStatement> block_statements_block_statements_block_statement(ListBuffer<JCStatement> blockStatements, List<JCStatement> blockStatement) {
    return blockStatements.appendList(blockStatement);
  }
  
  @Override
  public List<JCStatement> block_statement_declaration_local_class(JCClassDecl classDeclaration) {
    return List.<JCStatement>of(classDeclaration);
  }
  @Override
  public List<JCStatement> block_statement_declaration_local_interface(JCClassDecl interfaceDeclaration) {
    return List.<JCStatement>of(interfaceDeclaration);
  }
  @Override
  public List<JCStatement> block_statement_declaration_local_enum(JCClassDecl enumDeclaration) {
    return List.<JCStatement>of(enumDeclaration);
  }
  @Override
  public List<JCStatement> block_statement_declaration_local_annotation_type(JCClassDecl annotationTypeDeclaration) {
    return List.<JCStatement>of(annotationTypeDeclaration);
  }
  
  @Override
  public List<JCStatement> block_statement_locals_declaration(List<JCStatement> localVariableDeclaration) {
    return localVariableDeclaration;
  }
  @Override
  public List<JCStatement> block_statement_statement(JCStatement statement) {
    return List.of(statement);
  }

  
  // local variables, fields declaration
  
  private void variable_declaration(JCModifiers modifiers, ListBuffer<? super JCVariableDecl> variables, JCExpression type, ListBuffer<VariableDeclarator> variableDeclarators) {
    for(VariableDeclarator variableDeclarator:variableDeclarators) {
      JCExpression elemtype=createTypeArray(type, variableDeclarator.getDimension());  
      
      // if init expression is an array, it should have the same element type
      JCExpression initExpression=variableDeclarator.getInitExpression();
      if (initExpression!=null && initExpression.getTag()==JCTree.NEWARRAY) {
        ((JCNewArray)initExpression).elemtype=elemtype;
      }
      
      variables.append(
        endPos(variableDeclarator.getEndPosition(),
          maker().VarDef(modifiers, variableDeclarator.getIdentifier(), elemtype, initExpression)
        )
      );
    }
  }
  
  @Override
  public ListBuffer<JCTree> field_declaration(JCModifiers modifiers, JCExpression type, ListBuffer<VariableDeclarator> variableDeclarators) {
    ListBuffer<JCTree> variables=ListBuffer.lb();
    variable_declaration(modifiers, variables, type, variableDeclarators);
    return variables;
  }
  
  @Override
  public List<JCStatement> local_variable_declaration_default(JCExpression type, ListBuffer<VariableDeclarator> variableDeclarators) {
    ListBuffer<JCStatement> statements=ListBuffer.lb();
    JCModifiers noModifiers=maker(Position.NOPOS).Modifiers(0);
    variable_declaration(noModifiers, statements, type, variableDeclarators);
    return statements.toList();
  }
  @Override
  public List<JCStatement> local_variable_declaration_final(JCModifiers modifiers, JCExpression type, ListBuffer<VariableDeclarator> variableDeclarators) {
    ListBuffer<JCStatement> statements=ListBuffer.lb();
    variable_declaration(modifiers, statements, type, variableDeclarators);
    return statements.toList();
  }
  
  @Override
  public ListBuffer<VariableDeclarator> variable_declarators_variable_declarator(VariableDeclarator variableDeclarator) {
    return ListBuffer.of(variableDeclarator);
  }
  @Override
  public ListBuffer<VariableDeclarator> variable_declarators_variable_declarators_variable_declarator(ListBuffer<VariableDeclarator> variableDeclarators, VariableDeclarator variableDeclarator) {
    return variableDeclarators.append(variableDeclarator);
  }
  
  
  @Override
  public VariableDeclarator variable_declarator_identifier(String identifier, int dimsOpt, JCExpression variableDeclaratorRest) {
    Name name=names.fromString(identifier);
    return new VariableDeclarator(currentEndOffset(), name, dimsOpt, variableDeclaratorRest);
  }
  
  @Override
  public JCExpression variable_declarator_rest_init(JCExpression variableInitializer) {
    return variableInitializer;
  }
  @Override
  public JCExpression variable_declarator_rest_empty() {
    return null;
  }
  
  @Override
  public JCExpression variable_initializer_array(List<JCExpression> array_initializer) {
    return endPos(maker().NewArray(null, List.<JCExpression>nil(),array_initializer));
  }
  @Override
  public JCExpression variable_initializer_expression(JCExpression expression) {
    return expression;
  }
  
  @Override
  public List<JCExpression> array_initializer_empty() {
    return List.<JCExpression>nil();
  }
  @Override
  public List<JCExpression> array_initializer_variable_initializer_list(ListBuffer<JCExpression> variableInitializerList) {
    return variableInitializerList.toList();
  }
  
  @Override
  public ListBuffer<JCExpression> variable_initializer_list_variable_initializer(JCExpression variableInitializer) {
    return ListBuffer.of(variableInitializer);
  }
  @Override
  public ListBuffer<JCExpression> variable_initializer_list_variable_initializer_list_variable_initializer(ListBuffer<JCExpression> variableInitializerList, JCExpression variableInitializer) {
    return variableInitializerList.append(variableInitializer);
  }
  
  
  // statement expression ---
  
  @Override
  public JCStatement statement_expression(JCExpressionStatement statementExpression) {
    return statementExpression;
  }
  
  private JCExpressionStatement expression_statement(JCExpression expression) {
    return endPos(maker().Exec(expression));
  }
  
  @Override
  public JCExpressionStatement statement_expression_assignment(JCExpression assignment) {
    return expression_statement(assignment);
  }
  @Override
  public JCExpressionStatement statement_expression_instance_creation(JCNewClass classInstanceCreationExpression) {
    return expression_statement(classInstanceCreationExpression);
  }
  @Override
  public JCExpressionStatement statement_expression_method_invocation(JCMethodInvocation methodInvocation) {
    return expression_statement(methodInvocation);
  }
  @Override
  public JCExpressionStatement statement_expression_post_decrement(JCExpression postDecrementExpression) {
    return expression_statement(postDecrementExpression);
  }
  @Override
  public JCExpressionStatement statement_expression_post_increment(JCExpression postIncrementExpression) {
    return expression_statement(postIncrementExpression);
  }
  @Override
  public JCExpressionStatement statement_expression_pre_decrement(JCExpression preDecrementExpression) {
    return expression_statement(preDecrementExpression);
  }
  @Override
  public JCExpressionStatement statement_expression_pre_increment(JCExpression preIncrementExpression) {
    return expression_statement(preIncrementExpression);
  }
  
  
  // assert
  @Override
  public JCStatement statement_assert(JCAssert assertStatement) {
    return assertStatement;
  }
  @Override
  public JCAssert assert_statement(JCExpression condition, JCExpression message) {
    return endPos(maker().Assert(condition, message));
  }
  @Override
  public JCExpression assert_expression_rest_empty() {
    return null;
  }
  @Override
  public JCExpression assert_expression_rest_expression(JCExpression expression) {
    return expression;
  }
  
  // if, if...else ---
  
  @Override
  public JCStatement statement_if(JCIf ifStatement) {
    return ifStatement;
  }
  @Override
  public JCIf if_statement_if(JCExpression expression, JCStatement statement) {
    return endPos(maker().If(expression, statement, null));
  }
  @Override
  public JCIf if_statement_else(JCExpression expression, JCStatement statement, JCStatement statement2) {
    return endPos(maker().If(expression, statement, statement2));
  }
  
  
  // for loop ---
  
  @Override
  public JCStatement statement_for(JCStatement forStatement) {
    return forStatement;
  }
  @Override
  public JCStatement for_statement_classical(List<JCStatement> forInit, JCExpression expression_optional, List<JCExpressionStatement> forUpdate, JCStatement statement) {
    return endPos(maker().ForLoop(forInit, expression_optional, forUpdate, statement));
  }
  @Override
  public JCStatement for_statement_enhanced(JCVariableDecl forEnhancedInit, JCExpression expression, JCStatement statement) {
    return endPos(maker().ForeachLoop(forEnhancedInit, expression, statement));
  }
  
  @Override
  public JCVariableDecl for_enhanced_init_identifier(JCExpression type, String identifier) {
    JCModifiers noModifiers=maker(Position.NOPOS).Modifiers(0);
    return for_enhanced_init_modifiers(noModifiers, type, identifier);
  }
  @Override
  public JCVariableDecl for_enhanced_init_modifiers(JCModifiers modifiers, JCExpression type, String identifier) {
    Name name=names.fromString(identifier);
    return endPos(maker.VarDef(modifiers, name, type, null));
  }
  
  @Override
  public List<JCStatement> for_init_empty() {
    return List.nil();
  }
  @Override
  public List<JCStatement> for_init_local_variable_declaration(List<JCStatement> localVariableDeclaration) {
    return localVariableDeclaration;
  }
  @Override
  public List<JCStatement> for_init_statement_expression_list(ListBuffer<JCExpressionStatement> statementExpressionList) {
    return List.convert(JCStatement.class,statementExpressionList.toList());
  }
  
  @Override
  public List<JCExpressionStatement> for_update_empty() {
    return List.nil();
  }
  @Override
  public List<JCExpressionStatement> for_update_statement_expression_list(ListBuffer<JCExpressionStatement> statementExpressionList) {
    return statementExpressionList.toList();
  }
  
  @Override
  public ListBuffer<JCExpressionStatement> statement_expression_list_statement_expression(JCExpressionStatement statementExpression) {
    return ListBuffer.of(statementExpression);
  }
  @Override
  public ListBuffer<JCExpressionStatement> statement_expression_list_statement_expression_list_statement_expression(ListBuffer<JCExpressionStatement> statementExpressionList, JCExpressionStatement statementExpression) {
    return statementExpressionList.append(statementExpression);
  }

  
  // while, do...while loop ---
  
  @Override
  public JCStatement statement_while(JCWhileLoop whileStatement) {
    return whileStatement;
  }
  @Override
  public JCWhileLoop while_statement(JCExpression expression, JCStatement statement) {
    return endPos(maker().WhileLoop(expression, statement));
  }
  
  @Override
  public JCStatement statement_do(JCDoWhileLoop doStatement) {
    return doStatement;
  }
  @Override
  public JCDoWhileLoop do_statement(JCStatement statement, JCExpression expression) {
    return endPos(maker().DoLoop(statement,expression));
  }
  
  
  // switch ---
  
  @Override
  public JCStatement statement_switch(JCSwitch switchStatement) {
    return switchStatement;
  }
  
  @Override
  public JCSwitch switch_statement(JCExpression selector, List<JCCase> cases) {
    return endPos(maker().Switch(selector, cases));
  }
  
  @Override
  public List<JCCase> switch_block_empty() {
    return List.nil();
  }
  @Override
  public List<JCCase> switch_block_switch_cases(ListBuffer<JCCase> cases) {
    return cases.toList();
  }
  
  @Override
  public ListBuffer<JCCase> switch_cases_switch_case(JCCase switchCase) {
    return ListBuffer.of(switchCase);
  }
  @Override
  public ListBuffer<JCCase> switch_cases_switch_cases_switch_case(ListBuffer<JCCase> cases, JCCase switchCase) {
    return cases.append(switchCase);
  }
  @Override
  public JCCase switch_case_default(ListBuffer<JCStatement> blockStatements) {
    return endPos(maker().Case(null,blockStatements.toList()));
  }
  @Override
  public JCCase switch_case_case(JCExpression constantExpression, ListBuffer<JCStatement> blockStatements) {
    return endPos(maker().Case(constantExpression,blockStatements.toList()));
  }
  
  
  // label, break, continue, return ---
  @Override
  public JCStatement statement_label(String identifier, JCStatement statement) {
    Name label=names.fromString(identifier);
    return endPos(maker().Labelled(label,statement));
  }
  
  @Override
  public JCStatement statement_break(JCBreak breakStatement) {
    return breakStatement;
  }
  @Override
  public JCBreak break_statement_unamed() {
    return endPos(maker().Break(null));
  }
  @Override
  public JCBreak break_statement_identifier(String identifier) {
    Name name=names.fromString(identifier);
    return endPos(maker().Break(name));
  }
  
  @Override
  public JCStatement statement_continue(JCContinue continueStatement) {
    return continueStatement;
  }
  @Override
  public JCContinue continue_statement_unamed() {
    return endPos(maker().Continue(null));
  }
  @Override
  public JCContinue continue_statement_identifier(String identifier) {
    Name name=names.fromString(identifier);
    return endPos(maker().Continue(name));
  }
  
  @Override
  public JCStatement statement_return(JCReturn returnStatement) {
    return returnStatement;
  }
  @Override
  public JCReturn return_statement_void() {
    return endPos(maker().Return(null));
  }
  @Override
  public JCReturn return_statement_expression(JCExpression expression) {
    return endPos(maker().Return(expression));
  }
  
  
  // try, catch, finally ---
  
  @Override
  public JCStatement statement_try(JCTry tryStatement) {
    return tryStatement;
  }
  @Override
  public JCTry try_statement_catches(JCBlock block, ListBuffer<JCCatch> catches) {
    return endPos(maker().Try(block, catches.toList(), null));
  }
  @Override
  public JCTry try_statement_finally(JCBlock block, JCBlock finallyBlock) {
    return endPos(maker().Try(block, List.<JCCatch>nil(), finallyBlock));
  }
  @Override
  public JCTry try_statement_cacthes_finally(JCBlock block, ListBuffer<JCCatch> catches,JCBlock finallyBlock) {
    return endPos(maker().Try(block, catches.toList(), finallyBlock));
  }
  
  @Override
  public ListBuffer<JCCatch> catches_catch_clause(JCCatch catchClause) {
    return ListBuffer.of(catchClause);
  }
  @Override
  public ListBuffer<JCCatch> catches_catches_catch_clause(ListBuffer<JCCatch> catches,JCCatch catchClause) {
    return catches.append(catchClause);
  }
  @Override
  public JCCatch catch_clause(JCVariableDecl formalParameter, JCBlock block) {
    return endPos(maker().Catch(formalParameter, block));
  }
  
  @Override
  public JCBlock _finally(JCBlock block) {
    return block;
  }
  
  
  // other statements ---
  
  @Override
  public JCStatement statement_throw(JCExpression expression) {
    return endPos(maker().Throw(expression));
  }
  @Override
  public JCStatement statement_synchronized(JCExpression expression, JCBlock block) {
    return endPos(maker().Synchronized(expression, block));
  } 
  public JCStatement statement_block(JCBlock block) {
    return block;
  }
  @Override
  public JCStatement statement_empty() {
    return endPos(maker().Skip());
  }
  
  
  // primary ---
  
  @Override
  public JCExpression primary_no_new_array(JCExpression primaryNoNewArray) {
    return primaryNoNewArray;
  }
  @Override
  public JCExpression primary_literal(JCExpression literal) {
    return literal;
  }
  @Override
  public JCExpression primary_this() {
    return endPos(maker().Ident(names._this));
  }
  @Override
  public JCExpression primary_parenthesis_expression(JCExpression expression) {
    return endPos(maker().Parens(expression));
  }
  
  @Override
  public JCExpression primary_or_name_name(JCExpression name) {
    return name;
  }
  @Override
  public JCExpression primary_or_name_primary(JCExpression primary) {
    return primary;
  }
  
  
  // misc expressions ---
  
  @Override
  public JCExpression expression_primary(JCExpression primaryOrName) {
    return primaryOrName;
  }
  
  @Override
  public JCExpression expression_instanceof(JCExpression expression, JCExpression referenceType) {
    return endPos(maker().TypeTest(expression, referenceType));
  }
  
  public JCExpression expression_condition(JCExpression conditional, JCExpression thenPart, JCExpression elsePart) {
    return endPos(maker().Conditional(conditional, thenPart, elsePart));
  }
  
  
  
  // class instance creation ---
  
  @Override
  public JCExpression primary_instance_creation_expression(JCNewClass classInstanceCreationExpression) {
    return classInstanceCreationExpression;
  }
  private JCNewClass class_instance_creation_expression(JCExpression enclosingExpr, JCExpression classType, List<JCExpression> argumentList, JCClassDecl classDeclaration) {
    return endPos(maker().NewClass(enclosingExpr, List.<JCExpression>nil(), classType, argumentList, classDeclaration));
  }
  @Override
  public JCNewClass class_instance_creation_expression_default(JCExpression classType, List<JCExpression> argumentList) {
    return class_instance_creation_expression(null, classType, argumentList, null);
  }
  @Override
  public JCNewClass class_instance_creation_expression_anonymous(JCExpression classType, List<JCExpression> argumentList, JCClassDecl anonymousClassDeclaration) {
    return class_instance_creation_expression(null, classType, argumentList, anonymousClassDeclaration);
  }
  @Override
  public JCNewClass class_instance_creation_expression_expression_dot(JCExpression primary, JCExpression classType, List<JCExpression> argumentList) {
    return class_instance_creation_expression(primary, classType, argumentList, null);
  }
  @Override
  public JCNewClass class_instance_creation_expression_anonymous_expression_dot(JCExpression primary, JCExpression classType, List<JCExpression> argumentList,JCClassDecl anonymousClassDeclaration) {
    return class_instance_creation_expression(primary, classType, argumentList, anonymousClassDeclaration);
  }
  
  @Override
  public JCClassDecl anonymous_class_declaration(List<JCTree> classBody) {
    JCModifiers noModifiers= maker(Position.NOPOS).Modifiers(0);
    return endPos(maker().AnonymousClassDef(noModifiers, classBody));
  }
  
  @Override
  public List<JCExpression> argument_list_empty() {
    return List.nil();
  }
  @Override
  public List<JCExpression> argument_list_rest(ListBuffer<JCExpression> argumentListRest) {
    return argumentListRest.toList();
  }
  @Override
  public ListBuffer<JCExpression> argument_list_rest_expression(JCExpression expression) {
    return ListBuffer.of(expression);
  }
  @Override
  public ListBuffer<JCExpression> argument_list_rest_argument_list_rest_expression(ListBuffer<JCExpression> argumentListRest, JCExpression expression) {
    return argumentListRest.append(expression);
  }
  
  
  // array class instance creation ---
  
  @Override
  public JCExpression primary_array_creation_expression(JCNewArray arrayCreationExpression) {
    return arrayCreationExpression;
  }
  
  private JCNewArray array_creation_expression(JCExpression type, int dimension, ListBuffer<JCExpression> dimExprs, List<JCExpression> array_initializer) {
    return endPos(maker().NewArray(createTypeArray(type, dimension), dimExprs.toList(), array_initializer));
  }
  @Override
  public JCNewArray array_creation_expression_primive(JCPrimitiveTypeTree primitiveType, ListBuffer<JCExpression> dimExprs, int dimsOpt) {
    return array_creation_expression(primitiveType, dimsOpt, dimExprs, List.<JCExpression>nil());
  }
  @Override
  public JCNewArray array_creation_expression_reference(JCExpression classType, ListBuffer<JCExpression> dimExprs, int dimsOpt) {
    return array_creation_expression(classType, dimsOpt, dimExprs, List.<JCExpression>nil());
  }
  @Override
  public JCNewArray array_creation_expression_primitive_initializer(JCPrimitiveTypeTree primitiveType, ListBuffer<JCExpression> dimExprs, int dims, List<JCExpression> arrayInitializer) {
    return array_creation_expression(primitiveType, dims, dimExprs, arrayInitializer);
  }
  @Override
  public JCNewArray array_creation_expression_reference_initializer(JCExpression classType, ListBuffer<JCExpression> dimExprs, int dims, List<JCExpression> arrayInitializer) {
    return array_creation_expression(classType, dims, dimExprs, arrayInitializer);
  }
  
  @Override
  public ListBuffer<JCExpression> dim_exprs_dim_expr(JCExpression dimExpr) {
    return ListBuffer.of(dimExpr);
  }
  @Override
  public ListBuffer<JCExpression> dim_exprs_dim_exprs_dim_expr(ListBuffer<JCExpression> dimExprs,JCExpression dimExpr) {
    return dimExprs.append(dimExpr);
  }
  @Override
  public JCExpression dim_expr(JCExpression expression) {
    return expression;
  }
  
  @Override
  public int dims_opt_empty() {
    return 0;
  }
  @Override
  public int dims_opt_dims(int dims) {
    return dims;
  }
  @Override
  public int dims_singleton() {
    return 1;
  }
  @Override
  public int dims_recursive(int dims) {
    return dims+1;
  }
  
  
  // field access, array access, method invocation, class literal ---
  
  @Override
  public JCExpression primary_field_access(JCFieldAccess fieldAccess) {
    return fieldAccess;
  }
  @Override
  public JCFieldAccess field_access_primary(JCExpression primary, String identifier) {
    Name name=names.fromString(identifier);
    return endPos(maker().Select(primary, name));
  }
  @Override
  public JCFieldAccess field_access_super(String identifier) {
    Name name=names.fromString(identifier);
    JCIdent zuper = /*endPos(*/maker().Ident(names._super)/*)*/;
    return endPos(maker().Select(zuper, name));
  }
  
  @Override
  public JCExpression primary_array_access(JCArrayAccess arrayAccess) {
    return arrayAccess;
  }
  private JCArrayAccess array_access(JCExpression expression, JCExpression index) {
    return endPos(maker().Indexed(expression, index));
  }
  @Override
  public JCArrayAccess array_access_name(JCExpression name, JCExpression expression) {
    return array_access(name, expression);
  }
  @Override
  public JCArrayAccess array_access_primary(JCExpression primaryNoNewArray, JCExpression expression) {
    return array_access(primaryNoNewArray, expression);
  }
  
  @Override
  public JCExpression primary_method_invocation(JCMethodInvocation methodInvocation) {
    return methodInvocation;
  }
  
  private JCMethodInvocation method_invocaton(JCExpression method, List<JCExpression> argumentList) {
    return endPos(maker().Apply(List.<JCExpression>nil(), method, argumentList));
  }
  @Override
  public JCMethodInvocation method_invocation_name(JCExpression name, List<JCExpression> argumentList) {
    return method_invocaton(name,argumentList);
  }
  @Override
  public JCMethodInvocation method_invocation_primary_dot(JCExpression primary, String identifier, List<JCExpression> argumentList) {
    Name name=names.fromString(identifier);
    JCFieldAccess method = /*endPos(*/maker().Select(primary, name)/*)*/;
    return method_invocaton(method,argumentList);
  }
  @Override
  public JCMethodInvocation method_invocation_super_dot(String identifier, List<JCExpression> argumentList) {
    Name name=names.fromString(identifier);
    JCIdent zuper = /*endPos(*/maker().Ident(names._super)/*)*/;
    JCFieldAccess method = /*endPos(*/maker().Select(zuper, name)/*)*/;
    return method_invocaton(method,argumentList);
  }
  
  @Override
  public JCExpression primary_name_this(JCExpression name) {
    return endPos(maker().Select(name, names._this));
  }
  
  @Override
  public JCExpression primary_class_literal(JCFieldAccess classLiteral) {
    return classLiteral;
  }
  private JCFieldAccess class_literal(JCExpression expression) {
    return endPos(maker().Select(expression, names._class));
  }
  @Override
  public JCFieldAccess class_literal_void() {
    JCPrimitiveTypeTree voidType = maker().TypeIdent(TypeTags.VOID);
    return class_literal(voidType);
  }
  @Override
  public JCFieldAccess class_literal_name(JCExpression name) {
    return class_literal(name);
  }
  @Override
  public JCFieldAccess class_literal_array_type(JCArrayTypeTree arrayType) {
    return class_literal(arrayType);
  }
  @Override
  public JCFieldAccess class_literal_primitive(JCPrimitiveTypeTree primitiveType) {
    return class_literal(primitiveType);
  }
  
  // cast expressions ---
  
  @Override
  public JCExpression expression_cast(JCTypeCast cast) {
    return cast;
  }
  
  private JCTypeCast cast_expression(JCExpression clazz, JCExpression expr) {
    return endPos(maker().TypeCast(clazz, expr));
  }
  
  @Override
  public JCTypeCast cast_expression_expression(JCExpression clazz, JCExpression expr) {
    return cast_expression(clazz, expr);
  }
  @Override
  public JCTypeCast cast_expression_primitive(JCPrimitiveTypeTree primitiveType, int dims_optional, JCExpression expression) {
    JCExpression typeArray = createTypeArray(primitiveType, dims_optional);
    return cast_expression(typeArray, expression);
  }
  @Override
  public JCTypeCast cast_expression_name(JCExpression name, int dims, JCExpression expression) {
    JCExpression typeArray = createTypeArray(name, dims);
    return cast_expression(typeArray, expression);
  }
  
  
  
  
  // binary expressions ---
  
  private JCExpression binary(int opcode, JCExpression expr, JCExpression expr2) {
    return endPos(maker().Binary(opcode, expr, expr2));
  }
  
  @Override
  public JCExpression expression_bitwise_and(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.BITAND, expr, expr2);
  }
  @Override
  public JCExpression expression_bitwise_or(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.BITOR, expr, expr2);
  }
  @Override
  public JCExpression expression_bitwise_xor(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.BITXOR, expr, expr2);
  }
  
  @Override
  public JCExpression expression_boolean_and(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.AND, expr, expr2);
  }
  @Override
  public JCExpression expression_boolean_or(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.OR, expr, expr2);
  }
  
  @Override
  public JCExpression expression_divide(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.DIV, expr, expr2);
  }
  @Override
  public JCExpression expression_times(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.MUL, expr, expr2);
  }
  @Override
  public JCExpression expression_remainder(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.MOD, expr, expr2);
  }
  @Override
  public JCExpression expression_plus(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.PLUS, expr, expr2);
  }
  @Override
  public JCExpression expression_minus(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.MINUS, expr, expr2);
  }
  
  @Override
  public JCExpression expression_left_shift(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.SL, expr, expr2);
  }
  @Override
  public JCExpression expression_right_shift_signed(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.SR, expr, expr2);
  }
  @Override
  public JCExpression expression_right_shift_unsigned(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.USR, expr, expr2);
  }
  
  @Override
  public JCExpression expression_ge(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.GE, expr, expr2);
  }
  @Override
  public JCExpression expression_gt(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.GT, expr, expr2);
  }
  @Override
  public JCExpression expression_le(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.LE, expr, expr2);
  }
  @Override
  public JCExpression expression_lt(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.LT, expr, expr2);
  }
  
  @Override
  public JCExpression expression_ne(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.NE, expr, expr2);
  }
  @Override
  public JCExpression expression_eq(JCExpression expr, JCExpression expr2) {
    return binary(JCTree.EQ, expr, expr2);
  }
  
  // unary expressions ---
  
  private JCExpression unary(int opcode, JCExpression expr) {
    return endPos(maker().Unary(opcode, expr));
  }
  
  @Override
  public JCExpression expression_unary_minus(JCExpression expr) {
    return unary(JCTree.NEG, expr);
  }
  @Override
  public JCExpression expression_unary_plus(JCExpression expr) {
    return unary(JCTree.POS, expr);
  }
  
  @Override
  public JCExpression expression_complement(JCExpression expr) {
    return unary(JCTree.COMPL, expr);
  }
  @Override
  public JCExpression expression_not(JCExpression expr) {
    return unary(JCTree.NOT, expr);
  }
  
  @Override
  public JCExpression pre_increment_expression(JCExpression expr) {
    return unary(JCTree.PREINC, expr);
  }
  @Override
  public JCExpression pre_decrement_expression(JCExpression expr) {
    return unary(JCTree.PREDEC, expr);
  }
  @Override
  public JCExpression post_increment_expression(JCExpression expr) {
    return unary(JCTree.POSTINC, expr);
  }
  @Override
  public JCExpression post_decrement_expression(JCExpression expr) {
    return unary(JCTree.POSTDEC, expr);
  }
  
  @Override
  public JCExpression expression_pre_increment(JCExpression expr) {
    return expr;
  }
  @Override
  public JCExpression expression_pre_decrement(JCExpression expr) {
    return expr;
  }
  @Override
  public JCExpression expression_post_increment(JCExpression expr) {
    return expr;
  }
  @Override
  public JCExpression expression_post_decrement(JCExpression expr) {
    return expr;
  }
  
  
  // const expression ---
  
  @Override
  public JCExpression expression_const_expression(JCExpression constExpression) {
    return constExpression;
  }
  
  // assign operator expressions ---
  
  @Override
  public JCExpression expression_assignment(JCExpression assignment) {
    return assignment;
  }
  @Override
  public JCExpression assignment(JCExpression lhs, int assignmentOperator,JCExpression rhs) {
    if (assignmentOperator == JCTree.ASSIGN) {
      return endPos(maker().Assign(lhs, rhs));
    } else {
      return endPos(maker().Assignop(assignmentOperator, lhs, rhs));
    }
  }
  
  @Override
  public JCExpression lef_hand_side_array_access(JCArrayAccess arrayAccess) {
    return arrayAccess;
  }
  @Override
  public JCExpression lef_hand_side_field_access(JCFieldAccess fieldAccess) {
    return fieldAccess;
  }
  @Override
  public JCExpression lef_hand_side_name(JCExpression name) {
    return name;
  }
  
  @Override
  public int assign_op_assign() {
    return JCTree.ASSIGN;
  }
  @Override
  public int assign_op_and() {
    return JCTree.BITAND_ASG;
  }
  @Override
  public int assign_op_or() {
    return JCTree.BITOR_ASG;
  }
  @Override
  public int assign_op_xor() {
    return JCTree.BITXOR_ASG;
  }
  
  @Override
  public int assign_op_minus() {
    return JCTree.MINUS_ASG;
  }
  @Override
  public int assign_op_plus() {
    return JCTree.PLUS_ASG;
  }
  @Override
  public int assign_op_divide() {
    return JCTree.DIV_ASG;
  }
  @Override
  public int assign_op_times() {
    return JCTree.MUL_ASG;
  }
  @Override
  public int assign_op_remainder() {
    return JCTree.MOD_ASG;
  }
  
  @Override
  public int assign_op_left_shift() {
    return JCTree.SL_ASG;
  }
  @Override
  public int assign_op_right_sift_signed() {
    return JCTree.SR_ASG;
  }
  @Override
  public int assign_op_right_sift_unsigned() {
    return JCTree.USR_ASG;
  }

  
}
