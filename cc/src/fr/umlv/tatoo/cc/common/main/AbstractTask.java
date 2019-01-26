package fr.umlv.tatoo.cc.common.main;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import fr.umlv.tatoo.cc.common.log.Level;
import fr.umlv.tatoo.cc.common.main.GeneratorBean.GenerateOption;

/** Main of the parser generator.
 * 
 * @author remi
 * @param <B> type of the bean associated to this task
 *
 */
public abstract class AbstractTask<B extends GeneratorBean> {
  public AbstractTask(B bean,AliasPrototype[] aliases) {
    this.bean=bean;
    bean().registerDefaults(aliases);
    
    HashMap<String,AliasPrototype> aliasMap=
      new HashMap<String,AliasPrototype>();
    for(AliasPrototype alias:aliases)
      aliasMap.put(alias.name().toLowerCase(),alias);
    this.aliasMap=aliasMap;
  }
  
  public void setDestination(File destination) {
    bean.setDestination(destination);
  }
  
  public void setGeneratorDebugDir(File generatorDebugDir) {
    bean.setGeneratorDebugDir(generatorDebugDir);
  }
  
  public void setClassPath(String classPath) {
    HashSet<File> classPathSet=new HashSet<File>();
    for(String path:classPath.split(",;")) {
      classPathSet.add(new File(path));
    }
    bean.setClassPath(classPathSet);
  }

  public void setPackagePrefix(String packagePrefix) {
    bean.setAllPackages(packagePrefix);
  }
  
  public void setValidating(boolean validating) {
    bean.setValidating(validating);
  }
  
  public void setLogLevel(String level) {
    bean.setLogLevel(Level.parse(level));
  }
  
  public void setDefaultExtensions(boolean registerDefaultExtensions) {
    bean.setRegisterDefaultExtensions(registerDefaultExtensions);
  }
  
  public static class InputFile {
    public void setFile(File file) {
      this.file=file;
    }
    public void setUnit(String unitName) {
      this.unit=Unit.parse(unitName);
    }
    Unit unit;
    File file;
  }
  
  public class InputFiles {
    public void setUnit(String unitName) {
      this.unit=Unit.parse(unitName);
    }
    public void addConfiguredInputFile(InputFile inputFile) {
      Unit unit=inputFile.unit;
      if (unit==null)
        unit=this.unit;
      registerInputFile(unit,inputFile.file);
    }
    private Unit unit=getDefaultUnit();
  }
  
  public InputFiles createInputFiles() {
    return new InputFiles();
  }
  
  protected void registerInputFile(Unit unit,File inputFile) {
    bean().addInputFile(unit,inputFile);
  }
  
  protected abstract Unit getDefaultUnit();
  
  public class Extension {
    public void setClassName(String className) {
      // FIXME Remi will not work if classpath set after extension
      bean().getExtensionBus().loadAndRegisterExtension(bean().getClassPath(),className);   
    }
  }
  
  public Extension createExtension() {
    return new Extension();
  }
  
  public static class FileAlias {
    public void setType(String type) {
      this.type=type;
    }
    public void setName(String name) {
      this.name=name; 
    }
    public void setGenerate(boolean generate) {
      this.generate=generate?GenerateOption.TRUE:GenerateOption.FALSE; 
    }
    String type,name;
    GenerateOption generate=GenerateOption.DEFAULT;
  }
  
  public class Generation {
    public void addConfiguredFileAlias(FileAlias fileAlias) {
      if (fileAlias.type==null || fileAlias.name==null)
        throw new IllegalArgumentException("file alias must have a type and a name;"+
            " known types are "+aliasMap.keySet());
      
      String type=fileAlias.type.toLowerCase();
      AliasPrototype alias=aliasMap.get(type);
      if (alias==null)
        throw new IllegalArgumentException("unknown file alias "+type+
          "; known ones are "+aliasMap.keySet());
      
      bean().setTypeName(alias,fileAlias.name);
      bean().setGenerate(alias,fileAlias.generate);
    }
    
    /** 
     * @TODO This method will be removed in Tatoo v5.
     * @deprecated use setDefault() instead
     */
    @Deprecated
    public void setGenerateDefault(boolean generateDefault) {
      bean().setGenerateDefault(generateDefault);
    }
    
    public void setDefault(boolean generateDefault) {
      bean().setGenerateDefault(generateDefault);
    }
    
    public void setPrefix(String generatePrefix) {
      bean().setGeneratePrefix(generatePrefix);
    }
  }
  
  /** 
   * @TODO This method will be removed in Tatoo v5.
   * @deprecated use createGeneration() instead
   */
  @Deprecated
  public Generation createGenerated() {
    return new Generation();
  }
  
  public Generation createGeneration() {
    return new Generation();
  }
  
  /** Base class of package sub-task.
   *
   */
  protected static abstract class Package {
    // must be redefined in subclass
  }
  
  public abstract Package createPackage();
  
  public B bean() {
    return bean;
  }
  
  private final B bean;
  final HashMap<String,AliasPrototype> aliasMap;
}
