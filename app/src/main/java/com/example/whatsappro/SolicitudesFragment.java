package com.example.whatsappro;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SolicitudesFragment extends Fragment {

    private View SolicitudesFragmentView;
    private RecyclerView ReciclerSolicitudesLista;
    private DatabaseReference SolicitudesRef, UserRef;
    private FirebaseAuth auth;
    private String CurrentUserId;

    public SolicitudesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SolicitudesFragmentView = inflater.inflate(R.layout.fragment_solicitudes, container,false);
        ReciclerSolicitudesLista =(RecyclerView) SolicitudesFragmentView.findViewById(R.id.reciclersolicitudeslista);
        ReciclerSolicitudesLista.setLayoutManager(new LinearLayoutManager(getContext()));
        SolicitudesRef=FirebaseDatabase.getInstance().getReference().child("solicitudes");
        UserRef=FirebaseDatabase.getInstance().getReference().child("Usuarios");
        auth =FirebaseAuth.getInstance();
        CurrentUserId=auth.getCurrentUser().getUid();
        return SolicitudesFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contactos> options = new FirebaseRecyclerOptions.Builder<Contactos>()
                .setQuery(SolicitudesRef.child(CurrentUserId), Contactos.class)
                .build();

        FirebaseRecyclerAdapter<Contactos, SolicitudesViewHolder> adapter = new FirebaseRecyclerAdapter<Contactos, SolicitudesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SolicitudesViewHolder holder, int position, @NonNull Contactos model) {

                holder.itemView.findViewById(R.id.solicitud_aceptar_botoon).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.solicitud_cancelar_botoon).setVisibility(View.VISIBLE);

                final String user_id = getRef(position).getKey();
                DatabaseReference getTipo = getRef(position).child("tipo").getRef();
                getTipo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){
                            String tipo = snapshot.getValue().toString();
                            if (tipo.equals("recibido")){

                                UserRef.child(user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.hasChild("imagen")){
                                            String nom = snapshot.child("nombre").getValue().toString();
                                            String ciu = snapshot.child("ciudad").getValue().toString();
                                            String est = snapshot.child("estado").getValue().toString();
                                            String img = snapshot.child("imagen").getValue().toString();
                                            holder.nombre.setText(nom);
                                            holder.ciudad.setText(ciu);
                                            holder.estado.setText(est);
                                            Picasso.get().load(img).placeholder(R.drawable.image).into(holder.imagen);
                                        }else{
                                            String nom = snapshot.child("nombre").getValue().toString();
                                            String ciu = snapshot.child("ciudad").getValue().toString();
                                            String est = snapshot.child("estado").getValue().toString();
                                            holder.nombre.setText(nom);
                                            holder.ciudad.setText(ciu);
                                            holder.estado.setText(est);

                                        }

                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) { }});

                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }});

            }
            @NonNull
            @Override
            public SolicitudesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(getContext()).inflate(R.layout.user_display_layout,parent,false);
               SolicitudesViewHolder viewHolder = new SolicitudesViewHolder(view);
               return viewHolder;
            }
        };
        ReciclerSolicitudesLista.setAdapter(adapter);
        adapter.startListening();
    }
    private static class SolicitudesViewHolder extends RecyclerView.ViewHolder{
        TextView nombre,ciudad, estado;
        CircleImageView imagen;
        Button aceptar, cancelar;
        public SolicitudesViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre=itemView.findViewById(R.id.user_nombre);
            ciudad=itemView.findViewById(R.id.user_ciudad);
            estado=itemView.findViewById(R.id.user_estado);
            imagen=itemView.findViewById(R.id.user_image_perfil);
            aceptar=itemView.findViewById(R.id.solicitud_aceptar_botoon);
            cancelar=itemView.findViewById(R.id.solicitud_cancelar_botoon);
        }
    }
}

