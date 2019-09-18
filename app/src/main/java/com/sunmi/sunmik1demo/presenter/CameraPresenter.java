package com.sunmi.sunmik1demo.presenter;

import android.content.Context;

import com.example.camera.DecodePanel;
import com.example.camera.GLPanel;
import com.hjimi.api.iminect.ImiDevice;
import com.hjimi.api.iminect.ImiFrameMode;
import com.hjimi.api.iminect.Utils;

import java.nio.ByteBuffer;

/*
* created by mayflower on 19/7/10
* for camera
* get me:jiangli@sunmi.com
* */
public class CameraPresenter {
    private static final String TAG = "CameraPresenter";
    //打开摄像头的方式
    private OpenType openType = OpenType.OPEN_BY_HJIMI;
    public enum OpenType{
        OPEN_BY_HJIMI,      //华捷
        OPEN_BY_ORBBEC,     //奥比
        OPEN_BY_USB,        //没有深度，适用于双目摄像头
        OPEN_BY_CAMERA;     //没有深度，适用于双目摄像头
    }

    //人脸SDK库
    private SdkType sdkType = SdkType.SDK_HJIMI;
    public enum SdkType{
        SDK_HJIMI,     //华捷
        SDK_BAIDU;     //百度
    }


    private Context context = null;
    private GLPanel mGLPanel;
    private DecodePanel mDecodePanel;
    private ImiDevice mDevice;
    private ImiDevice.ImiStreamType mStreamType;
    private ImiFrameMode mCurrentMode;

    private boolean mShouldRun = false;

    public CameraPresenter(ImiDevice device, ImiDevice.ImiStreamType streamType) {
        mDevice = device;
        mStreamType = streamType;
    }

    public CameraPresenter(Context context){
        this.context = context;
    }

    public void setOpenType(OpenType type){ openType = type; }

    public void setFaceSDK(SdkType type){ sdkType = type; }

    public void setGLPanel(GLPanel GLPanel) {
        this.mGLPanel = GLPanel;
    }

    public void setDecodePanel(DecodePanel decodePanel) {
        this.mDecodePanel = decodePanel;
    }


    private class FrameThread extends Thread{
        @Override
        public void run() {
            super.run();
            //get current framemode.
            mCurrentMode = mDevice.getCurrentFrameMode(mStreamType);

            //start read frame.
            while (mShouldRun) {
                ImiDevice.ImiFrame nextFrame = mDevice.readNextFrame(mStreamType, 25);
                if(null == nextFrame)
                    continue;

                switch (mStreamType){
                    case COLOR:
                        drawColor(nextFrame);
                        break;
                    case DEPTH:
                        drawDepth(nextFrame);
                        break;
                }
            }
        }
    }
    private void drawColor(ImiDevice.ImiFrame frame){
        ByteBuffer frameData = frame.getData();
        int width = frame.getWidth();
        int height = frame.getHeight();

        //draw color image.
        switch (mCurrentMode.getFormat()){
            case IMI_PIXEL_FORMAT_IMAGE_H264:
                if(mDecodePanel != null)
                    mDecodePanel.paint(frameData,frame.getTimeStamp());
                break;
            case IMI_PIXEL_FORMAT_IMAGE_YUV420SP:
                frameData = Utils.yuv420sp2RGB(frame);
                if(mGLPanel != null)
                    mGLPanel.paint(null,frameData,width,height);
                break;
            case IMI_PIXEL_FORMAT_IMAGE_RGB24:
                if(mGLPanel != null)
                    mGLPanel.paint(null,frameData,width,height);
                break;
            default:
                break;
        }

    }
    private void drawDepth(ImiDevice.ImiFrame frame){
        ByteBuffer frameData = frame.getData();
        int width = frame.getWidth();
        int height = frame.getHeight();

        frameData = Utils.depth2RGB888(frame, true, false);

        mGLPanel.paint(null, frameData, width, height);
    }

    public void onPause(){
        if(mGLPanel != null){
            mGLPanel.onPause();
        }
    }

    public void onResume(){
        if(mGLPanel != null){
            mGLPanel.onResume();
        }
    }

    public void onStart(){
        if(!mShouldRun){
            mShouldRun = true;

            //start read thread
            new FrameThread().start();
        }
    }

    public void onDestroy(){
        mShouldRun = false;
    }
}
