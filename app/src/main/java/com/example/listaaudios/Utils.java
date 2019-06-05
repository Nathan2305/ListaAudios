package com.example.listaaudios;

import android.content.Context;
import android.widget.Toast;

public class Utils {
    public static String RECORD_AUDIO="RECORD_AUDIO";
    public static String PLAY_AUDIO="PLAY_AUDIO";
    public static String AUDIO_PATH="AUDIO_PATH";
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
