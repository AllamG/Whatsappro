package com.example.whatsappro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {
    private EditText nombre,ciudad,estado,genero,edad;
    private Button botonperfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        
        Componentes();
    }

    private void Componentes() {
        nombre=(EditText)findViewById(R.id.nombre_perfil);
        ciudad=(EditText)findViewById(R.id.ciudad_perfil);
        estado=(EditText)findViewById(R.id.estado_perfil);
        genero=(EditText)findViewById(R.id.genero_perfil);
        edad=(EditText)findViewById(R.id.edad_perfil);
    }
}