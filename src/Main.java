/**
 * @Author: Andy Su
 * @Date: Mar 12, 2018 
 * @Description: testing functionality of DBActivity 
 */

public class Main {
	public static void main(String[] args) {
		String url = "";
		String username = "";
		String password = "";
		DBActivity db = DBActivity.connect(url, username, password);
	}
}
