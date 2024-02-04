package com.example.kstream.core.serde;

import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class JavaDeserializer<T> implements Deserializer<T> {

    public T deserialize(String s, byte[] bytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                Object o = ois.readObject();
                return (T)o;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //throw new RuntimeException(ex);
        }
        return null;
    }

}
