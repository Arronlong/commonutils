package common.util.xml;  
  
import java.io.StringReader;  
import java.io.StringWriter;  
  
import java.nio.charset.Charset;

import javax.xml.bind.JAXBContext;  
import javax.xml.bind.Marshaller;  
import javax.xml.bind.Unmarshaller;  
  
/** 
 * Jaxb2工具类 
 * 
 * @author arron
 * @date 2015年12月21日 下午5:16:26 
 * @version 1.0
 */
public class JaxbUtils {  
  
    /** 
     * JavaBean转换成xml 
     * 
     * @param obj 
     * @param writer 
     * @return  
     */  
    public static String java2XML(Object obj) {  
        return java2XML(obj, Charset.defaultCharset().displayName());  
    }  
  
    /** 
     * JavaBean转换成xml 
     * @param obj 
     * @param encoding  
     * @return  
     */  
    public static String java2XML(Object obj, String encoding) {  
        String result = null;  
        try {  
            JAXBContext context = JAXBContext.newInstance(obj.getClass());  
            Marshaller marshaller = context.createMarshaller();  
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);//是否格式化  
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
  
            StringWriter writer = new StringWriter();  
            marshaller.marshal(obj, writer);  
            result = writer.toString();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return result;  
    }  
  
    /** 
     * xml转换成JavaBean 
     * @param xml 
     * @param c 
     * @return 
     */  
    @SuppressWarnings("unchecked")  
    public static <T> T XML2Java(String xml, Class<T> clazz) {  
        T obj = null;  
        try {  
            JAXBContext context = JAXBContext.newInstance(clazz);  
            Unmarshaller unmarshaller = context.createUnmarshaller();  
            obj = (T) unmarshaller.unmarshal(new StringReader(xml));  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return obj;  
    }  
}  