package fr.umlv.tatoo.cc.tools.main;

import java.io.File;

import fr.umlv.tatoo.cc.common.main.GeneratorBean;

public class ToolsBean extends GeneratorBean implements ToolsParam {
  public ToolsBean() {
    setDestination(new File("."));
  }
  
  public boolean isGenerateAST() {
    return generateAST;
  }
  public void setGenerateAST(boolean generateAST) {
    this.generateAST = generateAST;
  }
  
  private boolean generateAST;
}
