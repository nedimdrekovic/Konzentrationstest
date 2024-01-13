package com.example.konzentrationstest;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.konzentrationstest.Modules.Aufgabe_Farben;
import com.example.konzentrationstest.Modules.Aufgabe_Formen;
import com.example.konzentrationstest.Modules.Aufgabe_Rechnen;
import com.example.konzentrationstest.Modules.Aufgabe_waehleUnpassendeFarbe;

/**
 * This class represents the pop up window
 */
public class PopUpFenster extends AppCompatActivity {

    private final Object obj;

    private final Dialog epicDialog;

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor preferencesEditor;
    private final String KEY;      // fuer jede Klasse anderen Key fuer jeweils einen anderen Highscore

    private int punkte;
    private boolean newHighscore;

    /**
     * Constructor of the pop up window
     * @param obj object of the class in which the pop up window is called.
     * @param punkte reached score.
     * @param newHighscore new highscore.
     * @param epicDialog dialog of the class with its context.
     * @param preferences sharespreferences for the highscore.
     * @param preferencesEditor editor sharespreferences for the highscore.
     * @param key particular key for each module.
     */
    public PopUpFenster(Object obj, int punkte, boolean newHighscore, Dialog epicDialog, SharedPreferences preferences, SharedPreferences.Editor preferencesEditor, String key) {
        this.obj = obj;
        this.punkte = punkte;
        this.newHighscore = newHighscore;

        this.epicDialog = epicDialog;
        this.preferences = preferences;
        this.preferencesEditor = preferencesEditor;

        KEY = key;
    }

    public void setNewHighscore(boolean newHighscore) {
        this.newHighscore = newHighscore;
    }

    public SharedPreferences getPreferences() {
        return this.preferences;
    }

    public SharedPreferences.Editor getPreferencesEditor() {
        return this.preferencesEditor;
    }

    public String getKEY() {
        return this.KEY;
    }

    public int getPunkte() { return this.punkte; }

    public void increaseScore() { this.punkte += 1; }

    /**
     * calls pop up window when game is over
     */
    public void showPopUpWindow() {
        epicDialog.setContentView(R.layout.activity_popupfenster);

        Button leave = epicDialog.findViewById(R.id.verlassen);
        Button stay = epicDialog.findViewById(R.id.weiter);

        TextView text = epicDialog.findViewById(R.id.anzeigeScore);
        TextView text2 = epicDialog.findViewById(R.id.anzeigeHighscore);

        String punkteText = "\n\tScore: " + this.punkte;
        text.setText(punkteText);

        // Text fuer Highscore
        String displayedText = "\t\t\t\t\tHighscore: " + preferences.getInt(KEY, 0);
        if (newHighscore) {
            displayedText = "New Highscore: " + preferences.getInt(KEY, 0);
        }
        text2.setText(displayedText);

        leave.setOnClickListener((View view) -> {
                String difficulty = MainActivity.getCurrentDifficultyText()[0];
                if (difficulty.equals("Leicht")) {
                    MainActivity.lastdisabledButton = "Leicht";
                } else if (difficulty.equals("Mittel")) {
                    MainActivity.lastdisabledButton = "Mittel";
                } else { // if (difficulty.equals("Hard")) {
                    MainActivity.lastdisabledButton = "Schwer";
                }

                Intent myIntent = new Intent( (AppCompatActivity) obj, MainActivity.class);
                ((AppCompatActivity) obj).startActivity(myIntent);
            });

        stay.setOnClickListener((View view) -> {
            switch (KEY) {
                case "speicherPreferences_Rechnen":
                    // macht Buttons wieder frei sobald das Pop-Up-Fenster verlassen wurde
                    com.example.konzentrationstest.Modules.Aufgabe_Rechnen.down.setEnabled(true);
                    Aufgabe_Rechnen.up.setEnabled(true);
                    break;
                case "speicherPreferences_Farben":
                    Aufgabe_Farben.down.setEnabled(true);
                    Aufgabe_Farben.up.setEnabled(true);
                    break;
                case "speicherPreferences_Formen":
                    Aufgabe_Formen.down.setEnabled(true);
                    Aufgabe_Formen.up.setEnabled(true);
                    break;
                case "speicherPreferences_waehleUnpassendeFarbe":
                    // enablen der Buttons
                    for (ImageButton ib: Aufgabe_waehleUnpassendeFarbe.getButtons()) {
                        ib.setEnabled(true);
                    }
                    break;
            }
            epicDialog.dismiss();
            // aktiviert Back-Button wieder sobald auf "Weiter" geklickt wird
            // makes
            Zeit.active = true;
            // Punkte wieder zuruecksetzen
            punkte = 0;
        });

        // makes leaving to the module menu during game impossible
        epicDialog.setCancelable(false);
        epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        epicDialog.show();

    }

}