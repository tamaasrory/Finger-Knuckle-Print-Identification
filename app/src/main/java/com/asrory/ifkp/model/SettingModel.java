package com.asrory.ifkp.model;

import android.os.Environment;
import com.asrory.ifkp.R;
import org.json.simple.JSONObject;

/**
 * Created by asrory on 23/06/17.
 */

public class SettingModel extends BaseModel {
    private String settingFilename = "setting.ifkp";

    private String serverAddress = "serverAddress";
    private String flash = "flash";
    private String heightCaptureRange = "setHeightCaptureRange";
    private String widthCaptureRange = "setWidthCaptureRange";
    private String resizeHeightCapture = "setResizeHeightCapture";
    private String resizeWidthCapture = "setResizeWidthCapture";
    private String resolution = "resolusi";
    private String captureEvery = "captureEvery";
    private String captureSample = "captureSample";
    private String captureSampleTest = "captureSampleTest";
    private String delayCapture = "delayCapture";
    private String binaryConvert = "binConvert";
    private String autoBinaryTreshold = "autoBinaryTreshold";
    private String manualBinaryTreshold = "manualBinaryTreshold";
    private String maxEpoch = "maxEpoch";
    private String maxError = "maxError";
    private String alpha = "alpha";
    private String hiddenNeuron = "hiddenNeuron";
    private String pcaReduction = "pcaReduction";
    private String identifyMode = "identifyMode";

    private JSONObject set = new JSONObject();

    public String get(String key) {
        System.out.println("  get(String key: " + key + ") --> ");
        System.out.println("  return  ; ");
        return this.set.get(key).toString();
    }

    public String getSettingFilename() {
        System.out.println("  getSettingFilename() --> ");
        System.out.println("  return  ; ");
        return appBasePath + settingFilename;
    }

    public String getServerAddress() {
        System.out.println("  getServerAddress() --> ");
        System.out.println("  return  ; ");
        return get(serverAddress);
    }

    public boolean getFlash() {
        System.out.println("  getFlash() --> ");
        System.out.println("  return  ; ");
        return Boolean.parseBoolean(get(flash));
    }

    public int getHeightCaptureRange() {
        System.out.println("  getHeightCaptureRange() --> ");
        System.out.println("  return  ; ");
        return Integer.parseInt(get(heightCaptureRange));
    }

    public int getWidthCaptureRange() {
        System.out.println("  getWidthCaptureRange() --> ");
        System.out.println("  return  ; ");
        return Integer.parseInt(get(widthCaptureRange));
    }

    public int getResizeHeightCapture() {
        System.out.println("  getResizeHeightCapture() --> ");
        System.out.println("  return  ; ");
        return Integer.parseInt(get(resizeHeightCapture));
    }

    public int getResizeWidthCapture() {
        System.out.println("  getResizeWidthCapture() --> ");
        System.out.println("  return  ; ");
        return Integer.parseInt(get(resizeWidthCapture));
    }

    public int getResolution() {
        System.out.println("  getResolution() --> ");
        System.out.println("  return  ; ");
        return Integer.parseInt(get(resolution));
    }

    public int getCaptureEvery() {
        System.out.println("  getCaptureEvery() --> ");
        System.out.println("  return  ; ");
        return Integer.parseInt(get(captureEvery));
    }

    public int getCaptureSample() {
        System.out.println("  getCaptureSample() --> ");
        System.out.println("  return  ; ");
        return Integer.parseInt(get(captureSample));
    }

    public int getCaptureSampleTest() {
        System.out.println("  getCaptureSampleTest() --> ");
        System.out.println("  return  ; ");
        return Integer.parseInt(get(captureSampleTest));
    }

    public int getDelayCapture() {
        System.out.println("  getDelayCapture() --> ");
        System.out.println("  return  ; ");
        return Integer.parseInt(get(delayCapture));
    }

    public boolean getBinaryConvert() {
        System.out.println("  getBinaryConvert() --> ");
        System.out.println("  return  ; ");
        return Boolean.parseBoolean(get(binaryConvert));
    }

    public boolean getAutoBinaryTreshold() {
        System.out.println("  getAutoBinaryTreshold() --> ");
        System.out.println("  return  ; ");
        return Boolean.parseBoolean(get(autoBinaryTreshold));
    }

