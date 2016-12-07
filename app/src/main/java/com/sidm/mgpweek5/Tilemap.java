package com.sidm.mgpweek5;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Foo on 7/12/2016.
 */

public class Tilemap {

    private int rows = 0;   // Y
    private int cols = 0;   // X

    public int tilemap[][];     // [rows][cols]

    public float tileSize_X;
    public float tileSize_Y;

    public void Init(String filepath) {

        // read total number of rows and columns
        BufferedReader readerGetLines = null;
        int noOfLines = 0;
        int noOfCols = 0;

        try {
            readerGetLines = new BufferedReader(new FileReader(filepath));
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
            br = new BufferedReader(new FileReader(filepath));

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

    public String ToString()
    {
        String str = new String();
        str += String.valueOf(rows);
        for(int y = 0; y < cols; y++)
        {
            for(int x = 0; x < rows; x++)
            {
                //str += String.valueOf(tilemap[y][x]);
                str += ".";
                str += ",";
            }
            str += "\n";
        }
        return str;
    }
}
