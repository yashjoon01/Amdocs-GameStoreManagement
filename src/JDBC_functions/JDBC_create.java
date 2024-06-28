package JDBC_functions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class JDBC_create {
    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "sys as sysdba", "uioP124");
            Statement smt = con.createStatement();

            Scanner myObj = new Scanner(System.in);
            System.out.println("Enter table name - ");
            String table_name = myObj.nextLine();

            String sql = "create table " + table_name + " (eno number primary key, ename varchar(12) not null, esal number)";
            smt.executeUpdate(sql);

            System.out.println("Table Created Successfully !");
            con.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
