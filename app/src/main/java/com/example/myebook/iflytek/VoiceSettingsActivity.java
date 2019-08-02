package com.example.myebook.iflytek;

import com.example.myebook.iflytek.fragment.ComposeSettingsFragment;
import com.example.myebook.iflytek.fragment.RecognizeSettingsFragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

public class VoiceSettingsActivity extends AppCompatActivity {
	public static final String PREFER_NAME = "com.example.farmshop.iflytek";
	public static final int XF_RECOGNIZE = 0;
	public static final int XF_COMPOSE = 1;
	public static final int BD_RECOGNIZE = 2;
	public static final int BD_COMPOSE = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int type = getIntent().getIntExtra("type", XF_RECOGNIZE);
		if (type == XF_RECOGNIZE) {
			getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new RecognizeSettingsFragment())
				.commit();
		} else if (type == XF_COMPOSE) {
			getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new ComposeSettingsFragment())
				.commit();
		}
	}
}
