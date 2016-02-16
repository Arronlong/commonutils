package common.util.num;

import java.util.Arrays;
import java.util.List;

/**
 * 数字转汉字工具类
 * 
 * @author arron
 * @date 2015年7月21日 下午4:14:16 
 * @version 1.0
 */
public class NumUtils {
	
	//num 表示数字，lower表示小写，upper表示大写
	private static final String[] num_lower = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
	private static final String[] num_upper = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
	
	//unit 表示单位权值，lower表示小写，upper表示大写
	private static final String[] unit_lower = { "", "十", "百", "千" };
	private static final String[] unit_upper = { "", "拾", "佰", "仟"};
	private static final String[] unit_common = {"","万", "亿","兆","京","垓","秭","穰","沟","涧","正","载"};
	
	//允许的格式
	private static final List<String> promissTypes = Arrays.asList("INTEGER","INT","LONG","DECIMAL","FLOAT","DOUBLE","STRING","BYTE","TYPE","SHORT");

	//标记为小数点
	private static final int DOT=-99;
	//标记为无效数字
	private static final int INVALID=-100;
	

	/**
	 * 数字转化为小写的汉字
	 * 
	 * @param num 将要转化的数字
	 * @return
	 */
	public static String toChineseLower(Object num){
		return format(num, num_lower, unit_lower);
	}
	
	/**
	 *  数字转化为大写的汉字
	 *  
	 * @param num 将要转化的数字
	 * @return
	 */
	public static String toChineseUpper(Object num){
		return format(num, num_upper, unit_upper);
	}
	
	/**
	 * 大数加法
	 * 
	 * @param a	第一个数
	 * @param b	第二个数
	 * @return	最终结果
	 */
	public static String add(String a, String b){
		//检查数字格式
		checkNum(a);
		checkNum(b);
		
		//标记最终结果是否为负值
		boolean minus=false;
		
		//判断是否有带着-号
		if(a.startsWith("-") || b.startsWith("-")){
			//判断是否全带着-号
			if(a.startsWith("-") && b.startsWith("-")){
				//2个都带着-号，结果肯定为负值
				minus=true;
				if(a.startsWith("-")){
					a = a.substring(1);
				}
				if(b.startsWith("-")){
					b = b.substring(1);
				}
			}else{
				//如果只有一个是负值，则调用减法来完成操作
				if(a.startsWith("-")){//a是负数
					a = a.substring(1);
					return subduct(b, a);
				}else{
					b = b.substring(1);
					return subduct(a, b);
				}
				
			}
		}
		
		//获取a,b的整数和小数部分
		String a_int = getInt(a);
		String a_fraction = getFraction(a);
		String b_int = getInt(b);
		String b_fraction = getFraction(b);
		
		//计算小数部分最大长度
		int len_fraction = Math.max(a_fraction.length(), b_fraction.length());
		
		//计算整数部分最大长度
		int len_int = Math.max(a_int.length(), b_int.length())+1;
		
		//a,b两个数整数最大长度和小数最大长度之和+小数点（1位）
		int len = len_fraction + len_int+1;
		
		//创建结果数组
		int[] result = new int[len];//默认全为0
  
        //为了方便计算，去掉小数点（最后在结果中加上小数点）
		//将【整数部分】高低位对调(反转是为了低位对齐)，最终转化为char数组
		//小数部分不用调整
        char[] s_a_int = reverseStr(a_int);
        char[] s_b_int = reverseStr(b_int);
        char[] s_a_fraction = a_fraction.toCharArray();
        char[] s_b_fraction = b_fraction.toCharArray();

        //采用整数+整数，小数+小数的方式运算
        add(s_a_int, s_b_int, len_fraction, result);
        add(s_a_fraction, s_b_fraction, 1-len_fraction, result);

		// 处理结果集合，如果是大于10的就向前一位进位，本身进行除10取余
        accumulateResultArrays(result);
		
		//标记小数点位置
		markDot(len_fraction, result);
		
		//切掉无用的0
		cutUnusedZero(len_fraction, result);
		
		//然后将数据反转
        return (minus ? "-" : "")+reverseResult(result);
	}
	
