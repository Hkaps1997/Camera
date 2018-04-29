package com.example.harshit.cameraintent;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by HARSHIT on 10-03-2017.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {




    private  Camera camera;
    private  SurfaceHolder holder;
    private  boolean initialised=false;//to check whether surfaceView has been initialsed or not

    //SurfaceHolder.Callback acts as a holder for surface view to record changes made to the surface view
    /*
     when surface is created:  public void surfaceCreated(SurfaceHolder holder) and  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) are called
     when surface is rotated/changed= public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) is called
     when surface is destroyed:public void surfaceDestroyed(SurfaceHolder holder)

     */

    /*
    if we wanna add the view directly to our layout we can implement the other 2 ctors and instead of using the ctor
    given below we can do the work being done by the ctor in a separate method. otherwise we can use addView() method in the
    layouts(linear,frame etc..) and directly add our view
     */


    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.camera=camera;
        this.holder=getHolder();//getting the holder
        holder.addCallback(this);//implementation of the callback; it ensures that holder.addCallback() works on "this" holder only
                                 //other wise we can pass a new object of holder in this method

        initialised=true;
    }

//below 2 ctors only set the layout
    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }



    private  void initialise(Camera camera){
        if(initialised) {
            //if the preview has already been initialised then return from function
            return;
        }
        this.camera=camera;
        this.holder=getHolder();
        holder.addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {


        //try/catch is required bcoz if preview is set before the surface is created(which is not done immediately) an exception is thrown

        try {
            camera.setPreviewDisplay(holder);//setting the preview on the surface
            camera.startPreview();//starting that preview
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


        if(holder.getSurface()==null)
        {
            //if surface is not created further code will not execute
            return;
        }

        camera.stopPreview();//stop the preview before making any changes

        /*
        make some changes
         */

        try {
            camera.getParameters().setPreviewSize(width,height);
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
