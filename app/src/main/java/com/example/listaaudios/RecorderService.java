package com.example.listaaudios;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import static com.example.listaaudios.MainActivity.FOLDER_AUDIO;

public class RecorderService extends Service implements MediaPlayer.OnPreparedListener {
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    String nameAudio = "";
    File fileAudio = null;
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
        /*DateFormat sdf = DateFormat.getDateInstance();
        DateFormat sdf2 = DateFormat.getTimeInstance();
        Date date = new Date();
        nameAudio = sdf.format(date) + "_" + sdf2.format(date) + ".mp3";
        fileAudio = new File(FOLDER_AUDIO, nameAudio);
        try {
            if (fileAudio.createNewFile()) {
                Utils.showToast(this, "Se creó archivo de audio");
            } else {
                Utils.showToast(this, "No se creó el archivo :" + nameAudio);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
                    Utils.showToast(this, "Se creó archivo de audio");
                    OUTPUTFILE = FOLDER_AUDIO + "/" + nameAudio;
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    mediaRecorder.setOutputFile(OUTPUTFILE);
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    Utils.showToast(this, "Iniciando servicio de audio");
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
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
