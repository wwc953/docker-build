package org.example.docker.kryo2;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


/**
 * @Description: TODO
 * @author: wangwc
 * @date: 2020/11/1 10:41
 */
public class KryoSerializer {
    public static byte[] serialize(Object object) {
        Kryo kryo = KryoHolder.get();
        Output output=null;
        try {
            output= new Output(1024 * 4, -1);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            output.setOutputStream(outputStream);
            kryo.writeClassAndObject(output, object);

            output.flush();
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            output.close();
        }
        return null;
    }

    public static Object deserialize(byte[] byteBuf) {
        if (byteBuf == null)
            return null;

        Input input = new Input(new ByteArrayInputStream(byteBuf));
        Kryo kryo = KryoHolder.get();
        return kryo.readClassAndObject(input);
    }
}
