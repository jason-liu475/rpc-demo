package org.liu.rpc;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Data;

@Data
public class RpcInvocation implements Serializable {
	public static final long serialVersionUID = 42L;
	static AtomicLong SEQ = new AtomicLong();
	private long id;
	private String serviceName;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] arguments;

	public RpcInvocation(){
		this.setId(incrementAndGet());
	}
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes == null ? new Class<?>[0] : parameterTypes;
	}


	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments == null ? new Object[0] : arguments;
	}
	public final long incrementAndGet() {
		long current;
		long next;
		do {
			current = SEQ.get();
			next = current >= 2147483647 ? 0 : current + 1;
		} while (!SEQ.compareAndSet(current, next));

		return next;
	}
}
