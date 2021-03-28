package fr.cned.emdsgil.suividevosfrais;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

/**
 * Saisie des identifiants, transfert pour maj de la bdd distante
 * Created by Alexis Goutorbe on 11/12/2020.
 */
public class ConnexionActivity extends AppCompatActivity {

    private String login;
    private String mdp;
    private EditText txtLogin;
    private EditText txtMdp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        setTitle("GSB : Identification Utilisateur");
        cmdTransfert_clic();
    }

    /**
     * Retourne l'instance de ConnexionActivity, afin de pouvoir l'utiliser dans une autre classe
     * pour envoyer un Toast
     * @return
     */
    public ConnexionActivity getInstance() {
        return this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.retour_accueil))) {
            retourActivityPrincipale() ;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Retour à l'activité principale (le menu)
     */
    private void retourActivityPrincipale() {
        Intent intent = new Intent(ConnexionActivity.this, MainActivity.class) ;
        startActivity(intent) ;
    }

    /**
     * Click sur le bouton "transférer la fiche" : récupération et transtypage des login et mdp
     * saisis par l'utilisateur, envoie d'un Toast si ces champs sont vides, sinon envoi
     * des données au format JSON
     */
    private void cmdTransfert_clic() {
        findViewById(R.id.cmdTransfert).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                txtLogin = (EditText)findViewById(R.id.txtLogin);
                login = txtLogin.getText().toString();
                txtMdp = (EditText)findViewById(R.id.txtMdp);
                mdp = txtMdp.getText().toString();
                if(login.isEmpty() || mdp.isEmpty()) {
                    Toast.makeText(
                            ConnexionActivity.this,
                            "Veuillez saisir votre login et votre mot de passe",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    AccesDistant accesDistant = new AccesDistant();
                    Global.context = ConnexionActivity.this;
                    accesDistant.envoi("enreg", convertToJSONArray(Global.listFraisMois, login, mdp));
                    // pour qu'au clic sur le bouton transférer, le clavier disparaisse pour que
                    // l'utilisateur puisse correctement voir le Toast :
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }) ;
    }

    /**
     * Méthode qui permet de convertir le <hashtable>listFraisMois en JSONArray pour transfert
     * vers la page PHP
     * @param listeFrais : Hashtable<Integer, FraisMois>
     * @param login : String
     * @param mdp : String
     * @return JSONArray : [login, mdp, nbreKm, nbreNuitee, nbreRepas, nbreEtapes, périodeHF, jourHF,
     *                      montantHF, motifHF]
     */
    private JSONArray convertToJSONArray(Hashtable<Integer, FraisMois> listeFrais, String login, String mdp) {
        List laListe = new ArrayList();
        laListe.add(login);
        laListe.add(mdp);
        Integer annee = Calendar.getInstance().get(Calendar.YEAR);
        // Attention Calendar indexe les mois à partir de 0, donc il faut rajouter 1 pour avoir le bon
        // n° de mois
        Integer mois = Calendar.getInstance().get(Calendar.MONTH) + 1;
        Integer key = annee * 100 + mois;
        if (listeFrais.containsKey(key)) {
            laListe.add(listeFrais.get(key).getKm());
            laListe.add(listeFrais.get(key).getNuitee());
            laListe.add(listeFrais.get(key).getRepas());
            laListe.add(listeFrais.get(key).getEtape());
        }
        for (int m = 0; m < 12; m++) {
            // calcul de la période concernée
            if ((mois - m) >= 1) {
                key = annee * 100 + (mois - m);
            } else {
                key = (annee - 1) * 100 + (mois + 12 - m);
            }
            List laListeHF = new ArrayList();
            // vérification si dans la liste de frais du mois déterminé, il y a des frais
            if (listeFrais.containsKey(key)) {
                for (FraisHf n : listeFrais.get(key).getLesFraisHf()) {
                    laListe.add(key);
                    laListe.add(n.getJour());
                    laListe.add(n.getMontant());
                    laListe.add(n.getMotif());
                }
            }
        }
        return new JSONArray(laListe);
    }
}