package org.hanframework.beans.sort;

import org.hanframework.beans.beandefinition.BeanDefinition;

import java.util.*;

/**
 * @author liuxin
 * @version Id: BeanDefinitionSortTools.java, v 0.1 2019-06-13 17:08
 */
public class BeanDefinitionSortTools {

    public static void sort(Map<String, BeanDefinition> beanDefinitions) {
        Collections.sort(new ArrayList<>(beanDefinitions.entrySet()), new Comparator<Map.Entry<String, BeanDefinition>>() {
            //升序排序
            @Override
            public int compare(Map.Entry<String, BeanDefinition> o1, Map.Entry<String, BeanDefinition> o2) {
                return o1.getValue().getOrder() - o2.getValue().getOrder();
            }
        });
    }


}
