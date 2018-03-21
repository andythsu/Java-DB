
/**
 * @Author: Andy Su
 * @Date: Mar 12, 2018 
 * @Description: testing functionality of DBActivity 
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import Oracle_DB.DBActivity;

public class Main {
	static String url = "";
	static String username = "";
	static String password = "";
	static DBActivity db = DBActivity.connect(url, username, password);

	public static void main(String[] args) throws SQLException {
	}
}
