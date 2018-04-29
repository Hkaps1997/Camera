package com.example.harshit.cameraintent;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.hardware.*;
import android.hardware.Camera;
import android.media.Image;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.view.OrientationEventListener.ORIENTATION_UNKNOWN;

public class MainActivity extends AppCompatActivity {
    android.hardware.Camera cam=null; //we imported android.hardware.camera as graphics.camera
    //gives the zoom in camera like the one we used the mAPS
    //there are some memory leaks in this deprecated camera api
    //like if the activity is closed then to open the app phone must be rebooted

    /*
    deprecated apis are the ones which can work but can become disabled in future
    some apis are in the beta stage which are pre released but their documentation can be changed by google
    the ones that we use are the released apis
     */
Button clk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clk=(Button)findViewById(R.id.btnClick);



        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},111);
        //we request permissions at runtime otherwise app will crash

        if(cam==null)
        {
            cam= Camera.open();

            //to know more about camera.open() see its definition
            //basically we did this to assign a camera to the object cam

        }

        List<Camera.Size> picSizes=cam.getParameters().getSupportedPictureSizes();
        List<Camera.Size> vidSizes=cam.getParameters().getSupportedVideoSizes();
        List<Camera.Size> prevSizes=cam.getParameters().getSupportedPreviewSizes();

        /*
        we created a list of type camera.size which is basically a list of picture sizes that the camera
        can support. Parameters is a subclass of camera and cam.getParameteres() gives an object of class parameters
        parameters class contains all the info about the camera and its method getSupportedPictureSizes() gives the list of all the picture
        sizes that the camera can support
         */

        for(Camera.Size size:picSizes){
            Log.d("MA","PICS: "+size.height+" "+size.width);
            //height and width of each size will be logged
        }
        for(Camera.Size size:vidSizes){
            Log.d("MA","VIDS: "+size.height+" "+size.width);
            //height and width of each size will be logged
        }
        for(Camera.Size size:prevSizes){
            Log.d("MA","PREVS: "+size.height+" "+size.width);
            //height and width of each size will be logged
        }


        int rotation=0;
        switch (getWindowManager().getDefaultDisplay().getRotation()){
            //we put the rotation of our device in switch case
            //getWindowManager().getDefaultDisplay().getRotation() this gives the rotation of the device

            //surface's rotation and device's rotation have a diffrence of 90 degrees
            //also surface measure the angle clockwise and camera measures it anticlock wise
            case Surface.ROTATION_0:rotation=90; break;
            case Surface.ROTATION_90:rotation=0; break;
            case Surface.ROTATION_270:rotation=180; break;
            case Surface.ROTATION_180:rotation=270; break;



        }

        cam.setDisplayOrientation(rotation);//we set the rotation of the preview

        cam.getParameters().setRotation(rotation);//sets the rotation of the photo clicked



        FrameLayout fLayout=(FrameLayout)findViewById(R.id.preview_container);

        fLayout.addView(new CameraPreview(this,cam));







        //the below callback is used for taking pics
        final Camera.PictureCallback picCallBack=new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //do something when picture is taken
                //the byte[] data is your picture
                //you can store it in a file
                //the camera object is also passed if we wanna set orientation or something

                Log.d("MA","onPictureTaken: "+data.length);

                File pictureFile = getOutputMediaFile();
                if (pictureFile == null) {
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                } catch (FileNotFoundException e) {

                } catch (IOException e) {
                }

                cam.stopPreview();
                cam.startPreview();
            }
        };

        clk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cam.takePicture(null,null,picCallBack);//this method takes the pic and accept the following parameters
                                                       // shutter callback=callback for shutter speed of camera
                                                       //raw data callback =callback for getting the pixels in raw form without being converted to jpeg
                                                        //picturecallbak for taking pics
            }
        });

















//        /** Check if this device has a camera */
//        private boolean checkCameraHardware(Context context) {
//            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
//                // this device has a camera
//                return true;
//            } else {
//                // no camera on this device
//                return false;
//            }
//        }




    }
    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        Log.d("getOutputMediaStorage",mediaFile.getAbsolutePath());

        return mediaFile;
    }


    @Override
    protected void onStop() {
        cam.release();
        //if we dont release the cam we'll have to reeebot the phone after testing the our app
        super.onStop();
    }
}
