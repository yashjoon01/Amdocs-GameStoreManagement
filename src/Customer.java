import java.sql.*;
import java.util.Scanner;

public class Customer {

    int userId;
    Scanner scanner;
    Connection con;

    Customer(Scanner sc, Connection ct, int id){
        scanner = sc ;
        con = ct;
        userId = id;
    }

    public void viewCart() throws SQLException {
        Statement smt = con.createStatement();

        String sql = "SELECT c.cart_id, g.title, c.quantity " +
                "FROM cart c " +
                "JOIN games g ON c.game_id = g.game_id where user_id = '"+ userId +"'";
        ResultSet rs = smt.executeQuery(sql);

        if(!rs.next()){
            System.out.println("0 Items in the cart !");
        }

        rs = smt.executeQuery(sql);
        while(rs.next()){
            System.out.print("Cart ID - " + rs.getInt(1) + " | ");
            System.out.print("Game Name - " + rs.getString(2) + " | ");
            System.out.println("Quantity - " + rs.getInt(3) + "\n");
        }
    }

    public void viewPreviousPayments() throws SQLException {
        Statement smt = con.createStatement();

        String sql = "SELECT payment_id, amount, payment_date from payments where user_id = " + userId;
        ResultSet rs = smt.executeQuery(sql);

        if(!rs.next()){
            System.out.println("0 Payments made !");
        }

        rs = smt.executeQuery(sql);
        while(rs.next()){
            System.out.print("Payment ID - " + rs.getInt(1) + " | ");
            System.out.print("Amount - " + rs.getFloat(2) + " | ");
            System.out.println("Payment Date - " + rs.getDate(3));
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
                System.out.println("\nProfile Updated!\n");
                break;
            } else {
                System.out.println("Please enter valid username and password!");
            }
        }
    }
    public void displayCustomerView() throws SQLException {
        boolean logout = false;
        while (!logout) {
            System.out.println("\nCustomer Menu:");
            System.out.println("1. View Cart");
            System.out.println("2. Rent a Game");
            System.out.println("3. Pre-book a Game");
            System.out.println("4. Buy a Game");
            System.out.println("5. Request a Game");
            System.out.println("6. Update Profile");
            System.out.println("7. View Previous Payments");
            System.out.println("8. Checkout ");
            System.out.println("9. Logout");
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
                    System.out.println("\nViewing cart...");
                    viewCart();
                    break;
                case 2:
                    System.out.println("Renting a game...");
                    GameRenting gameRenting = new GameRenting(con, scanner, userId);
                    gameRenting.showMenu();
                    break;
                case 3:
                    System.out.println("Pre-booking a game...");
                    PreBooking preBooking = new PreBooking(con, scanner, userId);
                    preBooking.showMenu();
                    break;
                case 4:
                    System.out.println("Buying a game...\n");
                    Buy buy = new Buy(con, userId, scanner);
                    buy.display();
                    break;
                case 5:
                    System.out.println("Requesting a game...\n");
                    Request request = new Request(con, userId, scanner);
                    request.display();
                    break;
                case 6:
                    System.out.println("Update Profile...");
                    updateProfile();
                    break;
                case 7:
                    System.out.println("View Payments...");
                    viewPreviousPayments();
                    break;
                case 8:
                    System.out.println("Checking out...");
                    Payments payment = new Payments(scanner, con, userId);
                    payment.display();
                    break;
                case 9:
                    System.out.println("\nLogged Out !\n");
                    logout = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
