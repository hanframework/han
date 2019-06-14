package org.hanframework.web.engine;

import cn.hutool.core.lang.Dict;
import cn.hutool.extra.template.Engine;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateUtil;
import org.hanframework.context.annotation.ComponentScan;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuxin
 * @version Id: TemplateEngineTest.java, v 0.1 2019-04-17 17:41
 */
@ComponentScan
public class TemplateEngineTest {


  @Test
  public void engineTest() {
    Engine engine = TemplateUtil.createEngine(new TemplateConfig());

//假设我们引入的是Beetl引擎，则：
    Template template = engine.getTemplate("Hello Map{name}");
//Dict本质上为Map，此处可用Map
    Map map  =new HashMap();
    map.put("name","Hutool");
    String result = template.render(map);
    System.out.println(result);


  }
}
