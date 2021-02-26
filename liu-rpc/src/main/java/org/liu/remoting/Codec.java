package org.liu.remoting;


import java.util.List;

/**
 * 编解码器
 * @author liu
 */
public interface Codec {
	byte[] encode(Object msg) throws Exception;
	List<Object> decode(byte[] bytes) throws Exception;
}
