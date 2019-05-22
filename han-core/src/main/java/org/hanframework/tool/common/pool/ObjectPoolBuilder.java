package org.hanframework.tool.common.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.hanframework.tool.date.StopWatch;
import java.util.Date;

/**
 * @Package: smile.common.pool
 * @Description: 构建池化工具
 * @author: liuxin
 * @date: 2017/10/20 下午3:02
 */
public class ObjectPoolBuilder<T> {
    private ObjectPool objectPool;

    private ObjectPool getObjectPool() {
        return objectPool;
    }

    public ObjectPoolBuilder setObject(Object object) {
        if (objectPool == null) {
            objectPool = new ObjectPool<T>(object);
        }
        return this;
    }

    public ObjectPoolBuilder setConfig(GenericObjectPoolConfig config) {
        getObjectPool().setConfig(config);
        return this;
    }

    public ObjectPool create() {
        if (objectPool == null) {
            throw new RuntimeException("请设置要池化的对象类型");
        }
        return objectPool;
    }


}

