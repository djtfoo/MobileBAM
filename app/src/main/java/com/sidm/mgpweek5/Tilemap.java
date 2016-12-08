package com.sidm.mgpweek5;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Foo on 7/12/2016.
 */

public class Tilemap {

    private int rows = 0;   // Y
    private int cols = 0;   // X

    public int tilemap[][];     // [rows][cols]

    public float tileSize_X;
    public float tileSize_Y;

    public int GetRows() {
        return rows;
    }

    public int GetCols() {
        return cols;
    }

    public void Init(Context context, String filepath) {

        // read total number of rows and columns
        BufferedReader readerGetLines = null;
        int noOfLines = 0;
        int noOfCols = 0;

        try {

            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(filepath);
            InputStreamReader reader = new InputStreamReader(inputStream);

            readerGetLines = new BufferedReader(reader);
            String getLine;
            while ((getLine = readerGetLines.readLine()) != null) {
                if (noOfLines == 0) {
                    // get cols
                    String[] lines = getLine.split(",");
                    noOfCols = lines.length;
                }
                noOfLines++;
            }
            readerGetLines.close();
        } catch (Exception ex) {
            return;
        } finally {
            if (readerGetLines != null)
                try {
                    readerGetLines.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }


        // Set rows and cols
        rows = noOfLines;
        cols = noOfCols;
        tilemap = new int[rows][cols];


        // Read to get info of map
        BufferedReader br = null;
        String line = "";

        try {

            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(filepath);
            InputStreamReader reader = new InputStreamReader(inputStream);

            br = new BufferedReader(reader);

            int count = 0;
            while ((line = br.readLine()) != null) {
                // use comma as a separator
                String[] lines = line.split(",");
                for (int i = 0; i < cols; ++i) {
                    tilemap[count][i] = Integer.parseInt(lines[i]);
                }
                ++count;
            }

            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Debug
    public String ToString()
    {
        String str = new String();
        str += String.valueOf(rows);
        for(int y = 0; y < rows; y++)
        {
            for(int x = 0; x < cols; x++)
            {
                str += String.valueOf(tilemap[y][x]);
                str += ",";
            }
            str += "\n";
        }
        return str;
    }
}
