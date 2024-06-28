import java.sql.*;
import java.util.Scanner;

public class GameRenting {
    private Connection con;
    private Scanner scanner;
    private int userId;
    public GameRenting(Connection connection, Scanner sc, int id) {
        this.con = connection;
        this.scanner = sc;
        this.userId = id;
    }

    public void showMenu() throws SQLException {
        int choice;
        System.out.println("***************************************************************************");
        System.out.println("Welcome to Rental space...");
        System.out.println("***************************************************************************");

        do {
            System.out.println("Menu:");
            System.out.println("1. Display available games for renting");
            System.out.println("2. Rent a game");
            System.out.println("3. Return a rented game");
            System.out.println("4. Display all rented games");
            System.out.println("5. Exit\n");
            System.out.print("Enter your choice: ");

            String ip = scanner.nextLine();
            choice = 1;
            try{
                choice = Integer.parseInt(ip);
            }catch (NumberFormatException e){
                System.out.println("Wrong input !\n");
                continue;
            } // Consume newline

            switch (choice) {
                case 1:
                    displayAvailableGames();
                    break;
                case 2:
                    rentGame();
                    break;
                case 3:
                    returnRentedGame();
                    break;
                case 4:
                    displayRentedGames();
                    break;
                case 5:
                    System.out.println("Exiting the menu. Thank you!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 5);
    }

    private void displayAvailableGames() {
        try {
            Statement smt = con.createStatement();
            String query = "SELECT game_id, title, genre, price, stock_quantity FROM Games WHERE disc_type = 'second_hand' AND stock_quantity>0";

            ResultSet rs = smt.executeQuery(query);
            if(!rs.next()){
                System.out.println("No games available for renting!!\nPress Enter to continue.");
                scanner.nextLine();
                return;
            }
            rs = smt.executeQuery(query);
            System.out.println("Available games for renting (Second Hand):");
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

    private void rentGame() {
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
                try {
                    Statement smt = con.createStatement();
                    String query = "INSERT INTO Cart (user_id, game_id) VALUES (" + userId + ", " + gameId + ")";

                    int rowsAffected = smt.executeUpdate(query);
                    if (rowsAffected > 0) {
                        System.out.println("Game successfully added to cart!\n");

                        query = "SELECT cart_id from cart where user_id ="+userId +"and game_id = "+gameId;
                        ResultSet rs = smt.executeQuery(query);

                        if(rs.next()){
                            int cartId = rs.getInt(1);
                            Payments p = new Payments(scanner,con,userId);
                            boolean isPaid = p.buyFromCart(cartId);

                            if(isPaid){
                                query = "Insert into Rentals(user_id,game_id) values (?,?) ";
                                PreparedStatement pst = con.prepareStatement(query);
                                pst.setInt(1,userId);
                                pst.setInt(2,gameId);
                                pst.executeUpdate();
                            }else{
                                query = "Delete from cart where cart_id = "+ cartId;
                                smt.executeUpdate(query);
                                System.out.println("Game Removed from cart !");
                                System.out.println("Returning to Rental Space...");
                            }
                        }

                    } else {
                        System.out.println("Failed to add to cart.\n");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
            } else if (wantToBuy.equalsIgnoreCase("no")) {
                System.out.println("Order not confirmed, returning to menu.\n");
                break;
            }else{
                System.out.println("Wrong input");
            }

        } while (true);
    }

    private int displayRentedGames() {
        int cnt = 0;
        try {
            Statement smt = con.createStatement();
            String query = "SELECT r.rental_id, r.game_id, g.title, r.rental_date, r.return_date " +
                    "FROM Rentals r JOIN Games g ON r.game_id = g.game_id " +
                    "WHERE r.user_id = " + userId;

            ResultSet rs = smt.executeQuery(query);
            if (!rs.next()) {
                System.out.println("No games rented!!\nPress Enter to continue.");
                scanner.nextLine();
                return 0;
            }
            rs = smt.executeQuery(query);
            System.out.println("Your Rented Games:");
            System.out.println("---------------------------------------------------------------------------");
            while (rs.next()) {
                int rentalId = rs.getInt("rental_id");
                int gameId = rs.getInt("game_id");
                String title = rs.getString("title");
                java.sql.Date rentalDate = rs.getDate("rental_date");
                java.sql.Date returnDate = rs.getDate("return_date");

                System.out.print("Rental ID: " + rentalId + " | ");
                System.out.print("Game ID: " + gameId + " | ");
                System.out.print("Game Title: " + title + " | ");
                System.out.print("Rental Date: " + rentalDate + " | ");
                System.out.print("Return Date: " + (returnDate != null ? returnDate : "Not returned yet"));
                System.out.print("\n---------------------------------------------------------------------------\n");

                cnt++;
            }
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            rs.close();
            smt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cnt;
    }

    private void returnRentedGame() {
        int cnt = displayRentedGames();
        if(cnt == 0){
            return;
        }
        System.out.print("Please enter the rental ID to return the game: ");
        int rentalId = 0;
        try {
            rentalId = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid rentalId. Enter an integer value.");
            return;
        }
        scanner.nextLine(); // Consume newline

        if (!isValidRentalId(rentalId)) {
            System.out.println("Invalid rental ID or rental not found. Press Enter to continue.");
            scanner.nextLine();
            return;
        }

        try {
            Statement smt = con.createStatement();
            String query = "UPDATE Rentals SET return_date = SYSDATE WHERE rental_id = " + rentalId + " AND user_id = " + userId;
            String query2 = "select return_date from rentals where rental_id = " + rentalId;

            ResultSet rs = smt.executeQuery(query2);
            if(rs.next()){
                java.sql.Date rentalDate = rs.getDate(1);
                if(rentalDate != null){
                    System.out.println("Game already returned. Thank you!\n ");
                    return;
                }
            }

            int rowsAffected = smt.executeUpdate(query);
            if (rowsAffected > 0) {
                System.out.println("Game successfully returned!\n");
            } else {
                System.out.println("Failed to return the game.\n");
            }
            smt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidGameId(int gameId) {
        boolean isValid = false;
        try {
            Statement smt = con.createStatement();
            String query = "SELECT COUNT(*) AS count FROM Games WHERE game_id = " + gameId + " AND disc_type = 'second_hand' AND stock_quantity > 0";

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

    private boolean isValidRentalId(int rentalId) {
        boolean isValid = false;
        try {
            Statement smt = con.createStatement();
            String query = "SELECT COUNT(*) AS count FROM Rentals WHERE rental_id = " + rentalId + " AND user_id = " + userId;
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
}