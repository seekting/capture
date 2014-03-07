
package com.seekting.capture;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.zxing.ViewfinderView;
import com.zxing.camera.CameraManager;
import com.zxing.decode.CaptureActivityHandler;

public class CaptureActivity extends Activity implements Callback {
    private CameraManager cameraManager;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private CaptureActivityHandler handler;
    private static final String TAG = CaptureActivity.class.getSimpleName();
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraManager = new CameraManager(getApplicationContext());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            surfaceHolder.addCallback(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats, decodeHints,
                        characterSet, cameraManager);
            }
        } catch (IOException ioe) {
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     * 
     * @param rawResult The contents of the barcode.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param barcode A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {

        // inactivityTimer.onActivity();
//        viewfinderView.drawResultBitmap(barcode);
        // playBeepSoundAndVibrate();
        // txtResult.setText(obj.getBarcodeFormat().toString() +":"
        // + obj.getText());
        System.out.println(rawResult.getBarcodeFormat().toString() + ":"
                + rawResult.getText());

    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    public CaptureActivityHandler getHandler() {
        return handler;
    }

    public void setHandler(CaptureActivityHandler handler) {
        this.handler = handler;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public void setViewfinderView(ViewfinderView viewfinderView) {
        this.viewfinderView = viewfinderView;
    }
}
