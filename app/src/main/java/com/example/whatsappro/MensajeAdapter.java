package com.example.whatsappro;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajesViewHolder> {

    private List<Mensajes> usuarioMensajes;
    private FirebaseAuth auth;
    private DatabaseReference UserRef;

    public MensajeAdapter (List<Mensajes> usuarioMensajes){
        this.usuarioMensajes = usuarioMensajes;
    }

    public class MensajesViewHolder extends RecyclerView.ViewHolder{
        public TextView enviarMensajeTexto, recibirMensajeTexto;
        public CircleImageView recibirImagenPerfil;
        public MensajesViewHolder(@NonNull View itemView) {
            super(itemView);
            enviarMensajeTexto = (TextView) itemView.findViewById(R.id.enviar_mensaje);
            recibirMensajeTexto = (TextView) itemView.findViewById(R.id.recibir_mensaje);
            recibirImagenPerfil = (CircleImageView) itemView.findViewById(R.id.mensaje_imagen_perfil);
        }
    }

    @NonNull
    @Override
    public MensajesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usuario_mensaje_layout, parent, false);

        auth = FirebaseAuth.getInstance();
        return new MensajesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajesViewHolder holder, int i) {

        String mensajeEnviadoID = auth.getCurrentUser().getUid();
        Mensajes mensajes = usuarioMensajes.get(i);
        String DeUsuarioID = mensajes.getDe();
        String TipoMensaje = mensajes.getTipo();

        UserRef = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(DeUsuarioID);
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild("imagen")){
                    String ImagenRecibido = snapshot.child("imagen").getValue().toString();
                    Picasso.get().load(ImagenRecibido).placeholder(R.drawable.jk).into(holder.recibirImagenPerfil);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});

        if (TipoMensaje.equals("texto")){
            holder.recibirMensajeTexto.setVisibility(View.GONE);
            holder.recibirImagenPerfil.setVisibility(View.GONE);

            if (DeUsuarioID.equals(mensajeEnviadoID)){
                holder.enviarMensajeTexto.setBackgroundResource(R.drawable.enviar_mensaje_layout);
                holder.enviarMensajeTexto.setTextColor(Color.WHITE);
                holder.enviarMensajeTexto.setText(mensajes.getMensaje());
            }else {
                holder.enviarMensajeTexto.setVisibility(View.GONE);
                holder.recibirMensajeTexto.setVisibility(View.VISIBLE);
                holder.recibirMensajeTexto.setVisibility(View.VISIBLE);

                holder.recibirMensajeTexto.setBackgroundResource(R.drawable.recibir_mensaje_layout);
                holder.recibirMensajeTexto.setTextColor(Color.BLACK);
                holder.recibirMensajeTexto.setText(mensajes.getMensaje());
            }
        }

    }

    @Override
    public int getItemCount() {
        return usuarioMensajes.size();
    }



}
