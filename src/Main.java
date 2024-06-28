import java.sql.*;
import java.util.Scanner;

class Main{
    public static Connection giveConnector(){
        Connection con = null;
        try{
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "sys as sysdba", "uioP124");
            if(con != null){
//                System.out.println("Connection Established With Database !");
            }
            else{
                System.out.println("Error Occurred !");
            }
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return con;
    }

    public static String getUserType(Connection con, String username, String password) {
        String userType = "blank"; // Default value in case of incorrect credentials or no result

        String query = "SELECT user_type FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

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

    public static int getUserId(Connection con, String username, String password) {
        int userId = -1; // Default value in case of incorrect credentials or no result

        String query = "SELECT user_id FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }

    public static void signup(Connection con, Scanner scanner) throws SQLException {

        System.out.println("Creating Account for a customer !");
        String query = "INSERT INTO Users (username, password, user_type) VALUES (?,?,?)";
        PreparedStatement pst = con.prepareStatement(query);

        while(true){
            System.out.print("Username : ");
            String username = scanner.nextLine();

            System.out.print("Set Password : ");
            String password = scanner.nextLine();

            String user_type = "customer";

            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, user_type);

            int cnt = 0;
            try{
                cnt = pst.executeUpdate();
            }
            catch(SQLIntegrityConstraintViolationException e){
                System.out.println("\nEnter Unique Username !\n");
                continue;
            }

            if(cnt > 0){
                System.out.println("\nCustomer Created !\n");
                break;
            }
            else  System.out.println("Please enter valid username and password !");
        }
    }

    public static void login(Connection con, Scanner scanner, String userType) throws SQLException {

        int userId = -1;
        String username = "";
        String password = "";

        String type = "blank";

        System.out.println(userType + " Login !\n");

        while(type.equals("blank")){

            System.out.print("Enter username: ");
            username = scanner.nextLine();
            System.out.print("Enter password: ");
            password = scanner.nextLine();

            type = getUserType(con, username, password);
            if(!type.equals("blank")){
                break;
            }
            else{
                System.out.println("\nIncorrect Credentials !\n");
            }
        }

        if(!type.equals(userType)){
            System.out.println("\nUser Type Mismatch !\n");
            return;
        }

        System.out.println("\nYou Logged Into your account \uD83D\uDE0A");

        // Show appropriate menu based on user type
        switch (userType) {
            case "customer":
                System.out.println();
                userId = getUserId(con, username, password);
                Customer customer = new Customer(scanner, con, userId);
                customer.displayCustomerView();
                break;
            case "manager":
                userId = getUserId(con, username, password);
                Manager manager = new Manager(scanner, con, userId);
                manager.displayManagerView();
                break;
            case "sales_employee":
                userId = getUserId(con, username, password);
                SalesEmployee employee = new SalesEmployee(scanner, con, userId);
                employee.displayEmployeeView();
                break;
            default:
                System.out.println("Invalid user type.");
                break;

        }
    }

    public static void main(String[] args) throws SQLException {
        //All objects initialisations
        Connection con = giveConnector();

        //Application Interface
        Scanner scanner = new Scanner(System.in);


        System.out.println("\nWelcome to Game Store Manager !\n");
        boolean exit = false;

        while (!exit) {
            System.out.println("Select user type !\n");
            System.out.println("1. Manager");
            System.out.println("2. Employee");
            System.out.println("3. Customer");
            System.out.println("4. Exit");
            System.out.print("\nEnter your choice: ");

            String ip = scanner.nextLine();
            int choice = -1;
            try{
                choice = Integer.parseInt(ip);
            }catch (NumberFormatException e){
                System.out.println("Wrong input !\n");
                continue;
            }
            System.out.println();
            switch (choice) {
                case 1:
                    login(con, scanner, "manager");
                    break;
                case 2:
                    login(con, scanner, "sales_employee");
                    break;
                case 3:
                    while(true){
                        System.out.println("1. Create Account");
                        System.out.println("2. Login User");
                        System.out.print("\nEnter your choice: ");

                        ip = scanner.nextLine();
                        choice = -1;
                        try{
                            choice = Integer.parseInt(ip);
                        }catch (NumberFormatException e){
                            System.out.println("Wrong input !\n");
                            continue;
                        }

                        switch (choice){
                            case 1:
                                System.out.println();signup(con, scanner);break;
                            case 2:
                                System.out.println();login(con, scanner, "customer");break;
                            default:
                                System.out.println("\nInvalid Choice\n");
                        }
                        if(choice == 1 || choice == 2)
                            break;
                        else
                            continue;
                    }
                    break;
                case 4:
                    System.out.println("Thanks for visiting !");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }


        //Closing all objects that were allotted resources
        scanner.close();
        con.close();
    }
}