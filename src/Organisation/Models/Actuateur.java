package Organisation.Models;

public class Actuateur{
       // Classe représentant un actionneur
        private String nomActuateur;
        private String adresseIPActuateur;
        private String typeAction;
        


        public Actuateur(String nomActuateur, String adresseIPActuateur, String typeAction) 
        {

            
            this.nomActuateur          = nomActuateur;
            this.adresseIPActuateur    = adresseIPActuateur;
            this.typeAction            = typeAction;
        }
        public void actionner() {
            typeAction = "actionTest";
            System.out.println("Actuateur " + nomActuateur + " effectue une action de type " + typeAction);
        }

        // Méthodes spécifiques aux actionneurs
}
