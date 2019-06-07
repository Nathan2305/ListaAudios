package com.example.listaaudios;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton playStop;
    RecyclerView recycler;
    RecyclerView.LayoutManager layoutManager;
    static RecyclerView.Adapter adapter;
    public static String FOLDER_AUDIO = "";
    public static AppCompatSeekBar seekBar;
    static File file;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    static ArrayList<String> listAudios = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences=getSharedPreferences("AUDIO_PREFERENCES", Context.MODE_PRIVATE);
        seekBar = findViewById(R.id.seekBar);
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
        playStop = findViewById(R.id.playStop);
        playStop.setImageResource(R.drawable.mic_icon);
        playStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.contains("STATE")) {
                    if (sharedPreferences.getString("STATE","").equalsIgnoreCase("PLAYING")){
                        //Detener Audio
                        stopService(new Intent(getApplicationContext(), RecorderService.class));
                    }else { //Iniciar Audio
                        Intent intentRecordAudio = new Intent(getApplicationContext(), RecorderService.class);
                        intentRecordAudio.putExtra(Utils.RECORD_AUDIO, Utils.RECORD_AUDIO);
                        startService(intentRecordAudio);
                    }
                } else {
                    Intent intentRecordAudio = new Intent(getApplicationContext(), RecorderService.class);
                    intentRecordAudio.putExtra(Utils.RECORD_AUDIO, Utils.RECORD_AUDIO);
                    startService(intentRecordAudio);
                }
            }
        });


    }

    private void startPlayingAudio(String audioPath) {
        Intent intentPlayAudio = new Intent(getApplicationContext(), RecorderService.class);
        intentPlayAudio.putExtra(Utils.PLAY_AUDIO, Utils.PLAY_AUDIO);
        intentPlayAudio.putExtra(Utils.AUDIO_PATH, FOLDER_AUDIO + "/" + audioPath);
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