    public int getManualBinaryTreshold() {
        System.out.println("  getManualBinaryTreshold() --> ");
        System.out.println("  return  ; ");
        return Integer.parseInt(get(manualBinaryTreshold));
    }

//    public int getActivationFunction() {
//        System.out.println("  getActivationFunction() --> ");
//        System.out.println("  return  ; ");
 //return Integer.parseInt(get(activationFunction));
//    }

//    public boolean getAutoTresholdBpnn() {
//        System.out.println("  getAutoTresholdBpnn() --> ");
//        System.out.println("  return  ; ");
 //return Boolean.parseBoolean( get(autoTresholdBpnn));
//    }
//
//    public double getManualTresholdBpnn() {
//        System.out.println("  getManualTresholdBpnn() --> ");
//        System.out.println("  return  ; ");
// return Double.parseDouble( get(manualTresholdBpnn));
//    }

    public int getMaxEpoch() {
        System.out.println("  getMaxEpoch() --> ");
        System.out.println("  return  ; ");
        return Integer.parseInt(get(maxEpoch));
    }

    public double getMaxError() {
        System.out.println("  getMaxError() --> ");
        System.out.println("  return  ; ");
        return Double.parseDouble(get(maxError));
    }

    public double getAlpha() {
        System.out.println("  getAlpha() --> ");
        System.out.println("  return  ; ");
        return Double.parseDouble(get(alpha));
    }

    public int getHiddenNeuron() {
        System.out.println("  getHiddenNeuron() --> ");
        System.out.println("  return  ; ");
        return Integer.parseInt(get(hiddenNeuron));
    }

    public int getPcaReduction() {
        System.out.println("  getPcaReduction() --> ");
        System.out.println("  return  ; ");
        return Integer.parseInt(get(pcaReduction));
    }

    public int getIdentifyMode() {
        System.out.println("  getIdentifyMode() --> ");
        System.out.println("  return  ; ");
        return Integer.parseInt(get(identifyMode));
    }

    public void set(Object key, Object value) {
        System.out.println("  set(Object key, Object value) --> ");
        this.set.put(key, value);
    }

    public void setSettingFilename(String settingFilename) {
        System.out.println("  setSettingFilename(String settingFilename: " +"" + settingFilename + ") --> ");
        this.settingFilename = settingFilename;
    }

    public void setServerAddress(Object serverAddress) {
        System.out.println("  setServerAddress(Object serverAddress : " + serverAddress.toString() + ") ");
        set(this.serverAddress, serverAddress);
    }

    public void setFlash(Object flash) {
        System.out.println("  setFlash(Object flash : " + flash + ") --> ");
        set(this.flash, flash);
    }

    public void setHeightCaptureRange(Object heightCaptureRange) {
        System.out.println("  setHeightCaptureRange(Object heightCaptureRange : " + heightCaptureRange + ") --> ");
        set(this.heightCaptureRange, heightCaptureRange);
    }

    public void setWidthCaptureRange(Object widthCaptureRange) {
        System.out.println("  setWidthCaptureRange(Object widthCaptureRange : " + widthCaptureRange + ") --> ");
        set(this.widthCaptureRange, widthCaptureRange);
    }

    public void setResizeHeightCapture(Object resizeHeightCapture) {
        System.out.println("  setResizeHeightCapture(Object resizeHeightCapture : " + resizeHeightCapture + ") --> ");
        set(this.resizeHeightCapture, resizeHeightCapture);
    }

    public void setResizeWidthCapture(Object resizeWidthCapture) {
        System.out.println("  setResizeWidthCapture(Object resizeWidthCapture : " + resizeWidthCapture + ") --> ");
        set(this.resizeWidthCapture, resizeWidthCapture);
    }

    public void setResolution(Object resolution) {
        System.out.println("  setResolution(Object resolution : " + resolution + ") ");
        set(this.resolution, resolution);
    }

    public void setCaptureEvery(Object captureEvery) {
        System.out.println("  setCaptureEvery(Object captureEvery : " + captureEvery + ") --> ");
        set(this.captureEvery, captureEvery);
    }

    public void setCaptureSample(Object captureSample) {
        System.out.println("  setCaptureSample(Object captureSample : " + captureSample + ") --> ");
        set(this.captureSample, captureSample);
    }

    public void setCaptureSampleTest(Object captureSampleTest) {
        System.out.println("  setCaptureSampleTest(Object captureSampleTest : " +captureSampleTest + ") --> ");
        set(this.captureSampleTest, captureSampleTest);
    }

