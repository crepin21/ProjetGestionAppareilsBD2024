
package Organisation.Models;


public class Capteur
{
    private String nom;
    private String adresseIP;
    private double valeurMesure;
    private String typeMesure;


        public Capteur(String nom, String adresseIP, double valeurMesure, String typeMesure) 
        {
            this.nom          = nom;
            this.adresseIP    = adresseIP;
            this.valeurMesure = valeurMesure;
            this.typeMesure   = typeMesure;
        }

        
    public String getNom()
    {
        return nom;
    }

    public String getAdresseIP()
    {
        return adresseIP;
    }


      
        // Méthodes spécifiques aux capteurs
}
