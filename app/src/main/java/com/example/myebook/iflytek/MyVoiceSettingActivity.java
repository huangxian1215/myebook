package com.example.myebook.iflytek;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.myebook.MainApplication;
import com.example.myebook.R;


public class MyVoiceSettingActivity extends AppCompatActivity implements OnClickListener {
    private String[] mCloudVoicersEntries;
    private String[] mCloudVoicersValue ;
    private int selectedNum = 0;
    private String voicer = "xiaoyan";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myvoicesetting);
        findViewById(R.id.btn_speaker).setOnClickListener(this);
        findViewById(R.id.btn_voice).setOnClickListener(this);

        // 云端发音人名称列表
        mCloudVoicersEntries = getResources().getStringArray(R.array.voicer_cloud_entries);
        mCloudVoicersValue = getResources().getStringArray(R.array.voicer_cloud_values);
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.btn_speaker){
            new AlertDialog.Builder(this).setTitle("在线合成发音人选项")
                    .setSingleChoiceItems(mCloudVoicersEntries, // 单选框有几项,各是什么名字
                            selectedNum, // 默认的选项
                            new DialogInterface.OnClickListener() { // 点击单选框后的处理
                                public void onClick(DialogInterface dialog, int which) { // 点击了哪一项
                                    voicer = mCloudVoicersValue[which];
                                    if ("catherine".equals(voicer) || "henry".equals(voicer) || "vimary".equals(voicer)
                                            || "Mariane".equals(voicer) || "Allabent".equals(voicer) || "Gabriela".equals(voicer) || "Abha".equals(voicer) || "XiaoYun".equals(voicer)) {
//                                        mResourceText.setText(R.string.compose_source_en);

                                    } else {
//                                        mResourceText.setText(R.string.compose_source);
                                    }
                                    MainApplication.getInstance().setLocalStore("speaker", voicer);
                                    selectedNum = which;
                                    dialog.dismiss();
                                }
                            }).show();
        }

        if(v.getId() == R.id.btn_voice){
            Intent intent = new Intent(this, VoiceSettingsActivity.class);
            intent.putExtra("type", VoiceSettingsActivity.XF_COMPOSE);
            startActivity(intent);
        }
    }
}
