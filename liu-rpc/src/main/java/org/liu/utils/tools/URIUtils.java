package org.liu.utils.tools;

import java.net.URI;

public class URIUtils {
	public static String getParam(URI uri,String paramName){
		for (String param : uri.getQuery().split("&")) {
			if(param.startsWith(paramName + "=")){
				return param.replace(paramName + "=","");
			}
		}
		return null;
	}
	public static String getService(URI uri){
		return uri.getPath().replace("/","");
	}
}
