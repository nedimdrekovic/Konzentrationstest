package com.example.konzentrationstest;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

// wie soll das aussehen? Hat eher weniger mit Konzentration zu tun als vielmehr mit Vokabular, vielleicht zum Schluss machen als Extra
public class Aufgabe_Uebersetzen extends AppCompatActivity {

    String [] woerter_englisch = {"house", "car", ""};

    String [] woerter_deutsch = {"Haus", "Auto"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aufgabe__uebersetzen);
    }

    public void check(View view) {

    }
}