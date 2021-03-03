package org.liu.config.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Data;

@Data
public class ServiceConfig {
	private List<RegistryConfig> registryConfigs;
	private List<ProtocolConfig> protocolConfigs;
	private Class<?> serviceClass;
	private Object reference;
	private String version;
	public synchronized void addRegistryConfig(RegistryConfig registryConfig){
		if(Objects.isNull(this.registryConfigs)){
			this.registryConfigs = new ArrayList<>();
		}
		this.registryConfigs.add(registryConfig);
	}
	public synchronized void addProtocolConfig(ProtocolConfig protocolConfig){
		if(Objects.isNull(this.protocolConfigs)){
			this.protocolConfigs = new ArrayList<>();
		}
		this.protocolConfigs.add(protocolConfig);
	}
}
