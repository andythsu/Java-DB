/**
 * @Author: Andy Su
 * @Date: Mar 12, 2018 
 * @Description: 
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Can perform DB activities (insert, delete, select ...)
 */
public class DBActivity extends DBConnection{
	public DBActivity(String url, String username, String password) {
		super(url, username, password);
	}
	public static DBActivity connect(String url, String username, String password) {
		return new DBActivity(url, username, password);
	}
	public ResultSet executeQuery(String sql) throws SQLException {
		checkConnection();
		try {
			Statement stmt = this.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rtn = stmt.executeQuery(sql);
			return rtn;
		}catch(SQLException e) {
			System.out.println("error in SQL: " + e.getMessage().toString());
			return null;
		}
	}

	public int executeUpdate(String sql) {
		checkConnection();
		try {
			Statement stmt = this.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			int rtn = stmt.executeUpdate(sql);
			stmt.close();
			return rtn;
		}catch(SQLException e) {
			System.out.println("error in SQL: " + e.getMessage().toString());
			return -1;
		}
	}

	public ResultSet select(String tableName, String[] choices, Object[][] conditions, String logic) throws SQLException {
		String choice = choices == null ? "*" : generateValuesInParenthesis(choices, false);
		String condition;
		if(conditions == null) {
			condition = "";
		}else {
			condition = " WHERE ";
			for(int i=0; i<conditions.length; i++) {
				if(i > 0) condition = condition + " " + logic + " ";
				for(int j=0; j<conditions[i].length; j++) {
					if(j > 0) condition = condition + " = ";
					Object value = conditions[i][j];
					if(value instanceof Integer) {
						condition = condition + (int)value;
					}else if (value instanceof String) {
						condition = condition + (String)value;
					}else if (value instanceof Boolean) {
						condition = condition + (Boolean)value;
					}else if (value instanceof Double) {
						condition = condition + (Double)value;
					}
				}
			}
		}
		try {
			String sql = "SELECT " + choice + " FROM " + tableName + condition;
			Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			return st.executeQuery(sql);
		}catch(SQLException error) {
			System.out.println("error in SQL: " + error.getMessage().toString());
			return null;
		}
	}

	public int update(String tableName, String[] keys, Object[] values, String[][] condition, String intermediate) throws SQLException {
		checkConnection();
		String sql = "UPDATE " + tableName + " SET ";
		sql += generateValuesForUpdate(keys, true);
		sql += " WHERE ";
		sql += generateConditionForUpdate(condition, intermediate);
		PreparedStatement ps = con.prepareStatement(sql);
		for(int i=0; i<values.length; i++) {
			Object value = values[i];
			if(value instanceof Integer) {
				ps.setInt(i+1, (int)value);
			}else if (value instanceof String) {
				ps.setString(i+1, (String)value);
			}else if (value instanceof Boolean) {
				ps.setBoolean(i+1,(boolean)value);
			}else if (value instanceof Double) {
				ps.setDouble(i+1, (Double)value);
			}
		}
		return ps.executeUpdate();
	}


	/**
	 * @param condition
	 * @param 
	 * @return
	 */
	private String generateConditionForUpdate(String[][] condition, String intermediate) {
		String rtn = "";
		if(intermediate != null && condition.length > 1) {
			for(int i=0; i<condition.length; i++) {
				if(i > 0) rtn += " " + intermediate + " ";
				rtn += condition[i][0] + "=" + condition[i][1];
			}
		}else {
			rtn += condition[0][0] + "=" + condition[0][1];
		}
		return rtn;
	}
	// single insert
	public void insert(String tableName, String[] keys, Object[] values) throws SQLException {
		checkConnection();
		String sql = "INSERT INTO " + tableName;
		sql = sql + "(" + generateValuesInParenthesis(keys, false) + ")";
		sql = sql + " VALUES ";
		sql = sql + "(" + generateValuesInParenthesis(keys, true) + ")";
		PreparedStatement ps = con.prepareStatement(sql);
		for(int j=0; j<values.length; j++) {
			Object value = values[j];
			if(value instanceof Integer) {
				ps.setInt(j+1, (int)value);
			}else if (value instanceof String) {
				ps.setString(j+1, (String)value);
			}else if (value instanceof Boolean) {
				ps.setBoolean(j+1,(boolean)value);
			}else if (value instanceof Double) {
				ps.setDouble(j+1, (Double)value);
			}
		}
		ps.addBatch();
		ps.executeBatch();
		System.out.println("inserted successfully");
		ps.close();
	}
	// multiple insert
	public void insert(String tableName, String[] keys, Object[][] values) throws SQLException {
		checkConnection();
		String sql = "INSERT INTO " + tableName;
		sql = sql + "(" + generateValuesInParenthesis(keys, false) + ")";
		sql = sql + " VALUES ";
		sql = sql + "(" + generateValuesInParenthesis(keys, true) + ")";
		PreparedStatement ps = con.prepareStatement(sql);
		for(int i=0; i<values.length; i++) {
			for(int j=0; j<values[i].length; j++) {
				Object value = values[i][j];
				if(value instanceof Integer) {
					ps.setInt(j+1, (int)value);
				}else if (value instanceof String) {
					ps.setString(j+1, (String)value);
				}else if (value instanceof Boolean) {
					ps.setBoolean(j+1,(boolean)value);
				}else if (value instanceof Double) {
					ps.setDouble(j+1, (Double)value);
				}
			}
			ps.addBatch();
			System.out.println("adding..." + i);
		}
		ps.executeBatch();
		System.out.println("inserted successfully");
		ps.close();
	}

	public int getRowCount(ResultSet r) throws SQLException {
		int total = -1;
		while(r.next()) {
			r.last();
			total = r.getRow();
		}
		r.beforeFirst(); // reset the cursor to the beginning
		return total;
	}

	/* helper methods */

	/**
	 * @param keys
	 * @param b
	 * @return
	 */
	private String generateValuesForUpdate(String[] keys, boolean b) {
		String rtn = "";
		for(int i=0; i<keys.length; i++) {
			if(i > 0) rtn += ", ";
			rtn += keys[i] + "=" + "?";
		}
		return rtn;
	}

	private String generateValuesInParenthesis(Object[] keys, boolean includeQuestionMark) {
		String rtn = "";
		if(includeQuestionMark) {
			for(int i=0; i<keys.length; i++) {
				if(i > 0) rtn = rtn + ", ";
				rtn = rtn + "?";
			}
		}else {
			for(int i=0; i<keys.length; i++) {
				if(i > 0) rtn = rtn + ", ";
				rtn = rtn + keys[i];
			}
		}
		return (String)rtn;
	}

	private void checkConnection() {
		if(this.con == null) {
			System.out.println("error connecting to database");
			System.exit(0);
		}
	}
}

