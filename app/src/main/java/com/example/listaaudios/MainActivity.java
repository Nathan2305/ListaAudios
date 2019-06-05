package com.example.listaaudios;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button grabar, detener;
    RecyclerView recycler;
    RecyclerView.LayoutManager layoutManager;
    static RecyclerView.Adapter adapter;
    public static String FOLDER_AUDIO = "";
    static File file;
    static ArrayList<String> listAudios = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FOLDER_AUDIO = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Cibertec/Audios";
        file = new File(FOLDER_AUDIO);
        if (!file.exists()) {
            if (file.mkdirs()) {  //crea directios incluyendo la carpeta padre
                Utils.showToast(getApplicationContext(), "Se creó el directorio " + FOLDER_AUDIO);
            } else {
                Utils.showToast(getApplicationContext(), "Ya existe el directorio " + FOLDER_AUDIO);
            }
        }
        recycler = findViewById(R.id.recycler);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        loadListAudios(file);
        adapter = new AdapterForAudios(getApplicationContext(), listAudios);
        ((AdapterForAudios) adapter).setOnItemClickListener(new AdapterForAudios.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                startPlayingAudio(listAudios.get(position));
            }
        });
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
        recycler.hasFixedSize();
        grabar = findViewById(R.id.grabar);
        detener = findViewById(R.id.detener);
        grabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRecordAudio = new Intent(getApplicationContext(), RecorderService.class);
                intentRecordAudio.putExtra(Utils.RECORD_AUDIO, Utils.RECORD_AUDIO);
                startService(intentRecordAudio);
            }
        });
        detener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), RecorderService.class));
            }
        });


    }

    private void startPlayingAudio(String audioPath) {
        Intent intentPlayAudio = new Intent(getApplicationContext(), RecorderService.class);
        intentPlayAudio.putExtra(Utils.PLAY_AUDIO, Utils.PLAY_AUDIO);
        intentPlayAudio.putExtra(Utils.AUDIO_PATH,FOLDER_AUDIO+"/"+audioPath);
        startService(intentPlayAudio);
    }

    private static void loadListAudios(File file) {
        listAudios = new ArrayList<>();
        File[] audios = file.listFiles();
        for (File each_audio : audios) {
            listAudios.add(each_audio.getName());
        }

    }

    public static void addNewAudio(String name) {
        listAudios.add(name);
        loadListAudios(file);
        adapter.notifyDataSetChanged();
    }
}
