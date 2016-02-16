package common.util.thread;

/** 
 * 
 * @author arron
 * @date 2016年1月12日 上午8:59:34 
 * @version 1.0 
 */
public class ThreadUtils {

	public static void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void sleep(long millis, int nanos){
		try {
			Thread.sleep(millis, nanos);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
