package com.example.whatsappro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {
    private EditText nombre,ciudad,estado,genero,edad;
    private Button botonperfil;
    private Toolbar toolbar;
    private String CurrrentUserID;
    private FirebaseAuth auth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        
        Componentes();

        botonperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActualizarInformacion();
            }
        });
        RootRef = FirebaseDatabase.getInstance().getReference();
        auth= FirebaseAuth.getInstance();
        CurrrentUserID = auth.getCurrentUser().getUid();

        RootRef.child("Usuario").child(CurrrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    String nom1 = snapshot.child("nombre").getValue().toString();
                    String ciu1 = snapshot.child("ciudad").getValue().toString();
                    String eda1 = snapshot.child("edad").getValue().toString();
                    String gen1 = snapshot.child("genero").getValue().toString();


                    nombre.setText(nom1);
                    ciudad.setText(ciu1);
                    edad.setText(eda1);
                    genero.setText(gen1);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ActualizarInformacion() {
        String nom = nombre.getText().toString();
        String ciu = ciudad.getText().toString();
        String eda = edad.getText().toString();
        String gen = genero.getText().toString();
        String est = estado.getText().toString();
        if(TextUtils.isEmpty(nom)){
            Toast.makeText(this, "Ingrese su nombre", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(ciu)) {
            Toast.makeText(this, "Ingrese su ciudad ", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(eda)){
            Toast.makeText(this, "Ingrese su edad ", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(gen)) {
            Toast.makeText(this, "Ingrese su genero ", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(est)) {
            Toast.makeText(this, "Ingrese su estado ", Toast.LENGTH_SHORT).show();
        }else{
            HashMap <String, String> profile = new HashMap<>();
            profile.put("uid", CurrrentUserID);
            profile.put("nombre", nom);
            profile.put("ciudad", ciu);
            profile.put("genero", gen);
            profile.put("Estado", est);
            profile.put("edad", eda);
            RootRef.child("Usuario").child(CurrrentUserID).setValue(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                EnviaralInicio();
                                Toast.makeText(PerfilActivity.this, "Guardado Exitosamente", Toast.LENGTH_SHORT).show();
                            }else{
                                String err = task.getException().getMessage().toString();
                                Toast.makeText(PerfilActivity.this, "Error:"+err, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void EnviaralInicio() {
        Intent intent = new Intent(PerfilActivity.this, InicioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void Componentes() {
        nombre=(EditText)findViewById(R.id.nombre_perfil);
        ciudad=(EditText)findViewById(R.id.ciudad_perfil);
        estado=(EditText)findViewById(R.id.estado_perfil);
        genero=(EditText)findViewById(R.id.genero_perfil);
        edad=(EditText)findViewById(R.id.edad_perfil);
        botonperfil=(Button) findViewById(R.id.boton_perfil);
        toolbar = findViewById(R.id.toolbar_Perfil);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle( "Mi Perfil");
    }
}