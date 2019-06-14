package org.hanframework.tool.number;

import org.junit.Test;

import java.text.NumberFormat;

/**
 * @author liuxin
 * @version Id: NumberFormatTest.java, v 0.1 2019-06-13 23:10
 */
public class NumberFormatTest {

    @Test
    public void test(){
        NumberFormat pf = NumberFormat.getPercentInstance();
        pf.setMinimumIntegerDigits(3);
        pf.setGroupingUsed(false);
        String format = pf.format(10000);
        System.out.println(format);

        long a = System.currentTimeMillis();
        System.out.println(pf.format(2 / a+102313));

        NumberFormat nf=NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(3);
        System.out.println("格式化后显示数字："+nf.format(10000000));
        System.out.println("格式化后显示数字："+nf.format(10000.345));
    }
}
