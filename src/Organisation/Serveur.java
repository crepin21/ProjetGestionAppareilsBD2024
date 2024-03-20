/**
 * Titre      : Serveur IoT
 * Auteur     : Crepin Vardin Fouelefack
 * Date       : 15/01/2024
 * Description: Programme serveur pour interagir avec des appareils IoT en manipulant une base de données
 * Version    : 0.0.1 (Partie 4)
 */
package Organisation;
import Organisation.Models.*;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;
import org.json.JSONArray;


public class Serveur {

    private static final int PORT = 8000;
    private static ObjetConnecte base = new ObjetConnecte();
    Listes liste = new Listes();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/tel", new teleHandler());

        server.createContext("/objet/add", new objetHandler());
        server.createContext("/objet/get", new objetListHandler());
        server.createContext("/objet/update", new objetUpdateHandler());
        server.createContext("/objet/delete", new objetSuppHandler());

        server.createContext("/capteur/add", new capteurHandler());
        server.createContext("/capteur/get", new capteurListHandler());
        server.createContext("/capteur/update", new capteurUpdateHandler());
        server.createContext("/capteur/delete", new capteurSuppHandler());

        server.createContext("/actuateur/add", new actuateurHandler());
        server.createContext("/actuateur/get", new actuateurListHandler());
        server.createContext("/actuateur/update", new actuateurUpdateHandler());
        server.createContext("/actuateur/delete", new actuateurSuppHandler());

        server.createContext("/telem", new telemetrieListHandler()); 

