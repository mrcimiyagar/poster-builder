package com.one.two.three.poster.front.adapters;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.one.two.three.poster.R;

import java.util.ArrayList;

/**
 * Created by Pouyan-PC on 12/14/2017.
 */

public class AdapterFont extends RecyclerView.Adapter<AdapterFont.ViewHolder> {

    private ArrayList<Typeface> fonts;
    private OnFontClick listener;

    public interface OnFontClick {
        void onClick(Typeface font);
    }

    public AdapterFont(ArrayList<Typeface> fonts, OnFontClick listener) {
        this.fonts = fonts;
        this.listener = listener;
    }

    @Override
    public AdapterFont.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AdapterFont.ViewHolder viewHolder;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_font_select, parent, false);
        viewHolder = new AdapterFont.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AdapterFont.ViewHolder holder, final int position) {
        holder.txtFont.setTypeface(fonts.get(position));
        holder.txtFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onClick(fonts.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return fonts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtFont;

        ViewHolder(View itemView) {
            super(itemView);
            txtFont = itemView.findViewById(R.id.txtFont);
        }
    }

}
