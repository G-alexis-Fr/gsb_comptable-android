package fr.cned.emdsgil.suividevosfrais;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Permet l'affichage d'une liste avec le total des frais saisis avant transfert vers BDD
 * Created by Alexis Goutorbe on 11/12/2020
 */
public class TotalRecapActivity extends AppCompatActivity {

    private Integer mois; // mois concerné
    private Integer annee; // année concernée
    private Integer etape; // nombre d'étapes du mois
    private Integer km; // nombre de km du mois
    private Integer nuitee; // nombre de nuitées du mois
    private Integer repas; // nombre de repas du mois
    private Float totalFraisHf; // total des frais hors forfait du mois

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_recap);
        setTitle("GSB : Total des frais à transférer");
        afficheMois();
        calculForfait();
        calculHF();
        cmdMenu_clic((Button) findViewById(R.id.cmdTotalValider), ConnexionActivity.class);
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
        Intent intent = new Intent(TotalRecapActivity.this, MainActivity.class) ;
        startActivity(intent) ;
    }

    /**
     * Affiche le mois en cours dans le TextView approprié, format MM - AAAA
     */
    private void afficheMois() {
        annee = Calendar.getInstance().get(Calendar.YEAR);
        // Attention Calendar indexe les mois à partir de 0, donc il faut rajouter 1 pour avoir le bon
        // n° de mois
        mois = Calendar.getInstance().get(Calendar.MONTH) + 1;
        if(mois < 10) {
            ((TextView) findViewById(R.id.txtMoisAnnee)).setText("0" + mois.toString() + " - " + annee.toString());
        } else {
            ((TextView) findViewById(R.id.txtMoisAnnee)).setText(mois.toString() + " - " + annee.toString());
        }
    }

    /**
     * Récupère les informations de la liste de frais forfait du mois en cours,
     * et les affiche dans les TextView dédiés
     */
    private void calculForfait() {
        Integer key = annee*100+mois ;
        // Initialisation à 0 des frais
        km = 0 ;
        etape = 0;
        nuitee = 0;
        repas = 0;
        // récupération des frais :
        if (Global.listFraisMois.containsKey(key)) {
            km = Global.listFraisMois.get(key).getKm();
            etape = Global.listFraisMois.get(key).getEtape();
            nuitee = Global.listFraisMois.get(key).getNuitee();
            repas = Global.listFraisMois.get(key).getRepas();
        }
        // Maj des TextView concernés
        ((TextView)findViewById(R.id.txtKmMois)).setText(String.format(Locale.FRANCE, "%d", km));
        ((TextView)findViewById(R.id.txtEtpMois)).setText(String.format(Locale.FRANCE, "%d", etape));
        ((TextView)findViewById(R.id.txtNuitMois)).setText(String.format(Locale.FRANCE, "%d", nuitee));
        ((TextView)findViewById(R.id.txtRepasMois)).setText(String.format(Locale.FRANCE, "%d", repas));
    }

    /**
     * Récupère et additionne tous les frais hors forfait enregistrés depuis 1 an, et valorise
     * le TextView dédié
     */
    private void calculHF() {
        Integer key = annee*100+mois ;
        ArrayList<FraisHf> liste = new ArrayList<>();
        totalFraisHf = Float.valueOf(0);

        for(int m=0; m < 12; m++ ) {
            // calcul de la période concernée
            if((mois - m) >= 1) {
                key = annee*100+(mois-m);
            } else {
                key = (annee-1)*100+(mois + 12 - m);
            }
            // vérification si dans la liste de frais du mois déterminé, il y a des frais
            if(Global.listFraisMois.containsKey(key)) {
                // si oui on met les frais dans une liste
                liste = Global.listFraisMois.get(key).getLesFraisHf();
                // et on ajoute chaque frais de la liste au montant total
                for(FraisHf frais : liste) {
                    totalFraisHf += frais.getMontant();
                }
            }
        }
        // Maj du montant dans le TextView du récap total
        ((TextView)findViewById(R.id.txtHfMois)).setText(String.format(Locale.FRANCE, "%.2f", totalFraisHf));
    }

    /**
     * Sur la sélection d'un bouton dans l'activité principale ouverture de l'activité correspondante
     */
    private void cmdMenu_clic(Button button, final Class classe) {
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // ouvre l'activité
                Intent intent = new Intent(TotalRecapActivity.this, classe);
                startActivity(intent);
            }
        });
    }
}