        server.start();
        System.out.println("Le serveur est en cours d'exécution sur le port " + PORT);
    }

    static class teleHandler implements HttpHandler {


        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Vérifier la méthode de la requête (doit être POST)
            if ("POST".equals(exchange.getRequestMethod())) {
                // Récupérer le corps de la requête
                InputStream requestBody = exchange.getRequestBody();
                    
                   
                InputStreamReader reader = new InputStreamReader(requestBody);
                Scanner scanner = new Scanner(reader).useDelimiter("\\A");
                String requestBodyString = scanner.hasNext() ? scanner.next() : "";

                // Convert the JSON string to a JSONObject
                JSONObject jsonObject = new JSONObject(requestBodyString);
                
                try (Telemetrie telem = new Telemetrie()){

                    
                    
                    // Accéder aux valeurs individuelles et les stocker dans des variables
                    telem.idsensor = jsonObject.getInt("idsensor");
                    telem.valeur = jsonObject.getDouble("valeur");
                    telem.type = jsonObject.getString("type");
                                      
                    telem.Save();                  
                    
                }

                // Répondre au client avec un message de confirmation
                String response = "Données reçues avec succès !";
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream responseBody = exchange.getResponseBody()) {
                    responseBody.write(response.getBytes());
                }

            } else {
                // Répondre avec un code 405 (Méthode non autorisée) si la méthode de la requête n'est pas POST
                exchange.sendResponseHeaders(405, -1);
            }
        }

    }

        static class objetHandler implements HttpHandler {
            
    
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                // Allow pre-flight requests
                if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                    headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                    exchange.sendResponseHeaders(204, -1);
                } else {
                    // Set CORS headers for regular requests
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                    headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    
                // Vérifier la méthode de la requête (doit être POST)
                if ("POST".equals(exchange.getRequestMethod())) {
                    
                    // Récupérer le corps de la requête
                    InputStream requestBody = exchange.getRequestBody();
                    
                   
                    InputStreamReader reader = new InputStreamReader(requestBody);
                    Scanner scanner = new Scanner(reader).useDelimiter("\\A");
                    String requestBodyString = scanner.hasNext() ? scanner.next() : "";

                    // Convert the JSON string to a JSONObject
                    JSONObject jsonObject = new JSONObject(requestBodyString);
                    
                    try (ObjetConnecte objet = new ObjetConnecte()){

                        // Accéder aux valeurs individuelles et les stocker dans des variables
                        objet.name = jsonObject.getString("name");
                        objet.type = jsonObject.getString("type");
                        objet.localisation = jsonObject.getString("localisation");
                        
                        objet.ajouterObjet();   

                        
                    }
  
                    // Répondre au client avec un message de confirmation
                    String response = "Objet enregistré avec succès !" ;
                    
                    
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream responseBody = exchange.getResponseBody()) {
                        responseBody.write(response.getBytes());
                    }
                    
                } else {
                    // Répondre avec un code 405 (Méthode non autorisée) si la méthode de la requête n'est pas POST
                    exchange.sendResponseHeaders(405, -1);
                }
            }
            }
        }
    
        // liste des objets
        static class objetListHandler implements HttpHandler {

            Listes liste = new Listes();
   
            @Override
            public void handle(HttpExchange exchange) throws IOException {

               // Allow pre-flight requests
               if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                Headers headers = exchange.getResponseHeaders();
                headers.set("Content-Type", "application/json");
                headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                exchange.sendResponseHeaders(204, -1);
            } else {
                // Set CORS headers for regular requests
                Headers headers = exchange.getResponseHeaders();
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", "true");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With");
    
                if ("GET".equals(exchange.getRequestMethod())) {

            List<ObjetConnecte> objets_ = liste.chargerObjet();
            
            JSONArray jsonArray = new JSONArray();
            for (ObjetConnecte objet : objets_){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", objet.id);
                jsonObject.put("name", objet.name);
                jsonObject.put("type", objet.type);
                jsonObject.put("localisation", objet.localisation);
                jsonArray.put(jsonObject);
            }

            // Print the JSON string
                    String response = jsonArray.toString();

                     exchange.sendResponseHeaders(200, response.length());
                     try (OutputStream os = exchange.getResponseBody()) {
                         os.write(response.getBytes());
                     }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
                }
            }
            }
        
    
        //la mise à jour d'un objet
        static class objetUpdateHandler implements HttpHandler {
    
            @Override
            public void handle(HttpExchange exchange) throws IOException {
    
                // Allow pre-flight requests
                if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                    headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                    exchange.sendResponseHeaders(204, -1);
                } else {
                    // Set CORS headers for regular requests
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                    headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    
                // Vérifier la méthode de la requête (doit être POST)
                if ("POST".equals(exchange.getRequestMethod())) {
                    // Récupérer le corps de la requête

                    InputStream requestBody = exchange.getRequestBody();

                    InputStreamReader reader = new InputStreamReader(requestBody);
                    Scanner scanner = new Scanner(reader).useDelimiter("\\A");
                    String requestBodyString = scanner.hasNext() ? scanner.next() : "";

                    // Convert the JSON string to a JSONObject
                    JSONObject jsonObject = new JSONObject(requestBodyString);
                    
                    try (ObjetConnecte objet = new ObjetConnecte()){

                        // Accéder aux valeurs individuelles et les stocker dans des variables
                        objet.name = jsonObject.getString("name");
                        objet.type = jsonObject.getString("type");
                        objet.localisation = jsonObject.getString("localisation");
                        
                        objet.modifierObjet();
      
                    } 
                
                    // Répondre au client avec un message de confirmation
                    String response = "Objet Modifié avec succès !"  ;
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream responseBody = exchange.getResponseBody()) {
                        responseBody.write(response.getBytes());
                    }
            }
            }
        }
        }
    
        // la suppression d'un objet 
        static class objetSuppHandler implements HttpHandler {
            
            @Override
            public void handle(HttpExchange exchange) throws IOException {
    
                // Allow pre-flight requests
                if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                    headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                    exchange.sendResponseHeaders(204, -1);
                } else {
                    // Set CORS headers for regular requests
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                    headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    
                // Vérifier la méthode de la requête (doit être POST)
                if ("POST".equals(exchange.getRequestMethod())) {

                    // Récupérer le corps de la requête
                    InputStream requestBody = exchange.getRequestBody();
                    
                    InputStreamReader reader = new InputStreamReader(requestBody);
                    Scanner scanner = new Scanner(reader).useDelimiter("\\A");
                    String requestBodyString = scanner.hasNext() ? scanner.next() : "";

                    // Convert the JSON string to a JSONObject
                    JSONObject jsonObject = new JSONObject(requestBodyString);
                    
                    try (ObjetConnecte objet = new ObjetConnecte()){

                        // Accéder aux valeurs individuelles et les stocker dans des variables
                        objet.name = jsonObject.getString("name");
                        objet.supprimerObjet();    
                        
                    }
    
                    // Répondre au client avec un message de confirmation
                    String response = "Supprimé avec Succès  !";
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream responseBody = exchange.getResponseBody()) {
                        responseBody.write(response.getBytes());
                    }
            }
            }
            }
        }
        
        static class capteurHandler implements HttpHandler {
            
    
            @Override
            public void handle(HttpExchange exchange) throws IOException {
    
                // Allow pre-flight requests
                if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                    headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                    exchange.sendResponseHeaders(204, -1);
                } else {
                    // Set CORS headers for regular requests
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                    headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    
                // Vérifier la méthode de la requête (doit être POST)
                if ("POST".equals(exchange.getRequestMethod())) {
                    // Récupérer le corps de la requête

                    InputStream requestBody = exchange.getRequestBody();
                    
                    InputStreamReader reader = new InputStreamReader(requestBody);
                    Scanner scanner = new Scanner(reader).useDelimiter("\\A");
                    String requestBodyString = scanner.hasNext() ? scanner.next() : "";

                    // Convert the JSON string to a JSONObject
                    JSONObject jsonObject = new JSONObject(requestBodyString);
                    
                    try (Capteur capteur = new Capteur()){

                        // Accéder aux valeurs individuelles et les stocker dans des variables
                        capteur.name = jsonObject.getString("name");
                        capteur.idObjet = jsonObject.getInt("idObjet");
                        capteur.type = jsonObject.getString("type");
                        
                        capteur.ajouterObjet();                   
                        
                    }

                    // Répondre au client avec un message de confirmation
                    String response = "Objet enregistré avec succès !" ;
                   
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream responseBody = exchange.getResponseBody()) {
                        responseBody.write(response.getBytes());
                    }
    
                } else {
                    // Répondre avec un code 405 (Méthode non autorisée) si la méthode de la requête n'est pas POST
                    exchange.sendResponseHeaders(405, -1);
                }
            }
            }
    
        }
    
        // liste des capteurs
    
        static class capteurListHandler implements HttpHandler {

            Listes liste = new Listes();
            List<Capteur> capteurs = new ArrayList<>();
   
            @Override
            public void handle(HttpExchange exchange) throws IOException {
    
                // Allow pre-flight requests
                if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                    headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                    exchange.sendResponseHeaders(204, -1);
                } else {
                    // Set CORS headers for regular requests
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                    headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    
                if ("GET".equals(exchange.getRequestMethod())) {
   
            capteurs = liste.chargerCapteur();
            
            JSONArray jsonArray = new JSONArray();
            for (Capteur capteur : capteurs){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", capteur.id);
                jsonObject.put("idObjet", capteur.idObjet);
                jsonObject.put("name", capteur.name);
                jsonObject.put("type", capteur.type);
                jsonArray.put(jsonObject);
            }

            // Print the JSON string
                    String response = jsonArray.toString();
    
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            }
            }
            }
        
        //la mise à jour d'un capteur
        static class capteurUpdateHandler implements HttpHandler {
  
            @Override
            public void handle(HttpExchange exchange) throws IOException {
    
                // Allow pre-flight requests
                if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                    headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                    exchange.sendResponseHeaders(204, -1);
                } else {
                    // Set CORS headers for regular requests
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                    headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    
                // Vérifier la méthode de la requête (doit être POST)
                if ("POST".equals(exchange.getRequestMethod())) {
                    // Récupérer le corps de la requête
                    InputStream requestBody = exchange.getRequestBody();
                    
                   
                    InputStreamReader reader = new InputStreamReader(requestBody);
                    Scanner scanner = new Scanner(reader).useDelimiter("\\A");
                    String requestBodyString = scanner.hasNext() ? scanner.next() : "";

                    // Convert the JSON string to a JSONObject
                    JSONObject jsonObject = new JSONObject(requestBodyString);
                    
                    try (Capteur capteur = new Capteur()){
   
                        // Accéder aux valeurs individuelles et les stocker dans des variables
                        capteur.name = jsonObject.getString("name");
                        capteur.type = jsonObject.getString("type");
                 
                        capteur.modifierObjet();   
                        
                    }

                    // Répondre au client avec un message de confirmation
                    String response = "Objet Modifié avec succès !"  ;
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream responseBody = exchange.getResponseBody()) {
                        responseBody.write(response.getBytes());
                    }
            }
            }
        }
        }
    
        // la suppression d'un capteur 
        static class capteurSuppHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange exchange) throws IOException {
    
               // Allow pre-flight requests
               if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                exchange.sendResponseHeaders(204, -1);
            } else {
                // Set CORS headers for regular requests
                Headers headers = exchange.getResponseHeaders();
                headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    
                // Vérifier la méthode de la requête (doit être POST)
                if ("POST".equals(exchange.getRequestMethod())) {
                    // Récupérer le corps de la requête
                    InputStream requestBody = exchange.getRequestBody();

                    InputStreamReader reader = new InputStreamReader(requestBody);
                    Scanner scanner = new Scanner(reader).useDelimiter("\\A");
                    String requestBodyString = scanner.hasNext() ? scanner.next() : "";

                    // Convert the JSON string to a JSONObject
                    JSONObject jsonObject = new JSONObject(requestBodyString);
                    
                    try (Capteur capteur = new Capteur()){

                        // Accéder aux valeurs individuelles et les stocker dans des variables
                        capteur.name = jsonObject.getString("name");
                                         
                        capteur.supprimerObjet();   
                        
                    }

                    // Répondre au client avec un message de confirmation
                    String response = "Supprimé avec Succès  !";
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream responseBody = exchange.getResponseBody()) {
                        responseBody.write(response.getBytes());
                    }
            }
            }
            }
        }
        
        static class actuateurHandler implements HttpHandler {
            
    
            @Override
            public void handle(HttpExchange exchange) throws IOException {
    
               // Allow pre-flight requests
               if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                exchange.sendResponseHeaders(204, -1);
            } else {
                // Set CORS headers for regular requests
                Headers headers = exchange.getResponseHeaders();
                headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                // Vérifier la méthode de la requête (doit être POST)
                if ("POST".equals(exchange.getRequestMethod())) {

                    InputStream requestBody = exchange.getRequestBody();
                    
                   
                    InputStreamReader reader = new InputStreamReader(requestBody);
                    Scanner scanner = new Scanner(reader).useDelimiter("\\A");
                    String requestBodyString = scanner.hasNext() ? scanner.next() : "";

                    // Convert the JSON string to a JSONObject
                    JSONObject jsonObject = new JSONObject(requestBodyString);
                    
                    try (Actuateur actuateur = new Actuateur()){

                        // Accéder aux valeurs individuelles et les stocker dans des variables
                        actuateur.name = jsonObject.getString("name");
                        actuateur.idObjet = jsonObject.getInt("idObjet");
                        actuateur.type = jsonObject.getString("type");
                                          
                        actuateur.ajouterActuateur();                  
                        
                    }
    
                    // Répondre au client avec un message de confirmation
                    String response = "Objet enregistré avec succès !" ;
                   
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream responseBody = exchange.getResponseBody()) {
                        responseBody.write(response.getBytes());
                    }
    
                    
                } else {
                    // Répondre avec un code 405 (Méthode non autorisée) si la méthode de la requête n'est pas POST
                    exchange.sendResponseHeaders(405, -1);
                }
            }
            }
    
        }
    
        // liste des actuateurs
    
        static class actuateurListHandler implements HttpHandler {

            Listes liste = new Listes();
            List<Actuateur> actuateurs = new ArrayList<>();
   
            @Override
            public void handle(HttpExchange exchange) throws IOException {
    
                // Allow pre-flight requests
                if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                    headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                    exchange.sendResponseHeaders(204, -1);
                } else {
                    // Set CORS headers for regular requests
                    Headers headers = exchange.getResponseHeaders();
                    headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                    headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    
                if ("GET".equals(exchange.getRequestMethod())) {


            actuateurs = liste.chargerActuateur();
            
            JSONArray jsonArray = new JSONArray();
            for (Actuateur actuateur : actuateurs){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", actuateur.id);
                jsonObject.put("idObjet", actuateur.idObjet);
                jsonObject.put("name", actuateur.name);
                jsonObject.put("type", actuateur.type);
                jsonArray.put(jsonObject);
            }

            // Print the JSON string
                    String response = jsonArray.toString();

                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
                }
            }
            }
                //la mise à jour d'un capteur
        static class actuateurUpdateHandler implements HttpHandler {
    
            @Override
            public void handle(HttpExchange exchange) throws IOException {
    
               // Allow pre-flight requests
               if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                exchange.sendResponseHeaders(204, -1);
            } else {
                // Set CORS headers for regular requests
                Headers headers = exchange.getResponseHeaders();
                headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    
                // Vérifier la méthode de la requête (doit être POST)
                if ("POST".equals(exchange.getRequestMethod())) {
                    // Récupérer le corps de la requête
                    InputStream requestBody = exchange.getRequestBody();
                    
                   
                    InputStreamReader reader = new InputStreamReader(requestBody);
                    Scanner scanner = new Scanner(reader).useDelimiter("\\A");
                    String requestBodyString = scanner.hasNext() ? scanner.next() : "";

                    // Convert the JSON string to a JSONObject
                    JSONObject jsonObject = new JSONObject(requestBodyString);
                    
                    try (Actuateur actuateur = new Actuateur()){

                        // Accéder aux valeurs individuelles et les stocker dans des variables
                        actuateur.name = jsonObject.getString("name");
                        actuateur.type = jsonObject.getString("type");
                                          
                        actuateur.modifierActuateur();

                    }

                    // Répondre au client avec un message de confirmation
                    String response = "Objet Modifié avec succès !"  ;
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream responseBody = exchange.getResponseBody()) {
                        responseBody.write(response.getBytes());
                    }
            }
            }
        }
        }
    
        // la suppression d'un capteur 
        static class actuateurSuppHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange exchange) throws IOException {
    
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    
                // Vérifier la méthode de la requête (doit être POST)
                if ("POST".equals(exchange.getRequestMethod())) {
                    // Récupérer le corps de la requête
                    InputStream requestBody = exchange.getRequestBody();
                    
                    InputStreamReader reader = new InputStreamReader(requestBody);
                    Scanner scanner = new Scanner(reader).useDelimiter("\\A");
                    String requestBodyString = scanner.hasNext() ? scanner.next() : "";

                    // Convert the JSON string to a JSONObject
                    JSONObject jsonObject = new JSONObject(requestBodyString);
                    
                    try (Actuateur actuateur = new Actuateur()){

                        // Accéder aux valeurs individuelles et les stocker dans des variables
                        actuateur.name = jsonObject.getString("name");
                        actuateur.supprimerActuateur();
                        
                    }
    
                    // Répondre au client avec un message de confirmation
                    String response = "Supprimé avec Succès  !";
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream responseBody = exchange.getResponseBody()) {
                        responseBody.write(response.getBytes());
                    }
            }
            }
        }
        
        static class telemetrieListHandler implements HttpHandler {

            Listes liste = new Listes();
            List<Telemetrie> dataList = new ArrayList<>();
   
            @Override
            public void handle(HttpExchange exchange) throws IOException {
    
               // Allow pre-flight requests
               if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                Headers headers = exchange.getResponseHeaders();
                headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                exchange.sendResponseHeaders(204, -1);
            } else {
                // Set CORS headers for regular requests
                Headers headers = exchange.getResponseHeaders();
                headers.add("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
                headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    
                if ("GET".equals(exchange.getRequestMethod())) {

            dataList = liste.chargerTelemetrie();
            
            JSONArray jsonArray = new JSONArray();
            for (Telemetrie data : dataList){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("idsensor", data.id);
                jsonObject.put("value", data.valeur);
                jsonObject.put("typedonnees", data.type);
                jsonObject.put("date_", data.date_);
                jsonObject.put("heure_", data.heure_);
                jsonArray.put(jsonObject);
            }

            // Print the JSON string

                    String response = jsonArray.toString();

                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            }
            }
            }

}
