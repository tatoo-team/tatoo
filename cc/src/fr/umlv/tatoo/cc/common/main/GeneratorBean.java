package fr.umlv.tatoo.cc.common.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.umlv.tatoo.cc.common.extension.ExtensionBus;
import fr.umlv.tatoo.cc.common.generator.Type;
import fr.umlv.tatoo.cc.common.log.Level;
import fr.umlv.tatoo.cc.common.xml.JavaIds;

public abstract class GeneratorBean { 
  protected GeneratorBean() {
    for(Unit unit:Unit.values())
      setPackage(unit,unit.getDefaultSubPackage());
  }
  
  public void setAllPackages(String packagePrefix) {
    for(Map.Entry<Unit,String> entry:packageRoots.entrySet()) {
      String packageName=entry.getValue();
      if (packageName.indexOf('.')==-1)
        entry.setValue(packagePrefix+'.'+packageName);
    }
  }
  
  public String getPackage(Unit tool) {
    String packageName=packageRoots.get(tool);
    if (packageName==null)
      throw new IllegalArgumentException("no package name set for "+tool);
    return packageName;
  }
  
  public void setPackage(Unit unit,String packageRoot) {
    if (packageRoot==null)
      packageRoot=unit.getDefaultSubPackage();
    
    if (!JavaIds.validatePackageName(packageRoot))
      throw new IllegalArgumentException("invalid package name "+packageRoot+" for "+unit);
    
    packageRoots.put(unit,packageRoot);
  }
  
  public void addInputFiles(Unit unit,List<? extends File> files) {
    ArrayList<File> fileList=inputFilesMap.get(unit);
    if (fileList==null) {
      fileList=new ArrayList<File>();
      inputFilesMap.put(unit,fileList);
    }
    fileList.addAll(files);
  }
  
  public void addInputFile(Unit unit,File file) {
    addInputFiles(unit,Collections.singletonList(file));
  }
  
  public void addInputFilenames(Unit unit,List<? extends String> filenames) {
    ArrayList<File> files=new ArrayList<File>(filenames.size());
    for(String filename:filenames)
      files.add(new File(filename));
    addInputFiles(unit,files);
  }
  
  public List<? extends File> getInputFiles(Unit unit) {
    ArrayList<File> files=inputFilesMap.get(unit);
    if (files==null)
      return Collections.<File>emptyList();
    return Collections.unmodifiableList(files);
  }
  
  public List<? extends File> getAllInputFiles() {
    ArrayList<File> files=new ArrayList<File>();
    for(Unit unit:Unit.values()) {
      ArrayList<File> inputs=inputFilesMap.get(unit);
      if (inputs!=null)
        files.addAll(inputs);
    }
    return files;
  }
  
  public File getDestination() {
    return destination;
  }
  
  public void setDestination(File destination) {
    this.destination = destination;
  }
  
  public File getGeneratorDebugDir() {
    return generatorDebugDir;
  }
  
  public void setGeneratorDebugDir(File generatorDebugDir) {
    this.generatorDebugDir = generatorDebugDir;
  }
  
  public Set<File> getClassPath() {
    return classPath;
  }
  public void setClassPath(Set<File> classPath) {
    this.classPath=classPath;
  }
  
  public void setLogLevel(Level level) {
    this.level=level;
  }
  
  public Level getLogLevel() {
    return level;
  }
  
  public void setValidating(boolean validating) {
    this.validating=validating;
  }
  
  public boolean isValidating() {
    return validating;
  }
  
  public enum GenerateOption {
    DEFAULT, TRUE, FALSE
  }
  
  public boolean getGenerateDefault() {
    return generateDefault==GenerateOption.TRUE;
  }
  
  public void setGenerateDefault(boolean generateDefault) {
    this.generateDefault=generateDefault?GenerateOption.TRUE:GenerateOption.FALSE;
  }
  
  public String getGeneratePrefix() {
    return generatePrefix;
  }
  
