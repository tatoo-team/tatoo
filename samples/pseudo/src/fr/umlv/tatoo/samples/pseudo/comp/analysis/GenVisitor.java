package fr.umlv.tatoo.samples.pseudo.comp.analysis;

import static fr.umlv.tatoo.samples.pseudo.comp.analysis.ConstantMap.NO_CONST;
import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.I2C;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.INSTANCEOF;
import static org.objectweb.asm.Opcodes.INVOKEDYNAMIC;
import static org.objectweb.asm.Opcodes.INVOKEDYNAMIC_OWNER;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.SIPUSH;
import static org.objectweb.asm.Opcodes.V1_6;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.CheckClassAdapter;

import fr.umlv.tatoo.samples.pseudo.ast.AllocationArray;
import fr.umlv.tatoo.samples.pseudo.ast.AllocationObject;
import fr.umlv.tatoo.samples.pseudo.ast.ArrayIndex;
import fr.umlv.tatoo.samples.pseudo.ast.Assignation;
import fr.umlv.tatoo.samples.pseudo.ast.Block;
import fr.umlv.tatoo.samples.pseudo.ast.Builtin;
import fr.umlv.tatoo.samples.pseudo.ast.ConditionalIf;
import fr.umlv.tatoo.samples.pseudo.ast.ConditionalIfElse;
import fr.umlv.tatoo.samples.pseudo.ast.DeclVarLet;
import fr.umlv.tatoo.samples.pseudo.ast.DeclVarType;
import fr.umlv.tatoo.samples.pseudo.ast.Expr;
import fr.umlv.tatoo.samples.pseudo.ast.ExprAllocation;
import fr.umlv.tatoo.samples.pseudo.ast.ExprArrayIndex;
import fr.umlv.tatoo.samples.pseudo.ast.ExprAs;
import fr.umlv.tatoo.samples.pseudo.ast.ExprBooleanConst;
import fr.umlv.tatoo.samples.pseudo.ast.ExprError;
import fr.umlv.tatoo.samples.pseudo.ast.ExprFieldAccess;
import fr.umlv.tatoo.samples.pseudo.ast.ExprFuncall;
import fr.umlv.tatoo.samples.pseudo.ast.ExprInstanceof;
import fr.umlv.tatoo.samples.pseudo.ast.ExprNull;
import fr.umlv.tatoo.samples.pseudo.ast.ExprParens;
import fr.umlv.tatoo.samples.pseudo.ast.ExprString;
import fr.umlv.tatoo.samples.pseudo.ast.ExprValue;
import fr.umlv.tatoo.samples.pseudo.ast.ExprVar;
import fr.umlv.tatoo.samples.pseudo.ast.ForLoopIncr;
import fr.umlv.tatoo.samples.pseudo.ast.ForLoopIncrFuncall;
import fr.umlv.tatoo.samples.pseudo.ast.ForLoopInit;
import fr.umlv.tatoo.samples.pseudo.ast.ForLoopInitFuncall;
import fr.umlv.tatoo.samples.pseudo.ast.Funcall;
import fr.umlv.tatoo.samples.pseudo.ast.FunctionDecl;
import fr.umlv.tatoo.samples.pseudo.ast.IdToken;
import fr.umlv.tatoo.samples.pseudo.ast.Instr;
import fr.umlv.tatoo.samples.pseudo.ast.InstrBlock;
import fr.umlv.tatoo.samples.pseudo.ast.InstrBreak;
import fr.umlv.tatoo.samples.pseudo.ast.InstrContinue;
import fr.umlv.tatoo.samples.pseudo.ast.InstrFuncall;
import fr.umlv.tatoo.samples.pseudo.ast.InstrLoop;
import fr.umlv.tatoo.samples.pseudo.ast.InstrReturn;
import fr.umlv.tatoo.samples.pseudo.ast.InstrThrow;
import fr.umlv.tatoo.samples.pseudo.ast.LoopFor;
import fr.umlv.tatoo.samples.pseudo.ast.LoopLabel;
import fr.umlv.tatoo.samples.pseudo.ast.LoopWhile;
import fr.umlv.tatoo.samples.pseudo.ast.LvalueArrayIndex;
import fr.umlv.tatoo.samples.pseudo.ast.LvalueField;
import fr.umlv.tatoo.samples.pseudo.ast.LvalueId;
import fr.umlv.tatoo.samples.pseudo.ast.Member;
import fr.umlv.tatoo.samples.pseudo.ast.Node;
import fr.umlv.tatoo.samples.pseudo.ast.Script;
import fr.umlv.tatoo.samples.pseudo.ast.StructDecl;
import fr.umlv.tatoo.samples.pseudo.ast.VarInit;
import fr.umlv.tatoo.samples.pseudo.comp.BlockLocalVar;
import fr.umlv.tatoo.samples.pseudo.comp.BuiltInFunction;
import fr.umlv.tatoo.samples.pseudo.comp.Field;
import fr.umlv.tatoo.samples.pseudo.comp.Function;
import fr.umlv.tatoo.samples.pseudo.comp.LocalVar;
import fr.umlv.tatoo.samples.pseudo.comp.Struct;
import fr.umlv.tatoo.samples.pseudo.comp.SymbolMap;
import fr.umlv.tatoo.samples.pseudo.comp.Type;
import fr.umlv.tatoo.samples.pseudo.comp.Types;
import fr.umlv.tatoo.samples.pseudo.comp.UserFunction;
import fr.umlv.tatoo.samples.pseudo.comp.Var;
import fr.umlv.tatoo.samples.pseudo.comp.Types.AnyType;
import fr.umlv.tatoo.samples.pseudo.comp.Types.ArrayType;
import fr.umlv.tatoo.samples.pseudo.comp.Types.BooleanType;
import fr.umlv.tatoo.samples.pseudo.comp.Types.CharacterType;
import fr.umlv.tatoo.samples.pseudo.comp.Types.DoubleType;
import fr.umlv.tatoo.samples.pseudo.comp.Types.IntegerType;
import fr.umlv.tatoo.samples.pseudo.comp.Types.NullType;
import fr.umlv.tatoo.samples.pseudo.comp.Types.StringType;
import fr.umlv.tatoo.samples.pseudo.comp.Types.StructType;
import fr.umlv.tatoo.samples.pseudo.comp.Types.TypeVisitor;
import fr.umlv.tatoo.samples.pseudo.comp.Types.VoidType;
import fr.umlv.tatoo.samples.pseudo.comp.UserFunction.Parameter;
import fr.umlv.tatoo.samples.pseudo.comp.analysis.ConstantMap.Constant;
import fr.umlv.tatoo.samples.pseudo.comp.analysis.GenEnv.LoopContext;
import fr.umlv.tatoo.samples.pseudo.comp.analysis.LocalVarSlotScope.Slot;
import fr.umlv.tatoo.samples.pseudo.comp.main.LocationMap;

