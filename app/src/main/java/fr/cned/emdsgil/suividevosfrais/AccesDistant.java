package fr.cned.emdsgil.suividevosfrais;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;


/**
 * Created by emds on 25/02/2018.
 */

public class AccesDistant implements AsyncResponse {

    // Connexion au serveur via ce fichier PHP
    private static final String SERVERADDR = "http://gsbvisiteur.g-alexis.com/android/serveurGSB.php";
    /**
     * Constructeur
     */
    public AccesDistant(){
        super();
    }

    /**
     * Retour du serveur HTTP
     * @param output
     */
    @Override
    public void processFinish(String output) {
        // Vérification du contenu du retour dans la console
        Log.d("serveur", "************" + output);
        // découpage du message reçu
        String[] message = output.split("%");
        // Contrôle si le retour est correct (au moins 2 cases)
        if(message.length>1){
            if(message[0].equals("Echec")){
                Log.d("Echec","****************"+message[1]);
                Toast.makeText(Global.context, message[1], Toast.LENGTH_LONG).show();
            }else if(message[0].equals("Authentification_OK")){
                Log.d("Authentification","****************"+message[1]);
                Toast.makeText(Global.context, message[1], Toast.LENGTH_LONG).show();
                // Puisque le transfert s'est bien passé, remise à 0 du tableau de frais :
                Global.listFraisMois.clear();
                Serializer.serialize(Global.listFraisMois, Global.context);
            }else if(message[0].equals("Erreur !")){
                Log.d("Erreur !","****************"+message[1]);
                Toast.makeText(
                        Global.context,
                        "Connexion impossible ! Veuillez contacter votre service informatique",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Envoi de données vers le serveur distant
     * @param operation information précisant au serveur l'opération à exécuter
     * @param lesDonneesJSON les données à traiter par le serveur
     */
    public void envoi(String operation, JSONArray lesDonneesJSON){
        AccesHTTP accesDonnees = new AccesHTTP();
        // lien avec AccesHTTP pour permettre à delegate d'appeler la méthode processFinish
        // au retour du serveur
        accesDonnees.delegate = this;
        // ajout de paramètres dans l'enveloppe HTTP
        accesDonnees.addParam("operation", operation);
        accesDonnees.addParam("lesdonnees", lesDonneesJSON.toString());
        // envoi en post des paramètres, à l'adresse SERVERADDR
        accesDonnees.execute(SERVERADDR);
    }

}
