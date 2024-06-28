package JDBC_functions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class JDBC_insert {
    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "sys as sysdba", "uioP124");

            PreparedStatement pst = con.prepareStatement("insert into employee values(?,?,?)");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            while(true){

                System.out.print("ID : ");
                int eno = Integer.parseInt(br.readLine());

                System.out.print("Name : ");
                String ename = br.readLine();

                System.out.print("Salary : ");
                double sal = Double.parseDouble(br.readLine());

                pst.setInt(1, eno);
                pst.setString(2, ename);
                pst.setDouble(3, sal);

                int cnt = pst.executeUpdate();
                if(cnt > 0)     System.out.println(cnt + "record inserted !");
                else            System.out.println("No Record inserted !");

                System.out.println("Want to enter more ? (yes/no)");
                String ch = br.readLine();

                if(ch.equalsIgnoreCase("no"))   break;
            }
            con.close();
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
