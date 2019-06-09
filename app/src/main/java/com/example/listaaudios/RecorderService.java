package com.example.listaaudios;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.MediaController;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import static com.example.listaaudios.MainActivity.FOLDER_AUDIO;
import static com.example.listaaudios.MainActivity.draw_play;
import static com.example.listaaudios.MainActivity.draw_stop;
import static com.example.listaaudios.MainActivity.editor;
import static com.example.listaaudios.MainActivity.estado;
import static com.example.listaaudios.MainActivity.playStop;
import static com.example.listaaudios.MainActivity.seekBar;
import static com.example.listaaudios.MainActivity.sharedPreferences;
import static com.example.listaaudios.MainActivity.statePlaying;
import static com.example.listaaudios.MainActivity.statePlaying_play;
import static com.example.listaaudios.MainActivity.statePlaying_stop;

public class RecorderService extends Service implements MediaPlayer.OnPreparedListener {
    MediaRecorder mediaRecorder;
    public static MediaPlayer mediaPlayer;
    String nameAudio = "";
    File fileAudio = null;
    Runnable runnable;
    Handler handler;
    private static String OUTPUTFILE = "";

    public RecorderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Utils.RECORD_AUDIO.equalsIgnoreCase(intent.getStringExtra(Utils.RECORD_AUDIO))) {
            DateFormat sdf = DateFormat.getDateInstance();
            DateFormat sdf2 = DateFormat.getTimeInstance();
            Date date = new Date();
            nameAudio = sdf.format(date) + "_" + sdf2.format(date) + ".mp3";
            fileAudio = new File(FOLDER_AUDIO, nameAudio);
            try {
                if (fileAudio.createNewFile()) {
                    Utils.showToast(this, "Se creó archivo de audio " + fileAudio.getName());
                    OUTPUTFILE = FOLDER_AUDIO + "/" + nameAudio;
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    mediaRecorder.setOutputFile(OUTPUTFILE);
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    Utils.showToast(this, "Iniciando servicio de audio");
                    editor = sharedPreferences.edit();
                    editor.putString("STATE", "PLAYING");
                    editor.putInt("PLAYSTOP_IMAGE", draw_play);
                    editor.putString("STATE_TXT", "Grabando audio...");
                    editor.apply();
                    estado.setText(sharedPreferences.getString("STATE_TXT", ""));
                    playStop.setImageDrawable(getResources().getDrawable(sharedPreferences.getInt("PLAYSTOP_IMAGE", 0)));
                } else {
                    Utils.showToast(this, "No se creó el archivo :" + nameAudio);
                }
            } catch (IOException e) {
                Utils.showToast(this, "Error grabando audio " + e.getMessage());
            }
        } else if (Utils.PLAY_AUDIO.equalsIgnoreCase(intent.getStringExtra(Utils.PLAY_AUDIO))) {
            String audioPath = intent.getStringExtra(Utils.AUDIO_PATH);
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioPath);
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                Utils.showToast(getApplicationContext(), "Error reproduciendo audio " + e.getMessage());
            }
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            Utils.showToast(this, "Servicio de audio detenido");
            MainActivity.addNewAudio(fileAudio.getName());
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        editor = sharedPreferences.edit();
        editor.putString("STATE", "NO_PLAYING");
        editor.putString("STATE_TXT", "");
        editor.putInt("PLAYSTOP_IMAGE", draw_stop);
        editor.apply();
        estado.setText(sharedPreferences.getString("STATE_TXT", ""));
        playStop.setImageDrawable(getResources().getDrawable(sharedPreferences.getInt("PLAYSTOP_IMAGE", 0)));
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        editor=sharedPreferences.edit();
        editor.putInt("PLAYING_STATE_IMAGE", statePlaying_stop);
        editor.apply();
        statePlaying.setImageDrawable(getResources().getDrawable(sharedPreferences.getInt("PLAYING_STATE_IMAGE", 0)));
        seekBar.setMax(mp.getDuration());
        changeSeekbar(mp);
    }

    private void changeSeekbar(final MediaPlayer mp) {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        if (mp.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    changeSeekbar(mp);
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

}
