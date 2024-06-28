package JDBC_functions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class JDBC_update {
    public static void main(String[] args) {
        String value;
        int empid;
        double newSalary;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "sys as sysdba", "uioP124");
            Statement smt = con.createStatement();
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter employee id whose salary is to be updated : ");
            value = br.readLine();
            empid = Integer.parseInt(value);

            System.out.print("Enter new Salary : ");
            value = br.readLine();
            newSalary = Double.parseDouble(value);

            String sql = "update employee set esal = " + newSalary + " where eno = " + empid;
            int cnt = smt.executeUpdate(sql);
            if(cnt > 0){
                System.out.println(cnt + "rows updated !");
            }
            else{
                System.out.println("No rows found for this employee id !");
            }
            con.close();
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
