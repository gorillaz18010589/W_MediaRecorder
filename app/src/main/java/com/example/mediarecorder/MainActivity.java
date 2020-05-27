package com.example.mediarecorder;
//getAudioSourceMax() 獲取音頻信號源的最高值。
//        getMaxAmplitude() 最後調用這個方法採樣的時候返回最大振幅的絕對值
//        getMetrics() 返回當前Mediacorder測量的數據
//        getSurface() 當使用Surface作為視頻源的時候，返回Sufrace對象
//        pause() 暫停錄製
//        prepare() 準備錄製
//        resume() 恢復錄製
//        release() 釋放與此MediaRecorder對象關聯的資源
//        reset() 重新啟動mediarecorder到空閒狀態
//        setAudioChannels(int numChannels) 設置錄製的音頻通道數
//        setAudioEncoder(int audio_encoder) 設置audio的編碼格式
//        setAudioEncodingBitRate(int bitRate) 設置錄製的音頻編碼比特率
//        setAudioSamplingRate(int samplingRate) 設置錄製的音頻採樣率
//        setAudioSource(int audio_source) 設置用於錄製的音源
//        setAuxiliaryOutputFile(String path) 輔助時間的推移視頻文件的路徑傳遞
//        setAuxiliaryOutputFile(FileDescriptor fd) 在文件描述符傳遞的輔助時間的推移視頻
//        setCamera(Camera c) 設置一個recording的攝像頭，此方法在API21被遺棄，被getSurface替代
//        setCaptureRate(double fps) 設置視頻幀的捕獲率
//        setInputSurface(Surface surface) 設置持續的視頻數據來源
//        setMaxDuration(int max_duration_ms) 設置記錄會話的最大持續時間（毫秒）
//        setMaxFileSize(long max_filesize_bytes) 設置記錄會話的最大大小（以字節為單位）
//        setOutputFile(FileDescriptor fd) 傳遞要寫入的文件的文件描述符
//        setOutputFile(String path) 設置輸出文件的路徑
//        setOutputFormat(int output_format) 設置在錄製過程中產生的輸出文件的格式
//        setPreviewDisplay(Surface sv) 表面設置顯示記錄媒體（視頻）的預覽
//        setVideoEncoder(int video_encoder) 設置視頻編碼器，用於錄製
//        setVideoEncodingBitRate(int bitRate) 設置錄製的視頻編碼比特率
//        setVideoFrameRate(int rate) 設置要捕獲的視頻幀速率
//        setVideoSize(int width, int height) 設置要捕獲的視頻的寬度和高度
//        setVideoSource(int video_source) 開始捕捉和編碼數據到setOutputFile（指定的文件）
//        setLocation(float latitude, float longitude) 設置並存儲在輸出文件中的地理數據（經度和緯度）
//        setProfile(CamcorderProfile profile) 指定CamcorderProfile對象
//        setOrientationHint(int degrees) 設置輸出的視頻播放的方向提示
//        setOnErrorListener(MediaRecorder.OnErrorListener l) 註冊一個用於記錄錄製時出現的錯誤的

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button btnRecord, btnStopRecord, btnPlay, btnStop;
    final int REQUEST_PERMISSION_CODE = 1000;
    String pathSave = "";
    String TAG = "hank";

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init
        btnRecord = findViewById(R.id.btn_record);
        btnStopRecord = findViewById(R.id.stop_record);
        btnPlay = findViewById(R.id.btn_play);
        btnStop = findViewById(R.id.btn_stop);


        //如果沒有權限去要權限
        if (!checkPermissionFromDevice())
            requestPermission();

        //1.permission

        //have permission

        //4.btnStopRecord
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissionFromDevice()) {
                    //如果有權限
                    pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_audio_record.3gp";
                    setupMediaRecorder();
                    try {
                        mediaRecorder.prepare();//準備錄製紀錄編碼。必須在設置所需的音頻和視頻源，編碼器，文件格式等之後但在start（）之前調用此方法。
                        mediaRecorder.start(); //開始捕獲數據並將其編碼到使用setOutputFile（）指定的文件中。 在prepare（）之後調用它。

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //使用者Play按鍵不准按下去
                    btnPlay.setEnabled(false);
                    btnStop.setEnabled(false);
                    btnStopRecord.setEnabled(true);
                    Toast.makeText(MainActivity.this, "錄音中..", Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "btnRecord");
                } else {
                    //no permission
                    requestPermission();
                }
            }

        });
        //5.停止紀錄接收
        btnStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaRecorder.stop();
                } catch (Exception e) {
                    Log.v(TAG, e.toString());
                }
                btnStopRecord.setEnabled(false);
                btnPlay.setEnabled(true);
                btnRecord.setEnabled(true);
                btnStop.setEnabled(false);
                Log.v(TAG, "btnRecord");
            }
        });

        //6.開始撥放
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStop.setEnabled(true);
                btnRecord.setEnabled(false);
                btnStopRecord.setEnabled(false);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(pathSave);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                Toast.makeText(MainActivity.this, "播放中...", Toast.LENGTH_SHORT).show();
                Log.v(TAG, "btnPlay");
            }
        });

        //7.停止播放
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlay.setEnabled(true);
                btnStopRecord.setEnabled(false);
                btnRecord.setEnabled(true);
                btnStop.setEnabled(false);

                if (mediaPlayer != null) {
                    mediaPlayer.stop(); //停止播放
                    mediaPlayer.release(); //釋出資源
                    setupMediaRecorder();
                }
            }
        });

    }


    //設定錄音機
    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); //設定音源(音源參數)MediaRecorder.AudioSource.MIC：音源會使用外設麥克'
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//設定錄製過程中產生的輸出格式文件
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB); //設定Audio編碼格式
        mediaRecorder.setOutputFile(pathSave); //設定輸出的文件路徑
    }

    //2.去要權限
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                }, REQUEST_PERMISSION_CODE);
        Log.v(TAG, "requestPermission");
    }


    //3.使用者權限結果回傳
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                //如果回傳的權限數大於0 而且第0的位置權限是允許的話
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
                    Toast.makeText(this, "使用者有授權", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(this, "使用者未授權", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
        Log.v(TAG, "onRequestPermissionsResult:" + "/requestCode:" + requestCode + "/ String[] permissions:" + permissions + "/grantResults:" + grantResults);

    }

    //1.詢問是否權限
    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        Log.v(TAG, "checkPermissionFromDevice");
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }
}
