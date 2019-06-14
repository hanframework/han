package org.hanframework.web.view;

import cn.hutool.extra.template.Engine;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateUtil;

/**
 * @author liuxin
 * @version Id: VelocityViewEngine.java, v 0.1 2019-06-10 23:30
 */
public class VelocityViewEngine extends AbstractViewEngine {

    public VelocityViewEngine(Engine engine, String templatePath) {
        super(engine, templatePath);
    }

    @Override
    public String doRender(Engine engine, ViewModel viewModel) {
        Template template = engine.getTemplate(getTemplate(viewModel.getViewName()));
        return template.render(viewModel.getModel());
    }
}
