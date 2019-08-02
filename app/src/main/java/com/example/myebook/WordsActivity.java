package com.example.myebook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WordsActivity extends AppCompatActivity implements OnClickListener {
    private TextView tv_words;
    private Button bt_ok;
    private Button bt_cancle;
    private String mWords;
    private String mFileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);
        tv_words = findViewById(R.id.tv_words);

        bt_ok = findViewById(R.id.btn_ok);
        bt_cancle = findViewById(R.id.btn_cancle);
        bt_ok.setOnClickListener(this);
        bt_cancle.setOnClickListener(this);

        Intent intent  = getIntent();
        Bundle bundle = intent.getExtras();
        mWords = bundle.getString("words");
        mFileName = bundle.getString("filename");
        tv_words.setText(mWords);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:  //
                saveData();
                break;
            case R.id.btn_cancle:
                finish();
                break;
        }
    }

    private void saveData(){
        if(mFileName == null) {
            String strPath = Environment.getExternalStorageDirectory()+"/myebook/";
            File file = new File(strPath);
            if(!file.exists()){
                Boolean flag = file.mkdirs();
            }
            String format = "yyyyMMddhhmmss";
            SimpleDateFormat s_format = new SimpleDateFormat(format);
            Date d_date = new Date();
            String s_date = "";
            s_date = strPath + s_format.format(d_date)+".txt";
            saveFile(s_date);
        }else{
        }
        finish();
    }

    private  void saveFile(String filename){
        try {
            File file = new File(filename);
            FileOutputStream fos = new FileOutputStream(file);
//            FileOutputStream fos = this.openFileOutput(filename, MODE_PRIVATE);//获得FileOutputStream
            //将要写入的字符串转换为byte数组
            byte[]  bytes = mWords.getBytes();
            fos.write(bytes);//将byte数组写入文件
            fos.close();//关闭文件输出流
            } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
