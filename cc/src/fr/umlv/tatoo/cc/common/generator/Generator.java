/*
 * Created on 6 juil. 2005
 *
 */
package fr.umlv.tatoo.cc.common.generator;

import java.io.File;
import java.util.Map;

import fr.umlv.tatoo.cc.common.generator.impl.Motocity;
import fr.umlv.tatoo.cc.common.main.Alias;
import fr.umlv.tatoo.cc.common.main.AliasPrototype;

/** Generate file using a template system.
 *  
 * @author remi
 *
 */
public class Generator {
  public Generator(File sourceDir,Class<?> clazz,Iterable<? extends File> classPath, File debugFile) {
    this.clazz = clazz;
    this.sourceDir = sourceDir;
    this.classPath = classPath;
    this.debugFile = debugFile;
  }
  
  public void generate(Map<String,?> root,Map<AliasPrototype,? extends Alias> aliasMap,AliasPrototype prototype) throws GeneratorException {
    Alias alias=aliasMap.get(prototype);
    if (alias.generate())
      generate(root,prototype.getDefaultTypeName(),alias.getType());
  }
  
  /** Generates the source code of a type using a template.
   *  
   * @param map a map containg properties.
   * @param templateName the name of the velocity template.
   * @param type the generated type.
   * @throws GeneratorException 
   */
  protected void generate(Map<String,?> map,String templateName,Type type) throws GeneratorException {
    String packageName=type.getPackageName();
    String simpleName=type.getSimpleName();
    
    String pathName=packageName.replace('.',File.separatorChar);
    File path=new File(sourceDir,pathName);
    File file=new File(path,simpleName+".java");
    boolean done=path.mkdirs();
    if (!done && !path.exists()) {
      throw new GeneratorException("impossible to create directory "+path);
    }
    Motocity.INSTANCE.generate(map, templateName, file, clazz, classPath, debugFile);
  }
  
  private final Class<?> clazz;
  private final File sourceDir;
  private final File debugFile;
  private final Iterable<? extends File> classPath;
}
