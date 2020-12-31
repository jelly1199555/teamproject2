package DAY12_29test1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatDao extends ChatDB {

	public ChatDao() {
		super();
	}
	   public void writeLogin(String user_id) {
		      String sql = "INSERT INTO login_table(user_id, login_dt)VALUES(?,SYSDATE)";
		      Connection conn = null;
		      PreparedStatement psmt = null;
		      ResultSet rs = null;
		      try {
		         conn = getConnection();
		         psmt = conn.prepareStatement(sql);

		         int i = 1;
		         psmt.setString(i++, user_id);

		         psmt.executeUpdate();
		      } catch (SQLException e) {
		         e.printStackTrace();
		      } finally {
		         close(conn, psmt, null);
		      }
		   }
	   
	   
	   
	public void writeData(String user_id, String login_dt, String memo) {
		//String sql = "INSERT INTO login_table(user_id,login_dt,memo)VALUES(?,?,?,?)";
		String sql = "INSERT INTO LOGIN_TABLE VALUES(?,?,?)";
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			psmt = conn.prepareStatement(sql);

			int i = 1;
			psmt.setString(i++, user_id);
			psmt.setString(i++, login_dt);
			psmt.setString(i++, memo);

			psmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn, psmt, null);
		}
	}

	public String readData() {
		String sql = "select * from login_table order by user_id";
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;

		StringBuilder list = new StringBuilder();

		try {
			conn = getConnection();
			psmt = conn.prepareStatement(sql);
			rs = psmt.executeQuery();

			while (rs.next()) {
				list.append("유저 아이디 : " + rs.getString("user_id") + ", 로그인 날짜 : " + rs.getString("login_dt")
						+ ", 비고 : ");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn, psmt, null);
		}
		return list.toString();
	}
}
