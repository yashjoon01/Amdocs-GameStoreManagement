import java.sql.*;
import java.util.Scanner;

public class Buy {
    int userId;
    Scanner scanner;
    Connection con;

    Buy(Connection ct, int id, Scanner sc) {
        this.userId = id;
        this.scanner = sc;
        this.con = ct;
    }

    public void display() throws SQLException{
        System.out.println("Welcome to the buying section!");
        while(true){
            System.out.println("Please choose an option:");
            System.out.println("1. Buy Games");
            System.out.println("2. Exit");

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
                    buyGames();
                    break;
                case 2:
                    System.out.println("Thank you for visiting the Game Store. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice !");
            }
        }
    }

    private boolean isValidGameId(int gameId) {
        boolean isValid = false;
        try {
            Statement smt = con.createStatement();
            String query = "SELECT COUNT(*) AS count FROM Games WHERE game_id = " + gameId + " AND stock_quantity > 0";

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
    private void displayAvailableGames() {
        try {
            Statement smt = con.createStatement();
            String query = "SELECT game_id, title, genre, price, stock_quantity FROM Games WHERE stock_quantity>0";

            ResultSet rs = smt.executeQuery(query);
            if(!rs.next()){
                System.out.println("No games available for Buying!!\nPress Enter to continue.");
                scanner.nextLine();
                return;
            }
            rs = smt.executeQuery(query);
            System.out.println("Available games for buying :");
            System.out.println("---------------------------------------------------------------------------");
            while (rs.next()) {
                int gameId = rs.getInt("game_id");
                String title = rs.getString("title");
                String genre = rs.getString("genre");
                double price = rs.getDouble("price");
                int stockQuantity = rs.getInt("stock_quantity");

                System.out.print("Game ID: " + gameId + " | ");
                System.out.print("Title: " + title + " | ");
                System.out.print("Genre: " + genre + " | ");
                System.out.print("Price: $" + price + " | ");
                System.out.print("Stock Quantity: " + stockQuantity);
                System.out.println("\n---------------------------------------------------------------------------");
            }

            rs.close();
            smt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private boolean isValidCartId(int cart_id) {
        boolean isValid = false;
        try {
            Statement smt = con.createStatement();
            String query = "SELECT COUNT(*) AS count FROM cart WHERE cart_id = " + cart_id + " AND user_id = " + userId;
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
    private void buyGames() throws SQLException {

        System.out.println("Choose the game you want to buy !");
        displayAvailableGames();

        int gameId=0;
        do {
            System.out.print("Please enter a valid game ID to buy: ");
            String ip = scanner.nextLine();
            try{
                gameId =Integer.parseInt(ip);
            }catch(NumberFormatException e){
                System.out.println("Invalid gameId. Enter an integer value.");
                return;
            }
            if (!isValidGameId(gameId)) {
                System.out.println("OOPS!!......Game Not Available for buying");
            }
        } while (!isValidGameId(gameId));
        System.out.println("You have selected a valid game ID: " + gameId);

        String wantToBuy;
        do {
            System.out.print("Do you want to buy (yes/no): ");
            wantToBuy = scanner.nextLine().trim();
            if (wantToBuy.equalsIgnoreCase("yes")) {

                Statement smt = con.createStatement();
                String query = "INSERT INTO Cart (user_id, game_id) VALUES (" + userId + ", " + gameId + ")";

                smt.executeUpdate(query);

                System.out.println("Your Cart ...");
                viewCart();
                int cart_id = -1;

                while(true){
                    System.out.println("Enter cart id");
                    String ip = scanner.nextLine();

                    try{
                        cart_id = Integer.parseInt(ip);
                    }catch (NumberFormatException e){
                        System.out.println("Wrong input !\n");
                        continue;
                    }
                    if(isValidCartId(cart_id))
                        break;
                }

                Payments payment = new Payments(scanner, con, userId);
                payment.buyFromCart(cart_id);

                break;

            } else if (wantToBuy.equalsIgnoreCase("no")) {

                Statement smt = con.createStatement();
                String query = "INSERT INTO Cart (user_id, game_id) VALUES (" + userId + ", " + gameId + ")";

                smt.executeUpdate(query);

                System.out.println("Your Cart ...");
                viewCart();
                break;
            }else{
                System.out.println("Wrong input");
            }

        } while (true);

    }
}

