package com.devzine.inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InmuebleAdapter extends RecyclerView.Adapter<InmuebleAdapter.ViewHolder> {

    private List<Inmueble> listaInmuebles;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public InmuebleAdapter(List<Inmueble> listaInmuebles, OnItemClickListener onItemClickListener) {
        this.listaInmuebles = listaInmuebles;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inmueble, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inmueble inmueble = listaInmuebles.get(position);

        holder.txtNombre.setText(inmueble.getNombre());
        holder.txtPrecio.setText("Precio: S/ " + inmueble.getPrecio());
        holder.txtCantidad.setText("Cantidad: " + inmueble.getCantidad());

        // Cargar la imagen si existe
        if (inmueble.getImagenUri() != null && !inmueble.getImagenUri().isEmpty()) {
            try {
                Uri imagenUri = Uri.parse(inmueble.getImagenUri());
                holder.imgInmueble.setImageURI(imagenUri);
            } catch (Exception e) {
                e.printStackTrace();
                holder.imgInmueble.setImageResource(R.drawable.ic_launcher_background); // Imagen por defecto en caso de error
            }
        } else {
            holder.imgInmueble.setImageResource(R.drawable.ic_launcher_background);
        }

        // Botón eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            Inmueble inmuebleEliminar = listaInmuebles.get(position);
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(holder.itemView.getContext());
                db.inmuebleDao().eliminarInmueble(inmuebleEliminar);
                listaInmuebles.remove(position);
                ((android.app.Activity) holder.itemView.getContext()).runOnUiThread(() -> notifyItemRemoved(position));
            }).start();
        });
    }

    @Override
    public int getItemCount() {
        return listaInmuebles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtPrecio, txtCantidad;
        ImageView imgInmueble;
        Button btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);
            imgInmueble = itemView.findViewById(R.id.imgInmueble);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
