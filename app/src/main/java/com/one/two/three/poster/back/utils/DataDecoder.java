package com.one.two.three.poster.back.utils;

import android.graphics.Color;
import android.util.Log;

import com.one.two.three.poster.back.core.Core;
import com.one.two.three.poster.back.models.BackFrame;
import com.one.two.three.poster.back.models.BaseFrame;
import com.one.two.three.poster.back.models.Poster;
import com.one.two.three.poster.back.models.ImgFrame;
import com.one.two.three.poster.back.models.TxtFrame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;


/**
 * Created by Pouyan-PC on 7/30/2017.
 */

/*
    PKI File Structure

#############################################################################
#  Json File Size |             |  PNG background  | PNG images Components  #
#        4B       |  Json File  |     image of     |  (numbers and sizes    #
#                 |             |      poster      | saved ins json file)   #
#############################################################################

*/

public class DataDecoder {

    private final String TAG = "DataDecoder";

    private File pkiFile;
    private String posterID = "";
    private String folderPath = "";

    // Decoded information
    private int jsonSize;
    private int imagesNumber;
    private int textNumber;
    private int logoNumber;

    private ArrayList<BaseFrame> frames = new ArrayList<>();
    private BackFrame background;

    public DataDecoder(File pkiFile, String posterID){
        this.pkiFile = pkiFile;
        this.posterID = posterID;
        folderPath = Core.getInstance().TEMP_DIR_PATH;
    }

    public DataDecoder(String pkiFile, String posterID){
        this(new File(pkiFile), posterID);
    }

    public Poster deocode(){
        try {

            /**
             * Reading jason file size and file from first of file
             */
            byte[] buffer = new byte[4];
            FileInputStream inputStream = new FileInputStream(pkiFile);
            inputStream.read(buffer);
            /////////// python writes bytes on the other side!
            byte[] data = new byte[buffer.length];
            for(int i=0; i<4; i++){
                data[i] = buffer[buffer.length-1-i];
            }
            /////////////////
            jsonSize = ByteBuffer.wrap(buffer).getInt();
            buffer = new byte[jsonSize];
            inputStream.read(buffer);
            String json = new String(buffer);
            Log.d(TAG, "deocode: " + json);

            Log.d("KasperLogger", json);

            /**
             *  Reading all poster's information and decoding from json type
             */
            JSONObject object = new JSONObject(json);
            int backgroundSize = object.getInt("BACKGROUND_SIZE");
            int backgroundWidth = object.getInt("BACKGROUND_WIDTH");
            int backgroundHeight = object.getInt("BACKGROUND_HEIGHT");
            background = new BackFrame(backgroundWidth, backgroundHeight, folderPath + "/background.png");

            JSONArray componentsArray = object.getJSONArray("COMPONENTS");
            imagesNumber = componentsArray.getInt(0);
            textNumber = componentsArray.getInt(1);
            logoNumber = componentsArray.getInt(2);
            int[] frameSizes = new int[imagesNumber];
            for(int i = 1; i<= imagesNumber; i++){
                JSONObject frame = object.getJSONObject("FRAME_" + i);
                frameSizes[i - 1] = frame.getInt("SIZE");
                int x = frame.getInt("X");
                int y = frame.getInt("Y");
                int width = frame.getInt("WIDTH");
                int height = frame.getInt("HEIGHT");
                ImgFrame imgFrame = new ImgFrame(x, y, width, height, folderPath + "/frame_" + i + ".png");
                frames.add(imgFrame);
            }
            for(int i = 1; i<= textNumber; i++){
                JSONObject text = object.getJSONObject("TEXT_" + i);
                int x = text.getInt("X");
                int y = text.getInt("Y");
                int width = text.getInt("WIDTH");
                int height = text.getInt("HEIGHT");
                String color = text.getString("COLOR");
                TxtFrame txtFrame = new TxtFrame(x, y, width, height, Color.parseColor(color));
                frames.add(txtFrame);
            }


            /**
             *  Reading background image from file
             */
            new File(folderPath).mkdirs(); // Create file directory if not exists
            if(new File(folderPath + "/background.png").exists()){
                inputStream.skip(backgroundSize);
            }else{
                buffer = new byte[64 * 1024];
                int remainSize = backgroundSize;
                String backgroundDir = folderPath + "/background.png";
                FileOutputStream outputSteam = new FileOutputStream(backgroundDir);
                while(remainSize > 0){
                    if(remainSize / buffer.length > 0){
                        inputStream.read(buffer);
                        outputSteam.write(buffer);
                    }else{
                        inputStream.read(buffer, 0, remainSize);
                        outputSteam.write(buffer, 0, remainSize);
                    }
                    remainSize -= buffer.length;
                }
                outputSteam.flush();
                outputSteam.close();
                Log.d(TAG, "deocode: Read background file successfully");
            }

            /**
             * Reading Image frame files from pki file and write them on storage
             */
            for(int i = 1; i<= imagesNumber; i++){
                if(new File(folderPath + "/frame_" + i + ".png").exists()){
                    inputStream.skip(frameSizes[i-1]);
                    continue;
                }

                int fileSize = frameSizes[i - 1];
                int remainSize = fileSize;
                String fileDir = folderPath + "/frame_" + i + ".png";
                FileOutputStream outputSteam = new FileOutputStream(fileDir);
                while(remainSize > 0){
                    if(remainSize / buffer.length > 0){
                        inputStream.read(buffer);
                        outputSteam.write(buffer);
                    }else{
                        inputStream.read(buffer, 0, remainSize);
                        outputSteam.write(buffer, 0, remainSize);
                    }
                    remainSize -= buffer.length;
                }
                outputSteam.flush();
                outputSteam.close();
                Log.d(TAG, "deocode: Read image frame " + i);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Poster(posterID, background, frames);
    }

}
