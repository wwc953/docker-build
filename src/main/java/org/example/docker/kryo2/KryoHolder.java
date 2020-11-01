package org.example.docker.kryo2;

import com.esotericsoftware.kryo.Kryo;

/**
 * @Description: TODO
 * @author: wangwc
 * @date: 2020/11/1 10:42
 */
public class KryoHolder {
    private static ThreadLocal<Kryo> threadLocalKryo = new ThreadLocal<Kryo>()
    {
        protected Kryo initialValue()
        {
            Kryo kryo = new KryoReflectionFactory();

            return kryo;
        };
    };

    public static Kryo get()
    {
        return threadLocalKryo.get();
    }
}
