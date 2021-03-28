package fr.cned.emdsgil.suividevosfrais;

import android.os.Bundle;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Permet la saisie des frais Hors Forfait
 * Created by Emds & Alexis Goutorbe on 11/12/2020
 */
public class HfActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hf);
        setTitle("GSB : Frais HF");
        // paramétrage du DatePicker pour une saisie autorisée 11 mois en arrière
		setDateSaisieHF();
		// mise à 0 du montant
		((EditText)findViewById(R.id.txtHf)).setText("0") ;
        // chargement des méthodes événementielles
		imgReturn_clic() ;
		cmdAjouter_clic() ;
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
	 * Sur la selection de l'image : retour au menu principal
	 */
    private void imgReturn_clic() {
    	findViewById(R.id.imgHfReturn).setOnClickListener(new ImageView.OnClickListener() {
    		public void onClick(View v) {
    			retourActivityPrincipale() ;    		
    		}
    	}) ;
    }

    /**
     * Sur le clic du bouton ajouter : enregistrement dans la liste et sérialisation
     */
    private void cmdAjouter_clic() {
    	findViewById(R.id.cmdHfAjouter).setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			if(enregListe()) {
					Serializer.serialize(Global.listFraisMois, HfActivity.this) ;
					retourActivityPrincipale() ;
				} else {
					Toast.makeText(HfActivity.this,
							"Veuillez saisir un montant et un libellé",
							Toast.LENGTH_LONG).show();
				}
    		}
    	}) ;    	
    }

	/**
	 * Attribue une date minimum au DatePicker, puisque pour les frais HF peuvent remonter
	 * un an en arrière. 
	 * Attribue aussi une date maximum, le 1er jour du mois suivant
	 */
	private void setDateSaisieHF() {
		// instanciation d'une date :
		Calendar dateMinimum = Calendar.getInstance();
		// on remonte 11 mois en arrière
		dateMinimum.add(Calendar.MONTH, -11);
		// ensuite, on sélectionne le 1er jour du mois
		dateMinimum.set(Calendar.DAY_OF_MONTH, 1);
		// on attribue au DatePicker concerné cette date minimum
		((DatePicker)findViewById(R.id.datHf)).setMinDate(dateMinimum.getTimeInMillis());

		// instanciation d'une date maxi
		Calendar dateMaximum = Calendar.getInstance();
		// on ajoute 1 mois au mois en cours
		dateMaximum.add(Calendar.MONTH, 1);
		// on sélectionne le 1er jour du mois
		dateMaximum.set(Calendar.DAY_OF_MONTH, 1);
		// on attribue au DatePicker concerné cette date maximum de saisie
		((DatePicker)findViewById(R.id.datHf)).setMaxDate(dateMaximum.getTimeInMillis());
	}
    
	/**
	 * Enregistrement dans la liste du nouveau frais hors forfait
	 * Retourne false s'il manque une information (montant ou libellé)
	 */
	private boolean enregListe() {
		// Récupération des informations saisies
		Integer annee = ((DatePicker)findViewById(R.id.datHf)).getYear() ;
		Integer mois = ((DatePicker)findViewById(R.id.datHf)).getMonth() + 1 ;
		Integer jour = ((DatePicker)findViewById(R.id.datHf)).getDayOfMonth() ;
		Float montant = Float.valueOf((((EditText)findViewById(R.id.txtHf)).getText().toString()));
		String motif = ((EditText)findViewById(R.id.txtHfMotif)).getText().toString() ;
		// Enregistrement dans la liste
		Integer key = annee*100+mois ;
		// Vérification que l'utilisateur a bien saisi toutes les informations
		if(montant <= 0 || motif.isEmpty()) {
			return false;
		} else {
			if (!Global.listFraisMois.containsKey(key)) {
				// Creation du mois et de l'annee s'ils n'existent pas déjà
				Global.listFraisMois.put(key, new FraisMois(annee, mois)) ;
			}
			Global.listFraisMois.get(key).addFraisHf(montant, motif, jour) ;
			return true;
		}
	}

	/**
	 * Retour à l'activité principale (le menu)
	 */
	private void retourActivityPrincipale() {
		Intent intent = new Intent(HfActivity.this, MainActivity.class) ;
		startActivity(intent) ;   					
	}
}
