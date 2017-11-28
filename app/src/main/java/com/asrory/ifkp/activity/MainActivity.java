package com.asrory.ifkp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.asrory.ifkp.R;
import com.asrory.ifkp.presenter.BpnnIdentifyPresenter;
import com.asrory.ifkp.presenter.MainPresenter;
import com.asrory.ifkp.presenter.PcaPresenter;
import com.asrory.ifkp.view.MainView;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.*;

public class MainActivity extends AppCompatActivity implements MainView {
    // Preview preparation
    Camera camera;
    SurfaceView cameraPreview;
    SurfaceHolder surfaceHolder;
    public RelativeLayout headerContainer,
            borderFocusLeft,
            borderFocusCenter,
            borderFocusRight,
            borderFocusTop,
            containerButtonCapture,
            settingContainerButton;
    ImageButton buttonCapture;
    public CheckBox flash, binConvert, autoTreshold;
    public EditText server,
            setHeightCR,
            setWidthCR,
            setHeightRC,
            setWidthRC,
            setResolution,
            setCaptureEvery,
            setSamples,
            setSampleTest,
            setDelay,
            defaultTreshold,
            maxEpoch,
            targetError,
            alpha,
            neuronHidden,
            reduksiPCA;
    //mode for know now condition is run or no
    boolean isAddData = false,
            isPreview = false,
            isIdentify = false;
    // title
    TextView titleToolbar, titleButtonCapture;
    // setting
    public int setHeightCaptureRange = 0,
            setWidthCaptureRange = 0,
            setResizeHeightCapture = 0,
            setResizeWidthCapture = 0,
            resolusi = 0,// resolution
            innerMenuContainerHeight = 0,
            captureSampel = 0,
            captureSampelTest = 0,
            captureEvery = 0, // every 3 seconds
            delayCapture = 0, // delay
            imageRotation = 0;
    public RadioGroup identifyMode;
    public RadioButton liveIdentify, manualIdentify;
    // footer menu
    boolean footerMenuIsOpen = true;
    Button buttonShowMenu, buttonCloseMenu;
    LinearLayout innerMenuContainer,
            menuContainer,
            innerMenuContainerLeft,
            innerMenuContainerRight,
            innerContainerButtonCapture;
    ProgressDialog progressDialog;
    AlertDialog.Builder alertDialog;
    MainPresenter mainPresenter;
    PcaPresenter pcaPresenter;
    BpnnIdentifyPresenter bpnnIdentifyPresenter;

