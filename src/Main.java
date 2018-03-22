
/**
 * @Author: Andy Su
 * @Date: Mar 12, 2018 
 * @Description: testing functionality of DBActivity 
 */

import DB.*;

public class Main {
	static String oracle_url = "";
	static String oracle_user = "";
	static String oracle_pass = "";
	static String sql_url = "";
	static String sql_user = "";
	static String sql_pass = "";
	
	static DBActivity sql = SQLActivity.create(sql_url, sql_user, sql_pass); // creates connection for SQL server
	static DBActivity oracle = OracleActivity.create(oracle_url, oracle_user, oracle_pass); // creates connection for Oracle

	public static void main(String[] args){}
	
}