public class GenVisitor extends TraversalVisitor<GenEnv>{
  final ScriptEnv scriptEnv;
  private final LocationMap locationMap;
  private final ConstFoldEnv constFoldEnv;
  private final LivenessMap livenessMap;
  private final ClassVisitor scriptWriter;
  private final File scriptFile;
  private final ArrayList<ClassFile> classFileQueue=
    new ArrayList<ClassFile>();
  
  // used to generate line number table
  private int currentLineNumber=-1;
  
  public GenVisitor(File script,ScriptEnv scriptEnv,LocationMap locationMap,ConstFoldEnv constFoldEnv,LivenessMap livenessMap) {
    this.scriptEnv=scriptEnv;
    this.locationMap=locationMap;
    this.constFoldEnv=constFoldEnv;
    this.livenessMap=livenessMap;
    this.scriptFile=script;
    
    ClassWriter scriptWriter=new ClassWriter(ClassWriter.COMPUTE_MAXS/*|ClassWriter.COMPUTE_FRAMES*/);
    String classFileName=scriptInternalName();
    classFileQueue.add(new ClassFile(classFileName,scriptWriter));
    
    this.scriptWriter=new CheckClassAdapter(scriptWriter);
  }
  
  static class TypeDesc {
    private final String internalName;
    private final int arrayDim;
    
    public TypeDesc(String internalName, int arrayDim) {
      this.internalName=internalName;
      this.arrayDim=arrayDim;
    }
    
    public TypeDesc(String internalName) {
      this(internalName,0);
    }
    
    public String getInternalName() {
      return appendArrayDim(new StringBuilder()).append(internalName).toString();
    }
    
    public String getDescriptor() {
      return appendDescriptor(new StringBuilder()).toString();
    }
    
    public StringBuilder appendDescriptor(StringBuilder builder) {
      if (internalName.length()==1) {
        return appendArrayDim(builder).append(internalName);
      }
      return appendArrayDim(builder).append('L').append(internalName).append(';');
    }
    
    private StringBuilder appendArrayDim(StringBuilder builder) {
      int arrayDim=this.arrayDim;
      for(int i=0;i<arrayDim;i++) {
        builder.append('[');
      }
      return builder;
    }    
  }
  
  static final TypeDesc OBJECT_TYPE=new TypeDesc("java/lang/Object");
  static final TypeDesc BOOLEAN_TYPE=new TypeDesc("java/lang/Boolean");
  static final TypeDesc CHARACTER_TYPE=new TypeDesc("java/lang/Character");
  static final TypeDesc INTEGER_TYPE=new TypeDesc("java/lang/Integer");
  static final TypeDesc DOUBLE_TYPE=new TypeDesc("java/lang/Double");
  static final TypeDesc STRING_TYPE=new TypeDesc("java/lang/String");
  static final TypeDesc VOID_TYPE=new TypeDesc("V");
  
  private TypeDesc type(Type type) {
    return type.accept(type2TypeDescVisitor,null);
  } // where
  private final TypeVisitor<TypeDesc,Void,RuntimeException> type2TypeDescVisitor=
    new TypeVisitor<TypeDesc,Void,RuntimeException>() {
    @Override
    public TypeDesc visit(AnyType type,Void param) {
      return OBJECT_TYPE;
    }
    @Override
    public TypeDesc visit(BooleanType type,Void param) {
      return BOOLEAN_TYPE;
    }
    @Override
    public TypeDesc visit(CharacterType type,Void param) {
      return CHARACTER_TYPE;
    }
    @Override
    public TypeDesc visit(IntegerType type,Void param) {
      return INTEGER_TYPE;
    }
    @Override
    public TypeDesc visit(DoubleType type,Void param) {
      return DOUBLE_TYPE;
    }
    @Override
    public TypeDesc visit(StringType type,Void param) {
      return STRING_TYPE;
    }
    @Override
    public TypeDesc visit(StructType type,Void param) {
      return new TypeDesc(structInternalName(type.getName()));
    }
    @Override
    public TypeDesc visit(VoidType type,Void param) {
      return VOID_TYPE;
    }
    @Override
    public TypeDesc visit(NullType type,Void param) {
      throw new AssertionError("null type can't be exported");
    }
    @Override
    public TypeDesc visit(ArrayType arrayType,Void param) {
      int arrayDim=0;
      Type componentType;
      for(;;) {
        arrayDim++;
        componentType=arrayType.getComponentType();
        if (!(componentType instanceof ArrayType)) {
          break;
        }
        arrayType=(ArrayType)componentType;
      }
      TypeDesc componentTypeDesc=componentType.accept(this,null);
      return new TypeDesc(componentTypeDesc.getInternalName(),arrayDim);
    }
  };
  
