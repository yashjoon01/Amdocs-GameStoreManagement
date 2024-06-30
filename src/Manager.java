import java.sql.*;
import java.util.Scanner;

public class Manager {
    int userId;
    Scanner scanner;
    Connection con;

    Manager(Scanner sc, Connection ct, int id){
        scanner = sc ;
        con = ct;
        userId = id;
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

    public void addEmployee() throws SQLException {
        String query = "INSERT INTO Users (username, password, user_type) VALUES (?,?,?)";
        PreparedStatement pst = con.prepareStatement(query);

        while(true){
            System.out.print("Username : ");
            String username = scanner.nextLine();

            if(username.isEmpty()){
                System.out.println("False Input! Retry.");
                continue;
            }

            System.out.print("Set Password : ");
            String password = scanner.nextLine();

            if(password.isEmpty()){
                System.out.println("False Input! Retry.");
                continue;
            }

            String user_type = "sales_employee";

            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, user_type);

            int cnt = pst.executeUpdate();
            if(cnt > 0){
                System.out.println("Employee Created !");
                scanner.nextLine();
                break;
            }
            else  System.out.println("Please enter valid username and password !");
        }
    }
    public String checkType(int id) {
        String userType = "blank"; // Default value in case of incorrect credentials or no result

        String query = "SELECT user_type FROM users WHERE user_id = " + id;
        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    userType = rs.getString("user_type");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userType;
    }

    public void viewSales() throws SQLException{
        String sql = "SELECT s.sale_id, s.sale_date, s.quantity, s.total_price, g.title, u.username " +
                "FROM sales s " +
                "JOIN games g ON s.game_id = g.game_id " +
                "JOIN users u ON s.user_id = u.user_id";

        Statement smt = con.createStatement();

        ResultSet rs = smt.executeQuery(sql);

        while(rs.next()){
            System.out.print("Sale ID - " + rs.getInt(1) + " | ");
            System.out.print("Sale Date - " + rs.getDate(2) + " | ");
            System.out.print("Quantity - " + rs.getInt(3) + " | ");
            System.out.print("Total Price - " + rs.getFloat(4) + " | ");
            System.out.print("Game Title - " + rs.getString(5) + " | ");
            System.out.println("Customer Name - " + rs.getString(6));
        }
        scanner.nextLine();
    }

