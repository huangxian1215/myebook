package com.example.myebook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ChooseTXTTypeCode extends AppCompatActivity implements OnClickListener {
    private TextView tv_gbk;
    private TextView tv_utf8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosetypecode);
        tv_gbk = findViewById(R.id.tv_gbk);
        tv_utf8 = findViewById(R.id.tv_utf8);
        tv_gbk.setOnClickListener(this);
        tv_utf8.setOnClickListener(this);

        getFileContent("hello2.txt");
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.tv_gbk){
            MainApplication.gCode = "GBK";
        }else if(v.getId() == R.id.tv_utf8){
            MainApplication.gCode = "utf-8";
        }
        Intent intent = new Intent(this, EBookActivity.class);
        startActivity(intent);
    }

    public String getFileContent(String filename){
        String code ="gb2312";
        filename = Environment.getExternalStorageDirectory()+"/myebook/" + filename;
//        File file = new File(filename);
        try{
            InputStream inputStream = new FileInputStream(filename);
            byte[] head = new byte[3];
            inputStream.read(head);
            if (head[0] == -1 && head[1] == -2) {
                code = "Unicode";
            }
            if(head[0]==-17 && head[1]==-69 && head[2] ==-65) {
                code = "UTF-8";
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), code, Toast.LENGTH_LONG).show();


        return filename;
    }

}
