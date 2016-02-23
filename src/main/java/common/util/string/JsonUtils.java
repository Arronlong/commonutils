/**
 * 
 */
package common.util.string;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

/**
 * Json相关工具类
 * 
 * @author arron
 * @date 2015年9月15日 下午4:32:15 
 * @version 1.0
 */
public class JsonUtils {
	private static final Gson gson = new Gson();
	
	/**
	 * 序列化对象
	 * 
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj){  
		return gson.toJson(obj);
    }

	/**
	 * 反序列化对象
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 */
    public static <T> T  fromJson(String json,Class<T> clazz){
    	return gson.fromJson(json, clazz);
    }
    
    /**
     * 反序列化对象
     * 
     * @param json
     * @param clazz
     * @return
     */
    public static Object fromJson(String json,Type type){
    	return gson.fromJson(json, type);
    }
    
    /**
	 * 验证一个字符串是否是合法的JSON串
	 * 
	 * @param input
	 *            要验证的字符串
	 * @return true-合法 ，false-非法
	 */
	public static boolean validate(String json) {
		return (new JsonValidator()).validate(json);
	}
	
	/**
	 * 校验是否为get方式提交的参数
	 * 
	 * @param paras
	 * @return
	 */
	public static boolean validateModel(String paras){
		return Pattern.compile("\\w*[^&=]*=\\w*[^&=]*&?").matcher(paras).find();
	}
	
	/**
	 * 格式化为json格式
	 * 请使用@see{JsonXmlUtils}的 xml2JSON方法
	 * 
	 * @param result
	 * @return
	 */
	@Deprecated
	public static String fmt2Json(String result){
		if(validate(result)){
			return result;
		}
		result = result.replaceAll(">\\s*<", "><").replaceAll("<\\?([^>|^\\?]*)\\?>", "");
		String json = result;
		Matcher matcher = Pattern.compile("<([^>|^/]*)>").matcher(result);
		while(matcher.find()){
			for (int i = 0; i < matcher.groupCount(); i++) {
				String s = matcher.group(i+1);
				json = json.replaceAll("<"+s+">([^<|^\"]*)</"+s+">", "\""+s+"\":\"$1\",");
			}
		}
		json = "{"+json.replaceAll(",?</([^<]*)>", "},").replaceAll("<([^<]*)>", "\"$1\":{")+"}";
		json =json.replaceAll(",}","}").replaceAll("(\\s*\\w*)=\"(\\w*)\"\\s*\"?", "\"$1\":\"$2\",")
				.replaceAll("\\s+([^{]*),:" ,  ":{\"@attributes\":{\"$1},").replace("},{", "},")
				.replaceAll("},([^}|^\"]*)}", "},\"@value\":\"$1\"}");
		return json;
	}
	
	/**
	 * 格式化为xml格式
	 * 请使用@see{JsonXmlUtils}的 json2XML方法
	 * 
	 * @param json
	 * @return
	 */
	@Deprecated
	public static String fmt2Xml(String json){
		return fmt2Xml(json, "root");
	}
	
	/**
	 * 格式化为xml格式
	 * 请使用@see{JsonXmlUtils}的 json2XML方法
	 * 
	 * @param json
	 * @param rootEle
	 * @return
	 */
	@Deprecated
	public static String fmt2Xml(String json, String rootEle){
		if(!validate(json)){
			return fmt2Xml(fmt2Json(json),rootEle);
		}
		rootEle = rootEle.replaceAll("\\W", "");
		rootEle = StringUtils.isNullOrEmpty(rootEle)? "root": rootEle;
//		return json.replaceAll("\"(\\w*)\":\"?([^\",}]*)\"?,?","<$1>$2</$1>").replaceAll("\\{([^\\}]*)\\}", "<?xml version=\"1.0\" encoding=\"utf-8\" ?><"+rootEle+">$1"+"</"+rootEle+">");
		
		//去掉@attributes和@value
		Pattern pattern = Pattern.compile("\"@attributes\":\\{([^}]*)}");
		Matcher matcher = pattern.matcher(json);
		while(matcher.find()){
			String s = "";
			for (int i = 0; i < matcher.groupCount(); i++) {
				s = matcher.group(i+1);
				s = s.replaceAll("\"(\\w*)\":\"([^\"]*)\",?", " $1=$2");
			}
			json = json.replaceAll("[^,]\"(\\w*)\":\\{\"@attributes\":\\{[^}]*},?","{\"$1"+s+"\":{");
			//matcher = pattern.matcher(json);
		}
		json = json.replaceAll("\\{\"@value\":\"([^\"]*)\"}", "\"$1\"");
		
		//处理嵌套
		json = json.replaceAll("\"([\\w|\\s|=]*)\":\"([^\",{}]+)\",?", "<$1>$2</$1>");
		pattern = Pattern.compile("\"(\\w*)\":\\{([^{}]*)},?");
		while(pattern.matcher(json).find()){
			json = pattern.matcher(json).replaceAll("<$1>$2</$1>");
		}
		
		pattern = Pattern.compile("\"([\\w|\\s|=]*)\":([^}\"]*)},?");
		while(pattern.matcher(json).find()){
			json = pattern.matcher(json).replaceAll("<$1>$2</$1>");
		}
		
		json = json.replaceAll("(\\w*)=(\\w*)", "$1=\"$2\"").replaceAll("/(\\w*)\\s[\\w*)=\"\\w*\"\\s?]*", "/$1").replaceAll("[{|}]", "");
		json = "<?xml version=\"1.0\" ?><"+rootEle+">"+json+"</"+rootEle+">";
		return json;
	}

	public static void main(String[] args) {
		String str = "<Response a=\"123\" b=\"000\">"
								+ "<status  c=\"123\" d=\"000\">201</status>"
								+ "<A><status1>201</status1><message1>APP被用户自己禁用</message1></A>"
								+ "<A2><status1>201</status1><message1>APP被用户自己禁用</message1></A2>"
								+ "<B>"
								+ "	<BB><status1>201</status1><message1>APP被用户自己禁用</message1></BB>"
								+ "</B>"
								+ "<message>APP被用户自己禁用，请在控制台解禁</message>"
								+ "<C><status1>201</status1><message1>APP被用户自己禁用</message1></C>"
							+ "</Response>";
		
		String json = fmt2Json(str);
		String xml = fmt2Xml(json);
		System.out.println("xml转化为json：" + json);
		System.out.println("json转化为xml：" + xml);
	}
}