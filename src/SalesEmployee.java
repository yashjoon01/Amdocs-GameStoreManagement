import java.sql.*;
import java.util.Scanner;

public class SalesEmployee {

    int userId;
    Scanner scanner;
    Connection con;

    SalesEmployee(Scanner sc, Connection ct, int id){
        scanner = sc ;
        con = ct;
        userId = id;
    }

    public void viewPrebooking() throws SQLException {
        String sql = "SELECT p.prebooking_id, p.booking_date, g.title, u.username " +
                "FROM prebookings p " +
                "JOIN games g ON p.game_id = g.game_id " +
                "JOIN users u ON p.user_id = u.user_id";

        Statement smt = con.createStatement();

        ResultSet rs = smt.executeQuery(sql);

        while(rs.next()){
            System.out.print("Prebooking ID - " + rs.getInt(1) + " | ");
            System.out.print("Booking Date - " + rs.getDate(2) + " | ");
            System.out.print("Game Title - " + rs.getString(3) + " | ");
            System.out.println("Customer Name - " + rs.getString(4));
        }
        scanner.nextLine();
    }
    public void showGames() throws SQLException {
        Statement smt = con.createStatement();

        ResultSet rs = smt.executeQuery("select * from games");

        while(rs.next()){
            System.out.print("Game ID - " + rs.getInt(1) + " | ");
            System.out.print("Title - " + rs.getString(2) + " | ");
            System.out.print("Genre - " + rs.getString(3) + " | ");
            System.out.print("Price - " + rs.getFloat(4) + " | ");
            System.out.print("Quantity - " + rs.getInt(5) + " | ");
            System.out.println("Disc Type - " + rs.getString(6));
        }
        scanner.nextLine();
    }

    public void showRequests() throws SQLException {
        String sql = "SELECT r.request_id, r.game_title, r.request_date, r.status, u.username " +
                "FROM requests r " +
                "JOIN users u ON r.user_id = u.user_id";

        Statement smt = con.createStatement();

        ResultSet rs = smt.executeQuery(sql);

        while(rs.next()){
            System.out.print("Request ID - " + rs.getInt(1) + " | ");
            System.out.print("Game-title - " + rs.getString(2) + " | ");
            System.out.print("Request Date - " + rs.getDate(3) + " | ");
            System.out.print("Status - " + rs.getString(4) + " | ");
            System.out.println("Customer Name - " + rs.getString(5));
        }
        scanner.nextLine();
    }

    public void updateProfile() throws SQLException {
        String query = "UPDATE Users SET username = ?, password = ? WHERE user_id = ?";
        PreparedStatement pst = con.prepareStatement(query);

        while (true) {
            System.out.print("Enter new Username: ");
            String newUsername = scanner.nextLine();

            if(newUsername.isEmpty()){
                System.out.println("False Input! Retry.");
                continue;
            }

            System.out.print("Enter new Password: ");
            String newPassword = scanner.nextLine();

            if(newPassword.isEmpty()){
                System.out.println("False Input! Retry.");
                continue;
            }

            pst.setString(1, newUsername);
            pst.setString(2, newPassword);
            pst.setInt(3, userId);

            int cnt = 0;
            try {
                cnt = pst.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException e) {
                System.out.println("\nEnter Unique Username!\n");
                continue;
            }

            if (cnt > 0) {
                System.out.println("\nProfile Updated!");
                break;
            } else {
                System.out.println("Please enter valid username and password!");
            }
        }
    }

