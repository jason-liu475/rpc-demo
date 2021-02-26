package org.liu.utils.tools;

import java.util.Objects;
import java.util.ServiceLoader;

public class SpiUtils {
	public static <T> T getServiceImpl(String implName,Class<T> clazz){
		ServiceLoader<T> services = ServiceLoader.load(clazz);
		for (T service : services) {
			if(Objects.equals(implName,service.getClass().getSimpleName())){
				return service;
			}
		}
		return null;
	}
}
