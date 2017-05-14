package com.arronlong.common.util.string;

import java.io.CharArrayReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

/**
 * JSON-XML转换工具
 * 
 * @author arron
 * @date 2016年1月24日 下午10:50:59 
 * @version 1.0
 */
public class JsonXmlUtils {
	private static final XMLSerializer SERIALIZER = new XMLSerializer();
	
	/**
	 * xml转化为json
	 * 
	 * @param xml
	 * @return
	 */
    public static String xml2JSON(String xml){
        return SERIALIZER.read(xml).toString();
    }
     
    /**
     * json转化为xml
     * 
     * @param json
     * @return
     */
    public static String json2XML(String json){
        JSONObject jobj = JSONObject.fromObject(json);
        String xml =  SERIALIZER.write(jobj);
        return xml;
    }
    
    /**
     * json转map<String, Object>
     * 
     * @param jsonStr
     * @return
     */
    public static Map<String, Object> json2Map(String jsonStr){
        Map<String, Object> map = new HashMap<String, Object>();  
        //最外层解析  
        JSONObject json = JSONObject.fromObject(jsonStr);  
        for(Object k : json.keySet()){  
            Object v = json.get(k);   
            //如果内层还是数组的话，继续解析  
            if(v instanceof JSONArray){  
                List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();  
                @SuppressWarnings("unchecked")
				Iterator<JSONObject> it = ((JSONArray)v).iterator();  
                while(it.hasNext()){  
                    JSONObject json2 = it.next();  
                    list.add(json2Map(json2.toString()));  
                }  
                map.put(k.toString(), list);  
            } else {  
                map.put(k.toString(), v);  
            }  
        }  
        return map;  
    }  
     

    /**
     * json转map<String, String>
     * 
     * @param jsonStr
     * @return
     */
    public static Map<String, String> json2MapString(String jsonStr){
        Map<String, String> map = new HashMap<String, String>();  
        //最外层解析  
        JSONObject json = JSONObject.fromObject(jsonStr);  
        for(Object k : json.keySet()){ 
            Object v = json.get(k);   
            if(null!=v){
                map.put(k.toString(), v.toString());  
            }
        }  
        return map;  
    }
    
	/**
	 * xml转map
	 * 
	 * @param xml		xml内容
	 * @return
	 */
	public static Map<String, Object> xml2map(String xml) {
		return xml2map(xml, null);
	}

