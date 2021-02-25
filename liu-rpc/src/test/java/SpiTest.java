import java.util.ServiceLoader;

import org.liu.utils.serialize.Serialization;

public class SpiTest {
	public static void main(String[] args) {
		ServiceLoader<Serialization> serializations = ServiceLoader.load(Serialization.class,Thread.currentThread().getContextClassLoader());
		for (Serialization serialization : serializations){
			System.out.println(serialization.getClass().getSimpleName());
		}
	}
}
