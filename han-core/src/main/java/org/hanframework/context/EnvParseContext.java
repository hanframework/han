package org.hanframework.context;

import org.hanframework.env.loadstrategy.TypeLoadStrategyRegister;


/**
 * @author liuxin
 * @version Id: EnvParseContext.java, v 0.1 2019-05-24 10:56
 */
public class EnvParseContext {

  private String[] args;

  private TypeLoadStrategyRegister typeLoadStrategyRegistory;


  private String charset;


  public EnvParseContext(String[] args, TypeLoadStrategyRegister typeLoadStrategyRegistory, String charset) {
    this.args = args;
    this.typeLoadStrategyRegistory = typeLoadStrategyRegistory;
    this.charset = charset;
  }

  public EnvParseContext(String[] args, String charset) {
    this.args = args;
    this.typeLoadStrategyRegistory = new TypeLoadStrategyRegister();
    this.charset = charset;
  }

  public String[] getArgs() {
    return args;
  }

  public TypeLoadStrategyRegister getTypeLoadStrategyRegistory() {
    return typeLoadStrategyRegistory;
  }


  public String getCharset() {
    return charset;
  }
}
