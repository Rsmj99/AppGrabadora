package com.example.tarea13_ejercicio02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ListView lv_view;
    private MediaRecorder grabacion;
    private String archivoSalida = null;
    private ImageButton ib_record;
    ArrayList<File> arrayList;
    private MediaPlayer vectormp[] = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ib_record = findViewById(R.id.ib_record);
        lv_view = findViewById(R.id.lv_grabaciones);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1000);
        }

        Cargar_Lista();
    }

    private void Cargar_Lista(){
        arrayList = TraerGrabaciones(Environment.getExternalStorageDirectory());
        customAdapter customAdapter= new customAdapter();
        lv_view.setAdapter(customAdapter);
        vectormp = new MediaPlayer[arrayList.size()];
        Cargar_Pistas();
    }

    public void Recorder(View view){
        if (grabacion == null){
            String name = new Date().toString().replaceAll("[\\s+:]","");
            archivoSalida = Environment.getExternalStorageDirectory() + "/" + name + ".mp3";
            grabacion = new MediaRecorder();
            grabacion.setAudioSource(MediaRecorder.AudioSource.MIC);
            grabacion.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            grabacion.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            grabacion.setOutputFile(archivoSalida);
            try {
                grabacion.prepare();
                grabacion.start();
            } catch (IOException e){

            }
            ib_record.setBackgroundResource(R.drawable.ic_recordon);
            Toast.makeText(this, "Grabando...", Toast.LENGTH_SHORT).show();
        } else if (grabacion != null){
            grabacion.stop();
            grabacion.release();
            grabacion=null;
            ib_record.setBackgroundResource(R.drawable.ic_recordoff);
        }
        Cargar_Lista();
    }

    private ArrayList<File> TraerGrabaciones(File file) {

        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();
        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                arrayList.addAll(TraerGrabaciones(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3")) {
                    arrayList.add(singleFile);
                }
            }
        }
        return arrayList;
    }

    private void Cargar_Pistas(){
        for (int i=0; i< arrayList.size(); i++){
            vectormp[i] = MediaPlayer.create(this, Uri.parse(arrayList.get(i).toString()));
        }
    }

    class customAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View  myView= getLayoutInflater().inflate(R.layout.list_item, null);
            TextView tv_song= myView.findViewById(R.id.tv_songname);
            tv_song.setSelected(true);
            tv_song.setText(arrayList.get(position).getName());
            myView.findViewById(R.id.btn_play_pause).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(vectormp[position].isPlaying())
                    {
                        myView.findViewById(R.id.btn_play_pause).setBackgroundResource(R.drawable.ic_play);
                        vectormp[position].pause();
                    }
                    else{
                        myView.findViewById(R.id.btn_play_pause).setBackgroundResource(R.drawable.ic_pause);
                        vectormp[position].start();
                    }
                }
            });

            return myView;
        }
    }
}