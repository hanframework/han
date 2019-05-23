package org.hanframework.web.server.netty;

import lombok.extern.slf4j.Slf4j;
import org.hanframework.beans.beanfactory.lifecycle.DisposableBean;
import org.hanframework.beans.beanfactory.lifecycle.InitializingBean;
import org.hanframework.context.ApplicationContext;
import org.hanframework.context.aware.ApplicationContextAware;
import org.hanframework.env.Configuration;
import org.hanframework.web.server.HanServer;

/**
 * @author liuxin
 * @version Id: HanAbstractServer.java, v 0.1 2019-05-10 16:26
 */
@Slf4j
public abstract class AbstractServer implements HanServer, InitializingBean, DisposableBean, ApplicationContextAware {


  private ApplicationContext applicationContext;

  private Configuration configuration;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    this.configuration = applicationContext.getConfiguration();
  }

  @Override
  public int port() {
    return 0;
  }

  @Override
  public void open() {
    doOpen();
  }

  @Override
  public void close() {
    //关闭当前类要关闭的资源
    doClose();
  }

  public abstract void doClose();

  public abstract void doOpen();

  @Override
  public void destroy() {
    close();
  }

  @Override
  public void afterPropertiesSet() {
    showBanner();
    open();
  }

  public void showBanner() {
    System.out.println("      ___           ___           ___     \n" +
      "     /\\__\\         /\\  \\         /\\__\\    \n" +
      "    /:/  /        /::\\  \\       /::|  |   \n" +
      "   /:/__/        /:/\\:\\  \\     /:|:|  |   \n" +
      "  /::\\  \\ ___   /::\\~\\:\\  \\   /:/|:|  |__ \n" +
      " /:/\\:\\  /\\__\\ /:/\\:\\ \\:\\__\\ /:/ |:| /\\__\\\n" +
      " \\/__\\:\\/:/  / \\/__\\:\\/:/  / \\/__|:|/:/  /\n" +
      "      \\::/  /       \\::/  /      |:/:/  / \n" +
      "      /:/  /        /:/  /       |::/  /  \n" +
      "     /:/  /        /:/  /        /:/  /   \n" +
      "     \\/__/         \\/__/         \\/__/    ");


    System.err.println("Port:" + 6969);

  }

}
