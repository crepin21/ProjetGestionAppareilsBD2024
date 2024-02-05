/*
  Titre      : ManipulationDataBase(Partie 1)
  Auteur     : Crepin Vardin Fouelefack
  Date       : 15/01/2024
  Description: Programme permettant de manipuler les BD en vue d'interagir avec des appareils IoT du monde reel 
  Version    : 0.0.1
*/
package Organisation;
import Organisation.Models.ParametreDB;
/** 
 * Recherche Comment serialiser et deserialiser un objet
 * www.codejava.net
 * https://www.postgresqltutorial.com/postgresql-jdbc/
 
 * @author Crepin
 */

 public class App 
 {
    
    public static void main(String[] args) 
  {
       // Scanner scanner = new Scanner(System.in);
    try (ParametreDB parametre = new ParametreDB();)  // Creation de l'objet dans le try pour pouvoir appele directement la methode close()
    {
        parametre.connexion(); // Connexion a la base de donnees

        //Menu principal
        while (true) 
        {
            System.out.println("\n1-Ajouter un appareil");
            System.out.println("2- Afficher les appareils");
            System.out.println("3- Mettre a jour l'etat de fonctionnement");
            System.out.println("4-Supprimer un appareil");
            System.out.println("5-Quitter");
            System.out.print("Veiller choisir une option svp: ");

            switch (parametre.verificationEntier()) { // Appel de la methode pour verifier si l'entree est un entier
                case 1:
                    parametre.ajouterAppareil();
                    break;
                case 2:
                    parametre.afficherAppareils();          //Appel de la methode en fonction de l'entree choisis (1 a 5)
                    break;
                case 3:
                    parametre.modifierStatus();
                    break;
                case 4:
                    parametre.supprimerAppareil();
                    break;
                case 5:
                    parametre.close();
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