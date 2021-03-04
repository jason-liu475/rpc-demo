package org.liu.rpc.proxy;

import org.liu.rpc.Invoker;
import org.liu.rpc.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * @author liu
 */
public class InvokerInvocationHandler implements InvocationHandler {
    private final Invoker invoker;
    public InvokerInvocationHandler(Invoker invoker){
        this.invoker = invoker;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getDeclaringClass() == Object.class){
            return method.invoke(proxy,args);
        }
        String methodName = method.getName();
        Class<?>[] paramTypes = method.getParameterTypes();
        if(paramTypes.length == 0){
            if("toString".equals(methodName)){
                return invoker.toString();
            }else if("$destroy".equals(methodName)){
                return null;
            }else if("hashCode".equals(methodName)){
                return invoker.hashCode();
            }
        }else if(paramTypes.length == 1 && "equals".equals(methodName)){
            return invoker.equals(args[0]);
        }
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setParameterTypes(paramTypes);
        rpcInvocation.setArguments(args);
        rpcInvocation.setServiceName(method.getDeclaringClass().getName());
        rpcInvocation.setMethodName(methodName);
        return invoker.invoke(rpcInvocation);
    }
}
