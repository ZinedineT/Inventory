package com.devzine.inventory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InmuebleAdapter extends RecyclerView.Adapter<InmuebleAdapter.InmuebleViewHolder> {

    private List<Inmueble> listaInmuebles;
    private OnEliminarClickListener onEliminarClickListener;

    public interface OnEliminarClickListener {
        void onEliminarClick(int position);
    }

    public InmuebleAdapter(List<Inmueble> listaInmuebles, OnEliminarClickListener onEliminarClickListener) {
        this.listaInmuebles = listaInmuebles;
        this.onEliminarClickListener = onEliminarClickListener;
    }

    @NonNull
    @Override
    public InmuebleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inmueble, parent, false);
        return new InmuebleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InmuebleViewHolder holder, int position) {
        Inmueble inmueble = listaInmuebles.get(position);
        holder.txtNombre.setText(inmueble.getNombre());
        holder.txtDireccion.setText(inmueble.getDireccion());
        holder.txtPrecio.setText("S/ " + inmueble.getPrecio());

        // Botón de eliminar
        holder.btnEliminar.setOnClickListener(v -> onEliminarClickListener.onEliminarClick(position));
    }

    @Override
    public int getItemCount() {
        return listaInmuebles.size();
    }

    public static class InmuebleViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtDireccion, txtPrecio;
        Button btnEliminar;

        public InmuebleViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtDireccion = itemView.findViewById(R.id.txtDireccion);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
