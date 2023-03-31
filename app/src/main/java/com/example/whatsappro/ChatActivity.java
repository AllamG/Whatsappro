package com.example.whatsappro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private String RecibirUserID, nombre, imagens;

    private TextView nombreusuario, ultimaconexion;
    private CircleImageView usuarioimagen;
    private Toolbar toolbar;
    private EditText mensaje;
    private ImageView botonenviar;
    private DatabaseReference RootRef;
    private FirebaseAuth auth;
    private String EnviarUserID;
    private final List<Mensajes> mensajesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MensajeAdapter mensajeAdapter;
    private RecyclerView UsuariosrecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();
        EnviarUserID = auth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        RecibirUserID = getIntent().getExtras().get("user_id").toString();
        nombre = getIntent().getExtras().get("user_nombre").toString();
        imagens = getIntent().getExtras().get("user_imagen").toString();

        IniciarelLayount();

        nombreusuario.setText(nombre);
        Picasso.get().load(imagens).placeholder(R.drawable.jk).into(usuarioimagen);

        mensajeAdapter = new MensajeAdapter(mensajesList);
        UsuariosrecyclerView = (RecyclerView) findViewById(R.id.listamensajesrecicler);
        linearLayoutManager = new LinearLayoutManager(this);
        UsuariosrecyclerView.setLayoutManager(linearLayoutManager);
        UsuariosrecyclerView.setAdapter(mensajeAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        RootRef.child("Mensajes").child(EnviarUserID).child(RecibirUserID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Mensajes mensajes = snapshot.getValue(Mensajes.class);
                mensajesList.add(mensajes);
                mensajeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void IniciarelLayount() {
        toolbar=(Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_chat_bar, null);
        actionBar.setCustomView(view);

        nombreusuario=(TextView) findViewById(R.id.usuario_nombre);
        ultimaconexion=(TextView) findViewById(R.id.usuario_conexion);
        usuarioimagen=(CircleImageView) findViewById(R.id.usuario_imagen);
        mensaje=(EditText)findViewById(R.id.mensaje);
        botonenviar=(ImageView)findViewById(R.id.enviar_mensaje_boton);
        botonenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnviarMensaje();
            }
        });


    }

    private void EnviarMensaje() {

        String mensajeTexto = mensaje.getText().toString();
        if (TextUtils.isEmpty(mensajeTexto)){
            Toast.makeText(this, "Por favor escriba su mensaje", Toast.LENGTH_SHORT).show();
        }else {
            String mensajeEnviadoRef = "Mensaje/"+ EnviarUserID + "/"+ RecibirUserID;
            String mensajeRecibidoRef = "Mensaje/"+ RecibirUserID + "/"+ EnviarUserID;

            DatabaseReference usuarioMensajeRef = RootRef.child("Mensajes").child(EnviarUserID).child(RecibirUserID).push();
            String mensajePushID = usuarioMensajeRef.getKey();

            Map mensajeTxt = new HashMap();
            mensajeTxt.put("mensaje", mensajeTexto);
            mensajeTxt.put("tipo", "texto");
            mensajeTxt.put("de", EnviarUserID);

            Map mensajeTxtfull = new HashMap();
            mensajeTxtfull.put(mensajeEnviadoRef+"/"+mensajePushID,mensajeTxt);
            mensajeTxtfull.put(mensajeRecibidoRef+"/"+mensajePushID,mensajeTxt);

            RootRef.updateChildren(mensajeTxtfull).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "Mensaje Enviado", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ChatActivity.this, "Error al enviar", Toast.LENGTH_SHORT).show();
                    }
                    mensaje.setText("");
                }
            });


        }
    }
}