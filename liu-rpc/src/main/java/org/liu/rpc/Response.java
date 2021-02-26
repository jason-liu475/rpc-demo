package org.liu.rpc;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Response {
	private long requestId;
	private int status;
	private Object content;
}
