/**
 * Titre      : GestionBaseDonnees
 * Auteur     : Crepin Vardin Fouelefack
 * Date       : 15/01/2024
 * Description: Classe pour gérer les interactions avec la base de données PostgreSQL
 * Version    : 0.0.1
 */

 import java.sql.Connection;
 import java.sql.DriverManager;
 import java.sql.PreparedStatement;
 
 public class BaseDonnee {
     public void insert(String valeur) {
         Connection c = null;
         try {
             Class.forName("org.postgresql.Driver");
             c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ESP32", "postgres", "crepin");
             c.setAutoCommit(false);
             System.out.println("Connexion à la base de données établie avec succès");
 
             String sql = "INSERT INTO temperature (valeur) VALUES (?)";
             PreparedStatement pstmt = c.prepareStatement(sql);
             pstmt.setString(1, valeur);
             pstmt.executeUpdate();
             pstmt.close();
 
             c.commit();
             System.out.println("Enregistrements créés avec succès dans la base de données");
         } catch (Exception e) {
             System.err.println("Erreur lors de l'accès à la base de données : " + e.getMessage());
             System.exit(0);
         } finally {
             try {
                 if (c != null) {
                     c.close();
                 }
             } catch (Exception e) {
                 System.err.println("Erreur lors de la fermeture de la connexion à la base de données : " + e.getMessage());
                 System.exit(0);
             }
         }
     }
 }