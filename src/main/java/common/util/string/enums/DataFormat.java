package common.util.string.enums;

/**
 * 数据格式
 * 
 * @author arron
 * @date 2015年11月5日 下午1:55:08 
 * @version 1.0
 */
public enum DataFormat{
		JSON(0),
		XML(1),
		;
		private int code;
		
		public int getCode() {
			return code;
		}
		private DataFormat(int code){
			this.code=code;
		}
	}