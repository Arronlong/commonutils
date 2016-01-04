package common.util.string;

import java.lang.reflect.Field;
import java.util.Collection;

import com.google.common.base.Strings;
import common.util.reflect.ReflectUtils;
import common.util.string.enums.DataFormat;

/** 
 * 字符串工具类
 * 
 * @author arron
 * @date 2015年9月14日 上午10:29:15 
 * @version 1.0 
 */
public class StringsUtil {
	
	/**
	 * 判断是否是空字符串
	 * @param str 字符串
	 * @return
	 */
	public static boolean isNullOrEmpty(String str){
		return Strings.isNullOrEmpty(str);
	}
	
	/**
	 *  输出字符串，如果为空，则输出null
	 *  
	 * @param str 字符串
	 * @return
	 */
	public static String emptyToNull(String str){
		return Strings.emptyToNull(str);
	}

	/**
	 *  输出字符串，如果为null，则输出""
	 *  
	 * @param str 字符串
	 * @return
	 */
	public static String nullToEmpty(String str){
		return Strings.nullToEmpty(str);
	}
	
	/**
	 *  对象字段如果为空，则转化为null
	 *  
	 * @param obj 对象
	 * @return
	 */
	public static <T> T emptyToNull(T obj){
		Field[] fields = ReflectUtils.getFields(obj.getClass());
		for (Field field : fields) {
			field.setAccessible(true);
			if(java.util.List.class.isAssignableFrom(field.getType())){
				try {
					Collection<?> c = (Collection<?>) field.get(obj);
					for (Object object : c) {
						emptyToNull(object);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}else{
				try {
					if(field.get(obj).equals("")){
						field.set(obj, null);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return obj;
	}
	
	/**
	 *  对象字段如果为null，则转化为空
	 *  
	 * @param obj 对象
	 * @return
	 */
	public static <T> T  nullToEmpty(T obj){
		Field[] fields = ReflectUtils.getFields(obj.getClass());
		for (Field field : fields) {
			field.setAccessible(true);
			if(java.util.List.class.isAssignableFrom(field.getType())){
				try {
					Collection<?> c = (Collection<?>) field.get(obj);
					for (Object object : c) {
						emptyToNull(object);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}else{
				try {
					field.set(obj, Strings.nullToEmpty(String.valueOf(field.get(obj))));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return obj;
	}

	/**
	 * 如果长度为达到最小长度，在尾部使用指定的字符，补齐字符串
	 * 
	 * @param str 字符串
	 * @param minLength 最小长度
	 * @param padChar 补齐所用的字符
	 * @return
	 */
	public static String padEnd(String str, int minLength, char padChar){
		return Strings.padEnd(str, minLength, padChar);
	}

	/**
	 * 如果长度为达到最小长度，在头部使用指定的字符，补齐字符串
	 * 
	 * @param str 字符串
	 * @param minLength 最小长度
	 * @param padChar 补齐所用的字符
	 * @return
	 */
	public static String padStart(String str, int minLength, char padChar){
		return Strings.padStart(str, minLength, padChar);
	}
	
	/**
	 * 生成指定重复次数的字符串
	 * 
	 * @param str 字符串
	 * @param count 重复次数
	 * @return
	 */
	public static String repeat(String str, int count){
		return Strings.repeat(str, count);
	}
	
	/**
	 * 从头开始截取
	 * 
	 * @param str 字符串
	 * @param end 结束位置
	 * @return
	 */
	public static String subStrStart(String str, int end){
		return subStr(str, 0, end);
	}
	
	/**
	 * 从尾开始截取
	 * 
	 * @param str 字符串
	 * @param start 开始位置
	 * @return
	 */
	public static String subStrEnd(String str, int start){
		return subStr(str, str.length()-start, str.length());
	}

	/**
	 * 截取字符串 （支持正向、反向截取）<br/>
	 * 
	 * @param str 待截取的字符串
	 * @param length 长度 ，>=0时，从头开始向后截取length长度的字符串；<0时，从尾开始向前截取length长度的字符串
	 * @return 返回截取的字符串
	 * @throws RuntimeException
	 */
	public static String subStr(String str, int length) throws RuntimeException{
		if(str==null){
			throw new NullPointerException("字符串为null");
		}
		int len = str.length();
		if(len<Math.abs(length)){
			throw new StringIndexOutOfBoundsException("最大长度为"+len+"，索引超出范围为:"+(len-Math.abs(length)));
		}
		if(length>=0){
			return  subStr(str, 0,length);
		}else{
			return subStr(str, len-Math.abs(length), len);
		}
	}
	
	
	/**
	 * 截取字符串 （支持正向、反向选择）<br/>
	 * 
	 * @param str  待截取的字符串
	 * @param start 起始索引 ，>=0时，从start开始截取；<0时，从length-|start|开始截取
	 * @param end 结束索引 ，>=0时，从end结束截取；<0时，从length-|end|结束截取
	 * @return 返回截取的字符串
	 * @throws RuntimeException
	 */
	public static String subStr(String str, int start, int end) throws RuntimeException{
		if(str==null){
			throw new NullPointerException("");
		}
		int len = str.length();
		int s = 0;//记录起始索引
		int e = 0;//记录结尾索引
		if(len<Math.abs(start)){
			throw new StringIndexOutOfBoundsException("最大长度为"+len+"，索引超出范围为:"+(len-Math.abs(start)));
		}else if(start<0){
			s = len - Math.abs(start);
		}else if(start<0){
			s=0;
		}else{//>=0
			s = start;
		}
		if(len<Math.abs(end)){
			throw new StringIndexOutOfBoundsException("最大长度为"+len+"，索引超出范围为:"+(len-Math.abs(end)));
		}else if (end <0){
			e = len - Math.abs(end);
		}else if (end==0){
			e = len;
		}else{//>=0
			e = end;
		}
		if(e<s){
			throw new StringIndexOutOfBoundsException("截至索引小于起始索引:"+(e-s));
		}
		
		return str.substring(s, e);
	}

	/**
	 * 用指定字符串数组相连接，并返回
	 * 
	 * @param strs 字符串数组
	 * @param splitStr 连接数组的字符串
	 * @return
	 */
	public static String join(String[] strs, String splitStr){
		if(strs!=null){
			if(strs.length==1){
				return strs[0];
			}
			StringBuffer sb = new StringBuffer();
			for (String str : strs) {
				sb.append(str).append(splitStr);
			}
			if(sb.length()>0){
				sb.delete(sb.length()-splitStr.length(), sb.length());
			}
			return sb.toString();
		}
		return null;
	}
	
	/**
	 * 格式化字符串
	 * 
	 * @param str	原字符串
	 * @param fmt	格式化类型
	 * @return	返回格式化后的数据
	 */
	public static String fmt(String str, DataFormat fmt) {
		String result = "";
		switch (fmt.getCode()) {
			case 0://JSON
				result = JsonUtil.fmt2Json(str);
				break;
				
			case 1://XML
				result = JsonUtil.fmt2Xml(str, "root");
				break;
	
			default:
				result = str;
				break;
		}
		return result;
	}
	
	public static void main(String[] args) {
//		String str = "12345abcde";
//		System.out.println("--------------------------------");
//		System.out.println("正向截取长度为4，结果：\n" + StringsUtil.subStr(str, 4));
//		System.out.println("反向截取长度为4，结果：\n" + StringsUtil.subStr(str, -4));
//		System.out.println("--------------------------------");
//		System.out.println("正向截取到第4个字符的位置，结果：\n" + StringsUtil.subStrStart(str, 4));
//		System.out.println("反向截取到第4个字符的位置，结果：\n" + StringsUtil.subStrEnd(str, 4));
//		System.out.println("--------------------------------");
//		System.out.println("从第2个截取到第9个，结果：\n" + StringsUtil.subStr(str, 1, 9));
//		System.out.println("从第2个截取到倒数第1个，结果：\n" + StringsUtil.subStr(str, 1, -1));
//		System.out.println("从倒数第4个开始截取，结果：\n" + StringsUtil.subStr(str, -4, 0));
//		System.out.println("从倒数第4个开始截取，结果：\n" + StringsUtil.subStr(str, -4, 10));
		
		String[] ss = {"aaa","bbb","ccc"};
		System.out.println(StringsUtil.join(ss, "#|#"));
	}
}