    public void addGame() throws SQLException {

        String query = "INSERT INTO games (title, genre, price, stock_quantity, disc_type) VALUES (?,?,?,?,?)";
        PreparedStatement pst = con.prepareStatement(query);

        while(true){
            System.out.print("Title : ");
            String title = scanner.nextLine();

            if(title.isEmpty()){
                System.out.println("False Input! Retry.");
                continue;
            }

            System.out.print("Genre : ");
            String genre = scanner.nextLine();

            if(genre.isEmpty()){
                System.out.println("False Input! Retry.");
                continue;
            }

            System.out.print("Price : ");
            String ip = scanner.nextLine();

            Float price;
            try{
                price = Float.parseFloat(ip);
            }catch (NumberFormatException e){
                System.out.println("False Input! Retry.");
                continue;
            }

            System.out.print("Quantity : ");
            ip = scanner.nextLine();
            Integer quantity;
            try{
                quantity = Integer.parseInt(ip);
            }catch (NumberFormatException e){
                System.out.println("False Input! Retry.");
                continue;
            }

            System.out.print("Disc Type(brand_new/ second_hand) : ");
            String discType = scanner.nextLine();

            if(!(discType.equals("brand_new") || discType.equals("second_hand"))){
                System.out.println("False Input! Retry.");
                continue;
            }
            pst.setString(1, title);
            pst.setString(2, genre);
            pst.setFloat(3, price);
            pst.setInt(4, quantity);
            pst.setString(5, discType);

            int cnt = pst.executeUpdate();
            if(cnt > 0){
                System.out.println("Game Inserted !");
                break;
            }
            else  System.out.println("Please enter valid game details !");
        }
    }

    public void seePayments(int user_id) throws SQLException {
        String sql = "SELECT payment_id, amount, payment_date from payments where user_id = " + user_id;

        Statement smt = con.createStatement();

        ResultSet rs = smt.executeQuery(sql);

        while(rs.next()){
            System.out.print("Payment ID - " + rs.getInt(1) + " | ");
            System.out.print("Amount - " + rs.getString(2) + " | ");
            System.out.println("Payment Date - " + rs.getDate(3));
        }
        scanner.nextLine();
    }

    public boolean isValidCustomerID(int customer_id){

        boolean isValid = false;
        try {
            Statement smt = con.createStatement();
            String query = "SELECT COUNT(*) AS count FROM Users WHERE user_id = " + customer_id + " AND user_type = 'customer'";

            ResultSet rs = smt.executeQuery(query);

            if (rs.next() && rs.getInt("count") > 0) {
                isValid = true;
            }
            else{
                System.out.println("No record Found !");
            }
            rs.close();
            smt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isValid;

    }
    public void displayEmployeeView() throws SQLException {
        boolean logout = false;
        while (!logout) {
            System.out.println("\nSales Employee Menu:");

            System.out.println("1. View Available Games");
            System.out.println("2. View Customer Requests");
            System.out.println("3. Update Profile");
            System.out.println("4. View Pre - bookings");
            System.out.println("5. Add New Game");
            System.out.println("6. Add Game Request");
            System.out.println("7. View Customer Payment Record");
            System.out.println("8. Logout");

            System.out.print("\nEnter your choice: ");

            String ip = scanner.nextLine();
            int choice = -1;
            try{
                choice = Integer.parseInt(ip);
            }catch (NumberFormatException e){
                System.out.println("Wrong input !\n");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.println("\nViewing available games...\n");
                    showGames();
                    break;
                case 2:
                    System.out.println("\nViewing customer requests...\n");
                    showRequests();
                    break;
                case 3:
                    System.out.println("\nUpdate Profile...\n");
                    updateProfile();
                    break;
                case 4:
                    System.out.println("\nView Pr-booking...\n");
                    viewPrebooking();
                    break;
                case 5:
                    System.out.println("Adding a new game...");
                    addGame();
                    break;
                case 6:
                    System.out.println("Requesting a game...\n");
                    Request request = new Request(con, userId, scanner);
                    request.display();
                    break;
                case 7:
                    System.out.println("\nViewing payment record...\n");
                    System.out.print("Enter customer id : ");

                    ip = scanner.nextLine();
                    choice = -1;
                    try{
                        choice = Integer.parseInt(ip);
                    }catch (NumberFormatException e){
                        System.out.println("Wrong input !\n");
                        continue;
                    }

                    if(!isValidCustomerID(choice))
                        continue;

                    seePayments(choice);
                    break;
                case 8:
                    System.out.println("\nLogged Out !\n");
                    logout = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
