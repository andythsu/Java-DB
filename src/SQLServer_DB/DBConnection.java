package SQLServer_DB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @Author: Andy Su
 * @Date: Mar 12, 2018 
 * @Description: 
 */

public class DBConnection {
	String DBURL = "";
	String userName = "";
	String password = "";
	protected Connection con;
	
	public DBConnection(String url, String username, String password) {
		this.DBURL = url;
		this.userName = username;
		this.password = password;
		this.initConnection();
	}
	
	private void initConnection() {
		try {
			// step1 load the driver class
			loadDriver();

			// step2 create the connection object
			Connection con = connect();

			// retry if connection fails
			if (con == null)
				retry();

			// once connected, run the ServiceClass
			if (con != null) {
				System.out.println("connected successfully");
				this.con = con;
			}
		} catch (Exception e) {
			System.out.println("Unhandled exception, attempting to recover");
			System.out.println(e);
		}
	}

	public Connection getConnection() {
		return this.con;
	}

	private Connection connect() {
		Connection con = null;
		try {
			con = DriverManager.getConnection(DBURL, userName, password);
		} catch (SQLException s) {
			System.out.println("Database access error on dababase:");
			System.out.println(DBURL);
			System.out.println("Dumping exception text and terminating");
			System.out.println(s);
		}
		return con;
	}

	private Connection retry() {
		Connection con = null;
		for (int i = 1; i < 4 && con == null; ++i) {
			System.out.println("Failed to connect to DB. Retrying (" + i + "/3)");
			con = connect();
			if (con != null)
				break;
			System.out.println("Recovered connection, resuming operations.");
		}
		if (con == null) {
			System.out.println("Retry failed.");
		}
		return con;
	}

	private void loadDriver() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException c) {
			System.out.println(c);
		}
	}
}
