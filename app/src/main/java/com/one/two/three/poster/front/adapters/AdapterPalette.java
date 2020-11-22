package com.one.two.three.poster.front.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.one.two.three.poster.R;
import com.one.two.three.poster.back.models.PosterPreview;

import java.util.ArrayList;

/**
 * Created by Pouyan-PC on 12/14/2017.
 */

public class AdapterPalette extends RecyclerView.Adapter<AdapterPalette.ViewHolder> {

    private ArrayList<Integer> palettes;
    private OnPaletteClick listener;

    public interface OnPaletteClick {
        void onClick(int color);
    }

    public AdapterPalette(ArrayList<Integer> palettes, OnPaletteClick listener) {
        this.palettes = palettes;
        this.listener = listener;
    }

    @Override
    public AdapterPalette.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AdapterPalette.ViewHolder viewHolder;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_color_palette, parent, false);
        viewHolder = new AdapterPalette.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AdapterPalette.ViewHolder holder, final int position) {
        holder.imgPalette.setBackgroundColor(palettes.get(position));
        holder.imgPalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onClick(palettes.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return palettes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView imgPalette;

        ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imgPalette = (ImageView) itemView.findViewById(R.id.imgPalette);
        }
    }

}