    public void setDelayCapture(Object delayCapture) {
        System.out.println("  setDelayCapture(Object delayCapture : " + delayCapture + ") --> ");
        set(this.delayCapture, delayCapture);
    }

    public void setBinaryConvert(Object binaryConvert) {
        System.out.println("  setBinaryConvert(Object binaryConvert : " + binaryConvert + ") --> ");
        set(this.binaryConvert, binaryConvert);
    }

    public void setAutoBinaryTreshold(Object autoBinaryTreshold) {
        System.out.println("  setAutoBinaryTreshold(Object autoBinaryTreshold : " + autoBinaryTreshold + ") --> ");
        set(this.autoBinaryTreshold, autoBinaryTreshold);
    }

    public void setManualBinaryTreshold(Object manualBinaryTreshold) {
        System.out.println("  setManualBinaryTreshold(Object manualBinaryTreshold : " + manualBinaryTreshold + ") --> ");
        set(this.manualBinaryTreshold, manualBinaryTreshold);
    }

//    public void setActivationFunction(Object activationFunction) {
//        System.out.println("  setActivationFunction(Object activationFunction : " + activationFunction + ") --> ");
//        set(this.activationFunction, activationFunction);
//    }
//
//    public void setAutoTresholdBpnn(Object autoTresholdBpnn) {
//        System.out.println("  setAutoTresholdBpnn(Object autoTresholdBpnn: " + autoTresholdBpnn + ") --> ");
//        set(this.autoTresholdBpnn, autoTresholdBpnn);
//    }
//
//    public void setManualTresholdBpnn(Object manualTresholdBpnn) {
//        System.out.println("  setManualTresholdBpnn(Object manualTresholdBpnn : " +
//                "" + manualTresholdBpnn + ") --> ");
//        set(this.manualTresholdBpnn, manualTresholdBpnn);
//    }

    public void setMaxEpoch(Object maxEpoch) {
        System.out.println("  setMaxEpoch(Object maxEpoch : " + maxEpoch + ") ");
        set(this.maxEpoch, maxEpoch);
    }

    public void setMaxError(Object maxError) {
        System.out.println("  setMaxError(Object maxError : " + maxError + ") --> ");
        set(this.maxError, maxError);
    }

    public void setAlpha(Object alpha) {
        System.out.println("  setAlpha(Object alpha : " + alpha + ") --> ");
        set(this.alpha, alpha);
    }

    public void setHiddenNeuron(Object hiddenNeuron) {
        System.out.println("  setHiddenNeuron(Object hiddenNeuron : " + hiddenNeuron + ") --> ");
        set(this.hiddenNeuron, hiddenNeuron);
    }

    public void setPcaReduction(Object pcaReduction) {
        System.out.println("  setPcaReduction(Object pcaReduction : " + pcaReduction + ") --> ");
        set(this.pcaReduction, pcaReduction);
    }

    public void setIdentifyMode(Object identifyMode) {
        System.out.println("  setIdentifyMode(Object identifyMode:" + identifyMode + ") --> ");
        set(this.identifyMode, identifyMode);
    }

    public SettingModel loadSetting(String caller) {
        System.out.println("  loadSetting() , caller:" + caller + "");
        if (!fileChecker(getSettingFilename())) {
            // initlize onSetting
            initSetting();
        }
        this.set = loadJsonFile(getSettingFilename());
        System.out.println("  return  ; ");
        return this;
    }

    public void initSetting() {
        System.out.println("  initSetting() --> ");
        setServerAddress("http://192.168.43.142");
        setFlash(true);
        setHeightCaptureRange(300);
        setWidthCaptureRange(300);
        setResizeHeightCapture(60);
        setResizeWidthCapture(60);
        setResolution(720);
        setCaptureEvery(3);
        setCaptureSample(9);
        setCaptureSampleTest(1);
        setDelayCapture(2);
        setBinaryConvert(true);
        setAutoBinaryTreshold(true);
        setManualBinaryTreshold(128);
//        setAutoTresholdBpnn(true);
//        setManualTresholdBpnn(0.5);
        setMaxEpoch(100000);
        setMaxError(0.0001);
        setAlpha(0.5);
        setHiddenNeuron(17);
        setPcaReduction(9);
        setIdentifyMode(R.id.liveIdentify);
        // init set
        saveSetting();
    }

    public void saveSetting() {
        System.out.println("  saveSetting() --> ");

        writeFile(set.toString(), getSettingFilename());
    }

}
