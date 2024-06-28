package JDBC_functions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class JDBC_delete {
    public static void main(String[] args) {

        int empid;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "sys as sysdba", "uioP124");
            Statement smt = con.createStatement();
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


            while(true){

                System.out.print("Enter employee id whose record is to be deleted : ");
                empid = Integer.parseInt(br.readLine());

                int cnt = smt.executeUpdate("delete from employee where eno = " + empid);
                if(cnt > 0){
                    System.out.println("Deleted the record !");
                }
                else{
                    System.out.println("No employee found with that id !");
                }

                System.out.println("Want to delete more ? (yes/no)");
                String ch = br.readLine();

                if(ch.equalsIgnoreCase("no"))   break;
            }
            con.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
