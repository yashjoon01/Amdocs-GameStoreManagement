import java.sql.*;
import java.util.Scanner;

public class Request {
    int uid;
    Scanner scanner;
    Connection conn;

    Request(Connection ct,int id, Scanner sc){
        this.uid = id;
        this.scanner =sc;
        this.conn = ct;
    }

    public void display() throws SQLException{
        System.out.println("Welcome to request menu !");

        int choice = -1;
        do {
            System.out.println("Please choose an option:");
            System.out.println("1. Make a request");
            System.out.println("2. See request history");
            System.out.println("3. Exit");
            System.out.print("\nEnter your choice: ");

            String ip = scanner.nextLine();
            try{
                choice = Integer.parseInt(ip);
            }catch (NumberFormatException e){
                System.out.println("Wrong input !\n");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.println("Making a request...");
                    makeRequest();
                    break;
                case 2:
                    System.out.println("Request History...");
                    showRequests();
                    break;
                case 3:
                    System.out.println("Thank you for requesting games !");
                    return;
                default:
                    System.out.println("Invalid choice !");
            }
        } while (choice != 3);

    }

    private void makeRequest() throws SQLException
    {
        String name;
        while (true){

            System.out.print("Game Name - ");
            name = scanner.nextLine();

            if(name.isEmpty()){
                System.out.println("False Input! Retry.");
            }
            else break;
        }


        System.out.println();

        PreparedStatement pst = conn.prepareStatement("insert into requests(user_id,game_title) values(?,?)");
        pst.setInt(1,uid);
        pst.setString(2, name);
        pst.executeUpdate();

        System.out.println("A request was successfully raised.\n");
    }

    public void showRequests() throws SQLException {
        Statement smt = conn.createStatement();

        ResultSet rs = smt.executeQuery("select * from requests");

        while(rs.next()){
            System.out.print("Request ID - " + rs.getInt(1) + " | ");
            System.out.print("Game ID - " + rs.getInt(2) + " | ");
            System.out.print("Title - " + rs.getString(3) + " | ");
            System.out.print("Request Date - " + rs.getDate(4) + " | ");
            System.out.println("Status - " + rs.getString(5));
        }
        scanner.nextLine();
    }
}