	/**
	 * 大数减法
	 * 
	 * @param a	第一个数
	 * @param b	第二个数
	 * @return	最终结果
	 */
	public static String subduct(String a, String b){
		//检查数字格式
		checkNum(a);
		checkNum(b);
		
		//标记最终结果是否为负值
		boolean minus=false;
		
		//判断是否有带着-号
		if(a.startsWith("-") || b.startsWith("-")){
			//判断是否全带着-号
			if(a.startsWith("-") && b.startsWith("-")){
				//2个都带着-号
				if(a.startsWith("-")){
					a = a.substring(1);
				}
				if(b.startsWith("-")){
					b = b.substring(1);
				}
				return subduct(b, a);
			}else{
				//如果只有一个是负值，则调用加法来完成操作
				if(a.startsWith("-")){//a是负值，b是非负值
					return add(a, "-"+b);//2个负值的加法运算
				}else{//b是负值
					b = b.substring(1);
					return add(a, b);//2个正值的加法运算
				}
			}
		}
		 
		//获取a,b的整数和小数部分
		String a_int = getInt(a);
		String a_fraction = getFraction(a);
		String b_int = getInt(b);
		String b_fraction = getFraction(b);
		
		boolean isSame = false;
		//判断大小
		if(b_int.length()>a_int.length()){
			//如果b>a
			return "-"+subduct(b, a);
		}else if(b_int.length()==a_int.length()){
			char[] s_a = a_int.toCharArray();
			char[] s_b = b_int.toCharArray();
			for (int i = 0; i < s_a.length; i++) {
				if(s_b[i]>s_a[i]){
					minus=true;
					isSame=false;
					break;
				}else if(s_b[i]<s_a[i]){
					isSame=false;
					break;
				}else{
					isSame = true;
				}
			}
			if(isSame){//整数部分全部相同，对比小数部分
				s_a = a_fraction.toCharArray();
				s_b = b_fraction.toCharArray();
				for (int i = 0; i < Math.min(s_a.length, s_b.length); i++) {
					if(s_b[i]>s_a[i]){
						minus=true;
						isSame=false;
						break;
					}else if(s_b[i]<s_a[i]){
						isSame=false;
						break;
					}else{
						isSame = true;
					}
				}
				if(isSame){//前部分全相同
					if(s_b.length>s_a.length){//前部分全相同，b小数位数多,则 b>a
						return "-"+subduct(b, a);
					}else if(s_b.length == s_a.length){
						return "0";
					}
				}else if(minus){//如果b>a
					return "-"+subduct(b, a);
				}
			}
		}
		
		
		//计算小数部分最大长度
		int len_fraction = Math.max(a_fraction.length(), b_fraction.length());
		
		//计算整数部分最大长度
		int len_int = Math.max(a_int.length(), b_int.length());
		
		//a,b两个数整数最大长度和小数最大长度之和+小数点（1位）
		int len = len_fraction + len_int+1;
		
		//创建结果数组
		int[] result = new int[len];//默认全为0
		
		//为了方便计算，去掉小数点（最后在结果中加上小数点）
		//将【整数部分】高低位对调(反转是为了低位对齐)，最终转化为char数组
		//小数部分不用调整
		char[] s_a_int = reverseStr(a_int);
		char[] s_b_int = reverseStr(b_int);
		char[] s_a_fraction = a_fraction.toCharArray();
		char[] s_b_fraction = b_fraction.toCharArray();
		
		//采用整数+整数，小数+小数的方式运算
		subduct(s_a_int, s_b_int, len_fraction, result);
		subduct(s_a_fraction, s_b_fraction, 1-len_fraction, result);
		
		// 处理结果集合，如果是大于10的就向前一位进位，本身进行除10取余
		subductResultArrays(result);
		
		//标记小数点位置
		markDot(len_fraction, result);
		
		//切掉无用的0
		cutUnusedZero(len_fraction, result);
		
		//然后将数据反转
		return (minus ? "-" : "")+reverseResult(result);
	}
	