    /**
     * OnCreate activity
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(" onCreate() -->");
        mainPresenter = new MainPresenter(this);
        pcaPresenter = new PcaPresenter(this);
        bpnnIdentifyPresenter = new BpnnIdentifyPresenter(this);
        setContentView(R.layout.fkp_light_view_handler);
        // setup ui
        initializeMainView();
        // listener to the Capture button
        buttonCapture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onCapture();
            }
        });
        buttonCapture.setBackgroundResource(R.drawable.btn_light_mode);
        stopCameraPreview();
    }

    @Override
    public void initializeMainView() {
        System.out.println("  initializeMainView() -->");

        // fullscreen window
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // view handler
        cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);
        surfaceHolder = cameraPreview.getHolder();
        // Header
        headerContainer = (RelativeLayout) findViewById(R.id.headerContainer);
        // Range for Capture
        borderFocusTop = (RelativeLayout) findViewById(R.id.borderFocusTop);
        borderFocusLeft = (RelativeLayout) findViewById(R.id.borderFocusLeft);
        borderFocusCenter = (RelativeLayout) findViewById(R.id.borderFocusCenter);
        borderFocusRight = (RelativeLayout) findViewById(R.id.borderFocusRight);
        // footer menu
        menuContainer = (LinearLayout) findViewById(R.id.menuContainer);
        buttonShowMenu = (Button) findViewById(R.id.buttonShowMenu);
        buttonCloseMenu = (Button) findViewById(R.id.buttonCloseMenu);
        innerMenuContainer = (LinearLayout) findViewById(R.id.innerMenuContainer);
        innerMenuContainerLeft = (LinearLayout) findViewById(R.id.innerMenuContainerLeft);
        innerMenuContainerRight = (LinearLayout) findViewById(R.id.innerMenuContainerRight);
        innerContainerButtonCapture = (LinearLayout) findViewById(R.id.innerContainerButtonCapture);
        titleButtonCapture = (TextView) findViewById(R.id.titleButtonCapture);
        titleToolbar = (TextView) findViewById(R.id.titleToolbar);
        // surface holder callback
        surfaceHolder.addCallback(this);
        // container Button Capture
        containerButtonCapture = (RelativeLayout) findViewById(R.id.containerButtonCapture);
        settingContainerButton = (RelativeLayout) findViewById(R.id.settingContainer);
        buttonCapture = (ImageButton) findViewById(R.id.buttonCapture);
    }

    @Override
    public void onCapture() {
        System.out.println("  onCapture() -->");
        mainPresenter.capture();
    }

    @Override
    public boolean isAddData() {
        System.out.println("   isAddData() --> return isAddData;");

        return this.isAddData;
    }

    @Override
    public boolean isIdentify() {
        System.out.println("   isIdentify() --> return isIdentify;");
        return this.isIdentify;
    }

    @Override
    public void doIdentify() {
        System.out.println("   doIdentify() --> ");

        /**
         * {@link MainActivity#onIdentify(View)}
         */
        if (mainPresenter.getSettingModel().getIdentifyMode() == R.id.liveIdentify) {

            camera.takePicture(null, pictureCallback_RAW, new Camera.PictureCallback() {
                public void onPictureTaken(byte[] arg0, Camera arg1) {
                    mainPresenter.preprocessing(arg0, MainActivity.this);
                    identify();
                }
            });
        } else {
            showFileChooser();
        }
    }

    public void identify() {
        System.out.println("   identify() --> ");
        progressDialog = ProgressDialog.show(MainActivity.this, "Identifikasi",
                "Silahkan tunggu sebentar sedang mengidentifikasi", true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                initializeAlertDialog();
                alertDialog.setTitle("Hasil Identifikasi")
                        .setIcon(R.drawable.ic_identification);
                double start = System.nanoTime();
                mainPresenter.identify();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.show();
                        isAddData = false;
                        isIdentify = true;
                    }
                });
                refreshCamera();
                double finish = System.nanoTime();
                System.out.println("waktu eksekusi program: " + (finish - start) / 1000000000.0);
            }
        }).start();
    }

    @Override
    public void doPreview() {

        Log.i(this.getLocalClassName(), "doPreview() --> ");
        if (isPreview) {
            stopCameraPreview();
            Toast.makeText(MainActivity.this, "Preview Mode Off", Toast.LENGTH_SHORT).show();
        } else {
            showCameraPreview();
            // PRATINJAU MODE
            isAddData = false;
            isIdentify = false;
            isPreview = true;

            titleButtonCapture.setTextColor(getResources().getColor(R.color.colorAccent));
            buttonCapture.setImageResource(R.drawable.ic_highlight_off_red_700_24dp);

            titleButtonCapture.setText(R.string.preview_off);
            Toast.makeText(MainActivity.this, "Preview Mode On", Toast.LENGTH_SHORT).show();
        }
    }

    Camera.PictureCallback pictureCallback_RAW = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] arg0, Camera arg1) {

        }
    };

    /**
     * menu toggle
     *
     * @param view
     */
    @Override
    public void toggleMenu(View view) {
        System.out.println("   toggleMenu(View view) --> ");

        mainPresenter.toggleMenu(footerMenuIsOpen);
    }

    /**
     * closer footer menu
     */
    @Override
    public void closeFooterMenu() {
        System.out.println("   closeFooterMenu() --> ");
        footerMenuIsOpen = false;
        buttonShowMenu.getLayoutParams().height = mainPresenter.dpToPixel(38);
        buttonShowMenu.setLayoutParams(buttonShowMenu.getLayoutParams());

        buttonCloseMenu.getLayoutParams().height = 0;
        buttonCloseMenu.setLayoutParams(buttonCloseMenu.getLayoutParams());
        menuContainer.setBackgroundColor(Color.TRANSPARENT);
        innerMenuContainer.getLayoutParams().height = 0;
        innerMenuContainer.setLayoutParams(innerMenuContainer.getLayoutParams());
    }

    /**
     * shower footer menu
     */
    @Override
    public void showFooterMenu() {
        System.out.println("   showFooterMenu() --> ");
        footerMenuIsOpen = true;
        menuContainer.setBackgroundColor(getResources().getColor(R.color.customeTeal));

        buttonShowMenu.getLayoutParams().height = 0;
        buttonShowMenu.setLayoutParams(buttonShowMenu.getLayoutParams());

        buttonCloseMenu.getLayoutParams().height = mainPresenter.dpToPixel(38);
        buttonCloseMenu.setLayoutParams(buttonCloseMenu.getLayoutParams());

        innerMenuContainer.getLayoutParams().height = innerMenuContainerHeight;
        innerMenuContainer.setLayoutParams(innerMenuContainer.getLayoutParams());

        innerMenuContainerLeft.getLayoutParams().width = headerContainer.getMeasuredWidth() / 2;
        innerMenuContainerLeft.setLayoutParams(innerMenuContainerLeft.getLayoutParams());
        innerMenuContainerRight.setLayoutParams(innerMenuContainerLeft.getLayoutParams());
    }

    /**
     * do something on surface change
     *
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        System.out.println("   surfaceChanged(SurfaceHolder holder, int format, int width, " + "int height) --> ");
        setHeightCaptureRange = mainPresenter.getSettingModel().getHeightCaptureRange();
        setWidthCaptureRange = mainPresenter.getSettingModel().getWidthCaptureRange();
        setResizeHeightCapture = mainPresenter.getSettingModel().getResizeHeightCapture();
        setResizeWidthCapture = mainPresenter.getSettingModel().getResizeWidthCapture();
        resolusi = mainPresenter.getSettingModel().getResolution();
        captureEvery = mainPresenter.getSettingModel().getCaptureEvery();
        captureSampel = mainPresenter.getSettingModel().getCaptureSample();
        captureSampelTest = mainPresenter.getSettingModel().getCaptureSampleTest();
        delayCapture = mainPresenter.getSettingModel().getDelayCapture();
        // getting width dan height of cameraPreview
        int previewWidth = cameraPreview.getMeasuredWidth(),
                previewHeight = cameraPreview.getMeasuredHeight();
        int titleToolbarHeight = titleToolbar.getMeasuredHeight(),
                settingHeight = settingContainerButton.getMeasuredHeight(),
                alternativeHeight = (titleToolbarHeight - settingHeight) / 2;
        // getting LayoutParams
        RelativeLayout.LayoutParams
                setParamsContainerButtonCapture = getLayoutParams(containerButtonCapture),
                setParamsBorderFocusTop = getLayoutParams(borderFocusTop),
                setParamsBorderFocusLeft = getLayoutParams(borderFocusLeft),
                setParamsBorderFocusCenter = getLayoutParams(borderFocusCenter),
                setParamsBorderFocusRight = getLayoutParams(borderFocusRight),
                setParamsSetting = getLayoutParams(settingContainerButton);
        // settingContainerButton LayoutParams
        setParamsBorderFocusTop.height = ((previewHeight - headerContainer.getMeasuredHeight()
                - innerContainerButtonCapture.getMeasuredHeight()) / 2) - (setHeightCaptureRange / 2);

        setParamsBorderFocusCenter.height = setParamsBorderFocusLeft.height
                = setParamsBorderFocusRight.height = setHeightCaptureRange;

        setParamsBorderFocusLeft.width = setParamsBorderFocusRight.width =
                (previewWidth - setWidthCaptureRange) / 2;

        setParamsBorderFocusCenter.topMargin = setParamsBorderFocusLeft.topMargin =
                setParamsBorderFocusRight.topMargin = setParamsBorderFocusTop.height +
                        (setParamsBorderFocusTop.topMargin = headerContainer.getMeasuredHeight());

        setParamsSetting.topMargin = alternativeHeight;
        borderFocusTop.setLayoutParams(setParamsBorderFocusTop);
        borderFocusLeft.setLayoutParams(setParamsBorderFocusLeft);
        borderFocusCenter.setLayoutParams(setParamsBorderFocusCenter);
        borderFocusRight.setLayoutParams(setParamsBorderFocusRight);
        setParamsContainerButtonCapture.height = previewHeight - headerContainer.getMeasuredHeight() -
                setParamsBorderFocusTop.height - setHeightCaptureRange;
        containerButtonCapture.setLayoutParams(setParamsContainerButtonCapture);
        settingContainerButton.setLayoutParams(setParamsSetting);
        // setup menu
        if (innerMenuContainerHeight == 0) {
            innerMenuContainerHeight = innerMenuContainer.getHeight();
        }

        if (!isIdentify) {
            showFooterMenu();
        }
    }

    /**
     * showing camera preview
     */
    @Override
    public void showCameraPreview() {
        System.out.println("   showCameraPreview() --> ");
        // opening camera
        if (camera == null) {
            cameraPreview.setBackgroundColor(Color.TRANSPARENT);
            camera = Camera.open();
        }
        titleButtonCapture.setTextColor(getResources().getColor(R.color.customeTeal));
        buttonCapture.setImageResource(R.drawable.ic_camera_enable_lm);
        buttonCapture.setBackgroundResource(R.drawable.btn_light_mode);
        titleButtonCapture.setText(R.string.capture);
        // settingContainerButton camera parameters
        Camera.Parameters parameters = camera.getParameters();
        // getsupportpicturesize
        List<Camera.Size> sizeList = parameters.getSupportedPictureSizes();
        // settingContainerButton resolution or size for capture picture
        int chosenSize = mainPresenter.getPictureSizeIndexForHeight(sizeList, resolusi);
        parameters.setPictureSize(sizeList.get(chosenSize).width, sizeList.get(chosenSize).height);
        // set rotation for capture picture
        mainPresenter.setRotationParameter(this, 0, parameters);
        try {
            // settingContainerButton orientation
            camera.setDisplayOrientation(90);
//            for (String a:parameters.getSupportedFocusModes()) {
//                System.out.println("support focus mode : "+a);
//            }
//            for (String a:parameters.getSupportedFlashModes()) {
//                System.out.println("support flash mode : "+a);
//            }
            //set camera parameter support autofocus and flash lamp
            if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                System.out.println("autofocus work");
            }

            if (mainPresenter.getSettingModel().getFlash()) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                System.out.println("flash work");
            }
            // set camera params
            camera.setParameters(parameters);
            // settting preview display
            camera.setPreviewDisplay(surfaceHolder);
            // start preview
            camera.startPreview();
            isAddData = true;
        } catch (IOException e) {
            if (camera != null) {
                // release camera
                camera.release();
                // settingContainerButton camera to null
                camera = null;
            }
            isAddData = false;
            // print track error
            e.printStackTrace();
        }
    }

    /**
     * stop camera preview
     */
    @Override
    public void stopCameraPreview() {
        System.out.println("   stopCameraPreview() --> ");
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            if (parameters.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            // settting preview display
            camera.setParameters(parameters);
            camera.stopPreview();
            camera.release();
            camera = null;
        }

        isAddData = false;
        isIdentify = false;
        isPreview = false;
        cameraPreview.setBackgroundColor(getResources().getColor(R.color.colorWhite));

        titleButtonCapture.setText(R.string.standby);
        titleButtonCapture.setTextColor(Color.parseColor("#9e9e9e"));
        buttonCapture.setImageResource(R.drawable.ic_camera_disable_dm_lm);
        buttonCapture.setBackgroundResource(R.drawable.btn_light_mode);
    }

    /**
     * do something on surface creating
     *
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("   surfaceCreated() --> ");

    }

    /**
     * purge surface
     *
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        System.out.println("   surfaceDestroyed() --> ");

        if (camera != null) {
            // stop camera  preview
            camera.stopPreview();
            // release camera
            camera.release();
            // set camera objet to null
            camera = null;
        }
    }

    /**
     * get param of relative layout
     *
     * @param relativeLayout
     * @return
     */
    public RelativeLayout.LayoutParams getLayoutParams(RelativeLayout relativeLayout) {
        System.out.println("   getLayoutParams() --> ");

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
        return params;
    }

    /**
     * refreshing camera
     */
    @Override
    public void refreshCamera() {
        // stop preview before making changes
        try {
            System.out.println("   refreshCamera() --> ");
            camera.stopPreview();
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            Log.d("VIEW_LOG_TAG", "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     * set title
     *
     * @param title
     */
    public void setToolbarTitle(int title) {
        System.out.println("   setToolbarTitle(String title: " + title + ") --> ");
        titleToolbar.setText(title);
    }

    /**
     * ADD NEW FKP with auto capture
     *
     * @param view
     */
    @Override
    public void onAdd(View view) {
        System.out.println("   onAdd(View view) --> ");

        setToolbarTitle(R.string.add_data);
        closeFooterMenu();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_input_identitas, null);
        initializeAlertDialog();

        showCameraPreview();
        // ADD DATA mode
        isIdentify = false;
        isPreview = false;
        alertDialog.setView(dialogView)
                .setIcon(R.drawable.ic_identification)
                .setTitle("Identitas")
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText textId = (EditText) dialogView.findViewById(R.id.textId);
                        EditText textName = (EditText) dialogView.findViewById(R.id.textName);
                        final Object tmpNewID = textId.getText().toString();
                        final Object tmpNewName = textName.getText().toString();
                        final File JSONFile = new File(mainPresenter.getSettingModel().getJsonFileUri(tmpNewID));
                        if (textId.getText().length() != 0 && textName.getText().length() != 0) {
                            if (JSONFile.exists()) {
                                alertDialog.setView(null)
                                        .setMessage("User dengan ID " + tmpNewID + " sudah ada, simpan perubahan ?")
                                        .setIcon(R.drawable.ic_update_weight)
                                        .setTitle("Update")
                                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // auto capture
                                                autoCapture(JSONFile, alertDialog, tmpNewID, tmpNewName);
                                            }
                                        })
                                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                stopCameraPreview();
                                                setToolbarTitle(R.string.idetification);
                                            }
                                        });
                                alertDialog.show();
                            } else {
                                // auto capture
                                autoCapture(JSONFile, alertDialog, tmpNewID, tmpNewName);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "ID dan Nama Tidak boleh Kosong", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setToolbarTitle(R.string.idetification);
                        stopCameraPreview();
                    }
                });
        alertDialog.show();

    }

    /**
     * auto capture for capture image with auto capture mode
     *
     * @param JSONFile
     * @param alertDialog
     */
    public void autoCapture(final File JSONFile, final AlertDialog.Builder alertDialog,
                            final Object id, final Object name) {
        System.out.println("   autoCapture() --> ");

        int timer = (captureSampel + captureSampelTest + 1 + delayCapture) * captureEvery * 1000;
        int delay = captureEvery * 1000;
        // Count Down Timer
        new CountDownTimer(timer, delay) {
            int sampleCounter = 1, sampleTestCounter = 1, counter = 0;
            String message = "";

            @Override
            public void onFinish() {
                mainPresenter.writeJSONFile(JSONFile, id, name);
                // dialog
                alertDialog.setView(null).setMessage("Pengambilan sampel selesai")
                        .setTitle("Info").setPositiveButton(null, null).setNegativeButton(null, null).show();
                // reset title of toolbar
                setToolbarTitle(R.string.idetification);
                stopCameraPreview();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                if (counter >= delayCapture) {
                    // capture image
                    camera.takePicture(null, pictureCallback_RAW, new Camera.PictureCallback() {

                        public void onPictureTaken(byte[] arg0, Camera arg1) {
                            System.out.println("   takePicture() --> ");
                            File mediaStorageDir = new File(mainPresenter.getSettingModel().getAppBasePath(),
                                    mainPresenter.getSettingModel().getImagesDir());

                            if (!mediaStorageDir.exists()) {
                                if (!mediaStorageDir.mkdirs()) {
                                    Log.d("IFKP", "Gagal membuat direktori file");
                                }
                            }

                            String filename = "ID_" + id + "_FKP_" + (
                                    sampleCounter <= captureSampel ?
                                            sampleCounter : "TEST_" + sampleTestCounter) + ".jpeg";

                            File pictureFile = new File(mainPresenter.getSettingModel().getAppBasePath() +
                                    mainPresenter.getSettingModel().getImagesDir() + filename);
                            try {
                                FileOutputStream fos = new FileOutputStream(pictureFile);
                                // preprocessing and writing ouput
                                if (sampleCounter <= captureSampel) {
                                    message = "Pengambilan sampel data latih  ke-" + (sampleCounter++);
                                    fos.write(mainPresenter.preprocessing(arg0, MainActivity.this));
                                } else {
                                    message = "Pengambilan sampel data uji ke-" + (sampleTestCounter++);
                                    fos.write(arg0);
                                }
                                fos.close();
                                // info
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            } catch (IOException ex) {
                                StackTraceElement[] error = ex.getStackTrace();
                                for (StackTraceElement err : error) {
                                    System.out.println("ERROR : " + err);
                                }
                            }
                            //refresh camera
                            refreshCamera();
                        }
                    });
                }
                counter++;
            }
        }.start();
    }

    @Override
    public void onSetting(View view) {
        System.out.println("   onSetting(View view) --> ");

        stopCameraPreview();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.setting, null);
        initializeSettingView(dialogView);
        mainPresenter.setSettingView(this);
        initializeAlertDialog();
        // dialog
        alertDialog.setTitle("Setting")
                .setIcon(R.drawable.ic_settings_app)
                .setView(dialogView)
                .setPositiveButton(
                        "SIMPAN", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mainPresenter.saveSetting(MainActivity.this);
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        })
                .setNegativeButton("Batal", null);
        alertDialog.show();
    }

    public void initializeSettingView(View dialogView) {
        System.out.println("   initializeSettingView(View dialogView) --> ");

        server = (EditText) dialogView.findViewById(R.id.serverAddress);

        flash = (CheckBox) dialogView.findViewById(R.id.flash);
        setHeightCR = (EditText) dialogView.findViewById(R.id.setHeightCR);
        setWidthCR = (EditText) dialogView.findViewById(R.id.setWidthCR);
        setHeightRC = (EditText) dialogView.findViewById(R.id.setHeightRC);
        setWidthRC = (EditText) dialogView.findViewById(R.id.setWidthRC);
        setResolution = (EditText) dialogView.findViewById(R.id.setResolution);
        setCaptureEvery = (EditText) dialogView.findViewById(R.id.setCaptureEvery);
        setSamples = (EditText) dialogView.findViewById(R.id.setSamples);
        setSampleTest = (EditText) dialogView.findViewById(R.id.setSampleTest);
        setDelay = (EditText) dialogView.findViewById(R.id.setDelay);

        binConvert = (CheckBox) dialogView.findViewById(R.id.binConvert);
        autoTreshold = (CheckBox) dialogView.findViewById(R.id.autoTreshold);
        defaultTreshold = (EditText) dialogView.findViewById(R.id.defaultTreshold);

        maxEpoch = (EditText) dialogView.findViewById(R.id.maxEpoch);
        targetError = (EditText) dialogView.findViewById(R.id.targetError);
        alpha = (EditText) dialogView.findViewById(R.id.alpha);
        neuronHidden = (EditText) dialogView.findViewById(R.id.neuronHidden);
        reduksiPCA = (EditText) dialogView.findViewById(R.id.reduksiPCA);

        identifyMode = (RadioGroup) dialogView.findViewById(R.id.identifyMode);
        liveIdentify = (RadioButton) dialogView.findViewById(R.id.liveIdentify);
        manualIdentify = (RadioButton) dialogView.findViewById(R.id.manualIdentify);

        autoTreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoTreshold.isChecked()) {
                    System.out.println("treshold : false");
                    defaultTreshold.setEnabled(false);
                } else {
                    System.out.println("treshold : true");
                    defaultTreshold.setEnabled(true);
                }
            }
        });
    }

    /**
     * fkp identify
     *
     * @param view
     */
    @Override
    public void onIdentify(View view) {
        System.out.println("   onIdentify(View view) --> ");

        if (mainPresenter.fileChecker(mainPresenter.getPcaModel().getPcaFileUri())
                && mainPresenter.fileChecker(mainPresenter.getBpnnModel().getBpnnFileUri())) {
            setToolbarTitle(R.string.idetification);
            /**
             * {@link MainActivity#onCreate(Bundle)#surfaceChanged(SurfaceHolder, int, int, int)}
             */
            if (mainPresenter.getSettingModel().getIdentifyMode() == R.id.liveIdentify) {
                showCameraPreview();
            } else {
                buttonCapture.setImageResource(R.drawable.ic_file);
                buttonCapture.setBackgroundResource(R.drawable.btn_drak_mode);
                titleButtonCapture.setText(R.string.from_file);
            }
            // IDENTIFICATION MODE
            isAddData = isPreview = false;
            isIdentify = true;
            closeFooterMenu();
        } else {
            alertDialogWarning("Peringatan",
                    "Lakukan Ekstraksi ciri dan pelatihan data dengan metode PCA dan BPNN sebelum melakukan identifikasi");
        }

    }

    @Override
    public void recognized(String id) {
        System.out.println("   recognized() --> ");

        progressDialog.dismiss();
        JSONObject jsonObject = mainPresenter.getJsonFile(id);
        alertDialog.setMessage("\tID\t: " + jsonObject.get("id") + "\n\tNama\t: " + jsonObject.get("name"));
    }

    @Override
    public void unrecognized() {
        System.out.println("   unrecognized() --> ");

        progressDialog.dismiss();
        alertDialog.setMessage("\tTidak dikenali\n\tSilahka coba lagi");
    }

    @Override
    public void errorIdentification() {
        System.out.println("   errorIdentification() --> ");

        progressDialog.dismiss();
        alertDialogWarning("Terjadi kesalahan", "Terjadi kesalahan pada saat identifikasi, kemungkinan " +
                "karena kesalahan pada pengaturan pengambilan gambar atau capture " +
                "Setting pada Setting. Silahkan periksa kembali pengaturan pada " +
                "Setting > Capture Setting > Capture Resize");
    }

    /**
     * check any new file of BPNN in server or no
     * if any new file of BPNN get file
     *
     * @param view
     */
    @Override
    public void onSyncWeight(View view) {

        System.out.println("   onSyncWeight(View view) --> ");
        stopCameraPreview();
        initializeAlertDialog();
        alertDialog.setTitle("Peringatan").setIcon(R.drawable.ic_alert)
                .setMessage("Sinkronisasi akan mengubah data pada smartphone anda dengan data yang baru " +
                        "diunduh dari server. Apakah anda yakin untuk melakukan Sinkronisasi ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        progressDialog = ProgressDialog.show(MainActivity.this, "",
                                "Sedang Mengunduh file...", true);
                        new Thread(new Runnable() {
                            public void run() {
                                mainPresenter.syncWeight();
                                progressDialog.dismiss();
                            }
                        }).start();
                    }
                });
        alertDialog.show();

    }

    @Override
    public void successSyncWeight() {
        System.out.println("   successSyncWeight() --> ");
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, "Sinkronisasi telah selesai", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void failedSyncWeight() {

        System.out.println("   failedSyncWeight() --> ");
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this,
                        "Proses sinkronisasi bobot Gagal. Silahkan priksa kembali Server Address pada menu Setting",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * pca PCA and after that BPNN
     * uploading file pca to server
     *
     * @param view
     */
    @Override
    public void onProcess(View view) {
        System.out.println("   onProcess(View view) --> ");
        stopCameraPreview();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_train, null);
        initializeAlertDialog();
        alertDialog.setView(dialogView).setIcon(R.drawable.ic_setup).setTitle(R.string.process);
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        System.out.println("   onBackPressed() --> ");
        if (!(isIdentify | isAddData | isPreview)) {
            initializeAlertDialog();
            alertDialog.setIcon(R.drawable.ic_alert)
                    .setTitle("Peringatan")
                    .setMessage("Apakah anda ingin menutup aplikasi ini ?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("Tidak", null);
            alertDialog.show();
        } else {
            stopCameraPreview();
            Toast.makeText(MainActivity.this, "Clear All Process", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPcaExtraction(View view) {
        System.out.println("   onPcaExtraction(View view) --> ");
        if (pcaPresenter.isReady()) {
            initializeAlertDialog();
            alertDialog.setTitle("Peringatan")
                    .setIcon(R.drawable.ic_alert)
                    .setMessage("Lanjutkan Proses Ekstraksi Ciri ?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            progressDialog = ProgressDialog.show(MainActivity.this, "",
                                    "Mengekstraksi Ciri...", true);
                            new Thread(new Runnable() {
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(MainActivity.this,
                                                    "Silahkan menunggu beberapa saat, " +
                                                            "proses ekstraksi ciri sedang " +
                                                            "berlangsung", Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    });
                                    pcaPresenter.extract();
                                }
                            }).start();
                        }
                    }).show();
        } else {
            filedPcaExtraction();
        }
    }

    @Override
    public void successPcaExtraction() {
        System.out.println("   successPcaExtraction() --> ");
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, "Proses Ekstraksi Ciri Telah Selesai...", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void filedPcaExtraction() {
        System.out.println("   filedPcaExtraction() --> ");
        runOnUiThread(new Runnable() {
            public void run() {
                alertDialogWarning("Peringatan", "Tambah data terlebih dahulu sebelum melakukan Ekstraksi ciri " +
                        "dengan metode PCA");
            }
        });
    }

    @Override
    public void onBpnnTrain(View view) {
        System.out.println("   onBpnnTrain(View view) --> ");
        initializeAlertDialog();
        if (bpnnIdentifyPresenter.isReady()) {
            alertDialog.setTitle("Peringatan").setIcon(R.drawable.ic_alert)
                    .setMessage("Lanjutkan pelatihan ?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            progressDialog = ProgressDialog.show(MainActivity.this, "",
                                    "Melatih data...", true);
                            new Thread(new Runnable() {
                                public void run() {
                                    double start = System.nanoTime();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(MainActivity.this,
                                                    "Proses pelatihan data sedang " +
                                                            "berlangsung...",
                                                    Toast.LENGTH_SHORT).show();
                                            Toast.makeText(MainActivity.this,
                                                    "Mengunggah file PCA ke server : " +
                                                            mainPresenter.getSettingModel()
                                                                    .getServerAddress(),
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                    bpnnIdentifyPresenter.train();

                                    double finish = System.nanoTime();
                                    System.out.println("waktu train program: " +
                                            (finish - start) / 1000000000.0);
                                }
                            }).start();
                        }
                    }).show();
        } else {
            alertDialogWarning("Peringatan",
                    "Lakukan Ekstraksi ciri dengan metode PCA terlebih " +
                            "dahulu sebelum melakukan pelatihan dengan metode BPNN");
        }
    }

    @Override
    public void successBpnnTrain() {
        System.out.println("   successBpnnTrain() --> ");
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, "Proses Pelatihan Telah Selesai...", Toast.LENGTH_SHORT).show();
            }
        });
        progressDialog.dismiss();
    }

    @Override
    public void failedBpnnTrain() {
        System.out.println("   failedBpnnTrain() --> ");
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this,
                        "Proses Pelatihan Gagal. Silahkan priksa kembali Server Address pada menu Setting",
                        Toast.LENGTH_LONG).show();
            }
        });
        progressDialog.dismiss();
    }

    @Override
    public void alertDialogWarning(String title, String messages) {
        System.out.println("   alertDialogWarning(String " + title
                + ", String " + messages + ") --> ");
        initializeAlertDialog();
        alertDialog.setTitle(title).setIcon(R.drawable.ic_alert);
        alertDialog.setMessage(messages);
        alertDialog.show();
    }

    @Override
    public void initializeAlertDialog() {
        System.out.println("   initializeAlertDialog() --> ");
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.getContext().setTheme(R.style.Theme_AppCompat_Light_Dialog_Alert);
    }

    /**
     * file chooser handler
     */
    private static final int FILE_SELECT_CODE = 0;

    public void showFileChooser() {
        System.out.println("   showFileChooser() --> ");

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent,
                    "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("   onActivityResult() --> ");

        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    System.out.println("   File Uri: " + uri.toString());
                    // Get the path
                    String path = getPath(this, uri);
                    System.out.println("   File Path: " + path);
                    // Get the file instance
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    mainPresenter.doPreprocessing(bitmap, this);
                    identify();
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getPath(Context context, Uri uri) {
        System.out.print("   getPath() --> ");

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {

            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            System.out.println(" return " + uri.getPath() + " -->");

            return uri.getPath();
        }
        System.out.println(" return null -->");

        return null;
    }
}
