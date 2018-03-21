package Oracle_DB;

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
public class DBActivity extends DBConnection {
	private DBActivity(String url, String username, String password) {
		super(url, username, password);
	}

	public static DBActivity connect(String url, String username, String password) {
		return new DBActivity(url, username, password);
	}

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
			System.out.println("error in SQL: " + e.getMessage().toString());
			return null;
		}
	}
	
	/**
	 * batch update, can only have 1 condition. ex: update (table) set (field) = (?) where (field) = (value)
	 * @param tableName
	 * @param keys
	 * @param values
	 * @param condition
	 * @return
	 */
	public int[] update(String tableName, String[] keys, Object[][] values, String condition_field, String[] condition_vals) {
		checkConnection();
		String sql = "UPDATE " + tableName + " SET ";
		sql += generateValuesForUpdate(keys);
		sql += " WHERE ";
		sql += condition_field + "=?";
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(sql);
			for(int i=0; i<values.length; i++) {
				for(int j=0; j<values[i].length; j++) {
					Object value = values[i][j];
					if (value instanceof Integer) {
						ps.setInt(j + 1, (int) value);
					} else if (value instanceof String) {
						ps.setString(j + 1, (String) value);
					} else if (value instanceof Boolean) {
						ps.setBoolean(j + 1, (boolean) value);
					} else if (value instanceof Double) {
						ps.setDouble(j + 1, (Double) value);
					}
					
					// for the WHERE clause
					if ((j+1) == values[i].length) {
						ps.setString(j+2, condition_vals[i]);
					}
				}
				ps.addBatch();
			}
			System.out.println("finished updating");
			return ps.executeBatch();
		}catch(SQLException e) {
			e.printStackTrace();
			return null;
		}finally {
			close(ps, null, null, null);
		}
	}
	

	/**
	 * single update, can have multiple conditions. ex: update (table) set (field) = (?) where (field) = (value) OR/AND (field2) = (value2) ...
	 * @param tableName
	 * @param keys
	 * @param values
	 * @param condition
	 * @param intermediate
	 * @return
	 */
	public int update(String tableName, String[] keys, Object[] values, String[][] condition, String intermediate) {
		checkConnection();
		String sql = "UPDATE " + tableName + " SET ";
		sql += generateValuesForUpdate(keys);
		sql += " WHERE ";
		sql += generateConditionForUpdate(condition, intermediate);
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(sql);
			for (int i = 0; i < values.length; i++) {
				Object value = values[i];
				if (value instanceof Integer) {
					ps.setInt(i + 1, (int) value);
				} else if (value instanceof String) {
					ps.setString(i + 1, (String) value);
				} else if (value instanceof Boolean) {
					ps.setBoolean(i + 1, (boolean) value);
				} else if (value instanceof Double) {
					ps.setDouble(i + 1, (Double) value);
				}
			}
			System.out.println("finished updating");
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("error in updating");
			return -1;
		} finally {
			close(ps, null, null, null);
		}

	}

	/**
	 * called by client class to close connection object
	 */
	public void close() {
		close(null, null, null, this.con);
	}

	
	// single insert
	public int[] insert(String tableName, String[] keys, Object[] values) {
		checkConnection();
		String sql = "INSERT INTO " + tableName;
		sql = sql + "(" + generateValuesInParenthesis(keys, false) + ")";
		sql = sql + " VALUES ";
		sql = sql + "(" + generateValuesInParenthesis(keys, true) + ")";
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(sql);
			for (int j = 0; j < values.length; j++) {
				Object value = values[j];
				if (value instanceof Integer) {
					ps.setInt(j + 1, (int) value);
				} else if (value instanceof String) {
					ps.setString(j + 1, (String) value);
				} else if (value instanceof Boolean) {
					ps.setBoolean(j + 1, (boolean) value);
				} else if (value instanceof Double) {
					ps.setDouble(j + 1, (Double) value);
				}
			}
			ps.addBatch();
			return ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			close(ps, null, null, null);
		}
	}

	// batch insert
	public int[] insert(String tableName, String[] keys, Object[][] values) {
		checkConnection();
		String sql = "INSERT INTO " + tableName;
		sql = sql + "(" + generateValuesInParenthesis(keys, false) + ")";
		sql = sql + " VALUES ";
		sql = sql + "(" + generateValuesInParenthesis(keys, true) + ")";
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(sql);
			for (int i = 0; i < values.length; i++) {
				for (int j = 0; j < values[i].length; j++) {
					Object value = values[i][j];
					if (value instanceof Integer) {
						ps.setInt(j + 1, (int) value);
					} else if (value instanceof String) {
						ps.setString(j + 1, (String) value);
					} else if (value instanceof Boolean) {
						ps.setBoolean(j + 1, (boolean) value);
					} else if (value instanceof Double) {
						ps.setDouble(j + 1, (Double) value);
					}
				}
				ps.addBatch();
				System.out.println("adding..." + i);
			}
			return ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			close(ps, null, null, null);
		}
	}
	
	
	/**
	 * SQL approach to get row count
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public int getRowCount(String tableName) throws SQLException{
		String sql = "select count(*) AS cnt from " + tableName;
		ResultSet r = executeQuery(sql);
		String totalCount = "0";
		while(r.next()) {
			totalCount = r.getString("cnt");
		}
		return Integer.parseInt(totalCount);
	}

	/**
	 * ResultSet approach to get row count
	 * @param r
	 * @return
	 * @throws SQLException
	 */
	public int getRowCount(ResultSet r) throws SQLException {
		int total = -1;
		while (r.next()) {
			r.last();
			total = r.getRow();
		}
		r.beforeFirst(); // reset the cursor to the beginning
		return total;
	}

	/* helper methods */

	/**
	 * @param condition
	 * @param
	 * @return
	 */
	private String generateConditionForUpdate(String[][] condition, String intermediate) {
		String rtn = "";
		if (intermediate != null && condition.length > 1) {
			for (int i = 0; i < condition.length; i++) {
				if (i > 0)
					rtn += " " + intermediate + " ";
				rtn += condition[i][0] + "=" + condition[i][1];
			}
		} else {
			rtn += condition[0][0] + "=" + condition[0][1];
		}
		return rtn;
	}
	
	/**
	 * @param keys
	 * @param question_mark
	 * @return
	 */
	private String generateValuesForUpdate(String[] keys) {
		String rtn = "";
		for (int i = 0; i < keys.length; i++) {
			if (i > 0)
				rtn += ", ";
			rtn += keys[i] + "=" + "?";
		}
		return rtn;
	}

	private String generateValuesInParenthesis(Object[] keys, boolean includeQuestionMark) {
		String rtn = "";
		if (includeQuestionMark) {
			for (int i = 0; i < keys.length; i++) {
				if (i > 0)
					rtn = rtn + ", ";
				rtn = rtn + "?";
			}
		} else {
			for (int i = 0; i < keys.length; i++) {
				if (i > 0)
					rtn = rtn + ", ";
				rtn = rtn + keys[i];
			}
		}
		return (String) rtn;
	}
	
	/**
	 * Close all existing connections
	 * @param ps
	 * @param resultset
	 * @param connection
	 */
	private void close(PreparedStatement ps, Statement st, ResultSet rs, Connection con) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * check connection status
	 */
	private void checkConnection() {
		if (this.con == null) {
			System.out.println("error connecting to database");
			System.exit(0);
		}
	}
}
