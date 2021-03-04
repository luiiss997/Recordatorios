package com.example.recordatorios;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.astritveliu.boom.Boom;
import com.mzelzoghbi.zgallery.ZGrid;
import com.mzelzoghbi.zgallery.entities.ZColor;
import com.shivtechs.maplocationpicker.LocationPickerActivity;
import com.shivtechs.maplocationpicker.MapUtility;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import Recordatorios.Modelo;
import Recordatorios.Paquete;
import Recordatorios.Recordatorio;

public class VerRecordatorio extends AppCompatActivity {
    private Recordatorio recordatorio;
    private TextView NombreR;
    private TextView NotasR;
    private TextView AlarmaR;
    private TextView RepetirR;
    private TextView DocumentoR;
    private TextView LocacionR;
    private ImageView ImagenesR;
    private ArrayList ListaImagenesR;
    private Paquete pack;
    private Toolbar toolbar;
    private int ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordatorio);

        recordatorio = recibirDatos();

        toolbar = findViewById(R.id.toolbarR);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Ver Detalles");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(recordatorio.getColorR());
        colorTextTool colorTextTool = new colorTextTool();
        int colorFont = colorTextTool.colorTextTool(recordatorio.getColorR());
        toolbar.setTitleTextColor(colorFont);

        final Drawable upArrow;
        if (colorFont == Color.BLACK) {
            upArrow = getResources().getDrawable(R.drawable.arrow_back_black);
        } else {
            upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white);
        }
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean format=pref.getBoolean("format", false);

        NombreR = findViewById(R.id.nombreR);
        NotasR = findViewById(R.id.notasR);
        AlarmaR = findViewById(R.id.alarmaR);
        RepetirR = findViewById(R.id.repetirR);
        DocumentoR = findViewById(R.id.pdfR);
        ImagenesR = findViewById(R.id.imagenR);
        LocacionR = findViewById(R.id.locationR);

        NombreR.setText(recordatorio.getNombre());
        String alarma = "Alarma: " + recordatorio.getAlarma(format);
        AlarmaR.setText(alarma);

        MapUtility.apiKey = getResources().getString(R.string.API_KEY);

        switch (recordatorio.getRecordar()) {
            case 0:
                RepetirR.setText("Repetir: Nunca");
                break;
            case 1:
                RepetirR.setText("Repetir: Diario");
                break;
            case 2:
                RepetirR.setText("Repetir: Semanalmente");
                break;
            case 3:
                RepetirR.setText("Repetir: Cada 2 Semanas");
                break;
            case 4:
                RepetirR.setText("Repetir: Mensualmente");
                break;
            case 5:
                RepetirR.setText("Repetir: Anualmente");
                break;
            case 6:
                RepetirR.setText("Repetir: Personalizadamente");
                break;
        }

        if (recordatorio.getNotas().isEmpty()) {
            NotasR.setVisibility(View.GONE);
        } else {
            String notas = "Notas: \n" + recordatorio.getNotas();
            NotasR.setText(notas);
            NotasR.setVisibility(View.VISIBLE);
        }

        pack = recordatorio.getPaquete();
        if (pack.getFichero().isEmpty()) {
            DocumentoR.setEnabled(false);
        } else {
            DocumentoR.setEnabled(true);
            DocumentoR.setVisibility(View.VISIBLE);
            DocumentoR.setText(pack.getNombre());
            new Boom(DocumentoR);
        }

        ListaImagenesR = recordatorio.getImagenes();
        if (ListaImagenesR.isEmpty()) {
            ImagenesR.setEnabled(false);
            ImagenesR.setVisibility(View.INVISIBLE);
        } else {
            try {
                Uri uri = Uri.parse((String) ListaImagenesR.get(0));
                InputStream imageStream = this.getContentResolver().openInputStream(uri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ImagenesR.setImageBitmap(selectedImage);
                ImagenesR.setVisibility(View.VISIBLE);
                ImagenesR.setEnabled(true);
            } catch (IOException ex) {
                ex.getCause();
            }
        }

        if (!pack.getAddress().isEmpty()) {
            LocacionR.setText(pack.getAddress());
            LocacionR.setEnabled(true);
            LocacionR.setVisibility(View.VISIBLE);
        }
    }

    public Recordatorio recibirDatos() {
        int posicion = 0;
        try {
            Bundle datos = getIntent().getExtras();
            posicion = datos.getInt("RecordatorioID");
        } catch (Exception ex) {
            ex.getCause();
        }
        Modelo listaPrincipal = new Modelo(this);
        Recordatorio recordatorio = listaPrincipal.getRecordatorio(posicion);
        ID = recordatorio.getIdentificador();
        return recordatorio;
    }

    public void verDocumenteo(View V) {
        Uri data = Uri.parse(pack.getFichero());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(data, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            intent = Intent.createChooser(intent, "Completar Accion Con");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.getCause();
        }
    }

    public void verFotos(View view) {
        //verificarImagenes();
        ZGrid.with(this, ListaImagenesR)
                .setToolbarColorResId(R.color.colorPrimary)
                .setTitle("Imágenes Adjuntas")
                .setToolbarTitleColor(ZColor.WHITE)
                .setSpanCount(3)
                .setGridImgPlaceHolder(R.color.colorPrimary)
                .show();
    }

    public void verLocacion(View v) {
        Intent intent = new Intent(getApplicationContext(), LocationPickerActivity.class);
        intent.putExtra(MapUtility.ADDRESS, pack.getAddress());
        intent.putExtra(MapUtility.LATITUDE, pack.getLatitud());
        intent.putExtra(MapUtility.LONGITUDE, pack.getLongitud());
        startActivity(intent);
    }

}