    public void viewUsers() throws SQLException{
        String sql = "SELECT user_id, username, user_type from users";

        Statement smt = con.createStatement();

        ResultSet rs = smt.executeQuery(sql);

        while(rs.next()){
            System.out.print("User ID - " + rs.getInt(1) + " | ");
            System.out.print("Username - " + rs.getString(2) + " | ");
            System.out.println("User Type - " + rs.getString(3));
        }
        scanner.nextLine();
    }
    public void deleteUser() throws SQLException {

        Statement smt = con.createStatement();
        int id = -1;

        viewUsers();

        while(true){

            System.out.print("Enter customer / employee id whose record is to be deleted : ");


            String ip = scanner.nextLine();
            try{
                id = Integer.parseInt(ip);
            }catch (NumberFormatException e){
                System.out.println("Wrong input !\n");
                continue;
            }


            String userType = checkType(id);

            if(userType.equals("manager")){
                System.out.println("Not Allowed to delete this user !");
                continue;
            }

            try{
                int cnt = smt.executeUpdate("delete from users where user_id = " + id);
                if(cnt > 0) System.out.println("Deleted user with id : " + id);
                else            System.out.println("Wrong Id provided !");
            }catch(SQLIntegrityConstraintViolationException e){
                System.out.println(e.getMessage());
            }


            System.out.println("Want to delete more ? (yes/no)");
            String ch = scanner.nextLine();

            if(ch.equalsIgnoreCase("no"))   break;
        }
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
                System.out.println("\nProfile Updated!\n");
                break;
            } else {
                System.out.println("Please enter valid username and password!");
            }
        }
    }

    public void viewRequest() throws SQLException {
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

    private boolean isValidGameId(int gameId) {
        boolean isValid = false;
        try {
            Statement smt = con.createStatement();
            String query = "SELECT COUNT(*) AS count FROM Games WHERE game_id = " + gameId;

            ResultSet rs = smt.executeQuery(query);

            if (rs.next() && rs.getInt("count") > 0) {
                isValid = true;
            }
            rs.close();
            smt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isValid;
    }
    public void updateGame(int game_id) throws SQLException {
        String query = "UPDATE games SET price = ?, stock_quantity = ? WHERE game_id = ?";

        PreparedStatement pst = con.prepareStatement(query);

        while (true) {
            System.out.print("Enter price: ");

            String ip = scanner.nextLine();
            float price = -1;
            try{
                price = Float.parseFloat(ip);
            }catch (NumberFormatException e){
                System.out.println("Wrong input !\n");
                continue;
            }

            System.out.print("Enter stock_quantity: ");
            ip = scanner.nextLine();
            int stock_quantity = -1;
            try{
                stock_quantity = Integer.parseInt(ip);
            }catch (NumberFormatException e){
                System.out.println("Wrong input !\n");
                continue;
            }

            pst.setFloat(1, price);
            pst.setInt(2, stock_quantity);
            pst.setInt(3, game_id);

            int cnt = 0;
            try {
                cnt = pst.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException e) {
                System.out.println("\nEnter Unique Games!\n");
                continue;
            }

            if (cnt > 0) {
                System.out.println("\nGame Updated!\n");
                break;
            } else {
                System.out.println("Please enter valid stock quantity and price!");
            }
        }
    }

    private boolean isValidRequestId(int reqId) {
        boolean isValid = false;
        try {
            Statement smt = con.createStatement();
            String query = "SELECT COUNT(*) FROM requests WHERE request_id = " + reqId;

            ResultSet rs = smt.executeQuery(query);

            if (rs.next() && rs.getInt(1) > 0) {
                isValid = true;
            }
            rs.close();
            smt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isValid;
    }
    public void processRequest() throws SQLException{
        viewRequest();
        int id;

        while(true){
            System.out.print("Choose the request ID you want to process - ");
            String ip = scanner.nextLine();
            try{
                id = Integer.parseInt(ip);
                if(isValidRequestId(id))    break;
            }catch (NumberFormatException e){
                System.out.println("Wrong input !\n");
            }
        }

        int choice;

        while(true){
            try{
                System.out.print("Approve(1) or deny(2) the request : ");
                String ip = scanner.nextLine();
                choice = Integer.parseInt(ip);
                if(choice == 1 || choice == 2)
                    break;
            }catch (NumberFormatException e){
                System.out.println("Wrong input !\n");
            }
        }

        String query = "";
        if(choice == 1){
            query = "Update requests set status = 'approved' where request_id = " + id;
            System.out.println("Request Approved !\n");
        }
        else{
            query = "Update requests set status = 'denied' where request_id = " + id;
            System.out.println("Request Denied !\n");
        }

        Statement smt = con.createStatement();
        smt.executeQuery(query);
    }
    public void displayManagerView() throws SQLException {
        boolean logout = false;
        while (!logout) {
            System.out.println("\nManager Menu:");
            System.out.println("1. Add New Game");
            System.out.println("2. Update Game");
            System.out.println("3. View Games List");
            System.out.println("4. Add New Employee");
            System.out.println("5. Update Profile");
            System.out.println("6. Display user details");
            System.out.println("7. Remove User");
            System.out.println("8. View Sales Report");
            System.out.println("9. View Pre - Bookings");
            System.out.println("10. View Requests by Customers");
            System.out.println("11. Process Customer Request");
            System.out.println("12. Logout\n");
            System.out.print("Enter your choice: ");


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
                    System.out.println("Adding a new game...");
                    addGame();
                    break;
                case 2:
                    System.out.println("Update Game Details...");
                    System.out.print("Enter game id : ");

                    ip = scanner.nextLine();
                    choice = -1;
                    try{
                        choice = Integer.parseInt(ip);
                    }catch (NumberFormatException e){
                        System.out.println("Wrong input !\n");
                        continue;
                    }

                    if(!isValidGameId(choice)){
                        System.out.println("Invalid ID !");
                        continue;
                    }

                    updateGame(choice);
                    break;
                case 3:
                    System.out.println("Displaying Games...");
                    showGames();
                    break;
                case 4:
                    System.out.println("Adding a new Employee...");
                    addEmployee();
                    break;
                case 5:
                    System.out.println("Update Your Profile...");
                    updateProfile();
                    break;
                case 6:
                    System.out.println("Displaying users...");
                    viewUsers();
                    break;
                case 7:
                    System.out.println("Removing a customer...");
                    deleteUser();
                    break;
                case 8:
                    System.out.println("Viewing sales report...");
                    viewSales();
                    break;
                case 9:
                    viewPrebooking();
                    break;
                case 10:
                    System.out.println("Viewing request by customers...");
                    viewRequest();
                    break;
                case 11:
                    System.out.println("Request Processing Window...");
                    processRequest();
                    break;
                case 12:
                    System.out.println("\nLogged Out !\n");
                    logout = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
