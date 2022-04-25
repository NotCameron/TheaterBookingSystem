package assignments;

import java.sql.*;

public class Admin {
	private int adminID;
	private String password;
	private boolean valid;
	
	static final String DB_URL = "jdbc:mysql://localhost:3306/booking";
	static final String USER = "root";
	static final String PASS = "Rx5QiQrssvPw9N";
	
	public Admin(int nID, String nPassword) {
		adminID = nID;
		password = nPassword;
		valid = validateAdmin();
	}
	
	public boolean validateAdmin() {
		//Loop throught the Admin table and check to see if a the adminID matches any entries. 
		//If it does, check the password with that specfic one. If both match, validate the user.
		try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM admin")) {
			while(rs.next()) {
				if(this.adminID == rs.getInt("admin_id")) {
					if(this.password.equals(rs.getString("password"))) {
						return true;
					}
				}
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	public void addMovie(int MID, int RID, Timestamp time, int duration, String genre, int seats) {
		// Create a movie entry and add it to the movies table
		if(!valid) return;
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			String query = "insert into movie (movie_id, room_id, start_time, duration, genre, seats)" + " values (?, ?, ?, ?, ?)";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setInt(1, MID);
			preparedStmt.setInt(2, RID);
			preparedStmt.setTimestamp(3, time);
			preparedStmt.setInt(4, duration);
			preparedStmt.setString(5, genre);
			preparedStmt.setInt(6, seats);
			preparedStmt.execute();
			conn.close();
	    }
	    catch (SQLException e) {
	    	e.printStackTrace();
	    }
	}
	
	public void updateMovie(int MID, Timestamp time) {
		//loop through the entries in the Movie table until the MID matches one (or if none match, exit the method)
		//If movie found: carry out the update
		if(!valid) return;
		try  {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			String query = "update movie set start_time = ? where movie_id = ?";
		    PreparedStatement preparedStmt = conn.prepareStatement(query);
		    preparedStmt.setTimestamp(1, time);
		    preparedStmt.setInt(2, MID);
		    preparedStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeMovie(int MID) {
		//loop through the entries in the Movie table til the MID matches one (or if none match, exit the method)
		//If movie found, remove it
		if(!valid) return;
		try  {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			String query = "delete from movie where movie_id = ?";
		    PreparedStatement preparedStmt = conn.prepareStatement(query);
		    preparedStmt.setInt(1, MID);
		    preparedStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void refundTransaction(int TID) {
		//loop through entries in the Transaction table til the TID matches one (or if none match, exit the method)
		if(!valid) return;
		try  {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			String query = "delete from transaction where ticket_id = ?";
		    PreparedStatement preparedStmt = conn.prepareStatement(query);
		    preparedStmt.setInt(1, TID);
		    preparedStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
