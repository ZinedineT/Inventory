package com.devzine.inventory;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.List;
import android.content.Intent; // Para usar Intent
import android.app.Activity; // Para usar Activity

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
        context= parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_inmueble, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inmueble inmueble = listaInmuebles.get(position);

        holder.txtNombre.setText(inmueble.getNombre());
        holder.txtCodigo.setText("Codigo: " + inmueble.getCodigo());
        holder.txtCantidad.setText("Cantidad: " + inmueble.getCantidad());
        holder.txtPrecio.setText("Precio: S/ " + inmueble.getPrecio());


        // Cargar la imagen usando Glide
        if (inmueble.getImagenUri() != null && !inmueble.getImagenUri().isEmpty()) {
            Glide.with(context)
                    .load(inmueble.getImagenUri())
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Opcional: mejora el rendimiento del caché
                    .centerCrop()
                    .into(holder.imgInmueble);
        } else {
            // Imagen por defecto o dejar el ImageView vacío
            holder.imgInmueble.setImageResource(R.drawable.agregar); // Reemplaza con tu imagen predeterminada
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
        // Botón editar
        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, AgregarInmuebleActivity.class);
            intent.putExtra("MODO_EDICION", true); // Indicar que estamos en modo edición
            intent.putExtra("ID_INMUEBLE", inmueble.getId()); // Pasar el ID del inmueble
            intent.putExtra("NOMBRE", inmueble.getNombre());
            intent.putExtra("CODIGO", inmueble.getCodigo());
            intent.putExtra("CANTIDAD", inmueble.getCantidad());
            intent.putExtra("PRECIO", inmueble.getPrecio());
            intent.putExtra("IMAGEN_URI", inmueble.getImagenUri());
            intent.putExtra("AREA", inmueble.getArea());
            ((Activity) context).startActivityForResult(intent, 2); // Usar un código de solicitud diferente (2)
        });
    }

    @Override
    public int getItemCount() {
        return listaInmuebles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre,txtCodigo, txtPrecio, txtCantidad;
        ImageView imgInmueble;
        Button btnEliminar, btnEditar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtCodigo = itemView.findViewById(R.id.txtCodigo);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);
            imgInmueble = itemView.findViewById(R.id.imgInmueble);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }
}
