import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Payments {
    int userId;
    Scanner scanner;
    Connection con;

    Payments(Scanner sc, Connection ct, int id){
        scanner = sc ;
        con = ct;
        userId = id;
    }

    public boolean makePayment(int cart_id) throws SQLException {

        Statement smt = con.createStatement();

        String sql = "SELECT g.price, g.title, g.game_id, g.stock_quantity, c.quantity "+
                "FROM games g " +
                "JOIN cart c ON c.game_id = g.game_id where cart_id = " + cart_id;

        int game_id = -1;
        float amount = 0;
        int stockQuantity = -1;
        int quantity = 0;

        ResultSet rs = smt.executeQuery(sql);

        if(rs.next()){


            amount = rs.getFloat(1);
            quantity = rs.getInt(5);
            amount = amount * quantity;
            stockQuantity = rs.getInt(4);

            if(stockQuantity <= 0 || (stockQuantity - quantity) < 0){
                System.out.println("Cart ID : "+ cart_id+ " Sold Out !");
                return false;
            }

            System.out.println("Payment Success for : ");
            System.out.print("Amount - " + amount + " | ");
            System.out.println("Game Name - " + rs.getString(2));
            game_id = rs.getInt(3);
        }

        //payments update
        amount = amount * quantity;
        sql = "Insert into payments(user_id, amount) values( " +userId+", "+ amount + ")";
        smt.executeUpdate(sql);

        //sales update
        sql = "Insert into sales(user_id, game_id, total_price) values(" + userId +", " + game_id +"," + amount +")";
        smt.executeUpdate(sql);

        //games update
        stockQuantity = stockQuantity - quantity;
        sql = "Update games set stock_quantity = " + stockQuantity + " where game_id = "+game_id;
        smt.executeUpdate(sql);

        //removing from cart
        sql = "delete from cart where cart_id = " + cart_id;
        smt.executeUpdate(sql);

        return true;
    }

    boolean buyFromCart(int cart_id) throws SQLException {

        boolean isSuccess = false;
        int choice = -1;

        while(true){
            System.out.println("\n1. Pay now!");
            System.out.println("2. Cancel Payment!\n");

            String ip = scanner.nextLine();
            choice = -1;
            try{
                choice = Integer.parseInt(ip);
            }catch (NumberFormatException e){
                System.out.println("Wrong input !\n");
                continue;
            }

            if(choice == 1 || choice == 2)
                break;
        }



        switch (choice){
            case 1:
                isSuccess = makePayment(cart_id);;
                break;
            case 2:
                System.out.println("Payment Failed !\n");
                break;
            default:
        }
        return isSuccess;
    }
    public int viewCart() throws SQLException {
        Statement smt = con.createStatement();
        int x = 0;
        String sql = "SELECT c.cart_id, g.title, c.quantity " +
                "FROM cart c " +
                "JOIN games g ON c.game_id = g.game_id where user_id = '"+ userId +"'";
        ResultSet rs = smt.executeQuery(sql);

        if(!rs.next()){
            System.out.println("0 Items in the cart !\n");
            return x;
        }

        rs = smt.executeQuery(sql);
        while(rs.next()){
            System.out.print("Cart ID - " + rs.getInt(1) + " | ");
            System.out.print("Game Name - " + rs.getString(2) + " | ");
            System.out.println("Quantity - " + rs.getInt(3));
            System.out.println("\n");
            x++;
        }
        return x;
    }

    public void buyCart() throws SQLException {
        Statement smt = con.createStatement();

        String sql = "SELECT cart_id FROM cart where user_id = " + userId;
        ResultSet rs = smt.executeQuery(sql);

        if(!rs.next()){
            System.out.println("0 Items in the cart !");
        }

        rs = smt.executeQuery(sql);
        while(rs.next()){
            int cid = rs.getInt(1);
            makePayment(cid);
        }
        scanner.nextLine();
    }
    public void display() throws SQLException {

        while(true){
            System.out.println("1.) Buy items from cart !");
            System.out.println("2.) Buy complete cart !");
            System.out.println("3.) Exit !");

            String ip = scanner.nextLine();
            int choice = -1;
            try{
                choice = Integer.parseInt(ip);
            }catch (NumberFormatException e){
                System.out.println("Wrong input !\n");
                continue;
            }

            switch (choice){
                case 1:
                    int cnt = viewCart();
                    if(cnt == 0)
                        continue;
                    System.out.println("Enter cart_id !");
                    int cid = Integer.parseInt(scanner.nextLine());
                    buyFromCart(cid);
                    break;
                case 2:
                    cnt = viewCart();
                    if(cnt == 0)
                        continue;
                    System.out.println("Enter cart_id !");
                    buyCart();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Enter correct choice!");
            }
        }
    }
}
