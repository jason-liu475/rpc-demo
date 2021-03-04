package rpc;


import lombok.extern.slf4j.Slf4j;
import org.liu.rpc.Invoker;
import org.liu.rpc.RpcInvocation;
import org.liu.rpc.proxy.ProxyFactory;
@Slf4j
public class ProxyTest {
    public static void main(String[] args) {
        Invoker invoker = new Invoker() {
            @Override
            public Class<?> getInterface() {
                return null;
            }

            @Override
            public Object invoke(RpcInvocation rpcInvocation) throws Exception {
                return "hello";
            }
        };
        TestService proxy = (TestService)ProxyFactory.getProxy(invoker, new Class[]{TestService.class});
        String result = proxy.sayHello("jason");
        log.info(result);
    }

    public interface TestService{
        String sayHello(String name);
    }
}
