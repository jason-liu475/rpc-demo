package org.liu.utils.serialize;

// 接口
public interface Serialization {

    byte[] serialize(Object output) throws Exception;

	<T> T deserialize(byte[] input, Class<T> clazz) throws Exception;
}