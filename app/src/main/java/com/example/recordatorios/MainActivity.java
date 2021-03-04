package com.example.recordatorios;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import Recordatorios.Modelo;

/**
 * @author Luis
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ColorPickerDialogListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private boolean preferencesChanged=false;

    Modelo listaR;
    RecyclerView jList1;
    AdparterRecordatorios adapter;
    Modelo listaAux;
    AppBarLayout appBarLayout;
    Context context;
    FragmentActivity fragmentActivity;
    NavigationView navigationView;
    Toolbar toolbar;
    Bundle savIns;
    TextView mensaje;
    TextView fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savIns = savedInstanceState;
        setContentView(R.layout.activity_main);

        crearCanalDefault();

        if (savedInstanceState == null) {
            getApplication().onCreate();
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        appBarLayout = findViewById(R.id.app_bar);

        setWelcomeMessage();

        jList1 = findViewById(R.id.jList1);
        jList1.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        jList1.setLayoutManager(llm);
        context = MainActivity.this;
        fragmentActivity = MainActivity.this;

        listaR = new Modelo(context);
        listaAux = new Modelo();
        adapter = new AdparterRecordatorios(listaR, context, fragmentActivity);
        jList1.setAdapter(adapter);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_opc1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setQuery("", false);
        searchView.setQueryHint("Buscar...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                try {
                    listaAux = filter(listaR, s);
                    adapter = new AdparterRecordatorios(listaAux, context, fragmentActivity);
                    jList1.setAdapter(adapter);
                    jList1.notify();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        });

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                searchView.setQuery("", false);
                appBarLayout.setExpanded(false, true);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                appBarLayout.setExpanded(true, true);
                adapter = new AdparterRecordatorios(listaR, context, fragmentActivity);
                jList1.setAdapter(adapter);
                searchView.setQuery("", false);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void crearConfigurarMenu(View view) {
        Intent jFoatingButton = new Intent(this, Configurar.class);
        startActivity(jFoatingButton);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemSelected = menuItem.getItemId();
        switch (itemSelected) {
            case R.id.nav_settings:
                this.startSettingsActivity();
                break;
            case R.id.nav_help:
                this.viewAboutDialog();
                break;
        }
        return true;
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        adapter.setColorR(color, adapter.getPositionR());
    }

    @Override
    public void onDialogDismissed(int dialogId) {
    }

    public void startSettingsActivity() {
        Intent settings = new Intent(this, SettingsActivity.class);
        settings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(settings);
    }

    public void viewAboutDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Acerca de la Aplicación");
        dialog.setMessage("REMINDERS \n   \n Versión: 0.4.8 \n Actualizaciones disponibles: 0");
        dialog.setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        dialog.create();
        dialog.show();
    }

    private Modelo filter(Modelo listaAux, String busqueda) {
        Modelo listaFiltrada = new Modelo();
        try {
            busqueda = busqueda.toLowerCase();
            for (int i = 0; i < listaAux.size(); i++) {
                String recordatorio = listaAux.get(i).getNombre().toLowerCase();
                if (recordatorio.contains(busqueda)) {
                    listaFiltrada.addRecordatorio(listaAux.getRecordatorio(i));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listaFiltrada;
    }

    public void crearCanalDefault(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminders";
            String description = "Notificacion";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("chnelrec", name, importance);
            notificationChannel.setDescription(description);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            NotificationManager notificationManager = (NotificationManager)
                    getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (notificationManager.areNotificationsEnabled()) {
                    notificationManager.cancelAll();
                }
            } else {
                notificationManager.cancelAll();
            }
        }catch (Exception ex){

        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (preferencesChanged){
            this.onCreate(null);
        }
        preferencesChanged=false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        preferencesChanged=true;
    }

    public void setWelcomeMessage(){
        int wday,day,mounth,hour;
        Calendar today=Calendar.getInstance();
        wday=today.get(Calendar.DAY_OF_WEEK);
        mounth=today.get(Calendar.MONTH);
        day=today.get(Calendar.DAY_OF_MONTH);
        hour=today.get(Calendar.HOUR_OF_DAY);

        String mounthDay=
                new DateFormatSymbols().getMonths()[mounth];
        mounthDay = mounthDay.substring(0, 1).toUpperCase() + mounthDay.substring(1);
        String weekday =
                new DateFormatSymbols().getWeekdays()[wday];
        weekday = weekday.substring(0, 1).toUpperCase() + weekday.substring(1);

        String wenos;
        if (hour>=6&&hour<12){
           wenos="Buenos Días";
        }else{
            if (hour>=12&&hour<20){
              wenos="Buenas Tardes";
            }else{
                wenos="Buenas Noches";
            }
        }

        String  menssage = weekday + " " + day + " de "+mounthDay;

        navigationView =findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        mensaje=hView.findViewById(R.id.mensage);
        fecha=hView.findViewById(R.id.actualDate);

        fecha.setText(menssage);
        mensaje.setText(wenos);
    }


}