	/**
	 * xml转map
	 * 
	 * @param xml			xml内容
	 * @param xpath		指定路径（不用指定根目录）
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> xml2map(String xml, String xpath) {
		// SAXReader就是一个管道，用一个流的方式，把xml文件读出来
		SAXReader reader = new SAXReader();

		// 下面的是通过解析xml字符串的
		Document doc = null;
		try {
			doc = reader.read(new CharArrayReader(xml.toCharArray()));
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		// 获取根节点
		Element rootElt = doc.getRootElement(); // 获取根节点
//		System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(xpath==null || xpath.equals("")){//转换全部
			map.put(rootElt.getName(), ele2map(rootElt));
		}else{
			Element ele = (Element) rootElt.selectSingleNode(xpath);
			if(ele!=null){
				//判断是否是多个匹配
				if(rootElt.selectNodes(xpath).size()>1){
					List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
					for (Element element : (List<Element>)rootElt.selectNodes(xpath)) {
						list.add(ele2map(element));
					}
					map.put(ele.getName(), list);
				}else{
					map.put(ele.getName(), ele2map(ele));
				}
			}
		}
		return map;
	}

	/**
	 * element对象转化为map
	 * 
	 * @param rootElt
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Object> ele2map(Element rootElt) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Element> eles = rootElt.elements();
		for (Element ele : eles) {
			if(ele.elements().size()==0){
				map.put(ele.getName(), ele.getTextTrim());
			}else if(ele.elements().size()==1){
				map.put(ele.getName(), ele2map(ele));
			}else {//>1
				if(map.containsKey(ele.getName())){
					List<Map<String, Object>> list = null;
					if(List.class.isAssignableFrom(map.get(ele.getName()).getClass())){
						list = (List<Map<String, Object>>) map.get(ele.getName());
					}else{
						list = new ArrayList<Map<String,Object>>();
						list.add((Map<String, Object>)map.get(ele.getName()));
					}
					list.add(ele2map(ele));
					map.put(ele.getName(), list);
				}else{
					map.put(ele.getName(), ele2map(ele));
				}
			}
			// System.out.println("  " + ele.getName()); // 拿到节点的名称
		}
		return map;
	}
     
    public static void main(String[] args) {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><result><code>0</code><subcode>0</subcode><message></message><default>0</default><data><album><limit>0</limit><pic><batchId>1453602570122000</batchId><browser>0</browser><cameratype> </cameratype><cp_flag>true</cp_flag><cp_x>547</cp_x><cp_y>320</cp_y><desc/><exif><exposureCompensation/><exposureMode/><exposureProgram/><exposureTime/><flash/><fnumber/><focalLength/><iso/><lensModel/><make/><meteringMode/><model/><originalTime/></exif><forum>0</forum><frameno>0</frameno><height>640</height><id>0</id><is_weixin_mode>0</is_weixin_mode><ismultiup>0</ismultiup><lloc>NDR0F8gbCAo3pFYmIIcrbgAAAAAAAAA!</lloc><modifytime>1453602570</modifytime><name>photo-blank</name><origin>0</origin><origin_upload>0</origin_upload><origin_url/><owner>136038423</owner><ownername>136038423</ownername><photocubage>142480</photocubage><phototype>5</phototype><picmark_flag>0</picmark_flag><picrefer>1</picrefer><platformId>0</platformId><platformSubId>0</platformSubId><poiName/><pre>http://b110.photo.store.qq.com/psbe?/V10l2eOl01dgnV/FhSHGWUG2FNGCJERJqsUvB2QX359X35Twlgea5uXK.Rn4cdhY4nTI*1EvrFjw83m/a/dG4AAAAAAAAA&amp;bo=SAaAAgAAAAAFB.g!</pre><raw/><raw_upload>0</raw_upload><rawshoottime>0</rawshoottime><shoottime>0</shoottime><shorturl/><sloc>NDR0F8gbCAo3pFYmIIcrbgAAAAAAAAA!</sloc><tag/><uploadtime>2016-01-24 10:29:30</uploadtime><url>http://b110.photo.store.qq.com/psbe?/V10l2eOl01dgnV/FhSHGWUG2FNGCJERJqsUvB2QX359X35Twlgea5uXK.Rn4cdhY4nTI*1EvrFjw83m/b/dG4AAAAAAAAA&amp;bo=SAaAAgAAAAAFB.g!</url><width>1608</width><yurl>0</yurl></pic><t>696405303</t><topic><bitmap>10000010</bitmap><browser>0</browser><classid>100</classid><comment>1</comment><cover_id>NDR0F8gbCAo3pFYmIIcrbgAAAAAAAAA!</cover_id><createtime>1453602554</createtime><desc/><handset>0</handset><id>V10l2eOl01dgnV</id><lastuploadtime>1453602571</lastuploadtime><modifytime>1453602867</modifytime><name>我的秘密</name><ownerName>136038423</ownerName><ownerUin>136038423</ownerUin><pre>http://b110.photo.store.qq.com/psbe?/V10l2eOl01dgnV/lpX9Kd98uAOCcAkVV2zSlq0U.rS9mjlHDW70x2l67PlBtCgRaLHR9J.wCbKy8*Nu/a/dG4AAAAAAAAA</pre><priv>5</priv><pypriv>3</pypriv><total>1</total><url>http://b110.photo.store.qq.com/psbe?/V10l2eOl01dgnV/lpX9Kd98uAOCcAkVV2zSlq0U.rS9mjlHDW70x2l67PlBtCgRaLHR9J.wCbKy8*Nu/b/dG4AAAAAAAAA</url><viewtype>0</viewtype></topic><totalInAlbum>1</totalInAlbum><totalInPage>1</totalInPage></album></data></result>";
        String json = xml2JSON(xml);
        System.out.println("json="+json);
        xml = json2XML(json);
        System.out.println("xml = "+xml);
    }
}