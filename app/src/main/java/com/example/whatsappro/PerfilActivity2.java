package com.example.whatsappro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.internal.cache.DiskLruCache;

public class PerfilActivity2 extends AppCompatActivity {

    private String usuario_revid, usuario_enviarid, CurrenEstado;
    private TextView usuarionom, usuariociu, usuarioest;
    private CircleImageView usuarioima;
    private Button enviarmensaje;
    private DatabaseReference UserRef, SolicitudRef;
    private FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil2);
        usuario_revid = getIntent().getExtras().get("usuario_id").toString();
        Toast.makeText(this, "Usuario ID:"+usuario_revid, Toast.LENGTH_SHORT).show();
        auth = FirebaseAuth.getInstance();
        UserRef= FirebaseDatabase.getInstance().getReference().child("Usuario");
        SolicitudRef = FirebaseDatabase.getInstance().getReference().child("Solicitudes");
        usuario_enviarid = auth.getCurrentUser().getUid();
        usuarionom = (TextView)findViewById(R.id.usuario_vic_nombre);
        usuariociu = (TextView)findViewById(R.id.usuario_vic_ciudad);
        usuarioest = (TextView)findViewById(R.id.usuario_vic_estado);
        usuarioima = (CircleImageView) findViewById(R.id.usuario_vic_imagen);
        enviarmensaje = (Button) findViewById(R.id.enviar_mensaje_usuario_vic);
        CurrenEstado = "nueva";
        obtenerinformacionDB();


    }
    private void obtenerinformacionDB(){
        UserRef.child(usuario_revid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if ((snapshot.exists()) && (snapshot.hasChild("imagen"))) {
                    String nombreUser = snapshot.child("nombre").getValue().toString();
                    String ciudadUser = snapshot.child("ciudad").getValue().toString();
                    String estadoUser = snapshot.child("Estado").getValue().toString();
                    String imagenUser = snapshot.child("imagen").getValue().toString();

                    Picasso.get().load(imagenUser).placeholder(R.drawable.error).into(usuarioima);
                    usuarionom.setText(nombreUser);
                    usuariociu.setText(ciudadUser);
                    usuarioest.setText(estadoUser);

                    MotorenviarRequerimiento();

                }else{
                    String nombreUser = snapshot.child("nombre").getValue().toString();
                    String ciudadUser = snapshot.child("ciudad").getValue().toString();
                    String estadoUser = snapshot.child("Estado").getValue().toString();
                    usuarionom.setText(nombreUser);
                    usuariociu.setText(ciudadUser);
                    usuarioest.setText(estadoUser);
                    MotorenviarRequerimiento();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    private void MotorenviarRequerimiento() {

        SolicitudRef.child(usuario_enviarid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(usuario_revid)){
                    String requerimiento = snapshot.child(usuario_revid).child("tipo").getValue().toString();
                    if (requerimiento.equals("enviado")){
                        CurrenEstado = "vieja";
                        enviarmensaje.setText("cancelar requerimiento");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (!usuario_enviarid.equals(usuario_revid)){
            enviarmensaje.setOnClickListener(new View.OnClickListener() {
                public void onClick (View v){
                    enviarmensaje.setEnabled(false);
                    if (CurrenEstado.equals("nueva")){
                        EnviarSolicitudMensaje();
                    }
                    if (CurrenEstado.equals("vieja")){
                        CancelarsolicitudMensaje();
                    }
                }
                });}else{
                    enviarmensaje.setVisibility(View.GONE);
                }
    }

    private void CancelarsolicitudMensaje() {
        SolicitudRef.child(usuario_enviarid).child(usuario_revid)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            SolicitudRef.child(usuario_revid).child(usuario_enviarid)
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                enviarmensaje.setEnabled(true);
                                                CurrenEstado="nueva";
                                                enviarmensaje.setText("enviar Mensaje");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void EnviarSolicitudMensaje() {
        SolicitudRef.child(usuario_enviarid).child(usuario_revid)
                .child("tipo").setValue("enviado")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            SolicitudRef.child(usuario_revid).child(usuario_enviarid)
                                    .child("tipo").setValue("recibido")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                enviarmensaje.setEnabled(true);
                                                CurrenEstado="vieja";
                                                enviarmensaje.setText("Cancelar Requerimiento");
                                            }
                                        }
                                    });
                        }
                    }
                });

    }
}