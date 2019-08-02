package com.example.myebook.iflytek;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.myebook.R;
import com.iflytek.cloud.SpeechUtility;


/**
 * Created by ouyangshen on 2016/12/18.
 */
public class IflayMainActivity extends AppCompatActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ifly_main);

		findViewById(R.id.btn_voice_recognize).setOnClickListener(this);
		findViewById(R.id.btn_voice_compose).setOnClickListener(this);
		getPermissions();
		mscInit(null);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_voice_recognize) {
			Intent intent = new Intent(this, VoiceRecognizeActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_voice_compose) {
			Intent intent = new Intent(this, VoiceComposeActivity.class);
			startActivity(intent);
		}
	}

	private void getPermissions(){
		//申请SD卡读写权限
		ActivityCompat.requestPermissions(IflayMainActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
				Manifest.permission.RECORD_AUDIO,
				Manifest.permission.ACCESS_NETWORK_STATE,
				Manifest.permission.INTERNET,
		}, 1);
	}
	private void mscInit (String serverUrl){
		// 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
		// 参数间使用半角“,”分隔。
		// 设置你申请的应用appid,请勿在'='与appid之间添加空格及空转义符

		// 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误
		StringBuffer bf = new StringBuffer();
		bf.append("appid="+getString(R.string.app_id));
		bf.append(",");
		if (!TextUtils.isEmpty(serverUrl)) {
			bf.append("server_url="+serverUrl);
			bf.append(",");
		}
		//此处调用与SpeechDemo中重复，两处只调用其一即可
		SpeechUtility.createUtility(this.getApplicationContext(), bf.toString());
		// 以下语句用于设置日志开关（默认开启），设置成false时关闭语音云SDK日志打印
		// Setting.setShowLog(false);
	}
}
