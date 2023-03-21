package com.example.whatsappro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InicioActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager myviewPager;
    private TabLayout mytabLayout;
    private AcesoTabsAdapter myacesoTabsAdapter;
    private String CurrentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef, RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        toolbar = (Toolbar)findViewById(R.id.app_main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Whatsappro");

        myviewPager = (ViewPager)findViewById(R.id.main_tabs_pager);
        myacesoTabsAdapter = new AcesoTabsAdapter(getSupportFragmentManager());
        myviewPager.setAdapter(myacesoTabsAdapter);

        mytabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mytabLayout.setupWithViewPager(myviewPager);

        UserRef = FirebaseDatabase.getInstance().getReference().child("Usuario");
        RootRef = FirebaseDatabase.getInstance().getReference().child("Grupos");
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser curUser = mAuth.getCurrentUser();
        if(curUser == null){
            EnviarAlogin();
        }else{
            verificarUsuario();
        }
    }

    private void verificarUsuario() {
        final String currentUserID = mAuth.getCurrentUser().getUid();
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(currentUserID)){

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void EnviarAlogin(){
        Intent intent = new Intent(InicioActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menus_opciones, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.buscar_contactos_menu){
            Toast.makeText(this, "Buscar Amigos", Toast.LENGTH_SHORT).show();

        }
        if (item.getItemId() == R.id.crear_grupo_menu){
            CrearNuevoGrupo();

        }
        if (item.getItemId() == R.id.miperfil_menu){
            Intent intent = new Intent(InicioActivity.this, PerfilActivity.class);
            startActivity(intent);

        }
        if (item.getItemId() == R.id.cerrarsesion_menu){
            mAuth.signOut();
            EnviarAlogin();

        }
        return true;
    }

    private void CrearNuevoGrupo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InicioActivity.this, R.style.AlertDialog);
        builder.setTitle("Nombre del grupo:");
        final EditText nombregrupo = new EditText(InicioActivity.this);
        nombregrupo.setHint("ejemplo");
        builder.setView(nombregrupo);

        builder.setPositiveButton("Crear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                String nombreg = nombregrupo.getText().toString();
                if (TextUtils.isEmpty(nombreg)){
                    Toast.makeText(InicioActivity.this, "Ingrese el nombre del grupo", Toast.LENGTH_SHORT).show();
                }else{
                    CrearGrupoFirebase(nombreg);
                }
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void CrearGrupoFirebase(String nombreg) {

        RootRef.child(nombreg).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(InicioActivity.this, "Grupo creado con exito", Toast.LENGTH_SHORT).show();
                }else{
                    String error = task.getException().getMessage().toString();
                    Toast.makeText(InicioActivity.this, "Error"+error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}