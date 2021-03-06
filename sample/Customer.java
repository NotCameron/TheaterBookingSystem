import java.sql.*;
import java.util.Scanner;

public class Customer {
    private static String userID, password, address, email;
    private static Integer phoneNum, accountNum;
    private static boolean loggedin = false;

    //Connection information (ideally wouldn't use a root account but this is a simple prototype)
    static final String DB_URL = "jdbc:mysql://localhost:3306/booking";
    static final String USER = "root";
    static final String PASS = "Rx5QiQrssvPw9N";

    //Account creation methods
    public static void register() {
        Scanner scan = new Scanner(System.in);
        userID = scan.nextLine();
        password = scan.nextLine();
        email = scan.nextLine();
        String phoneNumStr = scan.nextLine();
        phoneNum = Integer.parseInt(phoneNumStr);
        address = scan.nextLine();

        //Insert into Database
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //generate customer_id
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT max(customer_id) as customer_id FROM customer");
            rs.next();
            int custID = rs.getInt("customer_id");
            custID++;

            //insert customer
            String query = "insert into customer (customer_id, name, password, email, address, phone_number) values (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, custID);
            preparedStmt.setString(2, userID);
            preparedStmt.setString(3, password);
            preparedStmt.setString(4, email);
            preparedStmt.setString(5, address);
            preparedStmt.setInt(1, phoneNum);
            preparedStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean login() {
        //Loop throught the User table and check to see if a the adminID matches any entries.
        //If it does, check the password with that specfic one. If both match, validate the user.
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customer")) {
            while(rs.next()) {
                if(userID.equals(rs.getString("name"))) {
                    if(password.equals(rs.getString("password"))) {
                        accountNum=rs.getInt("customer_id");
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

    //Prints transactions relevant to customer
    public static void printTransactions() {
        try {Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             String query = "SELECT * FROM transaction where customer_id = ?"; //filters out other customers
             PreparedStatement preparedStmt = conn.prepareStatement(query);
             preparedStmt.setInt(1, accountNum);
             ResultSet rs = preparedStmt.executeQuery();

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

    public static void changeSeat() {
        printTransactions();
        //User Input
        Scanner scan = new Scanner(System.in);
        System.out.print("Ticket #: ");
        String TIDstr = scan.nextLine();
        int TID = Integer.parseInt(TIDstr);
        System.out.print("New Seat #: ");
        String newSeatStr = scan.nextLine();
        int newSeat = Integer.parseInt(newSeatStr);

        try  {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //Unoccupy seat
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

            //Occupy new seat
            query = "update seat set occupied = 1 where seat_id = ? and movie_id = ?";
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, newSeat);
            preparedStmt.setInt(2, MID);
            preparedStmt.execute();

            //update transaction
            query = "update transaction set seat = ? where ticket_id = ? and movie_id = ?";
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, newSeat);
            preparedStmt.setInt(2, TID);
            preparedStmt.setInt(3, MID);
            preparedStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void cancelTicket() {
        printTransactions();
        //User Input
        Scanner scan = new Scanner(System.in);
        System.out.print("Ticket #: ");
        String TIDstr = scan.nextLine();
        int TID = Integer.parseInt(TIDstr);

        try  {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //Unoccupy seat
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

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String option;
        while (!loggedin) {
            System.out.print("Log In Screen:\n");
            System.out.print("1: Register Account\n");
            System.out.print("2: Log In\n");
            System.out.print("3: Exit program\n");
            System.out.print("Please select a function:\n");
            option = scan.nextLine();
            switch (option) {
                case "1": //register account
                    register();
                    break;
                case "2": //log in
                    System.out.print("Username: ");
                    userID = scan.nextLine();
                    System.out.print("Password: ");
                    password = scan.nextLine();
                    loggedin = login();
                    break;
                case "3": //exit program
                    System.out.print("Exiting...");
                    return;
                default: //if invalid option selected
                    System.out.print("Invalid Input! Select a function (1-5)");
            }
        }

        while (true) {
            System.out.print("Functions:\n");
            System.out.print("1: Print Movies\n");
            System.out.print("2: Book Ticket\n");
            System.out.print("3: Change Seat\n");
            System.out.print("4: Cancel Ticket\n");
            System.out.print("5: Exit program\n");
            System.out.print("Please select a function:\n");
            option = scan.nextLine();
            switch (option) {
                case "1": //print movies
                    printMovies();
                    break;
                case "2": //book ticket
                    bookTicket();
                    break;
                case "3": //change seat
                    changeSeat();
                    break;
                case "4": //cancel ticket
                    cancelTicket();
                    break;
                case "5": //exit program
                    System.out.print("Exiting...");
                    return;
                default: //if invalid option selected
                    System.out.print("Invalid Input! Select a function (1-5)");
            }
        }
    }
}
