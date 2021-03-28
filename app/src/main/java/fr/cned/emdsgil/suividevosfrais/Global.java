package fr.cned.emdsgil.suividevosfrais;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import org.json.JSONArray;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

abstract class Global {

    // tableau d'informations mémorisées
    public static volatile Hashtable<Integer, FraisMois> listFraisMois = new Hashtable<>();
    /* Retrait du type de l'Hashtable (Optimisation Android Studio)
     * Original : Typage explicit =
     * public static Hashtable<Integer, FraisMois> listFraisMois = new Hashtable<Integer, FraisMois>();
     */

    // fichier contenant les informations sérialisées
    public static final String filename = "save.fic";

    public static Context context;


    /**
     * Modification de l'affichage de la date (juste le mois et l'année, sans le jour)
     */
    public static void affichageDate(DatePicker datePicker, boolean afficheJours, boolean recap) {
        try {
            Field f[] = datePicker.getClass().getDeclaredFields();
            for (Field field : f) {
                int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
                datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), null);

                if (daySpinnerId != 0) {
                    View daySpinner = datePicker.findViewById(daySpinnerId);
                    if (!afficheJours && !recap) {
                        // pour les frais forfait, saisie autorisée uniquement sur le mois en cours :
                        datePicker.setMinDate(System.currentTimeMillis() - 1000);
                        datePicker.setMaxDate(System.currentTimeMillis() + 30000);
                        daySpinner.setVisibility(View.GONE);
                    } else if (!afficheJours && recap) {
                        // Pour le récap de frais HF, pas de blocage de dates
                        daySpinner.setVisibility(View.GONE);
                    }
                }
            }
        } catch (SecurityException | IllegalArgumentException e) {
            Log.d("ERROR", e.getMessage());
        }
    }
}
