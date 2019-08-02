package com.example.myebook.iflytek;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myebook.MainApplication;
import com.example.myebook.R;
import com.example.myebook.util.VirtureUtil.onPlayVoiceListener;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

import static android.content.Context.MODE_PRIVATE;

public class MySpeakOut {
    private static String TAG = "MySpeakOut";
    private  Context mContext;

    // 语音合成对象
    private SpeechSynthesizer mCompose;
    // 默认发音人
    private String voicer = "xiaoyan";
    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;

    private EditText mResourceText;
    private SharedPreferences mSharedPreferences;

    public MySpeakOut(Context context){
        mContext = context;
        initSpeak(null);
        mSharedPreferences = mContext.getSharedPreferences(VoiceSettingsActivity.PREFER_NAME, MODE_PRIVATE);
        // 初始化合成对象
        mCompose = SpeechSynthesizer.createSynthesizer(mContext, mComposeInitListener);
    }

    public void initSpeak(String serverUrl){
        StringBuffer bf = new StringBuffer();
        bf.append("appid="+ mContext.getString(R.string.app_id));
        bf.append(",");
        if (!TextUtils.isEmpty(serverUrl)) {
            bf.append("server_url="+serverUrl);
            bf.append(",");
        }
        //此处调用与SpeechDemo中重复，两处只调用其一即可
        SpeechUtility.createUtility(mContext, bf.toString());
        String speaker = MainApplication.getInstance().getLocalStore("speaker");
        voicer = speaker.equals("") ? voicer : speaker;
    }

    public void initParam(){
        setParam();
    }

    public void startSpeakOut(String wordsText){
        //收到onCompleted 回调时，合成结束、生成合成音频。合成的音频格式：只支持pcm格式
        // 设置参数
        int code = mCompose.startSpeaking(wordsText, mComposeListener);
        if (code != ErrorCode.SUCCESS) {
            showTip("语音合成失败,错误码: " + code);
        }
    }

    public void stopSpeak(){
        mCompose.stopSpeaking();
    }

    public void pauseSpeak(){
        mCompose.pauseSpeaking();
    }

    public void resumeSpeak(){
        mCompose.resumeSpeaking();
    }

    //初始化监听
    private InitListener mComposeInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码："+code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    private void showTip(final String str) {
        Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();
    }

    //参数设置
    private void setParam(){
        // 清空参数
        mCompose.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        mCompose.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置在线合成发音人
        mCompose.setParameter(SpeechConstant.VOICE_NAME, voicer);
        //设置合成语速
        mCompose.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
        //设置合成音调
        mCompose.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
        //设置合成音量
        mCompose.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
        //设置播放器音频流类型
        mCompose.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
        // 设置播放合成音频打断音乐播放，默认为true
        mCompose.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mCompose.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mCompose.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/compose.wav");
    }

    //合成回调监听
    private SynthesizerListener mComposeListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            showTip("开始播放");
        }

        @Override
        public void onSpeakPaused() {
            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            // 合成进度
            mPercentForBuffering = percent;
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            mPercentForPlaying = percent;
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                showTip("播放完成");
            } else if (error != null) {
                showTip(error.getPlainDescription(true));
            }
            mListener.finishSpeak();
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private onPlayVoiceListener mListener;

    public void setPlayVoiceListener(onPlayVoiceListener listener){
        mListener = listener;
    }

}
