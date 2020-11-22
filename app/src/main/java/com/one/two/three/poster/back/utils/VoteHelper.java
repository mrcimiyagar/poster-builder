package com.one.two.three.poster.back.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pouyan on 2/15/18.
 */

public class VoteHelper {

    private List<String> votedPosters;
    private Context mContext;

    public VoteHelper(Context context){
        mContext = context;
        loadVotes();
    }

    private void loadVotes(){
        votedPosters = new ArrayList<>();

        String FILENAME = "votes";

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(mContext.openFileInput(FILENAME)));
            String line;
            while((line = reader.readLine()) != null){
                votedPosters.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPoster(String name){
        votedPosters.add(name);
        saveList();
    }

    private void saveList() {
        String FILENAME = "votes";

        StringBuilder builder = new StringBuilder();
        for(int i=0; i<votedPosters.size(); i++){
            String name = votedPosters.get(i);
            builder.append(name);
            if(i != votedPosters.size() - 1)
                builder.append('\n');
        }

        FileOutputStream fos = null;
        try {
            fos = mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(builder.toString().getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos != null){
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<String> getVotedPosters(){
        return votedPosters;
    }

}
