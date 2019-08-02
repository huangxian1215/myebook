package com.example.myebook.util;

import android.util.Log;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class UtilFile {
    public static ArrayList<String> getFileName(String fileAbsolutePath) {
        ArrayList<String> ListFile = new ArrayList<String>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();

        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                Log.e("eee","文件名 ： " + filename);
                ListFile.add(filename);
            }
        }
        return ListFile;
    }
}
