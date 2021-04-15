package com.example.recordatorios;

import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.astritveliu.boom.Boom;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mzelzoghbi.zgallery.ZGrid;
import com.mzelzoghbi.zgallery.entities.ZColor;
import com.shivtechs.maplocationpicker.LocationPickerActivity;
import com.shivtechs.maplocationpicker.MapUtility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import Recordatorios.Identificador;
import Recordatorios.Modelo;
import Recordatorios.Paquete;
import Recordatorios.Recordatorio;


public class Configurar extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {
    private Calendar calendarioR;
    private Button jButton1;
    private boolean dataReceived = false;
    private boolean format;
    //Saul
    TextView alarmaRec;
    TextView pdfRec;
    TextView alarmaR;
    TextView location;
    ImageView jImageView;
    EditText notasRec;
    EditText nombreRec;
    Spinner sp;
    String minutey;
    private int day, mounth, year, hour, minute;
    private int dayx, mounthx, yearx, hourx, minutex;
    private int rec, color;
    private int position;
    private String nombre, notas, alarma;
    private Toolbar toolbar;

    private ArrayList<String> list = new ArrayList<>();
    private Paquete pack = new Paquete();
    private PopupMenu popupMenu;

    String[] repetir = {
            "Nunca",
            "Diariamente",
            "Semanalmente",
            "Cada 2 Semanas",
            "Mensualmente",
            "Anualmente",
            "Personalizado"
    };