	/**
	 * 大数乘法
	 * 
	 * @param a	第一个数
	 * @param b	第二个数
	 * @return	最终结果
	 */
	public static String multiply(String a, String b){
		//检查数字格式
		checkNum(a);
		checkNum(b);

		//标记最终结果是否为负值
		boolean minus=false;
		
		//判断是否有带着-号
		if(a.startsWith("-") || b.startsWith("-")){
			//判断是否全带着-号
			if(a.startsWith("-") && b.startsWith("-")){
			}else{
				//只有1个带着-号，则结果为负值
				minus=true;
			}
			if(a.startsWith("-")){
				a = a.substring(1);
			}
			if(b.startsWith("-")){
				b = b.substring(1);
			}
		}
		
		//获取a,b的整数和小数部分
		String a_int = getInt(a);
		String a_fraction = getFraction(a);
		String b_int = getInt(b);
		String b_fraction = getFraction(b);
		
		//计算小数部分的总长度
		int len_fraction = a_fraction.length() +b_fraction.length() ;
		
		//a,b两个数乘积的最大位数不会超过总位数之和+小数点（1位）
		int len = len_fraction +a_int.length()+b_int.length()+1;
		
		//创建结果数组
		int[] result = new int[len];//默认全为0
  
        //为了方便计算，去掉小数点（最后在结果中加上小数点）
		//并将高低位对调(反转是为了低位对齐)，最终转化为char数组
        char[] s_a_int = reverseStr(a_int);
        char[] s_a_fraction = reverseStr(a_fraction);
        char[] s_b_int = reverseStr(b_int);
        char[] s_b_fraction = reverseStr(b_fraction);

        //将a、b都拆分成整数+小数，然后
        //采用（x1+x2）（y1+y2）=x1y1+x1y2+x2y1+x2y2公式，分别计算乘积
        multiply(s_a_int, s_b_int, len_fraction, result);
        multiply(s_a_int, s_b_fraction, (len_fraction-s_b_fraction.length), result);
        multiply(s_b_int, s_a_fraction, (len_fraction-s_a_fraction.length), result);
        multiply(s_a_fraction, s_b_fraction, 0, result);

		// 处理结果集合，如果是大于10的就向前一位进位，本身进行除10取余
        accumulateResultArrays(result);
		
		//标记小数点位置
		markDot(len_fraction, result);
		
		//切掉无用的0
		cutUnusedZero(len_fraction, result);
		
		//然后将数据反转
		return (minus?"-":"") + reverseResult(result);
	}

	/**
	 * 反转字符串，并转化为数组
	 * 
	 * @param s		原字符串
	 * @return
	 */
	private static char[] reverseStr(String s) {
		return new StringBuffer(s).reverse().toString().toCharArray();
	}

	/**
	 * 累加每一位，超过10则然后进位
	 * 
	 * @param result	结果数组
	 */
	private static void accumulateResultArrays(int[] result) {
		for (int i = 0; i < result.length; i++) {
			if (result[i] >= 10) {
				result[i + 1] += result[i] / 10;
				result[i] %= 10;
			}
		}
	}
	
	/**
	 * 检查每一位，小于0（不含标记的小数点未和无效的0）则然后向高位借位。
	 * 
	 * @param result	结果数组
	 */
	private static void subductResultArrays(int[] result) {
		for (int i = 0; i < result.length-1; i++) {
			if (result[i] < 0 && result[i]>DOT) {
				result[i + 1]--;
				result[i] += 10;
			}
		}
	}

