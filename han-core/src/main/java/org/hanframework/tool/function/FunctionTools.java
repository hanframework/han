package org.hanframework.tool.function;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author liuxin
 * @version Id: FunctionTool.java, v 0.1 2019-06-05 10:13
 */
public class FunctionTools {

  /**
   * @param flag        状态标识
   * @param trueResult  成功执行函数
   * @param falseResult 失败执行函数
   * @param <T>         泛型
   * @return 泛型
   */
  public static <T> T chooseSupplier(boolean flag, Supplier<T> trueResult, Supplier<T> falseResult) {
    return flag ? trueResult.get() : falseResult.get();
  }

  /**
   * 类型转换
   *
   * @param list     原始数据集合
   * @param function 函数处理
   * @param <T>      原始类型
   * @param <R>      转换后类型
   * @return 处理后数据集合
   */
  public static <T, R> List<R> collectMapList(List<T> list, Function<T, R> function) {
    return list.stream().map(function).collect(Collectors.toList());
  }

  /**
   * 类型转换
   *
   * @param list     原始数据集合
   * @param function 函数处理
   * @param <T>      原始类型
   * @param <R>      转换后类型
   * @return 处理后数据集合
   */
  public static <T, R> Object[] collectMapArray(List<T> list, Function<T, R> function) {
    List<R> collect = collectMapList(list, function);
    if (collect.isEmpty()) {
      return null;
    }
    return collect.toArray();
  }
}
