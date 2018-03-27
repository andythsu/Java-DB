/**
 * @Author: Andy Su
 * @Date: Mar 22, 2018 
 * @Description: 
 */
package DB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OracleActivity extends DBActivity{

	/**
	 * @param url
	 * @param username
	 * @param password
	 * @param driver
	 */
	private OracleActivity(String url, String username, String password, String driver) {
		super(url, username, password, driver);
	}
	
	/**
	 * factory method
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public static OracleActivity connect(String url, String username, String password) {
		String driver = "oracle.jdbc.driver.OracleDriver";
		return new OracleActivity(url, username, password, driver);
	}

	/* (non-Javadoc)
	 * @see DB.DBActivity#executeQuery(java.lang.String)
	 */
	@Override
	public ResultSet executeQuery(String sql) {
		checkConnection();
		Statement stmt = null;
		ResultSet rtn = null;
		try {
			stmt = this.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rtn = stmt.executeQuery(sql);
			return rtn;
		} catch (SQLException e) {
			System.out.println("error in SQL: " + e.getMessage().toString());
			return null;
		} 
	}

	/* (non-Javadoc)
	 * @see DB.DBActivity#executeUpdate(java.lang.String)
	 */
	@Override
	public int executeUpdate(String sql) {
		checkConnection();
		Statement stmt = null;
		try {
			stmt = this.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("error in SQL: " + e.getMessage().toString());
			return -1;
		} finally {
			close(null, stmt, null, null);
		}
	}

	/* (non-Javadoc)
	 * @see DB.DBActivity#select(java.lang.String, java.lang.String[], java.lang.Object[][], java.lang.String)
	 */
	@Override
	public ResultSet select(String tableName, String[] choices, Object[][] conditions, String logic) {
		String choice = choices == null ? "*" : generateValuesInParenthesis(choices, false);
		String condition;
		Statement st = null;
		if (conditions == null) {
			condition = "";
		} else {
			condition = " WHERE ";
			for (int i = 0; i < conditions.length; i++) {
				if (i > 0)
					condition = condition + " " + logic + " ";
				for (int j = 0; j < conditions[i].length; j++) {
					if (j > 0)
						condition = condition + " = ";
					Object value = conditions[i][j];
					if (value instanceof Integer) {
						condition = condition + (int) value;
					} else if (value instanceof String) {
						condition = condition + (String) value;
					} else if (value instanceof Boolean) {
						condition = condition + (Boolean) value;
					} else if (value instanceof Double) {
						condition = condition + (Double) value;
					}
				}
			}
		}
		try {
			String sql = "SELECT " + choice + " FROM " + tableName + condition;
			st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			return st.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
