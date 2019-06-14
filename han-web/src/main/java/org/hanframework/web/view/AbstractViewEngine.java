package org.hanframework.web.view;

import cn.hutool.extra.template.Engine;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateUtil;
import org.hanframework.beans.beanfactory.lifecycle.InitializingBean;

/**
 * @author liuxin
 * @version Id: AbstractViewEngine.java, v 0.1 2019-06-10 23:33
 */
public abstract class AbstractViewEngine implements ViewEngine, InitializingBean {

    private Engine engine;

    private String templatePath;

    public AbstractViewEngine(Engine engine, String templatePath) {
        this.engine = engine;
        this.templatePath = templatePath;
    }

    @Override
    public void afterPropertiesSet() {
        this.engine = TemplateUtil.createEngine(new TemplateConfig(templatePath, TemplateConfig.ResourceMode.CLASSPATH));
    }

    @Override
    public String render(ViewModel viewModel) {
        return doRender(engine, viewModel);
    }

    public String getTemplate(String viewName) {
        String root = templatePath;
        if (templatePath.endsWith("/")) {
            root = templatePath.substring(0, templatePath.length() - 1);
        }
        if (viewName.startsWith("/")) {
            return root + viewName;
        } else {
            return root + "/" + viewName;
        }
    }

    public abstract String doRender(Engine engine, ViewModel viewModel);
}