  private String scriptInternalName() {
    String name=scriptFile.getName();
    int index=name.indexOf('.');
    if (index==-1)
      return name;
    return name.substring(0,index);
  }
  
  String structInternalName(Struct struct) {
    return structInternalName(struct.getName());
  }
  
  String structInternalName(String structName) {
    return scriptInternalName()+'$'+structName;
  }
  
  private String methodDesc(Function function) {
    StringBuilder builder=new StringBuilder();
    builder.append('(');
    for(Type type:function.getParameterTypeList())
      type(type).appendDescriptor(builder);
    builder.append(')');
    type(function.getReturnType()).appendDescriptor(builder);
    return builder.toString();
  }
  
  String varDesc(Var var) {
    return type(var.getType()).getDescriptor();
  }
  
  private void loadInteger(MethodVisitor methodVisitor,int constant) {
    if (constant>=-1 && constant<=5) {
      methodVisitor.visitInsn(ICONST_0+constant);
      return;
    }
    if (constant>=Byte.MIN_VALUE && constant<=Byte.MAX_VALUE) {
      methodVisitor.visitIntInsn(BIPUSH,constant);
      return;
    }
    if (constant>=Short.MIN_VALUE && constant<=Short.MAX_VALUE) {
      methodVisitor.visitIntInsn(SIPUSH,constant);
      return;
    } 
    methodVisitor.visitLdcInsn(constant);
  }
  
  private void genConstant(Object constantValue,GenEnv genEnv,Node node) {
    MethodVisitor methodVisitor=genEnv.getMethodVisitor();
    genLineNumber(node,methodVisitor,null);
    if (constantValue==null) {
      methodVisitor.visitInsn(ACONST_NULL);
      return;
    }
    if (constantValue instanceof Integer) {
      loadInteger(methodVisitor,(Integer)constantValue);
      methodVisitor.visitMethodInsn(INVOKESTATIC,"java/lang/Integer","valueOf","(I)Ljava/lang/Integer;");
      return;
    }
    if (constantValue instanceof Boolean) {
      methodVisitor.visitFieldInsn(GETSTATIC,
          "java/lang/Boolean",((Boolean)constantValue)?"TRUE":"FALSE","Ljava/lang/Boolean;");
      return;
    }
    if (constantValue instanceof Double) {
      methodVisitor.visitLdcInsn(constantValue);
      methodVisitor.visitMethodInsn(INVOKESTATIC,"java/lang/Double","valueOf","(D)Ljava/lang/Double;");
    }
    if (constantValue instanceof String) {
      String stringConstant=(String)constantValue;
      methodVisitor.visitLdcInsn(stringConstant);
      return;
    }
    throw new AssertionError("unkonwn constant type "+constantValue.getClass());
  }
  
  static class ClassFile {
    private final String classFileName;
    private final ClassWriter classWriter;
    
    public ClassFile(String classFileName,ClassWriter classWriter) {
      this.classFileName=classFileName;
      this.classWriter=classWriter;
    }
    
    public void generate() throws IOException {
      FileOutputStream outputStream=new FileOutputStream(classFileName+".class");
      try {
        outputStream.write(classWriter.toByteArray());
        outputStream.flush();
      } finally {
        outputStream.close();
      }
    }
  }
  
  // the caller must guarantee that this call will be followed by
  // the generation of an instruction
  // if the label is not null, if should be visited before a call to this method
  void genLineNumber(Node node,MethodVisitor methodVisitor, /* maybe null*/ Label label) {
    if (locationMap==null)
      return;
    int line=1+locationMap.getLocation(node).getLine();
    if (line!=currentLineNumber) {
      if (label==null) {
        label=new Label();
        methodVisitor.visitLabel(label);  
      }
      methodVisitor.visitLineNumber(line,label);
      currentLineNumber=line;
    }
  }
  
  // --- end of helper methods ---
  
  public void generate(Script scriptNode) throws IOException {
    scriptWriter.visit(V1_6,ACC_PUBLIC,scriptInternalName(),null,"java/lang/Object",null);
    scriptWriter.visitSource(scriptFile.getName(),null);
    scriptNode.accept(this,null); 
    
    // visit static init
    genStaticInit(scriptWriter);
    
    scriptWriter.visitEnd();
    
    for(ClassFile classFile:classFileQueue) {
      classFile.generate();
    }
  }
  
  private void genStaticInit(ClassVisitor scriptWriter) {
    /* TODO Remi
    MethodVisitor methodVisitor=scriptWriter.visitMethod(ACC_PUBLIC, "<clinit>", "()V", null, null);
    methodVisitor.visitLdcInsn(org.objectweb.asm.Type.getType(IndyRuntime.class));
    methodVisitor.visitLdcInsn("linker");
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/dyn/Linkage", "registerBootstrapMethod", "(Ljava/lang/Class;Ljava/lang/String)V");
    
    methodVisitor.visitMaxs(0, 0);
    methodVisitor.visitEnd();
    */
  }
  