	/**
	 * 去掉不必要的0（包括整数最前面的和小数最后面的0）
	 * 
	 * @param len_fraction	小数长度
	 * @param result		结果数组
	 */
	private static void cutUnusedZero(int len_fraction, int[] result) {
		//去掉小数部分不必要的0
		boolean flag_0_fraction = true;//标记一直是0
		for (int i =0; i< len_fraction; i++) {
			if(flag_0_fraction && result[i]==0){
				result[i]=INVALID;//为0时标记为无效
			}else{
				flag_0_fraction=false;
				break;
			}
		}
		
		//去掉整数部分的0
		boolean flag_0_int=true;
		for (int i =result.length-1; i > len_fraction || (len_fraction==0 && i==0); i--) {
			if(flag_0_int && result[i]==0){
				result[i]=INVALID;//为0时标记为无效
			}else{
				flag_0_int=false;//遇到不为0时，停止。
				break;
			}
		}
		if(flag_0_int){//整数部分全为0
			result[len_fraction+1]=0;
			if(flag_0_fraction){//同时，小数部分也全为0
				result[len_fraction]=INVALID;//不需要小数点了，所以置为无效
			}
		}else{//整数部分不为0
			if(flag_0_fraction && len_fraction>0){//小数部分全为0
				result[len_fraction]=INVALID;//不需要小数点了，所以置为无效
			}
		}
	}

	/**
	 * 反转结果，替换小数点，跳过无效的0
	 * 
	 * @param result		结果数组
	 * @return
	 */
	private static String reverseResult(int[] result) {
		//反转
		StringBuffer sb = new StringBuffer();
        for (int i = result.length - 1; i >= 0; i--) {
        	if(result[i]>INVALID){
        		sb.append(result[i]==DOT ? "." : result[i]);
        	}
        }
		return sb.toString();
	}

