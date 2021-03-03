package org.liu.config.beans;

import lombok.Data;

@Data
public class ProtocolConfig {
	private String name;
	private String host;
	private String port;
	private String serialization;
	private String transporter;
}