  @Override
  public Void visit(StructDecl struct_decl,GenEnv unused) {
    Struct struct=(Struct)scriptEnv.getSymbolMap().getSymbol(struct_decl);
    
    String structClassName=structInternalName(struct);
    ClassWriter classWriter=new ClassWriter(ClassWriter.COMPUTE_MAXS|ClassWriter.COMPUTE_FRAMES);
    classWriter.visit(V1_6,ACC_PUBLIC,structClassName,null,"java/lang/Object",null);
    classWriter.visitSource(scriptFile.getName(),null);
    classWriter.visitOuterClass(scriptInternalName(),null,null);
    scriptWriter.visitInnerClass(structClassName,scriptInternalName(),struct.getName(),ACC_PUBLIC);
    
    for(Field field:struct.getFieldDefScope().getItems()) {
      classWriter.visitField(ACC_PUBLIC,field.getName(),varDesc(field),null,null);  
    }
    // default constructor
    MethodVisitor methodVisitor=classWriter.visitMethod(ACC_PUBLIC,"<init>","()V",null,null);
    methodVisitor.visitVarInsn(ALOAD,0);
    methodVisitor.visitMethodInsn(INVOKESPECIAL,"java/lang/Object","<init>","()V");
    
    // init fields
    TypeVisitor<Void,Field,RuntimeException> defaultFieldValueTypeVisitor=
      defaultFieldValueTypeVisitor(structClassName,methodVisitor);
    for(Field field:struct.getFieldDefScope().getItems()) {
      field.getType().accept(defaultFieldValueTypeVisitor,field);
    }
    
    methodVisitor.visitInsn(RETURN);
    methodVisitor.visitMaxs(0,0);
    methodVisitor.visitEnd();
    
    classWriter.visitEnd();
    
    ClassFile classFile=new ClassFile(structClassName,classWriter);
    classFileQueue.add(classFile);
    return null;
  } // where
  private TypeVisitor<Void,Field,RuntimeException> defaultFieldValueTypeVisitor(
      final String structClassName,final MethodVisitor methodVisitor) {
    return new Types.TypeVisitor<Void,Field,RuntimeException>() {
      private void aload0() {
        methodVisitor.visitVarInsn(ALOAD,0);
      }
      private void putField(Field field) {
        methodVisitor.visitFieldInsn(PUTFIELD,structClassName,field.getName(),varDesc(field));
      }

      @Override
      public Void visit(BooleanType type,Field field) {
        aload0();
        methodVisitor.visitFieldInsn(GETSTATIC,"java/lang/Boolean","FALSE","Ljava/lang/Boolean;");
        putField(field);
        return null;
      }
      @Override
      public Void visit(CharacterType type,Field field) {
        aload0();
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitInsn(I2C);
        methodVisitor.visitMethodInsn(INVOKESTATIC,"java/lang/Character","valueOf","(C)Ljava/lang/Character;");
        putField(field);
        return null;
      }
      @Override
      public Void visit(IntegerType type,Field field) {
        aload0();
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitMethodInsn(INVOKESTATIC,"java/lang/Integer","valueOf","(I)Ljava/lang/Integer;");
        putField(field);
        return null;
      }
      @Override
      public Void visit(DoubleType type,Field field) {
        aload0();
        methodVisitor.visitLdcInsn(Double.valueOf(0.0));
        methodVisitor.visitMethodInsn(INVOKESTATIC,"java/lang/Double","valueOf","(D)Ljava/lang/Double;");
        putField(field);
        return null;
      }
      @Override
      public Void visit(StringType type,Field field) {
        aload0();
        methodVisitor.visitLdcInsn("");
        putField(field);
        return null;
      }
      @Override
      public Void visit(VoidType type,Field notUsed) {
        throw new AssertionError("void is not a valid type");
      }
      @Override
      public Void visit(NullType type,Field notUsed) {
        throw new AssertionError("null is not a valid type");
      }
      @Override
      protected Void visit(Type type,Field notUsed) {
        // do nothing
        return null;
      }
    };
  } 
  
  private void genPrimaryBlock(GenEnv genEnv,Block primaryBlock) {
    primaryBlock.accept(this,genEnv);
    
    MethodVisitor methodVisitor=genEnv.getMethodVisitor();
    if (livenessMap.isAlive(primaryBlock)) {
      //genLineNumber(primaryBlock,methodVisitor,null);
      methodVisitor.visitInsn(RETURN);
    }
    methodVisitor.visitMaxs(0,0);
    methodVisitor.visitEnd();
  }
  
  @Override
  public Void visit(FunctionDecl function_decl,GenEnv unused) {
    UserFunction function=(UserFunction)scriptEnv.getSymbolMap().getSymbol(function_decl);
    
    MethodVisitor methodVisitor=scriptWriter.visitMethod(ACC_PUBLIC|ACC_STATIC,
      function.getName(),methodDesc(function),null,null);
    methodVisitor.visitCode();
    
    GenEnv genEnv=new GenEnv(methodVisitor);
    Label start=new Label();
    methodVisitor.visitLabel(start);
    LocalVarSlotScope slotScope=genEnv.getSlotScope();
    for(Parameter parameter:function.getParameterList()) {
      slotScope.getSlot(parameter,start);
    }
    genPrimaryBlock(genEnv,function_decl.getBlock());
    return null;
  }  
  
  @Override
  public Void visit(Script script,GenEnv unused) {
    for(Member member:script.getMemberStar()) {
      member.accept(this,null);
    }
    
    MethodVisitor methodVisitor=scriptWriter.visitMethod(ACC_PUBLIC|ACC_STATIC,
      "main","([Ljava/lang/String;)V",null,null);
    methodVisitor.visitCode();
    
    GenEnv genEnv=new GenEnv(methodVisitor);
    Block block=script.getBlock();
    BlockEnv blockEnv=(BlockEnv)scriptEnv.getSymbolMap().getSymbol(block);
    LocalVar args=blockEnv.getLocalVarScope().lookupItem("ARGS");
    Label start=new Label();
    methodVisitor.visitLabel(start);
    genEnv.getSlotScope().getSlot(args,start);
    
    genPrimaryBlock(genEnv,block);
    
    return null;
  }
  
