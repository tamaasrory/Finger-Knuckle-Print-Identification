package com.asrory.ifkp.view;

import android.view.SurfaceHolder;
import android.view.View;

/**
 * Created by asrory on 24/06/17.
 */

public interface MainView extends SurfaceHolder.Callback {
    /**
     * initialization variable for main layout
     */
    void initializeMainView();

    /**
     * Task Pointer
     *
     * @return
     */
    boolean isAddData();

    boolean isIdentify();

    void onCapture();

//    void doAddData();

    void doIdentify();

    void doPreview();

    /**
     * Toggle Menu
     */
    void setToolbarTitle(int title);

    void toggleMenu(View view);

    void closeFooterMenu();

    void showFooterMenu();

    /**
     * Camera handler
     */
    void showCameraPreview();

    void stopCameraPreview();

    void refreshCamera();

    /**
     * metode manager
     *
     * @param view
     */
    void onProcess(View view);

    /**
     * BPNN
     *
     * @param view
     */
    void onBpnnTrain(View view);

    void successBpnnTrain();

    void failedBpnnTrain();

    /**
     * PCA
     *
     * @param view
     */

    void onPcaExtraction(View view);

    void successPcaExtraction();

    void filedPcaExtraction();

    /**
     * ADD DATA
     *
     * @param view
     */
    void onAdd(View view);

    /**
     * IDENTIFY
     *
     * @param view
     */
    void onIdentify(View view);

    void recognized(String id);

    void unrecognized();

    void errorIdentification();

    /**
     * SYNC
     *
     * @param view
     */
    void onSyncWeight(View view);

    void successSyncWeight();

    void failedSyncWeight();

    /**
     * SETTING
     */
    void onSetting(View view);

    /**
     * DIALOG
     *
     * @param title
     * @param messages
     */
    void alertDialogWarning(String title, String messages);

    void initializeAlertDialog();
}
