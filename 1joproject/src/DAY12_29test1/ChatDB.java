package DAY12_29test1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ChatDB {
	String url = "jdbc:oracle:thin:@localhost:1521:xe"; // localhost대신 ip주소가 들어갈수도
	String id = "hr";
	String pw = "hr";

	public ChatDB() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		 conn = DriverManager.getConnection(url, id, pw);
		return conn;
	}

	public void close(Connection conn, Statement stmt, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
	}
}
