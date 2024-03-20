/*
  Titre      : ManipulationDataBase
  Auteur     : Crepin Vardin Fouelefack
  Date       : 15/01/2024
  Description: Programme permettant de manipuler les BD en vue d'interagir avec des appareils IoT du monde reel
  Version    : 0.0.1 (Partie 4)
*/
package Organisation.Models;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

import java.sql.Time;
import java.util.Date;

public class Listes {

    public Listes(){


    }

    public  Connection connexion() {
       
            String dbURL2 = "jdbc:postgresql://localhost:5432/javadata";
            String user = "postgres";
            String pass = "crepin";

            Connection conn = null;

            try {
             conn = DriverManager.getConnection(dbURL2, user, pass);
            if (conn != null) {
                System.out.println("Connected to database. Bievenue user Postgres");
                
            }
        } catch (SQLException ex) {
            System.out.println("Erreur de connection a la BD: " + ex.getMessage());
        }

        return conn;
    }

    public List<ObjetConnecte> chargerObjet() {

        Connection conn = connexion();

            List<ObjetConnecte> objets = new ArrayList<>();
            String selectSQL = "SELECT * FROM objetconnecte where active = true";
    
            try (PreparedStatement preparedStatement = conn.prepareStatement(selectSQL)) {
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String type = rs.getString("type");
                    String localisation = rs.getString("localisation");
                    
    
                    ObjetConnecte objet = new ObjetConnecte(id, name, type, localisation);
                    objets.add(objet);
                }
            } catch (SQLException ex) {
                System.out.println("Erreur lors de la récupération des objets : " + ex.getMessage());
            }
        
            return objets;
        }

    public List<Capteur> chargerCapteur() {

        Connection conn = connexion();

            List<Capteur> capteurs = new ArrayList<>();
            String selectSQL = "SELECT * FROM sensors where active = true";
    
            try (PreparedStatement preparedStatement = conn.prepareStatement(selectSQL)) {
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int idObjet = rs.getInt("idobjet");
                    String name = rs.getString("name");
                    String type = rs.getString("type");

                    Capteur capteur = new Capteur(id, idObjet, name, type);
                    capteurs.add(capteur);
                }
            } catch (SQLException ex) {
                System.out.println("Erreur lors de la récupération des capteurs : " + ex.getMessage());
            }
            return capteurs;
        }

        public List<Actuateur> chargerActuateur() {

            Connection conn = connexion();
    
                List<Actuateur> actuateurs = new ArrayList<>();
                String selectSQL = "SELECT * FROM actuators where active = true";
        
                try (PreparedStatement preparedStatement = conn.prepareStatement(selectSQL)) {
                    ResultSet rs = preparedStatement.executeQuery();
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        int idObjet = rs.getInt("idobjet");
                        String name = rs.getString("name");
                        String type = rs.getString("type");

                        Actuateur actuateur = new Actuateur(id, idObjet, name, type);
                        actuateurs.add(actuateur);
                    }
                } catch (SQLException ex) {
                    System.out.println("Erreur lors de la récupération des capteurs : " + ex.getMessage());
                }
            
                return actuateurs;

        }

        public List<Telemetrie> chargerTelemetrie() {

            Connection conn = connexion();
    
                List<Telemetrie> datas = new ArrayList<>();
                String selectSQL = "SELECT * FROM donneestelemetrie where active = true";
        
                try (PreparedStatement preparedStatement = conn.prepareStatement(selectSQL)) {
                    ResultSet rs = preparedStatement.executeQuery();
                    while (rs.next()) {

                        Telemetrie data = new Telemetrie();
                        
                        data.idsensor = rs.getInt("idsensor");
                        data.valeur = rs.getDouble("valeur") ;
                        data.type = rs.getString("typedonnees");
                        data.date_ = rs.getDate("date_") ;
                        data.heure_ = rs.getTime("heure_");
        
                        datas.add(data);
                    }
                } catch (SQLException ex) {
                    System.out.println("Erreur lors de la récupération des capteurs : " + ex.getMessage());
                }
            
                return datas;
            }
    
    public static void main(String[] args) throws IOException {

        Listes list = new Listes();
        List<ObjetConnecte> objets = new ArrayList<>();

        objets = list.chargerObjet();
        for (ObjetConnecte objetConnecte : objets) {

            System.out.println(objetConnecte.type);
            
        }
        
    }

}
