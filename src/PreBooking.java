import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class PreBooking {
    private Connection con;
    private Scanner scanner;
    private int userId;

    public PreBooking(Connection connection, Scanner sc, int id) {
        this.con = connection;
        this.scanner = sc;
        this.userId = id;
    }

    public void showMenu() {
        int choice;

        System.out.println("***************************************************************************");
        System.out.println("Welcome to Pre-booking space...");
        System.out.println("***************************************************************************");
        do {
            System.out.println("Menu:");
            System.out.println("1. Show available games for pre-booking");
            System.out.println("2. Pre-book a game");
            System.out.println("3. Display all pre-bookings");
            System.out.println("4. Delete a pre-booking");
            System.out.println("5. Update a pre-booking");
            System.out.println("6. Exit\n");
            System.out.print("Enter your choice: ");

            String ip = scanner.nextLine();
            choice = -1;
            try{
                choice = Integer.parseInt(ip);
            }catch (NumberFormatException e){
                System.out.println("Wrong input !\n");
                continue;
            }

            switch (choice) {
                case 1:
                    showAvailableGames();
                    break;
                case 2:
                    preBookGame();
                    break;
                case 3:
                    displayPreBookings();
                    break;
                case 4:
                    deletePreBooking();
                    break;
                case 5:
                    updatePreBooking();
                    break;
                case 6:
                    System.out.println("Exiting the menu. Thank you!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 6);
    }

    private void showAvailableGames() {
        try {
            Statement smt = con.createStatement();
            String query = "SELECT game_id, title, genre, price FROM Games WHERE stock_quantity = 0";

            ResultSet rs = smt.executeQuery(query);
            if(!rs.next()){
                System.out.println("No games for pre-booking!!\nPress Enter to continue.");
                scanner.nextLine();
                return;
            }
            rs = smt.executeQuery(query);
            System.out.println("Available games for pre-booking:");
            System.out.println("---------------------------------------------------------------------------");
            while (rs.next()) {
                int gameId = rs.getInt("game_id");
                String title = rs.getString("title");
                String genre = rs.getString("genre");
                double price = rs.getDouble("price");

                System.out.print("Game ID: " + gameId+ " | ");
                System.out.print("Title: " + title+ " | ");
                System.out.print("Genre: " + genre+ " | ");
                System.out.print("Price: $" + price);
                System.out.println("\n---------------------------------------------------------------------------");
            }

            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            rs.close();
            smt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void preBookGame() {
        showAvailableGames();
        try {
            Statement smt = con.createStatement();
            String query = "SELECT game_id, title, genre, price FROM Games WHERE stock_quantity = 0";

            ResultSet rs = smt.executeQuery(query);
            if (!rs.next()) {
                System.out.println("No games available for pre-booking!!");
                scanner.nextLine();
                return;
            }
        }catch (Exception e) {
                e.printStackTrace();
        }

        System.out.print("Please enter the game ID to pre-book: ");
        int gameId=0;
        try{
            gameId = scanner.nextInt();
            scanner.nextLine();
        }catch(Exception e){
            System.out.println("Invalid gameId. Enter a integer value.");
            return;
        }
        scanner.nextLine(); // Consume newline

        if (!isValidGameId(gameId)) {
            System.out.println("Invalid game ID or game is not available for pre-booking. Press Enter to continue.");
            scanner.nextLine();
            return;
        }

        try {
            Statement smt = con.createStatement();
            String query = "INSERT INTO PreBookings (user_id, game_id) VALUES (" + userId + ", " + gameId + ")";

            int rowsAffected = smt.executeUpdate(query);
            if (rowsAffected > 0) {
                System.out.println("Game successfully pre-booked!\n");
            } else {
                System.out.println("Failed to pre-book the game.\n");
            }

            smt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deletePreBooking() {
        int cnt = displayPreBookings();
        if(cnt == 0)
            return;
        System.out.print("Please enter the preBookingID to delete the pre-booking: ");

        int prebookingId;
        try{
            prebookingId = scanner.nextInt();
            scanner.nextLine();
        }catch(Exception e){
            System.out.println("Invalid prebookingId. Enter a integer value.");
            return;
        }
        scanner.nextLine(); // Consume newline

        if (!isValidPrebookingId(prebookingId)) {
            System.out.println("Invalid game ID or game is not available for pre-booking. Press Enter to continue.");
            scanner.nextLine();
            return;
        }

        try {
            Statement smt = con.createStatement();
            String query = "DELETE FROM PreBookings WHERE user_id = " + userId + " AND prebooking_id = " + prebookingId;

            int rowsAffected = smt.executeUpdate(query);
            if (rowsAffected > 0) {
                System.out.println("Pre-booking successfully deleted!\n");
            } else {
                System.out.println("Failed to delete the pre-booking or no such pre-booking found.\n");
            }

            smt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int displayPreBookings() {
        int cnt = 0;
        try {
            Statement smt = con.createStatement();
            String query = "SELECT pb.prebooking_id, pb.game_id, g.title, pb.booking_date " +
                    "FROM PreBookings pb JOIN Games g ON pb.game_id = g.game_id " +
                    "WHERE pb.user_id = " + userId;

            ResultSet rs = smt.executeQuery(query);
            if(!rs.next()){
                System.out.println("No games selected for pre-booking!!\nPress Enter to continue.");
                scanner.nextLine();
                return cnt;
            }
            rs = smt.executeQuery(query);
            System.out.println("Your Pre-bookings:");
            System.out.println("---------------------------------------------------------------------------");
            while (rs.next()) {
                int preBookingId = rs.getInt("prebooking_id");
                int gameId = rs.getInt("game_id");
                String title = rs.getString("title");
                java.sql.Date bookingDate = rs.getDate("booking_date");

                System.out.print("PreBooking ID: " + preBookingId+ " | ");
                System.out.print("Game ID: " + gameId+ " | ");
                System.out.print("Game Title: " + title+ " | ");
                System.out.print("Booking Date: " + bookingDate);
                System.out.println("\n---------------------------------------------------------------------------");
                cnt++;
            }
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            rs.close();
            smt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cnt;
    }

    private void updatePreBooking() {
        int cnt = displayPreBookings();
        if(cnt == 0)    return;
        System.out.print("Please enter the preBookingID to update the pre-booking: ");

        int prebookingId;
        try {
            prebookingId = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Invalid prebookingId. Enter an integer value.");
            return;
        }
        scanner.nextLine(); // Consume newline

        if (!isValidPrebookingId(prebookingId)) {
            System.out.println("Invalid preBooking ID or pre-booking not found. Press Enter to continue.");
            scanner.nextLine();
            return;
        }

        showAvailableGames();
        System.out.print("Please enter the new game ID to update the pre-booking: ");
        int newGameId = 0;
        try {
            newGameId = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Invalid gameId. Enter an integer value.");
            return;
        }
        scanner.nextLine(); // Consume newline

        if (!isValidGameId(newGameId)) {
            System.out.println("Invalid game ID or game is not available for pre-booking. Press Enter to continue.");
            scanner.nextLine();
            return;
        }

        try {
            Statement smt = con.createStatement();
            String query = "UPDATE PreBookings SET game_id = " + newGameId + " WHERE prebooking_id = " + prebookingId + " AND user_id = " + userId;

            int rowsAffected = smt.executeUpdate(query);
            if (rowsAffected > 0) {
                System.out.println("Pre-booking successfully updated!\n");
            } else {
                System.out.println("Failed to update the pre-booking.\n");
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
            String query = "SELECT COUNT(*) AS count FROM Games WHERE game_id = " + gameId + " AND stock_quantity = 0";

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
    private boolean isValidPrebookingId(int prebookingId) {
        boolean isValid = false;
        try {
            Statement smt = con.createStatement();
            String query = "SELECT COUNT(*) AS count FROM PreBookings WHERE prebooking_id = " + prebookingId;

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