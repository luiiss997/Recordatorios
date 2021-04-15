package com.example.recordatorios;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;

import java.io.File;
import java.util.ArrayList;

import Recordatorios.Modelo;
import Recordatorios.Recordatorio;


public class AdparterRecordatorios extends RecyclerView.Adapter<AdparterRecordatorios.ViewHolder> {
    private Modelo listaR;
    private Context mContext;
    private int positionR;
    private FragmentActivity fragmentActivity;
    private boolean format;
    //Comentario
    public AdparterRecordatorios(Modelo listaR, Context context, FragmentActivity fragmentActivity) {
        this.listaR = listaR;
        this.mContext = context;
        this.fragmentActivity = fragmentActivity;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        this.format=pref.getBoolean("format", false);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.jlist_item,
                null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setRecordatorios(listaR.get(position));
        final Recordatorio r = listaR.getRecordatorio(position);
        holder.nombre.setText(r.getNombre());
        holder.alarma.setText(r.getAlarma(format));
        holder.color.setColorFilter(r.getColorR());
        holder.OptionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.OptionMenu);
                popupMenu.inflate(R.menu.menu_opciones);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.jMenuItem1: //Modificar recordatorio
                                discardNotification(r.getIdentificador());
                                Intent intent = new Intent(mContext, Configurar.class);
                                intent.putExtra("D0", position);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                mContext.startActivity(intent);
                                break;
                            case R.id.jMenuItem2: //Eliminar Recordatorio
                                eraseImages(r);
                                listaR.eliminarRecordatorio(position);
                                discardNotification(r.getIdentificador());
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, listaR.size());
                                listaR.guardar();
                                Snackbar.make(view, "Recordatorio Descartado!", Snackbar.LENGTH_LONG).show();
                                break;
                            case R.id.jMenuItem0: //Modificar Color
                                setPositionR(position);
                                ColorPickerDialog.newBuilder().setAllowCustom(false).
                                        setShowAlphaSlider(false).setColor(Color.WHITE).setShowColorShades(false).
                                        setDialogId(0).show(fragmentActivity);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, VerRecordatorio.class);
                intent.putExtra("RecordatorioID", position);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
            }
        });
    }

    public void eraseImages(Recordatorio recordatorio) {
        if (!recordatorio.getImagenes().isEmpty()) {
            ArrayList<String> arr = recordatorio.getImagenes();
            int n = arr.size();
            for (int i = 0; i < n; i++) {
                Uri uri = Uri.parse(arr.get(i));
                new File(uri.getPath()).getAbsoluteFile().delete();
            }
        }
    }

    public void discardNotification(int ID) {
        NotificacionRec delete = new NotificacionRec(mContext, ID);
        delete.cancelarAlarma();
        delete.cancelarNotificacion();
    }

    @Override
    public int getItemCount() {
        return listaR.size();
    }

    public int getPositionR() {
        return positionR;
    }

    public void setPositionR(int positionR) {
        this.positionR = positionR;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        TextView alarma;
        ImageView OptionMenu;
        ImageView color;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.nombre = itemView.findViewById(R.id.Nombre);
            this.alarma = itemView.findViewById(R.id.Alarma);
            this.color = itemView.findViewById(R.id.circle);
            this.OptionMenu = itemView.findViewById(R.id.OptionMenu);
            this.parentLayout = itemView.findViewById(R.id.cardViewR);
        }

        public void setRecordatorios(Recordatorio recordatorio) {
            nombre.setText(recordatorio.getNombre());
            alarma.setText(recordatorio.getAlarma(format));
            color.setColorFilter(recordatorio.getColorR());
            if (recordatorio.isRealizado()) {
                nombre.setTextColor(Color.RED);
            }
        }
    }

    public void setColorR(int color, int pos) {
        listaR.getRecordatorio(pos).setColorR(color);
        listaR.guardar();
        notifyItemChanged(pos);
    }

}
