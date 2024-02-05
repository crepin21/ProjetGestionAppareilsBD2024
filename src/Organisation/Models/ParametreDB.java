package Organisation.Models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ParametreDB implements AutoCloseable
{
    
    private Connection conn;
    Scanner scanner = new Scanner(System.in);
    
    /*
     * Methode pour la connexion a la BD
     */
    public void connexion() 
    {
        try 
        {
            String dbURL2 = "jdbc:postgresql://localhost:5432/DataJava";
            String user = "postgres";
            String pass = "crepin";

            conn = DriverManager.getConnection(dbURL2, user, pass);
            if (conn != null) {
                System.out.println("Connected to database. Bievenue user Postgres");
            }
            createTable();  //Creation de la table apres la connexion a la BD si elle n'existe pas
        } catch (SQLException ex) 
        {
            System.out.println("Erreur de connection a la BD: " + ex.getMessage());
        }
    }

    /*
     * Methode permettant de creer une table d'apareils
     */
    public void createTable() {
        //Creation de la table appareils si elle n'existe pas
        String createTableSQL = "CREATE TABLE IF NOT EXISTS apareils " +
                "(ID INT PRIMARY KEY, " +
                " appareil_name TEXT, " +
                " appareil_type VARCHAR(100), " +
                " appareil_status VARCHAR(50))";

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            System.out.println("La table a ete creee ou existait");
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la creation de la table pour les # appareils: " + ex.getMessage());
        }
    }

    public void ajouterAppareil() {

        System.out.print("Numero d'identification : ");
        int id                 = verificationEntier();
        System.out.print("Nom de l'appareil : ");
        String appareil_name   = scanner.nextLine();
        System.out.print("Type de l'appareil : ");
        String appareil_type   = scanner.nextLine();
        System.out.print("Statut de l'appareil : ");
        String appareil_status = scanner.nextLine();

        // Ajout de l'appareil a la base de donnees
        String insertSQL = "INSERT INTO apareils (id, appareil_name, appareil_type, appareil_status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(insertSQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, appareil_name);
            preparedStatement.setString(3, appareil_type);
            preparedStatement.setString(4, appareil_status);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("L'appareil a ete ajoute");
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de l'ajout d'un appareil: " + ex.getMessage());
        }
    }

    public void modifierStatus() {
        System.out.print("Entrer l'ID de l'appareil a mettre a jour : ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Entrer le nouveau type de l'appareil : ");
        String nouveauType = scanner.nextLine();
    
        // Mise a jour du type de l'appareil
        String updateSQL = "UPDATE apareils SET appareil_type = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, nouveauType);
            preparedStatement.setInt(2, id);
    
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Type de l'appareil mis a jour");
            } else {
                System.out.println("Aucun appareil trouve avec l'ID specifie");
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la mise a jour du statut de l'appareil : " + ex.getMessage());
        }
    }
    
    public void supprimerAppareil() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrer l'ID de l'appareil a supprimer: ");
        int id = scanner.nextInt();
        scanner.nextLine();
    
        // Supprimer l'appareil de la base de donnees
        String deleteSQL = "DELETE FROM apareils WHERE id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, id);
    
            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Appareil supprime avec succes");
            } else {
                System.out.println("Aucun appareil trouve avec l'ID mentionne");
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la suppression de l'appareil : " + ex.getMessage());
        }
    }

    public void afficherAppareils() {
        // Afficher tous les appareils de la base de donnees
        String selectSQL = "SELECT * FROM apareils";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            while (rs.next()) {
                //Affichage de qq informations
                System.out.println("ID : " + rs.getInt("id") + ", Nom : " + rs.getString("appareil_name") + ",      Type: " + rs.getString("appareil_type") + ",         Etat : " + rs.getString("appareil_status"));
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de l'affichage des appareils: " + ex.getMessage());
        }
    }

    /*
     * Methode pour fermer la connexion a la BD
     */
    @Override
    public void close() 
    {
        try 
        {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Connection fermee");
            }
        } catch (SQLException ex) 
        {
            ex.printStackTrace();
        }
    }
    /*
     * Fonction de verification d'entier
     */
    public int verificationEntier()
    {
        String option = null;
            int val = 0;
            try {
            BufferedReader is = new BufferedReader(
            new InputStreamReader(System.in));
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
