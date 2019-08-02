package com.example.myebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

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

}
