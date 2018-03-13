
/**
 * @Author: Andy Su
 * @Date: Mar 12, 2018 
 * @Description: testing functionality of DBActivity 
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import DB.DBActivity;

public class Main {
	static String url = "";
	static String username = "";
	static String password = "";
	static DBActivity db = DBActivity.connect(url, username, password);

	public static void main(String[] args) throws SQLException {
		int[] update = db.update("ANDY", new String[] {"NAME", "COLOR"}, new Object[][] {{"22", 2323}, {"33",3.33}}, "ID", new String[] {"22", "23"});
		System.out.println(Arrays.toString(update));
		}
}
