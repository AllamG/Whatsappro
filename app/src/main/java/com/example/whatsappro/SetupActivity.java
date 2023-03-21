package com.example.whatsappro;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private EditText nombre,ciudad,genero,edad,estado;
    private Button guardarinfo;
    private CircleImageView imagen_setup;
    private FirebaseAuth auth;
    private DatabaseReference UserRef;
    private ProgressDialog dialog;
    private String CurrentUserID;



    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        nombre=(EditText) findViewById(R.id.nombre_setup);
        ciudad=(EditText) findViewById(R.id.ciudad_setup);
        genero=(EditText) findViewById(R.id.Genero_setup);
        edad=(EditText) findViewById(R.id.Edad_setup);
        estado=(EditText) findViewById(R.id.Estado_setup);
        guardarinfo=(Button)findViewById(R.id.boton_setup);
        imagen_setup=(CircleImageView)findViewById(R.id.imagen_setup);
        toolbar = (Toolbar)findViewById(R.id.toolbar_setup);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Completa tu perfil");
        dialog = new ProgressDialog(this);
        auth=FirebaseAuth.getInstance();
        CurrentUserID= auth.getCurrentUser().getUid();
        UserRef= FirebaseDatabase.getInstance().getReference().child("Usuario");

        guardarinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuardarInformacion();
            }
        });


    }
    private void GuardarInformacion() {
        String nom= nombre.getText().toString();
        String ciu= ciudad.getText().toString();
        String gen= genero.getText().toString();
        String eda= edad.getText().toString();
        String est= estado.getText().toString();

        if (TextUtils.isEmpty(nom)){
            Toast.makeText(this, "Debe ingresar su nombre", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(eda)){
            Toast.makeText(this, "Debe ingresar su Edad", Toast.LENGTH_SHORT).show();
        }else{
            dialog.setTitle("Guardando su datos");
            dialog.setMessage("Por favor espere a que finalice el proceso");
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);

            HashMap map = new HashMap();
            map.put("nombre",nom);
            map.put("ciudad",ciu);
            map.put("genero",gen);
            map.put("edad",eda);
            map.put("Estado",est);

            UserRef.child(CurrentUserID).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SetupActivity.this, "Datos guardados", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        EnviarAlInicio();
                    }else{
                        String err = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void EnviarAlInicio() {
        Intent intent = new Intent(SetupActivity.this, InicioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }
}