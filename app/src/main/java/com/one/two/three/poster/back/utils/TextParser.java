package com.one.two.three.poster.back.utils;

import android.content.Context;
import android.graphics.Color;

import com.one.two.three.poster.back.core.Core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Pouyan-PC on 12/28/2017.
 */

public class TextParser {
    private static final TextParser ourInstance = new TextParser();

    public static TextParser getInstance() {
        return ourInstance;
    }

    private TextParser() {}

    public ArrayList<String> getLines(File file){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return readLines(reader);
    }

    private ArrayList<String> readLines(BufferedReader reader){
        ArrayList<String> lines = new ArrayList<>();
        try {
            String line;
            while((line = reader.readLine()) != null){
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public ArrayList<Integer> getImportedColors() {
        ArrayList<Integer> colors = new ArrayList<>();
        Context mContext = Core.getInstance().getContext();
        InputStream input = null;
        try {
            input = mContext.getAssets().open("colors.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            ArrayList<String> lines = readLines(reader);
            for(String line : lines){
                int color = Color.parseColor(line);
                colors.add(color);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
                try {
                    if(input != null)
                        input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return colors;
    }

}