	/**
	 * 标记小数点位置
	 * 
	 * @param len_fraction	小数长度
	 * @param result	结果数组（反转的）
	 */
	private static void markDot(int len_fraction, int[] result) {
		if(len_fraction>0){
			//标记小数点位置
			for (int i = result.length-1 ; i > len_fraction; i--) {
				result[i] = result[i-1];
			}
			result[len_fraction]=DOT;//标记小数点位置
		}
	}
	
	
	/**
	 * 计算2个数的每一位的乘积，放入到对应的结果数组中（未进位）
	 * 
	 * @param a	第一个数
	 * @param b	第二个数
	 * @param start	开始放入的偏移位置
	 * @param result	结果数组
	 */
	private static void multiply(char[] a, char[] b, int start , int[] result){
		// 计算结果集合
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < b.length; j++) {
				result[i + j + start] += (int) (a[i] - '0') * (int) (b[j] - '0');
			}
		}
	}
	
	/**
	 * 计算2个数的每一位的和，放入到对应的结果数组中（未进位）
	 * 
	 * @param a	第一个数
	 * @param b	第二个数
	 * @param start	开始放入的偏移位置
	 * @param result	结果数组
	 */
	private static void add(char[] a, char[] b, int start , int[] result){
		char[] c=null;
		//保证a是位数多的，如果b长度大于a，则交换a，b
		if(b.length>a.length){
			c=a;
			a=b;
			b=c;
		}
		// 计算结果集合，a的位数>=b的位数
		int i = 0, j=0;
		for (; i < a.length && j< b.length; i++,j++) {
			result[Math.abs(i + start)] += (int) (a[i] - '0') + (int) (b[j] - '0');
		}
		//如果a没有处理完毕，直接把a剩下的值赋值给结果数组即可
		for (; i < a.length; i++) {
			result[Math.abs(i + start)] += (int) (a[i] - '0');
		}
		if(c!=null){//如果交换过，则再交换回来
			c=a;
			a=b;
			b=c;
		}
		c=null;
	}
	
	/**
	 * 计算2个数的每一位的差，放入到对应的结果数组中（未进位）
	 * 
	 * @param a	第一个数
	 * @param b	第二个数
	 * @param start	开始放入的偏移位置
	 * @param result	结果数组
	 */
	private static void subduct(char[] a, char[] b, int start , int[] result){
		// 计算结果集合，a的位数>=b的位数
		int i = 0, j=0;
		for (; i < a.length && j< b.length; i++,j++) {
			result[Math.abs(i + start)] +=((int) (a[i] - '0') - (int) (b[j] - '0'));
		}
		//如果a没有处理完毕，直接把a剩下的值赋值给结果数组即可
		for (; i < a.length; i++) {
			result[Math.abs(i + start)] +=((int) (a[i] - '0'));
		}
		//如果a没有处理完毕，直接把a剩下的值赋值给结果数组即可
		for (; i < b.length; i++) {
			result[Math.abs(i + start)] +=-((int) (b[i] - '0'));
		}
	}
	
	/**
	 * 格式化数字
	 * 
	 * @param num			原数字
	 * @param numArray 	数字大小写数组
	 * @param unit			单位权值
	 * @return
	 */
	private static String format(Object num,String[] numArray,String[] unit){
		if(!promissTypes.contains(num.getClass().getSimpleName().toUpperCase())){
			throw new RuntimeException("不支持的格式类型");
		}
		//获取整数部分
		String intnum = getInt(String.valueOf(num));
		//获取小数部分
		String decimal = getFraction(String.valueOf(num));
		//格式化整数部分
		String result = formatIntPart(intnum,numArray,unit);
		if(!"".equals(decimal)){//小数部分不为空
			//格式化小数
			result += "点"+formatFractionalPart(decimal, numArray);
		}
		return result;
	}
	

	/**
	 * 获取整数部分
	 * 
	 * @param num
	 * @return
	 */
	private static String getInt(String num){
		//检查格式
		checkNum(num);
		
		char[] val = String.valueOf(num).toCharArray();
		StringBuffer sb = new StringBuffer();
		int t , s = 0;
		for (int i = 0; i < val.length; i++) {
			if(val[i]=='.') {
				break;
			}
			t = Integer.parseInt(val[i]+"",16);
			if(s+t==0){
				continue;
			}
			sb.append(t);
			s+=t;
		}
		return (sb.length()==0? "0":sb.toString());
	}

	/**
	 * 检查数字格式
	 * 
	 * @param num
	 */
	private static void checkNum(String num) {
		if(num.indexOf(".") != num.lastIndexOf(".")){
			throw new RuntimeException("数字["+num+"]格式不正确!");
		}
		if(num.indexOf("-") != num.lastIndexOf("-") || num.lastIndexOf("-")>0){
			throw new RuntimeException("数字["+num+"]格式不正确！");
		}
		if(num.indexOf("+") != num.lastIndexOf("+")){
			throw new RuntimeException("数字["+num+"]格式不正确！");
		}
		if(num.indexOf("+") != num.lastIndexOf("+")){
			throw new RuntimeException("数字["+num+"]格式不正确！");
		}
		if(num.replaceAll("[\\d|\\.|\\-|\\+]", "").length()>0){
			throw new RuntimeException("数字["+num+"]格式不正确！");
		}
	}

	/**
	 * 获取小数部分
	 * 
	 * @param num
	 * @return
	 */
	private static String getFraction(String num){
		int i = num.lastIndexOf(".");
		if(num.indexOf(".") != i){
			throw new RuntimeException("数字格式不正确，最多只能有一位小数点！");
		}
		String fraction =""; 
		if(i>=0){
			fraction = getInt(new StringBuffer(num).reverse().toString());
			if(fraction.equals("0")){
				return "";
			}
		}
		return new StringBuffer(fraction).reverse().toString();
	}
	
	/**
	 * 分割数字，每4位一组
	 * 
	 * @param num
	 * @return
	 */
	private static Integer[] split2IntArray(String num){
		String prev = num.substring(0,num.length() % 4);
		String stuff = num.substring(num.length() % 4);
		if(!"".equals(prev)){
			num = String.format("%04d",Integer.valueOf(prev))+stuff;
		}
		Integer[] ints = new Integer[num.length()/4];
		int idx=0;
		for(int i=0;i<num.length();i+=4){
			String n = num.substring(i,i+4);
			ints[idx++]=Integer.valueOf(n);
		}
		return ints;
	}
	
	/**
	 * 格式化整数部分
	 * 
	 * @param num	整数部分
	 * @param numArray 数字大小写数组
	 * @return
	 */
	private static String formatIntPart(String num,String[] numArray,String[] unit){
		
		//按4位分割成不同的组（不足四位的前面补0）
		Integer[] intnums = split2IntArray(num);

		boolean zero = false;
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<intnums.length;i++){
			//格式化当前4位
			String r = formatInt(intnums[i], numArray,unit);
			if("".equals(r)){//
				if((i+1)==intnums.length){
					sb.append(numArray[0]);//结果中追加“零”
				}else{
					zero=true;
				}
			}else{//当前4位格式化结果不为空（即不为0）
				if(zero || (i>0 && intnums[i]<1000)){//如果前4位为0，当前4位不为0
					sb.append(numArray[0]);//结果中追加“零”
				}
				sb.append(r);
				sb.append(unit_common[intnums.length-1-i]);//在结果中添加权值
				zero=false;
			}
		}
		return sb.toString();
	}

	/**
	 * 格式化小数部分
	 * 
	 * @param decimal 小数部分
	 * @param numArray 数字大小写数组
	 * @return
	 */
	private static String formatFractionalPart(String decimal,String[] numArray) {
		char[] val = String.valueOf(decimal).toCharArray();
		int len = val.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			int n = Integer.valueOf(val[i] + "");
			sb.append(numArray[n]);
		}
		return sb.toString();
	}
	
	
	/**
	 * 格式化4位整数
	 * 
	 * @param num
	 * @param numArray
	 * @return
	 */
	private static String formatInt(int num,String[] numArray,String[] unit){
		char[] val = String.valueOf(num).toCharArray();
		int len = val.length;
		StringBuilder sb = new StringBuilder();
		boolean isZero = false;
		for (int i = 0; i < len; i++) {
			int n = Integer.valueOf(val[i] + "");//获取当前位的数值
			if (n==0) {
				isZero = true;
			} else {
				if (isZero) {
					sb.append(numArray[Integer.valueOf(val[i-1] + "")]);
				}
				sb.append(numArray[n]);
				sb.append(unit[(len - 1) - i]);
				isZero=false;
			}
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
//		short s = 10;
//		byte b=10;
//		char c='A';
//		Object[] nums = {s, b, c, 0, 1001, 100100001L, 21., 205.23F, 205.23D, "01000010", "1000000100105.0123", ".142", "20.00", "1..2", true};
//		System.out.println("将任意数字转化为汉字(包括整数、小数以及各种类型的数字)");
//		System.out.println("--------------------------------------------");
//		for(Object num :nums){
//			try{
//				System.out.print("["+num.getClass().getSimpleName()+"]"+num);
//				for(int i=0;i<25-String.valueOf(num+num.getClass().getSimpleName()).length();i+=4){
//					System.out.print("\t");
//				}
//				System.out.print(" format:"+toChineseLower(num));
//				System.out.println("【"+toChineseUpper(num)+"】");
//			}catch(Exception e){
//				System.out.println(" 错误信息："+e.getMessage());
//			}
//		}
		String a = "9213213210.4508";
		String b = "12323245512512100.4500081";
		String r = multiply(a, b);
		System.out.println(a+"*"+b+"="+r);
		String r1 = add(a, b);
		System.out.println(a+"+"+b+"="+r1);
		String r2 = subduct(a, b);
		System.out.println(a+"-"+b+"="+r2);
	}
}