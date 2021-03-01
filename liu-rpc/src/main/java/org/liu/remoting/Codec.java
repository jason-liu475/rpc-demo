package org.liu.remoting;


import java.util.List;

/**
 * 编解码器
 * @author liu
 */
public interface Codec<T> {
	byte[] encode(T msg) throws Exception;
	List<T> decode(byte[] bytes) throws Exception;
	Codec<T> newInstance();
}
