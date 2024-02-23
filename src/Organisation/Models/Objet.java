package Organisation.Models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Objet {
    
    protected Connection conn;
    protected Scanner scanner = new Scanner(System.in);

    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Connection fermee");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public int verificationEntier() {
        String option = null;
        int val = 0;
        try {
            BufferedReader is = new BufferedReader(new InputStreamReader(System.in));
            option = is.readLine();
            val = Integer.parseInt(option);
        } catch (NumberFormatException ex) {
            System.err.println("Not a valid number: " + option);
        } catch (IOException e) {
            System.err.println("Unexpected IO ERROR: " + e);
        }
        return val;
    }

}
