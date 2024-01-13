package com.example.konzentrationstest.Modules;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.konzentrationstest.MainActivity;
import com.example.konzentrationstest.PopUpFenster;
import com.example.konzentrationstest.R;
import com.example.konzentrationstest.TopScore;
import com.example.konzentrationstest.Zeit;

/**
 * This class handles the arithmetic module.
 */
public class Aufgabe_Rechnen extends AppCompatActivity {

    private final int [] quadratzahlen = {1, 4, 9, 16, 25, 36, 49, 64, 81, 100};
    private final int [] summand1 = new int[10000 + quadratzahlen.length];
    private final int [] summand2 = new int[summand1.length];
    private final int [] summen = new int[summand1.length];

    private int nth_activity;

    String operator = "+";

    private TextView textFeld;

    // pop up variables
    private PopUpFenster pop;
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEditor;
    private final String KEY = "speicherPreferences_Rechnen";

    private ProgressBar timer;
    private Zeit z;

    public static ImageButton down, up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide(); // hide the title bar

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aufgabe_rechnen);

        textFeld = findViewById(R.id.textFarbe);
        timer = findViewById(R.id.timer_Rechnen);
        timer.setProgressTintList(ColorStateList.valueOf(Color.rgb(0,0, 139)));

        down = findViewById(R.id.unwahr);
        up = findViewById(R.id.wahr);

        // score of the current round
        int punkte = 0;

        // new highscore
        boolean newHighscore = false;

        Dialog epicDialog = new Dialog(this);

        // set maximal milliseconds of a difficulty
        String[] diff = MainActivity.getCurrentDifficultyText();
        int milliSec = Integer.parseInt(String.valueOf(Double.parseDouble(diff[1]) * 1000).split("\\.")[0]);

        // set maximum of timer
        timer.setMax((milliSec*9) / ((milliSec / 100) / 5));

        // fill time counter
        timer.setProgress(timer.getMax());

        // new timer
        z = new Zeit(timer);

        //setting preferences
        this.preferences = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        preferencesEditor = preferences.edit();
        preferencesEditor.apply();

        // temporary variable for computing for root numbers
        int [] temp_shuffle = new int[quadratzahlen.length];

        // for all square root numbers
        for (int j = 0; j < quadratzahlen.length; j++) {
            temp_shuffle[j] = (int) (Math.random() * 110);  // random-Index for root numbers
            summand1[temp_shuffle[j]] = quadratzahlen[j];
            summand2[temp_shuffle[j]] = 0;        // Merkmal einer Wurzelzahl einfach, dass 2.Summand eine 0 ist (um sie von einfachen Zahlen zu unterscheiden), siehe check Methode
            summen[temp_shuffle[j]] = generiereErgebnis_Wurzel(quadratzahlen[j]);    // bzw. selbst oben einfach eintragen
        }

        // calculate random values for all sums
        for (int i = 0; i < summand1.length; i++) {
            if (summand1[i] == 0) {         // besser das hier als summand2[i], da in Java bei einem leeren Int-Array alle Werte per default gleich 0 sind.
                summand1[i] = (int) (Math.random() * 20) + 1;
                summand2[i] = (int) (Math.random() * 20) + 1;
                summen[i] = generiereErgebnis(summand1[i], summand2[i]);
            }
        }

        // set values for first page
        int a = 13 + (int) (Math.random() * 12);            // [13, 24]
        int b = 5 + (int) (Math.random() * 28);             // [5, 32]
        int c = (a + b - 3) + (int) (Math.random() * 3);    // [a+b-3; a+b+2]  -> spaeter nach belieben Aendern
        summand1[0] = a;
        summand2[0] = b;
        summen[0] = c;
        String s = a + " " + operator + " " + b + " = " + c;
        textFeld.setText(s);    // default für den ersten Wert

        // reset counter for tasks
        nth_activity = 0;

        // pop up window
        pop = new PopUpFenster(this, punkte, newHighscore, epicDialog, preferences, preferencesEditor, KEY);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)  && (!Zeit.active)) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Generates a value for the result of an arithmetic task in a certain interval
     * @param wert1 first summand
     * @param wert2 secon summmand
     * @return the correct or uncorrect result of an task
     */
    public static int generiereErgebnis(int wert1, int wert2) {
        int genauigkeitsWert = 2;
        return (wert1 + wert2) + (int) (Math.random() * genauigkeitsWert);     // Intervall [wert1+wert2; wert1+wert2+(3-1)], z.B. 8 + 13 -> [21, 23]. Je höher der genauigkeitswert, umso weiter entfernt ist das Ergebnis (spaeter selbst festlegem)
    }

    /**
     * Generates the root the overgiven parameter with some probabilty.
     * @param wert value
     * @return root of the overgiven parameter with some probabilty.
     */
    public static int generiereErgebnis_Wurzel(int wert) {
        // weight is meant for first value
        return zufallsGenerator((int) (Math.sqrt(wert)), (int) (Math.sqrt(wert)) + (int) (Math.random() * 2), 60);     // Intervall [wurzel(wert); wurzel(wert)+1]
    }

    /**
     * Generates one value with a specific probablity.
     * @param wert1 first value.
     * @param wert2 second value.
     * @param gewichtung probablity to which the first value will be picked
     * @return wert1 to a specific probablity
     */
    public static int zufallsGenerator(int wert1, int wert2, int gewichtung) {
        int random = (int) (Math.random() * 100);
        if (random < gewichtung) {
            return wert1;
        }
        return wert2;
    }

    // variable to track event time
    private long mLastClickTime = 0;

    public void check(View view) {
        // time difference to prevent clicking on two buttons at the same time
        int difference = 150;
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < difference) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        // stops timer
        z.running = false;

        // new highscore is old score now
        pop.setNewHighscore(false);

        // checks if answer is correct
        boolean antwortIstKorrekt;
        if (summand2[nth_activity] == 0) {  // Quadrat
            antwortIstKorrekt = Math.sqrt(summand1[nth_activity]) == summen[nth_activity];
        } else {        // Summe
            antwortIstKorrekt = summand1[nth_activity] + summand2[nth_activity] == summen[nth_activity];
        }

        // wrong answer
        if (((view.getId() == R.id.unwahr) && antwortIstKorrekt) || (((view.getId() == R.id.wahr) && !antwortIstKorrekt))) {
            // set new highscore
            TopScore.highscore_rechnen = pop.getPunkte();

            if (preferences.getInt(KEY, 0) < TopScore.highscore_rechnen) {
                preferencesEditor.putInt(KEY, TopScore.highscore_rechnen);
                pop.setNewHighscore(true);
            }
            preferencesEditor.putInt("key", TopScore.highscore_rechnen);
            preferencesEditor.commit();

            pop.showPopUpWindow();

        } else {
            // increases score
            pop.increaseScore();

            // new timer for next page
            z = new Zeit(timer);

            // start timer
            z.laufen(pop);

            ++nth_activity;

            String displayedText;
            if (summand2[nth_activity] != 0) {  // gibt Summe aus
                displayedText = summand1[nth_activity] + " " + operator + " " + summand2[nth_activity] + " = " + summen[nth_activity];
                textFeld.setText(displayedText);
            } else if (summand2[nth_activity] == 0) {   // gibt Text aus
                //textFeld.setText(getResources().getString(R.string.sqr_root));
                displayedText = "&#x221a;" + summand1[nth_activity] + " = " + summen[nth_activity];
                textFeld.setText(Html.fromHtml(displayedText));
            }
        }

    }

}