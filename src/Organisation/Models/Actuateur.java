/*
  Titre      : ManipulationDataBase
  Auteur     : Crepin Vardin Fouelefack
  Date       : 15/01/2024
  Description: Programme permettant de manipuler les BD en vue d'interagir avec des appareils IoT du monde reel
  Version    : 0.0.1 (Partie 4)
*/
package Organisation.Models;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Actuateur extends Objet implements AutoCloseable {

    public int id;
    public int idObjet;
    public String name;
    public String type;
    public String localisation;


    // Constructeur par défaut
    public Actuateur() {
        
    }

    public Actuateur(int id, int idObjet, String name, String type) {

        this.id = id;
        this.idObjet = idObjet;
        this.name = name;
        this.type = type;
    }

    /*
     * Methode pour la connexion a la BD
     */
    public  void connexion() {
        try {
            String dbURL2 = "jdbc:postgresql://localhost:5432/javadata";
            String user = "postgres";
            String pass = "crepin";

            Connection conn = DriverManager.getConnection(dbURL2, user, pass);
            if (conn != null) {
                System.out.println("Connected to database. Bievenue user Postgres");
            }
        } catch (SQLException ex) {
            System.out.println("Erreur de connection a la BD: " + ex.getMessage());
        }
    }

// Méthode pour ajouter un appareil
public void ajouterActuateur() {

    try {
        String dbURL2 = "jdbc:postgresql://localhost:5432/javadata";
        String user = "postgres";
        String pass = "crepin";

        Connection conn = DriverManager.getConnection(dbURL2, user, pass);
        if (conn != null) {
            System.out.println("Connected to database. Bievenue user Postgres");
        }

        String insertSQL = "INSERT INTO actuators (name, type, idObjet) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, type);
            preparedStatement.setInt(3, idObjet);
            
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("L'appareil a été ajouté avec succès");
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de l'ajout d'un actuateur : " + ex.getMessage());
        }
        } catch (SQLException ex) {
            System.out.println("Erreur de connection a la BD: " + ex.getMessage());
        }
    }

    public void modifierActuateur() {
        
        try {
            String dbURL2 = "jdbc:postgresql://localhost:5432/javadata";
            String user = "postgres";
            String pass = "crepin";

            Connection conn = DriverManager.getConnection(dbURL2, user, pass);
            if (conn != null) {
                System.out.println("Connected to database. Bievenue user Postgres");
            }
 
            String SQL = " UPDATE actuators SET  type = ?  WHERE name = ?  ";
            
            try (
             PreparedStatement pstmt = conn.prepareStatement(SQL,
             Statement.RETURN_GENERATED_KEYS)) {
     
                 pstmt.setString(1, type);
                 pstmt.setString(2, name);
                 
                 int affectedRows = pstmt.executeUpdate();
                 // check the affected rows 
                 if (affectedRows > 0) {
                     // get the iid back
                     try (ResultSet rs = pstmt.getGeneratedKeys()) {
                         
                         if (rs.next()) {
                             rs.getLong(1);
                            }
                        } catch (SQLException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
                
            } catch (SQLException ex) {
                System.out.println("Erreur de connection a la BD: " + ex.getMessage());
            }
        }

            public void supprimerActuateur() {

        try {
            String dbURL2 = "jdbc:postgresql://localhost:5432/javadata";
            String user = "postgres";
            String pass = "crepin";

            Connection conn = DriverManager.getConnection(dbURL2, user, pass);
            if (conn != null) {
                System.out.println("Connected to database. Bievenue user Postgres");
            }
            // Supprimer l'appareil de la table correspondante
            String deleteSQL = "UPDATE actuators SET active = false WHERE name = ?";
            
            try (PreparedStatement preparedStatement = conn.prepareStatement(deleteSQL)) {
                preparedStatement.setString(1, name);
                
                int rowsDeleted = preparedStatement.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("actuateur supprimé avec succès");
                } else {
                    System.out.println("Aucun actuateur trouvé avec le nom spécifié");
                }
            } catch (SQLException ex) {
                System.out.println("Erreur lors de la suppression de l'actuateur : " + ex.getMessage());
            }
        } catch (SQLException ex) {
            System.out.println("Erreur de connection a la BD: " + ex.getMessage());
        }
    }
}
