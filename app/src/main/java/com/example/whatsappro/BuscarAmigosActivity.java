package com.example.whatsappro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class BuscarAmigosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView BuscaramigosReciclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_amigos);

        BuscaramigosReciclerView = (RecyclerView) findViewById(R.id.buscar_amigos_reciclerview);
        BuscaramigosReciclerView.setLayoutManager(new LinearLayoutManager(this));

        toolbar=(Toolbar) findViewById(R.id.buscar_amigos_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Buscar Amigos");
    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}