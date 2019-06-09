package com.example.listaaudios;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static FloatingActionButton playStop;
    RecyclerView recycler;
    RecyclerView.LayoutManager layoutManager;
    static RecyclerView.Adapter adapter;
    public static String FOLDER_AUDIO = "";
    public static AppCompatSeekBar seekBar;
    public static File file;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    static ArrayList<String> listAudios = null;
    public static TextView estado;
    public static ImageView statePlaying;
    public static int draw_play, draw_stop, statePlaying_play, statePlaying_stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statePlaying = findViewById(R.id.statePlaying);
        draw_play = R.drawable.baseline_record_voice_over_white_48;
        draw_stop = R.drawable.audio_icon;
        statePlaying_play = R.drawable.play_btn;
        statePlaying_stop = R.drawable.stop_btn;
        estado = findViewById(R.id.estado);
        sharedPreferences = getApplicationContext().getSharedPreferences("AUDIO_PREFERENCES", MODE_PRIVATE);
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
        if (!sharedPreferences.contains("PLAYSTOP_IMAGE")) {
            editor = sharedPreferences.edit();
            editor.putInt("PLAYSTOP_IMAGE", draw_stop);
            editor.apply();
            playStop.setImageDrawable(getResources().getDrawable(sharedPreferences.getInt("PLAYSTOP_IMAGE", 0)));
        }
        if (!sharedPreferences.contains("PLAYING_STATE_IMAGE")) {
            editor = sharedPreferences.edit();
            editor.putInt("PLAYING_STATE_IMAGE", statePlaying_stop);
            editor.apply();
            statePlaying.setImageDrawable(getResources().getDrawable(sharedPreferences.getInt("PLAYING_STATE_IMAGE", 0)));
        }
        playStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.contains("STATE")) {
                    if ("PLAYING".equalsIgnoreCase(sharedPreferences.getString("STATE", ""))) {
                        //Detener Audio
                        stopService(new Intent(MainActivity.this, RecorderService.class));
                    } else { //Iniciar Audio
                        Intent intentRecordAudio = new Intent(MainActivity.this, RecorderService.class);
                        intentRecordAudio.putExtra(Utils.RECORD_AUDIO, Utils.RECORD_AUDIO);
                        startService(intentRecordAudio);
                    }
                } else {
                    Intent intentRecordAudio = new Intent(MainActivity.this, RecorderService.class);
                    intentRecordAudio.putExtra(Utils.RECORD_AUDIO, Utils.RECORD_AUDIO);
                    startService(intentRecordAudio);

                }
            }
        });
        statePlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RecorderService.mediaPlayer != null) {
                    if (RecorderService.mediaPlayer.isPlaying()) {
                        RecorderService.mediaPlayer.stop();
                        editor = sharedPreferences.edit();
                        editor.putInt("PLAYING_STATE_IMAGE", statePlaying_play);
                        editor.apply();
                        statePlaying.setImageDrawable(getResources().getDrawable(sharedPreferences.getInt("PLAYING_STATE_IMAGE", 0)));
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.contains("STATE_TXT")) {
            estado.setText(sharedPreferences.getString("STATE_TXT", ""));
        }
        if (sharedPreferences.contains("PLAYSTOP_IMAGE")) {
            playStop.setImageDrawable(getResources().getDrawable(sharedPreferences.getInt("PLAYSTOP_IMAGE", 0)));
        }
        if (sharedPreferences.contains("PLAYING_STATE_IMAGE")) {
            statePlaying.setImageDrawable(getResources().getDrawable(sharedPreferences.getInt("PLAYING_STATE_IMAGE", 0)));
        }


    }

    private void startPlayingAudio(String audioPath) {
        Intent intentPlayAudio = new Intent(getApplicationContext(), RecorderService.class);
        intentPlayAudio.putExtra(Utils.PLAY_AUDIO, Utils.PLAY_AUDIO);
        intentPlayAudio.putExtra(Utils.AUDIO_PATH, FOLDER_AUDIO + "/" + audioPath);
        startService(intentPlayAudio);
    }

    private static void loadListAudios(File file) {
        if (file != null) {
            listAudios = new ArrayList<>();
            File[] audios = file.listFiles();
            if (audios != null) {
                for (File each_audio : audios) {
                    listAudios.add(each_audio.getName());
                }
            }
        }
    }

    public static void addNewAudio(String name) {
        listAudios.add(name);
        loadListAudios(file);
        adapter.notifyDataSetChanged();
    }
}
