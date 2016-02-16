package common.util.xml;

import java.io.StringWriter;
import java.io.Writer;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/** 
 * Simple-xml 序列化、反序列化工具类
 * 
 * @author arron
 * @date 2015年12月21日 上午10:18:28 
 * @version 1.0 
 */
public class SimpleXmlUtils {
    /**
     * XML对象（String类型）与Java对象转换器
     */
    private static final Serializer PERSISTER = new Persister();
    
	public static String java2XML(Object obj) throws Exception{
		Writer out = new StringWriter();
		PERSISTER.write(obj, out);
		String xml =out.toString();
		return xml;
	}
	
	public static <T> T XML2Java(String xml, Class<T> clazz) throws Exception{
		return PERSISTER.read(clazz, xml);
	}
}
