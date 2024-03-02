/**
 * Titre      : Serveur IoT
 * Auteur     : Crepin Vardin Fouelefack
 * Date       : 15/01/2024
 * Description: Programme serveur pour interagir avec des appareils IoT en manipulant une base de données
 * Version    : 0.0.1 (Partie 3)
 */

 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.PrintWriter;
 import java.net.ServerSocket;
 import java.net.Socket;
 
 public class Serveur {
     private static final int PORT = 2222;
     private static String temperature;
     private static String humidite;
     private static BaseDonnee base = new BaseDonnee();
 
     public static void main(String[] args) {
         try {
             ServerSocket serveur = new ServerSocket(PORT);
             System.out.println("Le serveur est en attente de connexions sur le port " + PORT);
             
             while (true) {
                 Socket socket = serveur.accept();
                 System.out.println("Nouvelle connexion entrante");
                 
                 try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                     String message = in.readLine();
                     System.out.println("Message reçu : " + message);
                     decryptData(message);
                     base.insert(temperature);
                     base.insert(humidite);
                 } catch (IOException e) {
                     System.err.println("Erreur lors de la lecture du message: " + e.getMessage());
                 } catch (Exception e) {
                     System.err.println("Erreur lors de l'insertion des données dans la base de données: " + e.getMessage());
                 } finally {
                     socket.close();
                 }
             }
         } catch (IOException e) {
             System.err.println("Erreur lors de la création du serveur : " + e.getMessage());
         }
     }
 
     private static void decryptData(String data) {
         int posTemperature = data.indexOf(":");
         int posC = data.indexOf("C");
         temperature = data.substring(posTemperature + 2, posC).trim();
 
         int posHumidity = data.lastIndexOf(":");
         int posPercent = data.lastIndexOf("%");
         humidite = data.substring(posHumidity + 2, posPercent).trim();
 
         System.out.println("Temp: " + temperature + " C");
         System.out.println("Hum: " + humidite + " %");
     }
 
    
 }