package Organisation.Models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;//import
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
        // Création de la table pour les capteurs
        String createSensorsTableSQL = "CREATE TABLE IF NOT EXISTS sensors (" +
                "id SERIAL PRIMARY KEY, " +
                "appareil_name TEXT, " +
                "appareil_status VARCHAR(50))";
        
        // Création de la table pour les actionneurs
        String createActuatorsTableSQL = "CREATE TABLE IF NOT EXISTS actuators (" +
                "id SERIAL PRIMARY KEY, " +
                "appareil_name TEXT, " +
                "appareil_status VARCHAR(50))";

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createSensorsTableSQL);
            stmt.executeUpdate(createActuatorsTableSQL);
            System.out.println("Les tables ont été créées ou existaient déjà");
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la création des tables : " + ex.getMessage());
        }
    }

    public void ajouterAppareil() {
        System.out.print("Nom de l'appareil : ");
        String appareil_name = scanner.nextLine();
        System.out.print("Type de l'appareil (Capteur/Actuateur) : ");
        String appareil_type = scanner.nextLine();
        System.out.print("Statut de l'appareil : ");
        String appareil_status = scanner.nextLine();

        String insertSQL;
        if ("Capteur".equalsIgnoreCase(appareil_type)) {
            insertSQL = "INSERT INTO sensors (appareil_name, appareil_status) VALUES (?, ?)";
        } else if ("Actuateur".equalsIgnoreCase(appareil_type)) {
            insertSQL = "INSERT INTO actuators (appareil_name, appareil_status) VALUES (?, ?)";
        } else {
            System.out.println("Type d'appareil non pris en charge");
            return;
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, appareil_name);
            preparedStatement.setString(2, appareil_status);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("L'appareil a été ajouté");
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de l'ajout d'un appareil : " + ex.getMessage());
        }
    }

    public void modifierAppareil() {
        System.out.print("Entrer le nom de l'appareil à mettre à jour : ");
        String nomAppareil = scanner.nextLine();
        System.out.print("Entrer le nouveau type de l'appareil (Capteur/Actuateur) : ");
        String nouveauType = scanner.nextLine().toLowerCase(); //Conversion en minuscule
        System.out.print("Entrer le nouveau statut de l'appareil : ");
        String nouveauStatut = scanner.nextLine();
    
        // Recherche de l'ID de l'appareil et de la table correspondante
        int id = -1;
        String table;
        String selectIDSQL = "SELECT id FROM ";
        if ("capteur".equalsIgnoreCase(nouveauType)) {
            table = "sensors";
        } else if ("actuateur".equalsIgnoreCase(nouveauType)) {
            table = "actuators";
        } else {
            System.out.println("Type d'appareil non pris en charge");
            return;
        }
        selectIDSQL += table + " WHERE appareil_name = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(selectIDSQL)) {
            preparedStatement.setString(1, nomAppareil);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la recherche de l'ID de l'appareil : " + ex.getMessage());
        }
    
        // Vérification de l'existence de l'appareil
        if (id == -1) {
            System.out.println("Aucun appareil trouvé avec le nom spécifié");
            return;
        }
    
        // Mise à jour de l'appareil dans la table correspondante
        String updateSQL = "UPDATE " + table + " SET appareil_status = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, nouveauStatut);
            preparedStatement.setInt(2, id);
    
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Statut de l'appareil mis à jour");
            } else {
                System.out.println("Aucun appareil trouvé avec l'ID spécifié");
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la mise à jour du statut de l'appareil : " + ex.getMessage());
        }
    }
    
    public void supprimerAppareil() {
        System.out.print("Entrer le nom de l'appareil à supprimer: ");
        String nomAppareil = scanner.nextLine();
    
        // Recherche de la table correspondante
        String table;
        if (isSensor(nomAppareil)) {
            table = "sensors";
        } else if (isActuator(nomAppareil)) {
            table = "actuators";
        } else {
            System.out.println("Aucun appareil trouvé avec le nom spécifié");
            return;
        }
    
        // Confirmation de la suppression
        System.out.print("Voulez-vous vraiment supprimer cet appareil? (oui pour supprimer): ");
        String confirmation = scanner.nextLine().toLowerCase();
        if (!confirmation.equals("oui")) {
            System.out.println("Suppression annulée");
            return;
        }
    
        // Supprimer l'appareil de la table correspondante
        String deleteSQL = "DELETE FROM " + table + " WHERE appareil_name = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(deleteSQL)) {
            preparedStatement.setString(1, nomAppareil);
    
            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Appareil supprimé avec succès");
            } else {
                System.out.println("Aucun appareil trouvé avec le nom spécifié");
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la suppression de l'appareil : " + ex.getMessage());
        }
    }
    
    public void afficherAppareil() {
        System.out.print("Entrez le nom de l'appareil à afficher: ");
        String nomAppareil = scanner.nextLine();
    
        // Recherche de la table correspondante
        String table;
        if (isSensor(nomAppareil)) {
            table = "sensors";
        } else if (isActuator(nomAppareil)) {
            table = "actuators";
        } else {
            System.out.println("Aucun appareil trouvé avec le nom spécifié");
            return;
        }
    
        // Afficher les informations de l'appareil de la table correspondante
        String selectSQL = "SELECT * FROM " + table + " WHERE appareil_name = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, nomAppareil);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                //Affichage des informations de l'appareil
                System.out.println("ID : " + rs.getInt("id") + ", Nom : " + rs.getString("appareil_name") + ",      Statut : " + rs.getString("appareil_status"));
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de l'affichage de l'appareil : " + ex.getMessage());
        }
    }
    
    // Méthodes utilitaires pour vérifier si un appareil est un capteur ou un actionneur
    private boolean isSensor(String appareil_name) {
        String selectSQL = "SELECT COUNT(*) FROM sensors WHERE appareil_name = ?";
        return checkTableForAppareil(selectSQL, appareil_name);
    }
    
    private boolean isActuator(String appareil_name) {
        String selectSQL = "SELECT COUNT(*) FROM actuators WHERE appareil_name = ?";
        return checkTableForAppareil(selectSQL, appareil_name);
    }
    
    private boolean checkTableForAppareil(String selectSQL, String appareil_name) 
    {
        try (PreparedStatement preparedStatement = conn.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, appareil_name);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            return count > 0;
            } catch (SQLException ex) {
            System.out.println("Erreur lors de la vérification de l'appareil dans la table : " + ex.getMessage());
            return false;
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
