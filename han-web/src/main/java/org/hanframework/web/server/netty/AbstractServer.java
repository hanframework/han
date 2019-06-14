package org.hanframework.web.server.netty;

import org.hanframework.beans.beanfactory.lifecycle.DisposableBean;
import org.hanframework.beans.beanfactory.lifecycle.InitializingBean;
import org.hanframework.tool.text.Ansi;
import org.hanframework.tool.text.Color;
import org.hanframework.tool.text.UnixColor;
import org.hanframework.web.server.HanServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuxin
 * @version Id: HanAbstractServer.java, v 0.1 2019-05-10 16:26
 */
public abstract class AbstractServer implements HanServer, InitializingBean, DisposableBean {

    protected Logger log = LoggerFactory.getLogger(getClass());

    private Color color = new UnixColor();

    private int port;

    public AbstractServer(int port) {
        this.port = port;
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

    @Override
    public int port() {
        return port;
    }

    public abstract void doClose();

    public abstract void doOpen();

    @Override
    public void destroy() {
        close();
    }

    @Override
    public void afterPropertiesSet() {
//        showBanner();
        open();
    }

    public void showBanner() {
        String banner ="      ___           ___           ___     \n" +
                "     /\\__\\         /\\  \\         /\\__\\    \n" +
                "    /:/  /        /::\\  \\       /::|  |   \n" +
                "   /:/__/        /:/\\:\\  \\     /:|:|  |   \n" +
                "  /::\\  \\ ___   /::\\~\\:\\  \\   /:/|:|  |__ \n" +
                " /:/\\:\\  /\\__\\ /:/\\:\\ \\:\\__\\ /:/ |:| /\\__\\\n" +
                " \\/__\\:\\/:/  / \\/__\\:\\/:/  / \\/__|:|/:/  /\n" +
                "      \\::/  /       \\::/  /      |:/:/  / \n" +
                "      /:/  /        /:/  /       |::/  /  \n" +
                "     /:/  /        /:/  /        /:/  /   \n" +
                "     \\/__/         \\/__/         \\/__/    ";

//        color.yellow(banner);
        log.info("Netty started on port(s): {} (http)", port);

    }

}
