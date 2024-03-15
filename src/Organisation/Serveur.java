/**
 * Titre      : Serveur IoT
 * Auteur     : Crepin Vardin Fouelefack
 * Date       : 15/01/2024
 * Description: Programme serveur pour interagir avec des appareils IoT en manipulant une base de données
 * Version    : 0.0.1 (Partie 4)
 */

 import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import Organisation.Models.ObjetConnecte;

public class Serveur {
    private static final int PORT = 2222;
    private static ObjetConnecte base = new ObjetConnecte();

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
                    processMessage(message);
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

    private static void processMessage(String data) {
        int posAppareil = data.indexOf("Appareil:");
        int posType = data.indexOf("Type:");
        int posTemperature = data.indexOf("Temp:");
        int posHumidity = data.indexOf("Hum:");

        String appareilName = data.substring(posAppareil + 9, posType).trim();
        String appareilType = data.substring(posType + 5, posTemperature).trim();
        String temperature = data.substring(posTemperature + 5, posHumidity).trim();
        String humidite = data.substring(posHumidity + 5).trim();

        System.out.println("Appareil: " + appareilName);
        System.out.println("Type: " + appareilType);
        System.out.println("Temp: " + temperature + " C");
        System.out.println("Hum: " + humidite + " %");

        base.ajouterAppareil(appareilName, appareilType, temperature, humidite);
    }


    private static void sendResponse(Socket socket) throws IOException {
        // Création d'un objet PrintWriter pour envoyer des données via le socket
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Envoi de l'en-tête de la réponse HTTP
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("Connection: close");
        out.println();

        // Envoi de la réponse HTML
        out.println("<html><head><title>Server Java</title></head><body>");
        out.println("<h1>Données reçues</h1>");
        out.println("<p>Température : " + temperature + " C</p>");
        out.println("<p>Humidité : " + humidite + " %</p>");
        out.println("</body></html>");

        // Fermeture du flux PrintWriter
        out.close();
    }
}