    String[] base = {
            "Horas",
            "Dias",
            "Semanas",
            "Meses",
            "Años"
    };

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurar);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Configurar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        jButton1 = findViewById(R.id.jButton1);
        alarmaRec = findViewById(R.id.jLabel1);
        nombreRec = findViewById(R.id.jTextField1);
        notasRec = findViewById(R.id.jTextField2);
        location = findViewById(R.id.conf_location);

        jImageView = findViewById(R.id.imageView22);
        pdfRec = findViewById(R.id.textView2);
        alarmaR = findViewById(R.id.repetirAlarma);

        sp = findViewById(R.id.jSpinner);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, repetir);
        sp.setAdapter(adapter);

        MapUtility.apiKey = getResources().getString(R.string.API_KEY);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        format=pref.getBoolean("format", false);

        new Boom(pdfRec);
        new Boom(jImageView);
        new Boom(location);

        if (recibirDatos()) {
            dataReceived = true;
            nombreRec.setText(nombre);
            notasRec.setText(notas);
            alarmaRec.setText(alarma);
            sp.setSelection(rec, false);
            if (!list.isEmpty()) { //IMAGENES
                try {
                    Uri uri = Uri.parse(list.get(0));
                    InputStream imageStream = this.getContentResolver().openInputStream(uri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    jImageView.setImageBitmap(selectedImage);
                    jImageView.setVisibility(View.VISIBLE);
                    jImageView.setEnabled(true);
                } catch (IOException ex) {
                    ex.getCause();
                }
            }
            if (!pack.getNombre().isEmpty()) { //PDF
                pdfRec.setText(pack.getNombre());
                pdfRec.setEnabled(true);
                pdfRec.setVisibility(View.VISIBLE);
            }
            if (!pack.getAddress().isEmpty()) { //Locacion
                location.setText(pack.getAddress());
                location.setEnabled(true);
                location.setVisibility(View.VISIBLE);
            }
        } else {
            dataReceived = false;
            calendarioR = Calendar.getInstance();
        }

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rec = sp.getSelectedItemPosition();
                String st = Integer.toString(rec);
                Log.i("Mensaje: ", st);
                switch (rec) {
                    case 0:
                        alarmaR.setText("");
                        alarmaR.setVisibility(View.GONE);
                        break;
                    case 1:
                        alarmaR.setText("La alarma se repetirá diariamente");
                        alarmaR.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        alarmaR.setText("La alarma se repetira semanalmente");
                        alarmaR.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        alarmaR.setText("La alarma se repetirá cada 2 Semanas");
                        alarmaR.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        alarmaR.setText("La alarma se repetirá mensualmente");
                        alarmaR.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        alarmaR.setText("La alarma se repetirá cada año");
                        alarmaR.setVisibility(View.VISIBLE);
                        break;
                    case 6:
                        alarmaR.setText("La alarma esta en modo personalizado");
                        alarmaR.setVisibility(View.VISIBLE);
                        createPickerDialog();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                rec = 0;
                alarmaR.setVisibility(View.GONE);
            }
        });

        jButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = calendarioR.get(Calendar.YEAR);
                mounth = calendarioR.get(Calendar.MONTH);
                day = calendarioR.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(Configurar.this,
                        Configurar.this, year, mounth, day);
                dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dpd.show();
            }
        });

        pdfRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LocationPickerActivity.class);
                intent.putExtra(MapUtility.ADDRESS, pack.getAddress());
                intent.putExtra(MapUtility.LATITUDE, pack.getLatitud());
                intent.putExtra(MapUtility.LONGITUDE, pack.getLongitud());
                startActivity(intent);
            }
        });

        pdfRec.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                CreatePopupMenu(1);
                return true;
            }
        });

        jImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                CreatePopupMenu(0);
                return true;
            }
        });

        location.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                CreatePopupMenu(2);
                return false;
            }
        });
    }

    public void CreatePopupMenu(int tipo) {
        final int caso = tipo;
        popupMenu = new PopupMenu(this, pdfRec);
        popupMenu.inflate(R.menu.popup_eliminar);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (caso) {
                    case (0):
                        deleteImages();
                        jImageView.setVisibility(View.INVISIBLE);
                        jImageView.setEnabled(false);
                        break;
                    case 1:
                        pdfRec.setText("");
                        pdfRec.setEnabled(false);
                        pack.setNombre("");
                        pack.setFichero("");
                        break;
                    case 2:
                        location.setText("");
                        location.setEnabled(false);
                        location.setVisibility(View.GONE);
                        pack.setAddress("");
                        pack.setLatitud(0.0);
                        pack.setLongitud(0.0);
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_r, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.aceptarbuton:
                returnMain();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearx = i;
        mounthx = i1;
        dayx = i2;
        hour = calendarioR.get(Calendar.HOUR);
        minute = calendarioR.get(Calendar.MINUTE);


        TimePickerDialog tpd = new TimePickerDialog(Configurar.this, Configurar.this,
                hour, minute, format);
        tpd.show();
    }

    @Override
    public void onTimeSet(TimePicker tP, int i, int i1) {
        hourx = i;
        minutex = i1;
        Calendar c = Calendar.getInstance();
        c.clear();

        c.set(Calendar.YEAR, yearx);
        c.set(Calendar.MONTH, mounthx);//Esta atrasado por 1
        c.set(Calendar.DAY_OF_MONTH, dayx);
        c.set(Calendar.HOUR, hourx);
        c.set(Calendar.MINUTE, minutex);


        int hora=c.get(Calendar.HOUR);

        if (minutex < 10) {
            minutey = "0" + minutex;
        } else {
            minutey = Integer.toString(minutex);
        }
        mounthx += 1;
        calendarioR.clear();
        calendarioR = c;
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        String weekday = new DateFormatSymbols()
                .getShortWeekdays()[dayOfWeek];

        String  st = weekday + ", " + dayx + "/" + mounthx + "/" + yearx + ", ";

        if (format){
            st+= hourx + ":" + minutey;
        }else{
            String aa = "p.m.";
            if (c.get(Calendar.AM_PM) == 0) {
                aa = "a.m.";
            }
            st+= hora + ":"+ minutey +" "+ aa;
        }
        alarmaRec.setText(st);

    }

    public void createPickerDialog() {
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMaxValue(100);
        numberPicker.setMinValue(1);

        final NumberPicker numberPicker1 = new NumberPicker(this);
        numberPicker1.setMaxValue(5);
        numberPicker1.setMinValue(1);
        numberPicker1.setDisplayedValues(base);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 50);

        LinearLayout.LayoutParams numPicerParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        numPicerParams.weight = 1;

        LinearLayout.LayoutParams qPicerParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        qPicerParams.weight = 1;


        ll.setLayoutParams(params);
        ll.addView(numberPicker, numPicerParams);
        ll.addView(numberPicker1, qPicerParams);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Personalizar");
        alertDialogBuilder.setView(ll);
        alertDialogBuilder
                .setCancelable(true)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        sp.setSelection(0);
                    }
                })
                .setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.e("", "New Quantity Value : " + numberPicker.getValue());
                            }
                        })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                sp.setSelection(0);
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void returnMain() {
        String titulo = nombreRec.getText().toString();
        String st1 = notasRec.getText().toString();
        if (titulo.isEmpty()) {
            Toast toast = Toast.makeText(this, "Introduce un Nombre", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Calendar instance = Calendar.getInstance();
            if (calendarioR.before(instance)) {
                Toast toast = Toast.makeText(this, "Introduce una alarma correcta",
                        Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Recordatorio r = new Recordatorio(titulo, st1, calendarioR, rec, list, pack);
                Identificador ident = new Identificador(getApplicationContext());
                r.setIdentificador(ident.newIdentificador());

                Modelo modelo = new Modelo(this);
                if (!dataReceived) {
                    modelo.addRecordatorio(r);
                    modelo.guardar();
                    position = modelo.size();
                } else {
                    r.setColorR(color);
                    modelo.modificarRecordatorio(r, position);
                    modelo.guardar();
                }

                NotificacionRec notificacion = new NotificacionRec(titulo, r.getIdentificador(),
                        calendarioR.getTimeInMillis(), getApplicationContext());
                notificacion.crearAlarma();

                Intent jButton2 = new Intent(this, MainActivity.class);
                jButton2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(jButton2);
                this.finish();
            }
        }
    }

    public boolean recibirDatos() {
        try {
            Bundle datos = getIntent().getExtras();
            position = datos.getInt("D0");

            Modelo modelo = new Modelo(this);

            Recordatorio recordatorio = new Recordatorio();
            recordatorio = modelo.getRecordatorio(position);
            nombre = recordatorio.getNombre();
            alarma = recordatorio.getAlarma(format);
            notas = recordatorio.getNotas();
            rec = recordatorio.getRecordar();
            color = recordatorio.getColorR();
            list = recordatorio.getImagenes();
            pack = recordatorio.getPaquete();
            calendarioR = recordatorio.getFecha();

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void onCreateButtonMenu(View view) {
        BottomMenu bottomMenu = new BottomMenu(this, jImageView, pdfRec, location, list, pack);
        bottomMenu.show(getSupportFragmentManager(), "BottomMenu");
        pack = bottomMenu.getPack();
        list = bottomMenu.getImageList();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    public void gallery(View v) {
        ZGrid.with(this, list)
                .setToolbarColorResId(R.color.colorPrimary)
                .setTitle("Imágenes Adjuntas")
                .setToolbarTitleColor(ZColor.WHITE)
                .setSpanCount(3)
                .setGridImgPlaceHolder(R.color.colorPrimary)
                .show();
    }

    public void deleteImages() {
        if (!list.isEmpty()) {
            int n = list.size();
            for (int i = 0; i < n; i++) {
                Uri uri = Uri.parse(list.get(i));
                new File(uri.getPath()).getAbsoluteFile().delete();
            }
            list.clear();
        }
    }

}