  // local block
  @Override
  public Void visit(InstrBlock instr_block,GenEnv genEnv)  {
    genEnv.enterScope();
    super.visit(instr_block,genEnv);
    genEnv.exitScope();
    return null;
  }
  
  // all block
  @Override
  public Void visit(Block block,GenEnv genEnv) {
    super.visit(block,genEnv);
    
    // generate local var table
    MethodVisitor methodVisitor=genEnv.getMethodVisitor();
    for(Slot slot:genEnv.getSlotScope().slots()) {
      LocalVar localVar=slot.getLocalVar();
      methodVisitor.visitLocalVariable(localVar.getName(),
        varDesc(localVar),null,
        slot.getStart(),slot.getEnd(),
        slot.getSlotIndex());
    }
    return null;
  }
  
  @Override
  protected Void visit(Builtin builtin,GenEnv genEnv) {
    genFunctionCall(builtin,genEnv);
    return null;
  }
  
  @Override
  public Void visit(Assignation assignation,GenEnv genEnv) {
    final Expr expr=assignation.getExpr();
    
    TraversalVisitor<GenEnv> lvalueGen=new TraversalVisitor<GenEnv>() {
      @Override
      public Void visit(LvalueId lvalue_id,GenEnv genEnv) {
        expr.accept(GenVisitor.this,genEnv);
        
        IdToken tokenId=lvalue_id.getId();
        LocalVar localVar=(LocalVar)scriptEnv.getSymbolMap().getSymbol(tokenId);
        MethodVisitor methodVisitor=genEnv.getMethodVisitor();
        Label label=new Label();
        methodVisitor.visitLabel(label);
        genLineNumber(lvalue_id.getId(),methodVisitor,label);
        Slot slot=genEnv.getSlotScope().getSlot(localVar,label);
        methodVisitor.visitVarInsn(ASTORE,slot.getSlotIndex());
        return null;
      }
      @Override
      public Void visit(LvalueField lvalue_field,GenEnv genEnv) {
        lvalue_field.accept(GenVisitor.this,genEnv);
        expr.accept(GenVisitor.this,genEnv);
        
        IdToken tokenId=lvalue_field.getId();
        Field field=(Field)scriptEnv.getSymbolMap().getSymbol(tokenId);
        Struct struct=field.getStruct();
        
        MethodVisitor methodVisitor=genEnv.getMethodVisitor();
        genLineNumber(tokenId,methodVisitor,null);
        methodVisitor.visitFieldInsn(PUTFIELD,structInternalName(struct),field.getName(),varDesc(field));
        return null;
      }
      @Override
      public Void visit(LvalueArrayIndex lvalue_array_index,GenEnv genEnv) {
        lvalue_array_index.getExpr().accept(GenVisitor.this,genEnv);
        lvalue_array_index.getArrayIndex().accept(GenVisitor.this,genEnv);
        expr.accept(GenVisitor.this,genEnv);
        MethodVisitor methodVisitor=genEnv.getMethodVisitor();
        genLineNumber(lvalue_array_index.getArrayIndex(),methodVisitor,null);
        methodVisitor.visitInsn(AASTORE);
        return null;
      }
    };
    assignation.getLvalue().accept(lvalueGen,genEnv);
    
    return null;
  }
  
  private void genVarInit(Node varNode, Node initNode,GenEnv genEnv) {
    initNode.accept(this,genEnv);
    
    SymbolMap symbolMap=scriptEnv.getSymbolMap();
    BlockLocalVar localVar=(BlockLocalVar)symbolMap.getSymbol(varNode);
    MethodVisitor methodVisitor=genEnv.getMethodVisitor();
    Label label=new Label();
    methodVisitor.visitLabel(label);
    genLineNumber(varNode,methodVisitor,label);
    Slot slot=genEnv.getSlotScope().getSlot(localVar,label);
    methodVisitor.visitVarInsn(ASTORE,slot.getSlotIndex()); 
  }
  
  @Override
  public Void visit(DeclVarType decl_var_type,GenEnv genEnv) {
    VarInit init=decl_var_type.getVarInitOptional();
    if (init!=null) {
      genVarInit(decl_var_type,init,genEnv);
    }
    return null;
  }
  
  @Override
  public Void visit(DeclVarLet decl_var_let,GenEnv genEnv)  {
    LocalVar localVar=(LocalVar)scriptEnv.getSymbolMap().getSymbol(decl_var_let);
    if (constFoldEnv.getConstFoldVarMap().isConstant(localVar)) {
      // 'let' variable is a constant, so no generation here
      return null;
    }
    
    genVarInit(decl_var_let,decl_var_let.getExpr(),genEnv);
    return null;
  }
  
  @Override
  public Void visit(ConditionalIf conditional,GenEnv genEnv) {
    Expr b_expr=conditional.getExpr();
    Constant constValue=constFoldEnv.getConstFoldMap().getConstant(b_expr);
    if (constValue!=NO_CONST) {
      boolean value=(Boolean)constValue.getConstant();
      if (value) {
        // condition always true
        conditional.getInstr().accept(this,genEnv);
        return null;
      } else {
        // condition always false
        return null;
      }
    }
   
    b_expr.accept(this,genEnv);
    MethodVisitor methodVisitor=genEnv.getMethodVisitor();
    genLineNumber(conditional,methodVisitor,null);
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL,"java/lang/Boolean","booleanValue","()Z");
    
