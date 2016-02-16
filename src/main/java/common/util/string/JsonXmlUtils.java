package common.util.string;

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
    public static String xml2JSON(String xml){
        return SERIALIZER.read(xml).toString();
    }
     
    public static String json2XML(String json){
        JSONObject jobj = JSONObject.fromObject(json);
        String xml =  SERIALIZER.write(jobj);
        return xml;
    }
     
    public static void main(String[] args) {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><result><code>0</code><subcode>0</subcode><message></message><default>0</default><data><album><limit>0</limit><pic><batchId>1453602570122000</batchId><browser>0</browser><cameratype> </cameratype><cp_flag>true</cp_flag><cp_x>547</cp_x><cp_y>320</cp_y><desc/><exif><exposureCompensation/><exposureMode/><exposureProgram/><exposureTime/><flash/><fnumber/><focalLength/><iso/><lensModel/><make/><meteringMode/><model/><originalTime/></exif><forum>0</forum><frameno>0</frameno><height>640</height><id>0</id><is_weixin_mode>0</is_weixin_mode><ismultiup>0</ismultiup><lloc>NDR0F8gbCAo3pFYmIIcrbgAAAAAAAAA!</lloc><modifytime>1453602570</modifytime><name>photo-blank</name><origin>0</origin><origin_upload>0</origin_upload><origin_url/><owner>136038423</owner><ownername>136038423</ownername><photocubage>142480</photocubage><phototype>5</phototype><picmark_flag>0</picmark_flag><picrefer>1</picrefer><platformId>0</platformId><platformSubId>0</platformSubId><poiName/><pre>http://b110.photo.store.qq.com/psbe?/V10l2eOl01dgnV/FhSHGWUG2FNGCJERJqsUvB2QX359X35Twlgea5uXK.Rn4cdhY4nTI*1EvrFjw83m/a/dG4AAAAAAAAA&amp;bo=SAaAAgAAAAAFB.g!</pre><raw/><raw_upload>0</raw_upload><rawshoottime>0</rawshoottime><shoottime>0</shoottime><shorturl/><sloc>NDR0F8gbCAo3pFYmIIcrbgAAAAAAAAA!</sloc><tag/><uploadtime>2016-01-24 10:29:30</uploadtime><url>http://b110.photo.store.qq.com/psbe?/V10l2eOl01dgnV/FhSHGWUG2FNGCJERJqsUvB2QX359X35Twlgea5uXK.Rn4cdhY4nTI*1EvrFjw83m/b/dG4AAAAAAAAA&amp;bo=SAaAAgAAAAAFB.g!</url><width>1608</width><yurl>0</yurl></pic><t>696405303</t><topic><bitmap>10000010</bitmap><browser>0</browser><classid>100</classid><comment>1</comment><cover_id>NDR0F8gbCAo3pFYmIIcrbgAAAAAAAAA!</cover_id><createtime>1453602554</createtime><desc/><handset>0</handset><id>V10l2eOl01dgnV</id><lastuploadtime>1453602571</lastuploadtime><modifytime>1453602867</modifytime><name>我的秘密</name><ownerName>136038423</ownerName><ownerUin>136038423</ownerUin><pre>http://b110.photo.store.qq.com/psbe?/V10l2eOl01dgnV/lpX9Kd98uAOCcAkVV2zSlq0U.rS9mjlHDW70x2l67PlBtCgRaLHR9J.wCbKy8*Nu/a/dG4AAAAAAAAA</pre><priv>5</priv><pypriv>3</pypriv><total>1</total><url>http://b110.photo.store.qq.com/psbe?/V10l2eOl01dgnV/lpX9Kd98uAOCcAkVV2zSlq0U.rS9mjlHDW70x2l67PlBtCgRaLHR9J.wCbKy8*Nu/b/dG4AAAAAAAAA</url><viewtype>0</viewtype></topic><totalInAlbum>1</totalInAlbum><totalInPage>1</totalInPage></album></data></result>";
        String json = xml2JSON(xml);
        System.out.println("json="+json);
        xml = json2XML(json);
        System.out.println("xml = "+xml);
    }
}