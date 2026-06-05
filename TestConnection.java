package gui;

import java.sql.*;

public class TestConnection {
	public static void main(String[] args) throws SQLException{
		Connection conn = DatabaseConnection.getConnection();
		System.out.println("Connected Successfully!\n");
		
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM Investor ORDER BY InvestorID");
		
		System.out.printf("%s %s %s %s %s %n", "InvestorID", "FirstName", "LastName", "Email", "Phone");
		System.out.println("-".repeat(77));
		
		while (rs.next()) {
			System.out.printf("%d %s %s %s %s", 
					rs.getInt("InvestorID"),
					rs.getString("FirstName"),
					rs.getString("LastName"),
					rs.getString("Email"),
					rs.getString("Phone"));
		}
		rs.close();
		st.close();
		DatabaseConnection.closeConnection();
	}
}
