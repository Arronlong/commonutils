package common.util.reflect;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 通用对象copy工具类
 * 
 * @author arron
 * @date 2015年1月9日 上午10:50:32
 * @version 1.0
 */
public class ObjectCopyUtils {

	/**
	 * 拷贝对象方法（适合同一类型的对象复制，但结果需强制转换）
	 * 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static Object copy(Object objSource) throws InstantiationException, IllegalAccessException{
        return copy(objSource,objSource.getClass());
	}
	
	/**
	 * 拷贝对象方法（适合同一类型的对象复制）
	 * 
	 * @param objSource 源对象
	 * @param clazz 目标类
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> T copy(Object objSource,Class<T> clazz) throws InstantiationException, IllegalAccessException{

		if(null == objSource) return null;//如果源对象为空，则直接返回null
		
		T objDes = clazz.newInstance();
		
		// 获得源对象所有属性
//		Field[] fields = clazz.getDeclaredFields();
		Field[] fields = ReflectUtils.getFields(clazz);
		
		// 循环遍历字段，获取字段对应的属性值  
		for ( Field field : fields )  
		{  
			//去掉final字段
			if((field.getModifiers() & Modifier.FINAL)!=0) continue;
			
			// 如果不为空，设置可见性，然后返回  
			field.setAccessible( true );  
			
			try  
			{  
				field.set(objDes, field.get(objSource));
			}  
			catch ( Exception e )  
			{
            	System.err.println("执行"+clazz.getSimpleName()+"类的"+field.getName()+"属性的set方法时出错。"+e.getMessage());
			}  
		}  
		return objDes;
	}
	
	/**
	 * 将SQL查询出来的map对象转成实体对象
	 * 
	 * @param map
	 * @param cs
	 * @return
	 * @throws Exception
	 */
	public static <T> T map2Bean(LinkedHashMap<String,Object> map, Class<T> clazz)
			throws Exception {
		 T obj = clazz.newInstance();
        // 获得对象属性     
        Field[]  fields = ReflectUtils.getFields(clazz);
        for (Field field : fields) {
        	if(!map.containsKey(field.getName())){
				continue;
			}
        	//去掉final字段
			if((field.getModifiers() & Modifier.FINAL)!=0) continue;
			
			// 如果不为空，设置可见性
			field.setAccessible(true);  
			
            try {
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);    
                Method writeMethod = pd.getWriteMethod();  
                writeMethod.invoke(obj, map.get(field.getName()));  
            } catch (Exception e) {
            	e.printStackTrace();
            }  
        }  
		return obj;
	}
	
	@SuppressWarnings("unused")
	private static void setValue(Field field,Object... v) throws Exception{
		Class<?> clazz = field.getType();
		if(clazz == String.class){
			field.set(v[0], v[1]);
		}else if( clazz.getSimpleName().equals("boolean")){
			field.setBoolean(v[0], (boolean)Boolean.parseBoolean(v[1].toString()));
		}else if(clazz.getSimpleName().equals("byte")){
			field.setByte(v[0], (byte)Byte.parseByte(v[1].toString()));
		}else if(clazz.getSimpleName().equals("char")){
			field.setChar(v[0], (char)v[1]);
		}else if(clazz==Double.class || clazz.getSimpleName().equals("double")){
			field.setDouble(v[0], (double)Double.parseDouble(v[1].toString()));
		}else if(clazz.getSimpleName().equals("float")){
			field.setFloat(v[0], (float)Float.parseFloat(v[1].toString()));
		}else if(clazz.getSimpleName().equals("int")){
			field.setInt(v[0], (int)Integer.parseInt(v[1].toString()));
		}else if(clazz.getSimpleName().equals("long")){
			field.setLong(v[0], (long)Long.parseLong(v[1].toString()));
		}else if(clazz.getSimpleName().equals("short")){
			field.setShort(v[0], (short)Short.parseShort(v[1].toString()));
		}else{
			String setMethodName ="set";
			String name = clazz.getSimpleName();
			String firstLetter = name.substring(0, 1).toUpperCase();// 获取属性首字母
			// 拼接set方法名
			setMethodName += firstLetter + name.substring(1);
			Method m = Field.class.getMethod(setMethodName, clazz);
			m.invoke(field, v);
		}
	}
	
	/**
	 * 拷贝对象方法（适合不同类型的转换）<br/>
	 * 前提是，源类中的所有属性在目标类中都存在
	 * 
	 * @param objSource 源对象
	 * @param clazzSrc 源对象所属class
	 * @param clazzDes 目标class
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static <T, K> T copy(Object objSource,Class<K> clazzSrc,Class<T> clazzDes ) throws InstantiationException, IllegalAccessException{
		
		if(null == objSource) return null;//如果源对象为空，则直接返回null
		
		T objDes = clazzDes.newInstance();
		
		return merge((K)objSource, objDes, clazzSrc, clazzDes);
		
	}
	
	/**
	 * 拷贝对象方法（适合不同类型的转换）<br/>
	 * 前提是，源类中的所有属性在目标类中都存在
	 * 
	 * @param objSource 					源对象
	 * @param clazzSrc 						源对象所属class
	 * @param clazzDes 					目标class
	 * @param overrideDefaultValue 	是否重写默认值
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static <T, K> T copy(Object objSource,Class<K> clazzSrc,Class<T> clazzDes ,boolean overrideDefaultValue) throws InstantiationException, IllegalAccessException{
		
		if(null == objSource) return null;//如果源对象为空，则直接返回null
		
		T objDes = clazzDes.newInstance();
		
		return merge((K)objSource, objDes, clazzSrc, clazzDes, overrideDefaultValue);
		
	}
	
	/**
	 * 合并对象方法（适合不同类型的转换）<br/>
	 * 前提是，源类中的所有属性在目标类中都存在
	 * 
	 * @param objSource 源对象
	 * @param clazzSrc 源对象所属class
	 * @param clazzDes 目标class
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T, K> T merge(K objSource,T objDes,Class<K> clazzSrc,Class<T> clazzDes) throws InstantiationException, IllegalAccessException{
		return merge(objSource, objDes, clazzSrc,clazzDes, true);
	}
	
	/**
	 * 合并对象方法（适合不同类型的转换）<br/>
	 * 前提是，源类中的所有属性在目标类中都存在
	 * 
	 * @param objSource 源对象
	 * @param clazzSrc 源对象所属class
	 * @param clazzDes 目标class
	 * @param overwrite 是否覆盖已存在的属性值
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T, K> T merge(K objSource,T objDes,Class<K> clazzSrc,Class<T> clazzDes,boolean overwrite) throws InstantiationException, IllegalAccessException{
		return merge(objSource,  objDes, clazzSrc,clazzDes, overwrite,null);
	}
	
	/**
	 * 合并对象方法（适合不同类型的转换）<br/>
	 * 前提是，源类中的所有属性在目标类中都存在
	 * 
	 * @param objSource 源对象
	 * @param objDes 目标对象
	 * @param clazzSrc 源对象所属class
	 * @param clazzDes 目标class
	 * @param overwrite 是否覆盖已存在的属性值
	 * @param IgnoreMap 忽略的属性值
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T, K> T merge(K objSource,T objDes,Class<K> clazzSrc,Class<T> clazzDes,boolean overwrite,Set<String> IgnoreSet) throws InstantiationException, IllegalAccessException{
		
		if(null == objSource) return null;//如果源对象为空，则直接返回null

		//获取目标对象的所有属性
//		Field[] fieldDeses = clazzDes.getDeclaredFields();
		Field[] fieldDeses = ReflectUtils.getFields(clazzDes);
		Map<String,Field> m = new HashMap<String, Field>();
		// 循环遍历字段，获取字段对应的属性值  
		for ( Field field : fieldDeses )  
		{ 
			// 如果不为空，设置可见性，然后返回  
			field.setAccessible( true );  
			m.put(field.getName(), field);
		}
		
		
		// 获得源对象所有属性
//		Field[] fields = clazzSrc.getDeclaredFields();
		Field[] fields = ReflectUtils.getFields(clazzSrc);
		// 循环遍历字段，获取字段对应的属性值  
		for ( Field field : fields )  
		{  
			//如果目标对象不存在该字段，则跳过
			if(!m.containsKey(field.getName())) continue;
			
			//去掉final字段
			if((field.getModifiers() & Modifier.FINAL)!=0) continue;
			
			// 如果不为空，设置可见性，然后返回  
			field.setAccessible( true );  
			
			try  
			{ 
				Object value = field.get(objSource);
				String fieldName = field.getName();// 属性名
				
				if(java.util.Collection.class.isAssignableFrom(field.getType()) && ((Class<?>) ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0])!=Object.class){
					//如果该字段是Collection，且指定了泛型类型，则将其子类全部进行转换
					Collection c; 
					if(java.util.List.class.isAssignableFrom(field.getType())){
						c = new ArrayList();
					}else if(java.util.Set.class.isAssignableFrom(field.getType())){
						c = new HashSet();
					}else{
						 c = (Collection) field.getType().newInstance();
					}
					Class<?> clsSrc = (Class<?>) ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
					Class<?> clsDesc = (Class<?>) ((ParameterizedType)m.get(fieldName).getGenericType()).getActualTypeArguments()[0]; 
					for (Object ele : (Collection)value) {
						c.add(copy(ele, clsSrc, clsDesc, overwrite));
					}
					value = c;
//				}else if(java.util.Map.class.isAssignableFrom(field.getType()) && field.getGenericType()!=Object.class){
					//如果该字段是Map，且指定了泛型类型，则将其子类全部进行转换
				}else{
					//如果目标对象当前属性不为空
					if(null!=m.get(fieldName).get(objDes)){
						if(overwrite){//如果覆盖当前属性值，但map中存在，则不覆盖，否则覆盖
							if(null!=IgnoreSet && IgnoreSet.contains(fieldName.toUpperCase())){//如果map中有值
								continue;
							}
						}else{//如果不覆盖，但是map存在，则必须覆盖，否则不覆盖
							if(null==IgnoreSet || !IgnoreSet.contains(fieldName.toUpperCase())){//如果map中没有值
								continue;
							}
						}
					}
				}
				
				String firstLetter = fieldName.substring(0, 1).toUpperCase();// 获取属性首字母
				// 拼接set方法名
				String setMethodName = "set" + firstLetter + fieldName.substring(1);
				// 获取set方法对象
				Method setMethod = clazzDes.getMethod(setMethodName,new Class[]{field.getType()});

				// 对目标对象调用set方法装入属性值
				setMethod.invoke(objDes, value);
			}  
			catch ( Exception e )  
			{
            	System.err.println("执行"+clazzDes.getSimpleName()+"类的"+field.getName()+"属性的set方法时出错。"+e.getMessage());
			}  
		}  
		return objDes;
	}
}
