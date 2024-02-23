/*
  Titre      : ManipulationDataBase
  Auteur     : Crepin Vardin Fouelefack
  Date       : 15/01/2024
  Description: Programme permettant de manipuler les BD en vue d'interagir avec des appareils IoT du monde reel
  Version    : 0.0.1 (Partie 2)
*/
package Organisation;
import Organisation.Models.*;
/** 
 * Recherche Comment serialiser et deserialiser un objet
 * www.codejava.net
 * https://www.postgresqltutorial.com/postgresql-jdbc/eragrst
 * 
 
 * @author Crepin
 * 
 */

 public class App 
 {
    
    public static void main(String[] args) 
  {
       // Scanner scanner = new Scanner(System.in);
    try (ObjetConnecte ObjetConnecte = new ObjetConnecte();)  // Creation de l'objet dans le try pour pouvoir appele directement la methode close()
    {
        ObjetConnecte.connexion(); // Connexion a la base de donnees

        //Menu principal
        while (true) 
        {
            System.out.println("\n1-Ajouter un appareil");
            System.out.println("2- Afficher appareil");
            System.out.println("3- Mettre a jour un appareil");
            System.out.println("4-Supprimer un appareil");
            System.out.println("5-Quitter");
            System.out.print("Veiller choisir une option svp: ");

            switch (ObjetConnecte.verificationEntier()) { // Appel de la methode pour verifier si l'entree est un entier
                case 1:
                    ObjetConnecte.ajouterAppareil();
                    break;
                case 2:
                    ObjetConnecte.afficherAppareil(); // Afficher les appareils correspondant au nom spécifié
                    break;
                case 3:
                    ObjetConnecte.modifierAppareil();
                    break;
                case 4:
                    ObjetConnecte.supprimerAppareil();
                    break;
                case 5:
                    ObjetConnecte.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Choix non valide");
                    break;
            }
        }
    }catch (Exception e) 
    {
        e.printStackTrace();
    }
 }

}