    Label end=new Label();
    methodVisitor.visitJumpInsn(IFEQ,end); // IFEQ because true=1
    conditional.getInstr().accept(this,genEnv);
    methodVisitor.visitLabel(end);
    return null;
  }
  
  @Override
  public Void visit(ConditionalIfElse conditional,GenEnv genEnv) {
    Expr b_expr=conditional.getExpr();
    Instr instr=conditional.getInstr();
    Instr instr2=conditional.getInstr2();
    Constant constValue=constFoldEnv.getConstFoldMap().getConstant(b_expr);
    if (constValue!=NO_CONST) {
      boolean value=(Boolean)constValue.getConstant();
      if (value) {
        // condition always true
        instr.accept(this,genEnv);
        return null;
      } else {
        // condition always false
        instr2.accept(this,genEnv);
        return null;
      }
    }
    
    b_expr.accept(this,genEnv);
    MethodVisitor methodVisitor=genEnv.getMethodVisitor();
    genLineNumber(conditional,methodVisitor,null);
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL,"java/lang/Boolean","booleanValue","()Z");
    
    boolean instrIsAlive=livenessMap.isAlive(instr);
    Label falseBranch=new Label();
    Label end=new Label();
    methodVisitor.visitJumpInsn(IFEQ,falseBranch); // IFEQ because true=1
    instr.accept(this,genEnv);
    if (instrIsAlive)
      methodVisitor.visitJumpInsn(GOTO,end);

    methodVisitor.visitLabel(falseBranch);
    instr2.accept(this,genEnv);
    if (instrIsAlive)
      methodVisitor.visitLabel(end);
    return null;
  }
  
  private void genLoop(Node loopNode,Expr b_expr,InstrLoop parent,Instr instr,ForLoopIncr incr,GenEnv genEnv) {
    Constant constValue;
    if (b_expr!=null) {
      constValue=constFoldEnv.getConstFoldMap().getConstant(b_expr);
      if (constValue!=NO_CONST && ((Boolean)constValue.getConstant())==false) {
        // loop condition always false
        return;
      }
    } else {
      constValue=null;
    }
    
    LoopLabel loopLabel=parent.getLoopLabelOptional();
    String label=(loopLabel==null)?null:loopLabel.getId().getValue();
    LoopStack<LoopContext> loopContextStack=genEnv.getLoopContextStack();
    
    MethodVisitor methodVisitor=genEnv.getMethodVisitor(); 
    Label continueLabel=new Label();
    Label breakLabel=new Label();
    Label conditionLabel=new Label();
    
    if (incr!=null) {
      methodVisitor.visitJumpInsn(GOTO,conditionLabel); 
    }
    methodVisitor.visitLabel(continueLabel);
    if (incr!=null) {
      incr.accept(this,genEnv);
      methodVisitor.visitLabel(conditionLabel);
    }
    
    if (b_expr!=null && constValue==NO_CONST) {
      b_expr.accept(this,genEnv);
      genLineNumber(loopNode,methodVisitor,null);
      methodVisitor.visitMethodInsn(INVOKEVIRTUAL,"java/lang/Boolean","booleanValue","()Z");
      methodVisitor.visitJumpInsn(IFEQ,breakLabel); // IFEQ because true=1
    }
    
    loopContextStack.push(label,new LoopContext(breakLabel,continueLabel));
    instr.accept(this,genEnv);
    loopContextStack.pop();
    
    if (livenessMap.isAlive(instr))
      methodVisitor.visitJumpInsn(GOTO,continueLabel);
    
    // break label not needed if while true/for ever with no break
    if (livenessMap.isAlive(loopNode)) {
      methodVisitor.visitLabel(breakLabel);  
    }
  }
  
  @Override
  public Void visit(LoopWhile loop_while,GenEnv genEnv) {
    genLoop(loop_while,loop_while.getExpr(),loop_while.getParent(),
      loop_while.getInstr(),null,genEnv);
    return null;
  }
  
  @Override
  public Void visit(LoopFor loop_for,GenEnv genEnv) {
    ForLoopInit forLoopInitOpt=loop_for.getForLoopInitOptional();
    if (forLoopInitOpt!=null) {
      forLoopInitOpt.accept(this,genEnv);
    }
    genLoop(loop_for,loop_for.getExprOptional(),loop_for.getParent(),
        loop_for.getInstr(),loop_for.getForLoopIncrOptional(),genEnv);
    return null;
  }
  
  private LoopContext getLoopContext(IdToken label,GenEnv genEnv) {
    LoopStack<LoopContext> loopContextStack=genEnv.getLoopContextStack();
    if (label==null) {
      return loopContextStack.getCurrentLoopContext();
    } else {
      return loopContextStack.getLoopContext(label.getValue());
    }
  }
  
  @Override
  public Void visit(InstrBreak loop_break,GenEnv genEnv) {
    LoopContext loopContext=getLoopContext(loop_break.getIdOptional(),genEnv);
    MethodVisitor methodVisitor=genEnv.getMethodVisitor();
    genLineNumber(loop_break,methodVisitor,null);
    methodVisitor.visitJumpInsn(GOTO,loopContext.getBreakLabel());
    return null;
  }
  
  @Override
  public Void visit(InstrContinue loop_continue,GenEnv genEnv) {
    LoopContext loopContext=getLoopContext(loop_continue.getIdOptional(),genEnv);
    MethodVisitor methodVisitor=genEnv.getMethodVisitor(); 
    genLineNumber(loop_continue,methodVisitor,null);
    methodVisitor.visitJumpInsn(GOTO,loopContext.getContinueLabel());
    return null;
  }
  
  @Override
  public Void visit(InstrReturn instr_return,GenEnv genEnv) {
    BlockEnv blockEnv=(BlockEnv)scriptEnv.getSymbolMap().getSymbol(instr_return);
    
    Expr expr=instr_return.getExprOptional();
    if (expr!=null)
      expr.accept(this,genEnv);
    
    MethodVisitor methodVisitor=genEnv.getMethodVisitor();
    genLineNumber(instr_return,methodVisitor,null);
    if (blockEnv.getExpectedReturnType()==scriptEnv.getTypeScope().voidType()) {
      methodVisitor.visitInsn(RETURN);
    } else {
      methodVisitor.visitInsn(ARETURN);
    }
    return null;
  }
  
  @Override
  public Void visit(InstrThrow instr_throw,GenEnv genEnv) {
    MethodVisitor methodVisitor=genEnv.getMethodVisitor();
    
    genLineNumber(instr_throw,methodVisitor,null);
    methodVisitor.visitTypeInsn(NEW,"java/lang/RuntimeException");
    methodVisitor.visitInsn(DUP);
    
    instr_throw.getExpr().accept(this,genEnv);
    
    Function function=scriptEnv.getFunctionScope().lookupFunction("toString",
        Arrays.<Type>asList(scriptEnv.getTypeScope().anyType()));
    if (function==null)
      throw new AssertionError("function scope must contains toString(any)");
    genFunctionCall(function,instr_throw,genEnv);
    
    genLineNumber(instr_throw,methodVisitor,null);
    methodVisitor.visitMethodInsn(INVOKESPECIAL,"java/lang/RuntimeException","<init>","(Ljava/lang/String;)V");
    methodVisitor.visitInsn(ATHROW);
    return null;
  }
  
  
  private void genInstrFuncall(Node instrFuncallNode,Funcall funcall,GenEnv genEnv) {
    funcall.accept(this,genEnv);
    Function function=(Function)scriptEnv.getSymbolMap().getSymbol(funcall);
    if (function.getReturnType()!=scriptEnv.getTypeScope().voidType()) {
      MethodVisitor methodVisitor=genEnv.getMethodVisitor();
      genLineNumber(instrFuncallNode,methodVisitor,null);
      methodVisitor.visitInsn(POP);
    }
  }
  
  @Override
  public Void visit(InstrFuncall instr_funcall,GenEnv genEnv) {
    genInstrFuncall(instr_funcall,instr_funcall.getFuncall(),genEnv);
    return null;
  }
  
  @Override
  public Void visit(ForLoopIncrFuncall for_loop_incr_funcall,GenEnv genEnv) {
    genInstrFuncall(for_loop_incr_funcall,for_loop_incr_funcall.getFuncall(),genEnv);
    return null;
  }
  
  @Override
  public Void visit(ForLoopInitFuncall for_loop_init_funcall,GenEnv genEnv) {
    genInstrFuncall(for_loop_init_funcall,for_loop_init_funcall.getFuncall(),genEnv);
    return null;
  }
  
  private boolean genConstFoldedValue(Node operatorNode,GenEnv genEnv) {
    Constant constant=constFoldEnv.getConstFoldMap().getConstant(operatorNode);
    if (constant==NO_CONST)
      return false;
    genConstant(constant.getConstant(),genEnv,operatorNode);
    return true;
  }
  
  //operatorNode is an expr or a boolean expr
  private void genFunctionCall(Node operatorNode,GenEnv genEnv) {
    // check if value is constant folded
    if (genConstFoldedValue(operatorNode,genEnv))
      return;
    
    // generate arguments
    for(Node node:operatorNode.nodeList()) {
      if (node.isToken())
        continue;
      node.accept(this,genEnv);
    }
    
    SymbolMap symbolMap=scriptEnv.getSymbolMap();
    Function function=(Function)symbolMap.getSymbol(operatorNode);
    
    genFunctionCall(function,operatorNode,genEnv);
  }
  
  private void genFunctionCall(Function function,Node operatorNode,GenEnv genEnv) {
    String owner;
    String methodName;
    if (function.isBuiltin()) {
      BuiltInFunction builtInFunction=(BuiltInFunction)function;
      Method reflectMethod=builtInFunction.getReflectMethod();
      owner=reflectMethod.getDeclaringClass().getName().replace('.','/');
      methodName=reflectMethod.getName();
    } else {
      owner=scriptInternalName();
      methodName=function.getName();
    }
    MethodVisitor methodVisitor=genEnv.getMethodVisitor();
    genLineNumber(operatorNode,methodVisitor,null);
    
    if (function.isBuiltin()) {
      methodVisitor.visitMethodInsn(INVOKESTATIC,owner,methodName,methodDesc(function));
    } else {
      methodVisitor.visitMethodInsn(INVOKEDYNAMIC,INVOKEDYNAMIC_OWNER,methodName,methodDesc(function));
    }
  }
  
  @Override
  public Void visit(Funcall funcall,GenEnv genEnv) {
    genFunctionCall(funcall,genEnv);
    return null;
  }
  
  @Override
  public Void visit(ExprFuncall expr_funcall,GenEnv genEnv) {
    return expr_funcall.getFuncall().accept(this,genEnv);
  }
  
  @Override
  public Void visit(ExprVar expr_var,GenEnv genEnv) {
    IdToken tokenId=expr_var.getId();
    
    // check if the expression is constant folded
    if (genConstFoldedValue(tokenId,genEnv))
      return null;
    
    LocalVar localVar=(LocalVar)scriptEnv.getSymbolMap().getSymbol(tokenId);
    
    MethodVisitor methodVisitor=genEnv.getMethodVisitor();
    Label label=new Label();
    methodVisitor.visitLabel(label);
    genLineNumber(tokenId,methodVisitor,label);
    Slot slot=genEnv.getSlotScope().getSlot(localVar,label);
    methodVisitor.visitVarInsn(ALOAD,slot.getSlotIndex());
    return null;
  }
  
  @Override
  public Void visit(ExprFieldAccess expr_field_access,GenEnv genEnv) {
    expr_field_access.getExpr().accept(this,genEnv);
    
    IdToken tokenId=expr_field_access.getId();
    Field field=(Field)scriptEnv.getSymbolMap().getSymbol(tokenId);
    
    MethodVisitor methodVisitor=genEnv.getMethodVisitor();
    genLineNumber(tokenId,methodVisitor,null);
    String owner=structInternalName(field.getStruct());
    methodVisitor.visitFieldInsn(GETFIELD,owner,field.getName(),varDesc(field));
    return null;
  }
  
  @Override
  public Void visit(ExprArrayIndex expr_array_index,GenEnv genEnv) {
    expr_array_index.getExpr().accept(this,genEnv);
    expr_array_index.getArrayIndex().accept(this,genEnv);
    
    MethodVisitor methodVisitor=genEnv.getMethodVisitor();
    genLineNumber(expr_array_index.getExpr(),methodVisitor,null);
    methodVisitor.visitInsn(AALOAD);
    return null;
  }
  
  @Override
  public Void visit(ExprBooleanConst expr_const,GenEnv genEnv) {
    genConstant(expr_const.getBooleanConst().getValue(),genEnv,expr_const);
    return null;
  }
  
  @Override
  public Void visit(ExprParens expr_parens,GenEnv genEnv) {
    return expr_parens.getExpr().accept(this,genEnv);
  }
  
  @Override
  public Void visit(ExprInstanceof expr_instanceof,GenEnv genEnv) {
    expr_instanceof.getExpr().accept(this,genEnv);
    
    Type type=TypeResolver.resolveType(scriptEnv,expr_instanceof.getTypeName());
    MethodVisitor methodVisitor=genEnv.getMethodVisitor();
    genLineNumber(expr_instanceof,methodVisitor,null);
    methodVisitor.visitTypeInsn(INSTANCEOF,type(type).getInternalName());
    methodVisitor.visitMethodInsn(INVOKESTATIC,"java/lang/Boolean","valueOf","(Z)Ljava/lang/Boolean;");
    return null;
  }
  
  @Override
  public Void visit(AllocationObject allocation_object,GenEnv genEnv) {
    Struct struct=scriptEnv.getStructScope().lookupItem(allocation_object.getId().getValue());
    MethodVisitor methodVisitor=genEnv.getMethodVisitor();
    String structName=structInternalName(struct);
    genLineNumber(allocation_object,methodVisitor,null);
    methodVisitor.visitTypeInsn(NEW,structName);
    methodVisitor.visitInsn(DUP);
    methodVisitor.visitMethodInsn(INVOKESPECIAL,structName,"<init>","()V");
    return null;
  }
  
  @Override
  public Void visit(AllocationArray allocation_array,GenEnv genEnv) {
    IdToken idToken=allocation_array.getId();
    List<ArrayIndex> arrayIndexList=allocation_array.getArrayIndexPlus();
    Type type=Types.arrayType(
      TypeResolver.resolveType(scriptEnv,idToken),
      allocation_array.getAngleBracketsStar().size()+
        arrayIndexList.size()
      );
    MethodVisitor methodVisitor=genEnv.getMethodVisitor();
    
    for(ArrayIndex arrayIndex:arrayIndexList) {
      arrayIndex.accept(this,genEnv);
    }
    genLineNumber(allocation_array,methodVisitor,null);
    methodVisitor.visitMultiANewArrayInsn(type(type).getDescriptor(),arrayIndexList.size());
    return null;
  }
  
  @Override
  public Void visit(ArrayIndex array_index,GenEnv genEnv) {
    Expr expr=array_index.getExpr();
    if (!genConstFoldedValue(expr,genEnv)) {
      expr.accept(this,genEnv);
    }
    MethodVisitor methodVisitor=genEnv.getMethodVisitor();
    methodVisitor.visitMethodInsn(INVOKEVIRTUAL,"java/lang/Integer","intValue","()I");
    
    return null;
  }
  
  @Override
  protected Void visit(Expr expr,GenEnv genEnv) {
    genFunctionCall(expr,genEnv);
    return null;
  }
  
  @Override
  public Void visit(ExprAs expr_as,GenEnv genEnv) {
    expr_as.getExpr().accept(this,genEnv);
    
    Type type=TypeResolver.resolveType(scriptEnv,expr_as.getTypeName());
    MethodVisitor methodVisitor=genEnv.getMethodVisitor();
    genLineNumber(expr_as,methodVisitor,null);
    methodVisitor.visitTypeInsn(CHECKCAST,type(type).getInternalName());
    return null;
  }
  
  @Override
  public Void visit(ExprError expr_error,GenEnv unused) {
    throw new AssertionError();
  }
  
  @Override
  public Void visit(ExprAllocation b_expr_allocation,GenEnv genEnv) {
    visit((Node)b_expr_allocation,genEnv);
    return null;
  }
  
  @Override
  public Void visit(ExprValue expr_value,GenEnv genEnv) {
    genConstant(expr_value.getValue().getValue(),genEnv,expr_value);
    return null;
  }
  
  @Override
  public Void visit(ExprString expr_string,GenEnv genEnv) {
    genConstant(expr_string.getStringConst().getValue(),genEnv,expr_string);
    return null;
  }
  
  @Override
  public Void visit(ExprNull expr_null,GenEnv genEnv) {
    genConstant(null,genEnv,expr_null);
    return null;
  }
}
