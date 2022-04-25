import java.sql.*;
import java.util.Scanner;

public class Customer {
    private static String userID, password, address;
    private static Integer phoneNum, accountNum;

    //Connection information (ideally wouldn't use a root account but this is a simple prototype)
    static final String DB_URL = "jdbc:mysql://localhost:3306/booking";
    static final String USER = "root";
    static final String PASS = "Rx5QiQrssvPw9N";

    //Account creation methods
    public void register() {

    }

    public boolean login() {
        //Loop throught the Admin table and check to see if a the adminID matches any entries.
        //If it does, check the password with that specfic one. If both match, validate the user.
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM admin")) {
            while(rs.next()) {
                if(this.userID == rs.getString("customer_id")) {
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

    //Movie related methods
    public static void printMovies() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM movie")) {
        // Extract data from result set
        while (rs.next()) {
            // Retrieve by column name
            System.out.print("Movie #: " + rs.getInt("movie_id"));
            System.out.print("Movie Name: " + rs.getString("movie_name"));
            System.out.print(", Room: " + rs.getInt("room_id"));
            System.out.print(", Start Time: " + rs.getTimestamp("start_time"));
            System.out.print(", Duration: " + rs.getString("duration"));
            System.out.print(", Genre: " + rs.getString("genre"));
            System.out.print(", Available Seats: " + rs.getInt("avaliable_seats") + "\n");
        }
        conn.close();
    } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void bookTicket() {
        //Print movies for user
        printMovies();

        //Take user input
        System.out.print("Please enter the movie's respective #: ");
        Scanner scan = new Scanner(System.in);
        String movie_selection = scan.nextLine();


        int movie_num = Integer.parseInt(movie_selection);

        //Database Querying
        try {
            //Select Movie
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String query = "select * from movie where movie_id = ?";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, movie_num);
            ResultSet rs = preparedStmt.executeQuery();
            rs.next();
            if (rs.getInt("avaliable_seats")==0){
                 System.out.print ("No more available seats!");
                 return;
            }
            int price = rs.getInt("price");

            //Show available seats
            query = "select * from seat where movie_id = ? and occupied = 0";
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, rs.getString("movie_id"));
            rs = preparedStmt.executeQuery();

            System.out.print("Available Seats:\n");
            while (rs.next()) {
                 System.out.print("Seat #: " + rs.getInt("seat_id"));
                 System.out.print(", Row #: " + rs.getInt("row"));
                 System.out.print(", Column #: " + rs.getInt("column") + "\n");
            }

            //Select Seat
            System.out.print("Please enter the seat's respective #: ");
            scan = new Scanner(System.in);
            String seat_selection = scan.nextLine();

            //Select Payment Account
            query = "select * from payment_account where customer_id = ?";
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, accountNum);
            rs = preparedStmt.executeQuery();

            System.out.print("Available Accounts:\n");
            while (rs.next()) {
                System.out.print("Account Index #: " + rs.getInt("payment_id"));
                System.out.print(", Account #: " + rs.getInt("account_number") + "\n");
            }

            System.out.print("Please enter the Account's index (0 to cancel transaction): ");
            String pAcc_selection = scan.nextLine();
            if (pAcc_selection == "0") return;

            //Mark Seat as Occupied
            query = "update seat set occupied = 1 where seat_id = ?";
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, Integer.parseInt(seat_selection));
            preparedStmt.execute();

            //Create transaction
                //generate ticket_id
                query = "select max(ticket_id) as ticket_id from transaction as ticket_id";
                preparedStmt = conn.prepareStatement(query);
                rs = preparedStmt.executeQuery();
                rs.next();
                int ticketID = rs.getInt("ticket_id");
                ticketID++;

            query = "INSERT INTO transaction (ticket_id, customer_id, movie_id, seat, price) values (?, ?, ?, ?, ?)";
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, ticketID);
            preparedStmt.setInt(2, accountNum);
            preparedStmt.setInt(3, Integer.parseInt(movie_selection));
            preparedStmt.setInt(4, Integer.parseInt(seat_selection));
            preparedStmt.setInt(5, price);
            preparedStmt.execute();

            //Close scanner & connection
            scan.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public void changeSeat() {

    }

    public void cancelTicket() {

    }

    public static void main(String[] args) {
        printMovies();
        accountNum = 1;
        bookTicket();
    }
}
