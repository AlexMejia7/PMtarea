package com.example.pmtarea;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PersonaAdapter extends RecyclerView.Adapter<PersonaAdapter.PersonaViewHolder> {

    private List<Persona> personaList;

    public PersonaAdapter(List<Persona> personaList) {
        this.personaList = personaList;
    }

    @NonNull
    @Override
    public PersonaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_persona, parent, false);
        return new PersonaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonaViewHolder holder, int position) {
        Persona persona = personaList.get(position);

        holder.tvNombreItem.setText(persona.getNombres() + " " + persona.getApellidos());

        // Mostrar teléfono o dirección según disponibilidad
        String datos = "";
        if (persona.getTelefono() != null && !persona.getTelefono().isEmpty()) {
            datos = persona.getTelefono();
        } else if (persona.getDireccion() != null && !persona.getDireccion().isEmpty()) {
            datos = persona.getDireccion();
        }
        holder.tvDatosItem.setText(datos);

        // Decodificar imagen base64 y mostrar en ImageView
        if (persona.getFoto() != null && !persona.getFoto().isEmpty()) {
            try {
                // Si la cadena Base64 incluye el prefijo "data:image/xxx;base64,", se elimina
                String base64Image = persona.getFoto();
                if (base64Image.contains(",")) {
                    base64Image = base64Image.split(",")[1];
                }

                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.imgFotoItem.setImageBitmap(decodedByte);
            } catch (IllegalArgumentException e) {
                // En caso de error al decodificar la imagen, muestra imagen por defecto
                holder.imgFotoItem.setImageResource(R.drawable.ic_person_placeholder);
            }
        } else {
            holder.imgFotoItem.setImageResource(R.drawable.ic_person_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return personaList.size();
    }

    public static class PersonaViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFotoItem;
        TextView tvNombreItem, tvDatosItem;

        public PersonaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFotoItem = itemView.findViewById(R.id.imgFotoItem);
            tvNombreItem = itemView.findViewById(R.id.tvNombreItem);
            tvDatosItem = itemView.findViewById(R.id.tvDatosItem);
        }
    }

    // Método para actualizar la lista y refrescar RecyclerView
    public void setPersonaList(List<Persona> personaList) {
        this.personaList = personaList;
        notifyDataSetChanged();
    }
}
