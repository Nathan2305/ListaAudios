package com.example.listaaudios;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterForAudios extends RecyclerView.Adapter<AdapterForAudios.ViewHolder> {
    Context context;
    ArrayList<String> lista;
    private OnItemClickListener mListener;


    public AdapterForAudios(Context context, ArrayList<String> lista) {
        this.context = context;
        this.lista = lista;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutAudio = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.container_audio, viewGroup, false);
        return new ViewHolder(layoutAudio, mListener);
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

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            name_audio = itemView.findViewById(R.id.name_audio);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