  public void setGeneratePrefix(String generatePrefix) {
    this.generatePrefix=generatePrefix;
  }
  
  private AliasEntry getEntry(AliasPrototype prototype) {
    AliasEntry entry=aliasMap.get(prototype);
    if (entry==null)
      throw new IllegalArgumentException("unkown alias prototype "+prototype);
    return entry;
  }
  
  public void setGenerate(AliasPrototype prototype,GenerateOption option) {
    getEntry(prototype).generate=option;
  }
  
  public void setTypeName(AliasPrototype prototype,String typeName) {
    getEntry(prototype).typeName=typeName;
  }
  
  public void registerDefaults(AliasPrototype... prototypes) {
    for(AliasPrototype prototype:prototypes) {
      aliasMap.put(prototype,new AliasEntry(prototype));
    }
  }
  
  public ExtensionBus getExtensionBus() {
    return bus;
  }
  
  public boolean isRegisterDefaultExtensions() {
    return registerDefaultExtensions;
  }
  public void setRegisterDefaultExtensions(boolean registerDefaultExtensions) {
    this.registerDefaultExtensions = registerDefaultExtensions;
  }
  
  public Map<AliasPrototype,? extends Alias> getAliasMap() {
    return Collections.unmodifiableMap(aliasMap);
  }
  
  public void finish() {
    String prefix=upperCaseFirstLetter(generatePrefix);
    for(Map.Entry<?,AliasEntry> entry:aliasMap.entrySet()) {
      AliasEntry aliasEntry=entry.getValue();
      String typeName=aliasEntry.typeName;
      boolean isNotQualified=typeName.indexOf('.')==-1;
      
      // append prefix
      if (prefix!=null) {
        typeName=prefix+typeName;
      }
      
      // append package
      if (isNotQualified) {
        Unit unit=aliasEntry.alias.getUnit();
        String packageRoot=packageRoots.get(unit);
            
        if (packageRoot==null)
          throw new IllegalStateException("no package specified for the "+unit);
        if ("".equals(packageRoot))
          throw new IllegalStateException("default package is prohibited for the "+unit);
        
        typeName=packageRoot+'.'+typeName;
      }
      aliasEntry.type=Type.createQualifiedType(typeName);
      
      // resolve generation flag using generateDefault
      if (aliasEntry.generate==GenerateOption.DEFAULT)
        aliasEntry.generate=generateDefault;
    }
    
    // publish the bean
    //TODO Remi perhaps move this line in another compilation unit
    bus.publish(CommonDataKeys.bean,this);
  }
  
  private static String upperCaseFirstLetter(String name) {
    if (name == null || name.length() == 0)
      return name;
    return Character.toUpperCase(name.charAt(0)) +
      name.substring(1);
  }
  
  private File destination;
  private File generatorDebugDir;
  private Set<File> classPath;
  private Level level;
  private boolean validating=true;
  private boolean registerDefaultExtensions=true;
  private final EnumMap<Unit,ArrayList<File>> inputFilesMap=
    new EnumMap<Unit,ArrayList<File>>(Unit.class);
  
  private GenerateOption generateDefault=GenerateOption.TRUE; // must be TRUE or FALSE
  private String generatePrefix;
  
  private final ExtensionBus bus=new ExtensionBus();
  private final EnumMap<Unit,String> packageRoots=
    new EnumMap<Unit,String>(Unit.class);
  
  private HashMap<AliasPrototype,AliasEntry> aliasMap=
    new HashMap<AliasPrototype,AliasEntry>();
  
  private static class AliasEntry implements Alias {
    AliasEntry(AliasPrototype alias) {
      this.alias=alias;
      this.typeName=alias.getDefaultTypeName();
    }
    public Type getType() {
      if (type==null)
        throw new IllegalStateException("bean not finished");
      return type;
    }
    public boolean generate() {
      return generate==GenerateOption.TRUE;
    }
    
    final AliasPrototype alias;
    String typeName;
    Type type;
    GenerateOption generate=GenerateOption.DEFAULT;
  }
}
