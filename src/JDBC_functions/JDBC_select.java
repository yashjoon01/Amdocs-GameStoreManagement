package JDBC_functions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class JDBC_select {
    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "sys as sysdba", "uioP124");
            Statement smt = con.createStatement();

            ResultSet rs = smt.executeQuery("select * from yash");

            while(rs.next()){
                System.out.println("ID - " + rs.getInt(1));
            }
            con.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
