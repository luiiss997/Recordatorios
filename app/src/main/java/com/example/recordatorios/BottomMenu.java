package com.example.recordatorios;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.astritveliu.boom.Boom;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.shivtechs.maplocationpicker.LocationPickerActivity;
import com.shivtechs.maplocationpicker.MapUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import Recordatorios.Paquete;


public class BottomMenu extends BottomSheetDialogFragment implements View.OnClickListener {

    private LinearLayout tomarFoto;
    private LinearLayout selectImagen;
    private LinearLayout selectArch;
    private LinearLayout selectLoc;
    private final int SELECT_PICTURE = 200;
    private final int TAKE_FOTO = 100;
    private final int SELECT_PDF = 300;
    private final int ADDRESS_PICKER_REQUEST = 400;
    private ImageView imageView;
    private TextView textView;
    private TextView location;
    private Activity activity;
    private Context context;

    private Uri ficheroSalida;
    private String fileName;

    private Uri ficheroImagen;
    private ArrayList<String> imageList;
    private String folder = "/Recordatorios";
    private String imageName = "recordatorio";

    private boolean permisos = false;

    private Paquete pack;

    private PedirPermisos pedirPermisos;

    public BottomMenu() {
    }

    public BottomMenu(Activity activity, ImageView imageView, TextView textView, TextView location,
                      ArrayList list, Paquete pack) {
        this.textView = textView;
        this.imageView = imageView;
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.imageList = list;
        this.pack = pack;
        this.location = location;
    }

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public Paquete getPack() {
        return pack;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_menu, container, false);
        tomarFoto = v.findViewById(R.id.tomarfoto_l);
        selectImagen = v.findViewById(R.id.select_image);
        selectArch = v.findViewById(R.id.select_archive);
        selectLoc = v.findViewById(R.id.select_location);

        tomarFoto.setOnClickListener(this);
        selectImagen.setOnClickListener(this);
        selectArch.setOnClickListener(this);
        selectLoc.setOnClickListener(this);

        pedirPermisos = new PedirPermisos(activity, context);
        permisos = pedirPermisos.isPermisos();

        MapUtility.apiKey = getResources().getString(R.string.API_KEY);

        new Boom(tomarFoto);
        new Boom(selectArch);
        new Boom(selectImagen);
        new Boom(selectLoc);

        return v;
    }

    @Override
    public void onClick(View view) {
        if (permisos) {
            switch (view.getId()) {
                case R.id.tomarfoto_l:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) { //Puede que sea otra version :v
                        ficheroImagen = FileProvider.getUriForFile(context, context.
                                        getApplicationContext().getPackageName() + ".fileprovider",
                                createFile());
                    } else {
                        File file = createFile();
                        ficheroImagen = Uri.fromFile(file);
                    }

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, ficheroImagen);
                    startActivityForResult(intent, TAKE_FOTO);

                    break;
                case R.id.select_image:
                    String action;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        action = Intent.ACTION_OPEN_DOCUMENT;
                    } else {
                        action = Intent.ACTION_PICK;
                    }
                    Intent intent1 = new Intent(action);
                    //intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent1.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent1, "Seleccionar"), SELECT_PICTURE);
                    break;
                case R.id.select_archive:
                    Intent intent3 = new Intent(Intent.ACTION_GET_CONTENT);
                    intent3.setType("application/pdf");
                    intent3.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(Intent.createChooser(intent3, "Seleccionar"), SELECT_PDF);
                    break;
                case R.id.select_location:
                    Intent i = new Intent(context, LocationPickerActivity.class);
                    startActivityForResult(i, ADDRESS_PICKER_REQUEST);
                    break;
            }
        } else {
            //Permisos denegados
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED) {
            dismiss();
        } else {
            switch (requestCode) {
                case SELECT_PICTURE:
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = activity.getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imageView.setImageBitmap(selectedImage);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setEnabled(true);
                        imageList.add(String.valueOf(imageUri));
                        // if (data.hasExtra(Intent.EXTRA_ALLOW_MULTIPLE)){
                        //imageList.addAll(data.getStringArrayListExtra());
                        // }
                    } catch (NullPointerException | FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case TAKE_FOTO:
                    try {
                        if (data != null) {
                            if (data.hasExtra("data")) {
                                Bitmap bitmap1 = data.getParcelableExtra("data");
                                imageView.setImageBitmap(bitmap1);
                                imageList.add(String.valueOf(data.getData()));
                                imageView.setVisibility(View.VISIBLE);
                                imageView.setEnabled(true);
                            } else {
                                final InputStream imageStream = activity.getContentResolver()
                                        .openInputStream(ficheroImagen);
                                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                imageView.setImageBitmap(selectedImage);
                                imageView.setVisibility(View.VISIBLE);
                                imageList.add(String.valueOf(ficheroImagen));
                            }
                        } else {
                            final InputStream imageStream = activity.getContentResolver()
                                    .openInputStream(ficheroImagen);
                            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            imageView.setImageBitmap(selectedImage);
                            imageView.setVisibility(View.VISIBLE);
                            imageList.add(String.valueOf(ficheroImagen));
                        }
                        imageView.setEnabled(true);
                    } catch (NullPointerException | FileNotFoundException ex) {
                        dismiss();
                    }
                    break;
                case SELECT_PDF:
                    try {
                        Uri uri = data.getData();
                        fileName = getFileName(uri);

                        if (fileName.endsWith("pdf")) {
                            ficheroSalida = uri;
                            textView.setText(fileName);
                            textView.setEnabled(true);
                            textView.setVisibility(View.VISIBLE);
                            pack.setFichero(uri.toString());
                            pack.setNombre(fileName);
                        } else {
                            Snackbar.make(activity.getCurrentFocus(),
                                    "Solo se pueder guardar documentos PDF",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (NullPointerException ex) {
                        dismiss();
                    }
                    break;
                case ADDRESS_PICKER_REQUEST:
                    try {
                        if (data != null && data.getStringExtra(MapUtility.ADDRESS) != null) {
                            String address = data.getStringExtra(MapUtility.ADDRESS);
                            double selectedLatitude = data.getDoubleExtra(MapUtility.LATITUDE, 0.0);
                            double selectedLongitude = data.getDoubleExtra(MapUtility.LONGITUDE, 0.0);
                            pack.setAddress(address);
                            pack.setLatitud(selectedLatitude);
                            pack.setLongitud(selectedLongitude);
                            location.setText(address);
                            location.setEnabled(true);
                            location.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception ex) {
                        dismiss();
                        ex.printStackTrace();
                    }
                    break;
            }
        }
        dismiss();
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = activity.getContentResolver().query(uri, null, null,
                    null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String getCurrentDateAndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String date = sdf.format(c.getTime());
        return date;
    }

    private File createFile() {
        String filePath = context.getExternalFilesDir(null).getAbsolutePath() + folder;
        //String filePath = Environment.getDataDirectory().getAbsolutePath() + folder;
        String currentTime = getCurrentDateAndTime();
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File image = new File(dir, imageName + currentTime + ".jpg");
        return image;
    }

}
