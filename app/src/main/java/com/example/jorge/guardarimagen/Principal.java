package com.example.jorge.guardarimagen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class Principal extends Activity {
    private EditText etUrl,etNombre;
    public static final String url ="";
    private ImageView imgImagen;
    private Button btDescarga;
    private RadioButton rbPublico,rbPrivado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
        etUrl = (EditText)findViewById(R.id.etUrl);
        etNombre = (EditText)findViewById(R.id.etGuardar);
        btDescarga = (Button)findViewById(R.id.btDescargar);
        rbPublico = (RadioButton)findViewById(R.id.rbPublico);
        rbPrivado = (RadioButton)findViewById(R.id.rbPrivado);
        imgImagen = (ImageView)findViewById(R.id.imageView);
        btDescarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CargaImagenes nuevaTarea = new CargaImagenes();
                nuevaTarea.execute(url);
            }
        });
    }

    private class CargaImagenes extends AsyncTask<String, Void, Bitmap> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(Principal.this);
            pDialog.setMessage("Cargando Imagen");
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap imagen = descargarImagen(url);
            guardarImagen(url);
            return imagen;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            imgImagen.setImageBitmap(result);
            pDialog.dismiss();
        }

    }

    private Bitmap descargarImagen (String imageHttpAddress){
        URL imageUrl = null;
        InputStream is = null;
        Bitmap imagen = null;
        imageHttpAddress = etUrl.getText().toString();
        try{
            imageUrl = new URL(imageHttpAddress);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
            imagen = BitmapFactory.decodeStream(conn.getInputStream());
        }catch(IOException ex){
            ex.printStackTrace();
        }

        return imagen;
    }

    public void guardarImagen(String imageHttpAddress) {
        URL url;
        File f = null;
        InputStream is = null;
        String directorio="",nomGuardar, nomArchivo="",extension="";
        imageHttpAddress = etUrl.getText().toString();
        nomGuardar = etNombre.getText().toString();
        if(rbPublico.isChecked()){
            directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        }else if(rbPrivado.isChecked()){
            directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        }
        try {
            url = new URL(imageHttpAddress);
            is = url.openStream();
            if (nomGuardar.equals("")){
                nomArchivo = imageHttpAddress.substring(imageHttpAddress.lastIndexOf("/"));
                f = new File(directorio,nomArchivo);
            }else if(!nomGuardar.equals("")){
                extension = imageHttpAddress.substring(imageHttpAddress.lastIndexOf("."));
                f = new File(directorio,nomGuardar+extension);
            }
            FileOutputStream os = new FileOutputStream(f);
            byte[] b = new byte[2048];
            int length;
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
