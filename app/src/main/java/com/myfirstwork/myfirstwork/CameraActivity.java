package com.myfirstwork.myfirstwork;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String LOG_TAG = "myLogs";
    public static final String NAME_VIDEO = "video";
    private String[] permission = { Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE};
    String[] myCameras = null;
    CameraService [] cameraServices = null;
    private CameraManager mCameraManager = null;
    private TextureView textureView;
    private ImageButton selectCamera,startRecording,stopRecording;
    private int idCamera=0;
    private HandlerThread backgroundThread;
    private Handler handler;
    private MediaRecorder mediaRecorder;
    private File mCurrentFile;
    private int count=0;
    private int [] ORIENTATION = null;
    private boolean flagRecording=false;
    private Context context;
    private void startBackgroundThread(){
        backgroundThread = new HandlerThread("CameraHandler");
        backgroundThread.start();
        handler=new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread(){
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            handler=null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        textureView=findViewById(R.id.video);
        selectCamera=findViewById(R.id.reverse);
        startRecording=findViewById(R.id.rec);
        stopRecording=findViewById(R.id.stop);
        selectCamera.setOnClickListener(this);
        startRecording.setOnClickListener(this);
        context=this;
        requestPermissions(permission,0);
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            myCameras = new String[mCameraManager.getCameraIdList().length];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean flag = true;
        if (requestCode==0){
            for(int i = 0; i < permissions.length; i++){
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    flag=flag&&true;
                }
                else {
                    flag=false;
                }
            }
            if (flag){
                startCameraActivity();
            }
            else {
                onBackPressed();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startCameraActivity() {
        Log.i(LOG_TAG,"Start Camera");
        try {
            getInfoCameras();
            setUpMediaRecorder();
            createCameras();
            cameraServices[idCamera].openCamera();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void getInfoCameras() throws CameraAccessException {
        ORIENTATION = new int[mCameraManager.getCameraIdList().length];
        for (String cameraID : mCameraManager.getCameraIdList()) {
            Log.i(LOG_TAG, "cameraID: " + cameraID);
            int id = Integer.parseInt(cameraID);
            CameraCharacteristics cc = mCameraManager.getCameraCharacteristics(cameraID); // Получения списка выходного формата, который поддерживает камера
            StreamConfigurationMap configurationMap =
                    cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            int Faceing = cc.get(CameraCharacteristics.LENS_FACING);

            if (Faceing ==  CameraCharacteristics.LENS_FACING_FRONT)
            {
                Log.i(LOG_TAG,"Camera with ID: "+cameraID +  "  is FRONT CAMERA  ");
            }

            if (Faceing ==  CameraCharacteristics.LENS_FACING_BACK)
            {
                Log.i(LOG_TAG,"Camera with: ID "+cameraID +  " is BACK CAMERA  ");
            }

            Size[] sizesJPEG = configurationMap.getOutputSizes(ImageFormat.JPEG);

            if (sizesJPEG != null) {
                for (Size item:sizesJPEG) {
                    Log.i(LOG_TAG, "w:"+item.getWidth()+" h:"+item.getHeight());
                }
            }  else {
                Log.i(LOG_TAG, "camera don`t support JPEG");
            }
        }
    }

    private void createCameras() throws CameraAccessException {
        cameraServices= new CameraService[mCameraManager.getCameraIdList().length];
        for (String cameraID:mCameraManager.getCameraIdList()){
            Log.i(LOG_TAG, "cameraID: "+cameraID);
            int id = Integer.parseInt(cameraID);
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraID);
            int facing = characteristics.get(CameraCharacteristics.LENS_FACING);
            if(facing==CameraCharacteristics.LENS_FACING_FRONT){
                ORIENTATION[id]=270;
            }
            else{
                ORIENTATION[id]=90;
            }
            // создаем обработчик для камеры
            cameraServices[id] = new CameraService(mCameraManager,cameraID);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopBackgroundThread();
        if(cameraServices!=null) {
            for (CameraService cameraService : cameraServices) {
                if (cameraService.isOpen()) {
                    cameraService.closeCamera();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        for (CameraService cameraService:cameraServices){
            cameraService.closeCamera();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.reverse:
                nextCamera();
                break;
            case R.id.rec:
                if(!flagRecording){
                    selectCamera.setVisibility(View.INVISIBLE);
                    mediaRecorder.start();
                    startRecording.setImageResource(R.drawable.stop);
                    flagRecording=!flagRecording;
                    Toast.makeText(this, mCurrentFile.getPath(), Toast.LENGTH_SHORT).show();
                }else {
                    selectCamera.setVisibility(View.VISIBLE);
                    startRecording.setImageResource(R.drawable.record);
                    Intent intent = new Intent(CameraActivity.this,PreviewActivity.class);
                    intent.putExtra("path",mCurrentFile.getAbsolutePath());
                    cameraServices[idCamera].stopRecordingVideo();
                    flagRecording=!flagRecording;
                    startActivity(intent);
                    finish();
                    Toast.makeText(this, "stop recording", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.stop:
                selectCamera.setVisibility(View.VISIBLE);
                cameraServices[idCamera].stopRecordingVideo();
                Toast.makeText(this, "stop recording", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void nextCamera() {
        cameraServices[idCamera].closeCamera();
        try {
            if(mCameraManager.getCameraIdList().length-1>idCamera) {
                idCamera++;
            }else {
                idCamera=0;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        startCameraActivity();

    }

    private void setUpMediaRecorder(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mCurrentFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), NAME_VIDEO+count+".mp4");
        mediaRecorder.setOutputFile(mCurrentFile.getAbsolutePath());
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
        mediaRecorder.setVideoFrameRate(profile.videoFrameRate);
        mediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
        mediaRecorder.setOrientationHint(ORIENTATION[idCamera]);
        mediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioEncodingBitRate(profile.audioBitRate);
        mediaRecorder.setAudioSamplingRate(profile.audioSampleRate);

        try {
            mediaRecorder.prepare();
            Log.i(LOG_TAG, " запустили медиа рекордер");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(LOG_TAG, "не запустили медиа рекордер");
        }
    }
     ///////////////////////////////////////////////
    ///////////////////////////////////////////////
   ////////////////CAMERA SERVISE ////////////////
  ///////////////////////////////////////////////
 ///////////////////////////////////////////////
    public class CameraService{
        private String mCameraID;
        private CameraDevice mCameraDevice = null;
        private CameraCaptureSession mCaptureSession;
        private CaptureRequest.Builder builder ;
        public CameraService(CameraManager cameraManager, String cameraID){
            mCameraManager = cameraManager;
            mCameraID = cameraID;
        }

        public boolean isOpen() {

            if (mCameraDevice == null) {
                return false;
            } else {
                return true;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void openCamera() {
            try {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                    mCameraManager.openCamera(mCameraID,stateCallback,handler);
                }
            }
            catch (CameraAccessException e) {
                Log.i(LOG_TAG,e.getMessage());
            }
        }

        public void closeCamera() {

            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
                Log.i(LOG_TAG,"close device");
            }
        }

        private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                mCameraDevice=camera;

                Log.i(LOG_TAG,"Open camera with id :"+ mCameraDevice.getId());
                createCameraPreviewSession();
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                mCameraDevice.close();
                Log.i(LOG_TAG,"Disconnect camera with id :"+ mCameraDevice.getId());
                mCameraDevice=null;
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                Log.i(LOG_TAG, "error! camera id:"+camera.getId()+" error:"+error);
            }
        };

        private void createCameraPreviewSession() {
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(640,480);
            Surface surface = new Surface(surfaceTexture);
            try {
                builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                builder.addTarget(surface);
                Surface recordeSurface = mediaRecorder.getSurface();
                builder.addTarget(recordeSurface);

                mCameraDevice.createCaptureSession(Arrays.asList(surface,mediaRecorder.getSurface()), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        try {
                            mCaptureSession = session;
                            mCaptureSession.setRepeatingRequest(builder.build(),null,handler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                    }
                },handler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        public void stopRecordingVideo(){
            try{
                mCaptureSession.stopRepeating();
                mCaptureSession.abortCaptures();
                mCaptureSession.close();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            mediaRecorder.stop();
            mediaRecorder.release();
            count++;
            setUpMediaRecorder();
            createCameraPreviewSession();
        }

    }

//    public class MergeVideo extends AsyncTask<String,Integer,String> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            ProgressDialog.show(context,"Ожидание","Склейка",true);
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            try {
//                String paths[] = new String[count];
//                Movie[] movies = new Movie[count];
//                for(int i = 0 ; i< count; i++){
//                    paths[i]= mCurrentFile.getPath()+NAME_VIDEO+i+".mp4";
//                    movies[i]= Movie
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }
}