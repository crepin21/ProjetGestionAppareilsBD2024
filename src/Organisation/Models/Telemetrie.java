/*
  Titre      : ManipulationDataBase
  Auteur     : Crepin Vardin Fouelefack
  Date       : 15/01/2024
  Description: Programme permettant de manipuler les BD en vue d'interagir avec des appareils IoT du monde reel
  Version    : 0.0.1 (Partie 4)
*/
package Organisation.Models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;
import java.sql.Statement;

public class Telemetrie extends Objet implements AutoCloseable{

    public int id;
    public int idsensor;
    public String type;
    public double valeur;
    public Date date_;
    public Time heure_;

    public Telemetrie(){

    }

    public  void connexion() {
        try {
            String dbURL2 = "jdbc:postgresql://localhost:5432/javadata";
            String user = "postgres";
            String pass = "crepin";

            Connection conn = DriverManager.getConnection(dbURL2, user, pass);
            if (conn != null) {
                System.out.println("Connected to database. Bievenue user Postgres");
            }
            //createTable();  
        } catch (SQLException ex) {
            System.out.println("Erreur de connection a la BD: " + ex.getMessage());
        }
    }

    public void Save() {

        try {
            String dbURL2 = "jdbc:postgresql://localhost:5432/javadata";
            String user = "postgres";
            String pass = "crepin";

            Connection conn = DriverManager.getConnection(dbURL2, user, pass);
            if (conn != null) {
                System.out.println("Connected to database. Bievenue user Postgres");
            }
            //createTable();  
            
            String insertSQL = "INSERT INTO donneestelemetrie (idsensor, typedonnees, valeur) VALUES (?, ?, ?)";
            
            
            
            try (PreparedStatement preparedStatement = conn.prepareStatement(insertSQL)) {
                preparedStatement.setInt(1, idsensor);
                preparedStatement.setString(2, type);
                preparedStatement.setDouble(3, valeur);
                
                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Telemetrie ajoutée avec succès");
                }
            } catch (SQLException ex) {
                System.out.println("Erreur lors de l'ajout  : " + ex.getMessage());
    }
    } catch (SQLException ex) {
        System.out.println("Erreur de connection a la BD: " + ex.getMessage());
    }
}

}
