package com.example.listaaudios;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class AdapterForAudios extends RecyclerView.Adapter<AdapterForAudios.ViewHolder> {
    Context context;
    File roothFile;
    File[] audios;
    ArrayList<String> lista;


    public AdapterForAudios(Context context, ArrayList<String> lista) {
        this.context = context;
        this.lista = lista;
        /*lista = new ArrayList<>();
        audios = this.roothFile.listFiles();
        for (File auxFile : audios) {
            lista.add(auxFile.getName());
        }*/
        /* try {
         *//*BufferedReader bufferedReader = new BufferedReader(new FileReader(this.roothFile));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lista.add(line);
            }*//*

        } catch (IOException e) {
            Utils.showToast(context,e.getMessage());
        }*/

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutAudio = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.container_audio, viewGroup, false);
        return new ViewHolder(layoutAudio);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterForAudios.ViewHolder viewHolder, int i) {
        viewHolder.name_audio.setText(lista.get(i));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_audio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name_audio = itemView.findViewById(R.id.name_audio);
        }
    }
}
