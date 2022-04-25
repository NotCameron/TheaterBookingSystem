import java.sql.*;

public class Customer {
    private String userID, password, address;
    private Integer phoneNum, accountNum;

    //Connection information (ideally wouldn't use a root account but this is a simple prototype)
    static final String DB_URL = "jdbc:mysql://localhost:3306/booking";
    static final String USER = "root";
    static final String PASS = "Rx5QiQrssvPw9N";
    static String QUERY = "";

    //Account creation methods
    public void register() {

    }

    public void login() {

    }

    //Movie related methods
    public static void printMovies() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM movie")) {
        // Extract data from result set
        while (rs.next()) {
            // Retrieve by column name
            System.out.print("Movie #: " + rs.getInt("movie_id"));
            System.out.print(", Room: " + rs.getInt("room_id"));
            System.out.print(", Start Time: " + rs.getTimestamp("start_time"));
            System.out.print(", Duration: " + rs.getString("duration"));
            System.out.print(", Genre: " + rs.getString("genre"));
            System.out.print(", Available Seats: " + rs.getInt("avaliable_seats"));
        }
    } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void bookTicket() {

    }

    public void changeSeat() {

    }

    public void cancelTicket() {

    }

    public static void main(String[] args) {
        printMovies();
    }
}
