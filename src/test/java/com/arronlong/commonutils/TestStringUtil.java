package com.arronlong.commonutils;

import org.junit.Test;

import com.arronlong.common.util.string.StringUtils;

public class TestStringUtil {

	@Test
	public void testAll(){
		String str = "12345abcde";
		System.out.println("--------------------------------");
		System.out.println("正向截取长度为4，结果：\n" + StringUtils.subStr(str, 4));
		System.out.println("反向截取长度为4，结果：\n" + StringUtils.subStr(str, -4));
		System.out.println("--------------------------------");
		System.out.println("正向截取到第4个字符的位置，结果：\n" + StringUtils.subStrStart(str, 4));
		System.out.println("反向截取到第4个字符的位置，结果：\n" + StringUtils.subStrEnd(str, 4));
		System.out.println("--------------------------------");
		System.out.println("从第2个截取到第9个，结果：\n" + StringUtils.subStr(str, 1, 9));
		System.out.println("从第2个截取到倒数第1个，结果：\n" + StringUtils.subStr(str, 1, -1));
		System.out.println("从倒数第4个开始截取，结果：\n" + StringUtils.subStr(str, -4, 0));
		System.out.println("从倒数第4个开始截取，结果：\n" + StringUtils.subStr(str, -4, 10));
		
		String[] ss = {"aaa","bbb","ccc"};
		System.out.println(StringUtils.join(ss, "#|#"));
		
		String template = "<root><id></id><name>#@#</name><tel>#@#</tel><addr>#@#</addr></root>";
		String[] paras={"001","aaa","","海淀"};
		String data = StringUtils.format(template, "#@#", paras);
		System.out.println(data);
	}
}
