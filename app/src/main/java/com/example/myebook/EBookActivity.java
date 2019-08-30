package com.example.myebook;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.example.myebook.adapter.IndexAdapter;
import com.example.myebook.bean.PageIndex;
import com.example.myebook.iflytek.MySpeakOut;
import com.example.myebook.iflytek.MyVoiceSettingActivity;
import com.example.myebook.util.JsonUtil;
import com.example.myebook.util.UtilFile;
import com.example.myebook.util.VirtureUtil.onPlayVoiceListener;
import com.example.myebook.util.VirtureUtil.onClickItemListener;
import com.example.myebook.util.VirtureUtil.onLongClickItemListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class EBookActivity extends AppCompatActivity implements onPlayVoiceListener, OnClickListener, onClickItemListener, onLongClickItemListener{
    private static final int REQUEST_CODE_ACCURATE_BASIC = 107;
    private boolean hasGotToken = false;
    private AlertDialog.Builder alertDialog;
    private GridView gv_List;
    private Button bt_play;
    private Button bt_set;
    private ArrayList<String> mfiles = new ArrayList<>();
    private String mFileContent = "";
    private IndexAdapter madapter = null;
    private ArrayList<PageIndex> mPgList = new ArrayList<>();
    private int lastId = -1;
    private ArrayList<String> selectCheck = new ArrayList<>();
    private Boolean isDelete = false;
    //语音
    private ArrayList<String> messageList = new ArrayList<>();
    private Boolean isPlay = false;
    private MySpeakOut mSpk;
    private ArrayList<String> mPages = new ArrayList<>();
    private int curReadIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebook);

        bt_play = (Button) findViewById(R.id.bt_play);
        bt_set = (Button)findViewById(R.id.bt_set);
        gv_List = (GridView) findViewById(R.id.gv_indexList);
        bt_play.setOnClickListener(this);
        bt_set.setOnClickListener(this);
        // 通用文字识别(高精度版)
        findViewById(R.id.accurate_basic_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(EBookActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_ACCURATE_BASIC);
            }
        });


        alertDialog = new AlertDialog.Builder(this);
        getPermissions();
        // 请选择您的初始化方式
        initAccessToken();
        //        initAccessTokenWithAkSk();
        initSpk();

        initFiles();
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.bt_play){
            if(!isPlay){
                bt_play.setText("暂停");
                isPlay = !isPlay;
                messageList.add(mFileContent);
                mSpk.initSpeak(null);
                mSpk.initParam();
                if(messageList.size() != 0){
                    mSpk.stopSpeak();
                    mSpk.startSpeakOut(messageList.get(0));
                    messageList.remove(0);
                }
                setReadPosition(curReadIndex);
            }else{
                isPlay = !isPlay;
                bt_play.setText("播放");
                mSpk.stopSpeak();
            }
        }

        if(v.getId() == R.id.bt_set && !isDelete){
            Intent intent = new Intent(this, MyVoiceSettingActivity.class);
            startActivity(intent);
        }else{
            //删除文件
            int pgLen = mPgList.size();
            for(int n = 0; n < selectCheck.size(); n++){
                if(selectCheck.size() <= pgLen){
                    String name = Environment.getExternalStorageDirectory()+"/myebook/" + selectCheck.get(n);
                    File file = new File(name);
                    if(file.exists()){
                        file.delete();
                    }
                    for(int i = 0; i < mPgList.size(); i++){
                        if(selectCheck.get(n).equals(mPgList.get(i).title)){
                            mPgList.remove(i);
                            break;
                        }
                    }
                }
            }
            refreshFiles();
            curReadIndex = 0;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 识别成功回调，通用文字识别（高精度版）
        if (requestCode == REQUEST_CODE_ACCURATE_BASIC && resultCode == Activity.RESULT_OK) {
            RecognizeService.recAccurateBasic(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }
    }
    private void getPermissions(){
        //申请SD卡读写权限
        ActivityCompat.requestPermissions(EBookActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                Manifest.permission.DELETE_CACHE_FILES,

        }, 1);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initAccessToken();
        } else {
            Toast.makeText(getApplicationContext(), "需要android.permission.READ_PHONE_STATE", Toast.LENGTH_LONG).show();
        }
    }

    private void infoPopText(final String result) {
        String str = JsonUtil.stringDataFindArrayList(result);
        Intent intent = new Intent(this, WordsActivity.class);
        intent.putExtra("words", str);
        startActivity(intent);
    }

    private boolean checkTokenStatus() {
        if (!hasGotToken) {
            Toast.makeText(getApplicationContext(), "token还未成功获取", Toast.LENGTH_LONG).show();
        }
        return hasGotToken;
    }

    /**
     * 以license文件方式初始化
     */
    private void initAccessToken() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("aip.license", error.getMessage());
            }
        }, getApplicationContext());
    }

    /**
     * 用明文ak，sk初始化
     */
    private void initAccessTokenWithAkSk() {
        OCR.getInstance(this).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                hasGotToken = true;
            }
            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("AK，SK方式获取token失败", error.getMessage());
            }
        }, getApplicationContext(),  "KFYyPA9isMYR1pU6kB2QQAzq", "T0aPGFzaA3x2V5rKGwpdXmnR5vw5Bgru");
    }

    private void alertText(final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }

    private Handler strhd = new Handler();
    private Runnable strRb = new Runnable() {
        @Override
        public void run() {
            if(messageList.size() > 0 /*&& isPlay*/){
                mSpk.startSpeakOut(messageList.get(0));
            }else{
                strhd.postDelayed(strRb, 5000);
            }
        }
    };

    @Override
    public void finishSpeak(){
        //自动播放下一个文件
        if(++curReadIndex < mfiles.size()){
            messageList.add(getFileContent(mfiles.get(curReadIndex)));
            //刷新显示
            madapter.setCheckId(curReadIndex);
            madapter.notifyDataSetChanged();
            setReadPosition(curReadIndex);
        }
        if(messageList.size() != 0){
            mSpk.stopSpeak();
            mSpk.startSpeakOut(messageList.get(0));
            messageList.remove(0);
        }
        if(messageList.size() != 0){
            strhd.postDelayed(strRb, 1);
        }else{
            strhd.postDelayed(strRb, 5000);
        }
    }

    private void initSpk() {
        mSpk = new MySpeakOut(this);
        mSpk.initParam();
        mSpk.setPlayVoiceListener(this);
    }

    private void initFiles(){
        String strPath = Environment.getExternalStorageDirectory()+"/myebook/";
        File file = new File(strPath);
        if(!file.exists()){
            file.mkdir();
            return;
        }
        mfiles = UtilFile.getFileName(strPath);
        
        for(int n = 0; n < mfiles.size(); n++){
            PageIndex pg = new PageIndex();
            pg.title = mfiles.get(n);
            pg.isCheck = false;
            mPgList.add(pg);
        }
        madapter = new IndexAdapter(this, mPgList);
        gv_List.setAdapter(madapter);
        madapter.setOnClickItemListener(this);
        madapter.setOnLongClickItemListener(this);

        //设置选中
        String strposition = MainApplication.getInstance().getLocalStore("positionread");
        if(strposition != null && strposition != ""){
            int position = Integer.parseInt(strposition);
            if(position >= mfiles.size()) return;
            mFileContent = getFileContent(mfiles.get(position));
            madapter.setCheckId(position);
            madapter.notifyDataSetChanged();
            curReadIndex = position;
        }
    }

    private void refreshFiles(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String strPath = Environment.getExternalStorageDirectory()+"/myebook/";
                File file = new File(strPath);
                if(!file.exists()){
                    Boolean flag = file.mkdirs();
                    return ;
                }
                mfiles = UtilFile.getFileName(strPath);
                mPgList = new ArrayList<>();
                for(int n = 0; n < mfiles.size(); n++){
                    PageIndex pg = new PageIndex();
                    pg.title = mfiles.get(n);
                    pg.isCheck = false;
                    pg.index = String.valueOf(n);
                    mPgList.add(pg);
                }
//                if(mfiles.size() == 0) return;
                
                madapter.freshListIndex(mPgList);
                madapter.notifyDataSetChanged();
            }
        }, 300);

    }

    public String getFileContent(String filename){
        filename = Environment.getExternalStorageDirectory()+"/myebook/" + filename;
        String code = "GBK";
        try{
            InputStream inputStream = new FileInputStream(filename);
            byte[] head = new byte[3];
            inputStream.read(head);
            if (head[0] == -1 && head[1] == -2) {
                code = "Unicode";
            }
            if(head[0]==-17 && head[1]==-69 && head[2] ==-65) {
                code = "utf-8";
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        String str = "";
        StringBuilder sb = new StringBuilder("");
        try {
            //打开文件输入流
            FileInputStream inputStream = new FileInputStream(filename);
            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            //读取文件内容
            while(len > 0){
                sb.append(new String(buffer,0,len, code));
                //继续将数据放到buffer中
                len = inputStream.read(buffer);
            }
            str = sb.toString();

            //关闭输入流
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }


    @Override
    public void onItemClick(View view, int position){
        if(isDelete){
            if(selectCheck.contains(position)){
                for(int n = 0; n < selectCheck.size(); n++){
                    if(selectCheck.get(n).equals(mPgList.get(position).title)){
                        selectCheck.remove(n);
                        break;
                    }
                }
            }else{
                selectCheck.add(mPgList.get(position).title);
            }
            mPgList.get(position).isCheck = !mPgList.get(position).isCheck;
            madapter.freshListIndex(mPgList);
            madapter.notifyDataSetChanged();
        }else{
            if(lastId == -1){
                lastId = position;
            }else{
                if(lastId == position){
                    mFileContent = getFileContent(mfiles.get(position));
                    Intent intent = new Intent(this, WordsActivity.class);
                    intent.putExtra("words", mFileContent);
                    intent.putExtra("filename", mfiles.get(position));
                    startActivity(intent);
                    lastId = -1;
                }else{
                    lastId = position;
                }
            }
            mFileContent = getFileContent(mfiles.get(position));
            madapter.notifyDataSetChanged();
            curReadIndex = position;
        }
    }

    @Override
    public void onLongClickItem(View view, int position){
        isDelete = !isDelete;
        for(int n = 0; n < mPgList.size(); n++){
            mPgList.get(n).isCheck = false;
        }
        madapter.freshListIndex(mPgList);
        madapter.notifyDataSetChanged();
        if(isDelete){
            bt_set.setText("删除");
        }else{
            bt_set.setText("设置");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshFiles();
    }

    private void setReadPosition(int position){
        MainApplication.getInstance().setLocalStore("positionread", String.valueOf(position));
    }
}
