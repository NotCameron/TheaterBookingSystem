import java.sql.*;
import java.util.Scanner;

public class Admin {
    private static String adminID="nope";
    private static String password="nothing";

    static final String DB_URL = "jdbc:mysql://localhost:3306/booking";
    static final String USER = "root";
    static final String PASS = "Rx5QiQrssvPw9N";

    public static int generateMID() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            //Retrieve highest movie_id value
            ResultSet rs = stmt.executeQuery("SELECT max(movie_id) as movie_id FROM movie");
            rs.next();
            int MID = rs.getInt("movie_id");
            //Add 1
            MID++;
            //Return max(movie_id)+1
            return MID;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void printMovies() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM movie")) {
            // Extract data from result set
            while (rs.next()) {
                // Retrieve by column name
                System.out.print("Movie #: " + rs.getInt("movie_id"));
                System.out.print(", Movie Name: " + rs.getString("movie_name"));
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

    public static void printTransactions() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM transaction ")) {
            // Extract data from result set
            while (rs.next()) {
                // Retrieve by column name
                System.out.print("Ticket #: " + rs.getInt("ticket_id"));
                System.out.print(", Customer Id: " + rs.getInt("customer_id"));
                System.out.print(", Movie Id: " + rs.getInt("movie_id"));
                System.out.print(", Seat Id: " + rs.getInt("seat"));
                System.out.print(", Price: " + rs.getInt("price") + "\n");
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean validateAdmin() {
        //Loop throught the Admin table and check to see if a the adminID matches any entries.
        //If it does, check the password with that specfic one. If both match, validate the user.
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM admin")) {
            while(rs.next()) {
                if(adminID.equals(rs.getString("name"))) {
                    if(password.equals(rs.getString("password"))) {
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
    public static void addMovie(String movieName, int RID, Timestamp time, String duration, String genre, int seats, int price) {
        // Create a movie entry and add it to the movies table
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //Generate MID
            int MID = generateMID();

            //Insert into table
            String query = "insert into movie (movie_id, room_id, start_time, duration, genre, avaliable_seats, movie_name, price)" + " values (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, MID);
            preparedStmt.setInt(2, RID);
            preparedStmt.setTimestamp(3, time);
            preparedStmt.setString(4, duration);
            preparedStmt.setString(5, genre);
            preparedStmt.setInt(6, seats);
            preparedStmt.setString(7, movieName);
            preparedStmt.setInt(8, price);
            preparedStmt.execute();
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateMovie(int MID, Timestamp time) {
        //loop through the entries in the Movie table until the MID matches one (or if none match, exit the method)
        //If movie found: carry out the update
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

    public static void removeMovie(int MID) {
        //loop through the entries in the Movie table til the MID matches one (or if none match, exit the method)
        //If movie found, remove it
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

    public static void refundTransaction(int TID) {
        //loop through entries in the Transaction table til the TID matches one (or if none match, exit the method)
        try  {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            //Find Seat
            String query = "select * from transaction where ticket_id = ?";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, TID);
            ResultSet rs = preparedStmt.executeQuery();
            rs.next();
            int MID = rs.getInt("movie_id");
            int SID = rs.getInt("seat");

            //Unoccupy
            query = "update seat set occupied = 0 where seat_id = ? and movie_id = ?";
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, SID);
            preparedStmt.setInt(2, MID);
            preparedStmt.execute();

            //delete transaction
            query = "delete from transaction where ticket_id = ?";
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, TID);
            preparedStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)  {
        int MID, RID, TID, seats, price;
        Timestamp time;
        String movieName, MIDstr, RIDstr, TIDstr, seatsStr, priceStr, timeStr, duration, genre, option;
        Scanner scan = new Scanner(System.in);

        //validate an admin is signed in
        while (!validateAdmin()) {
            System.out.print("Username: ");
            adminID = scan.nextLine();
            System.out.print("Password: ");
            password = scan.nextLine();
        }

        //Function selection menu
        while(true) {
            System.out.print("Functions:\n");
            System.out.print("1: Add movie\n");
            System.out.print("2: Update movie\n");
            System.out.print("3: Remove movie\n");
            System.out.print("4: Refund movie\n");
            System.out.print("5: Print Movies\n");
            System.out.print("6: Print Transactions\n");
            System.out.print("7: Exit program\n");
            System.out.print("Please select a function:\n");
            option = scan.nextLine();
            switch (option) {

                case "1": //add movie
                    //User input
                    System.out.print("Movie name: ");
                    movieName = scan.nextLine();
                    System.out.print("Room #: ");
                        RIDstr = scan.nextLine();
                        RID = Integer.parseInt(RIDstr);
                    System.out.print("Timestamp (yyyy-mm-dd hh:mm:ss): ");
                        timeStr = scan.nextLine();
                        time = Timestamp.valueOf(timeStr);
                    System.out.print("Duration (x hours, y minutes): ");
                        duration = scan.nextLine();
                    System.out.print("Genre: ");
                        genre = scan.nextLine();
                    System.out.print("Available seats: ");
                        seatsStr = scan.nextLine();
                        seats = Integer.parseInt(seatsStr);
                    System.out.print("Price: ");
                        priceStr = scan.nextLine();
                        price = Integer.parseInt(priceStr);
                    addMovie(movieName, RID, time, duration, genre, seats, price);
                    break;
                case "2": //update movie
                    System.out.print("Movie Id: ");
                    MIDstr = scan.nextLine();
                    MID = Integer.parseInt(MIDstr);
                    //User input
                    System.out.print("Timestamp (yyyy-mm-dd hh:mm:ss): ");
                        timeStr = scan.nextLine();
                        time = Timestamp.valueOf(timeStr);
                    updateMovie(MID, time);
                    break;
                case "3": //remove movie
                    //User input
                    System.out.print("Movie Id: ");
                    MIDstr = scan.nextLine();
                    MID = Integer.parseInt(MIDstr);
                    removeMovie(MID);
                    break;
                case "4": //refund movie
                    //Print transaction history
                    printTransactions();
                    //User Input
                    System.out.print("Transaction #: ");
                    TIDstr = scan.nextLine();
                    TID = Integer.parseInt(TIDstr);

                    refundTransaction(TID);
                    break;
                case "5": //print movies
                    printMovies();
                    break;
                case "6"://print transactions
                    printTransactions();
                    break;
                case "7": //exit program
                    System.out.print("Exiting...");
                    return;
                default: //if invalid option selected
                    System.out.print("Invalid Input! Select a function (1-5)");
            }
        }
    }
}
