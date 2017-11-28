package com.asrory.ifkp.presenter;

import Jama.Matrix;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import com.asrory.ifkp.R;
import com.asrory.ifkp.activity.MainActivity;
import com.asrory.ifkp.view.MainView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Created by asrory on 23/06/17.
 */
public class MainPresenter extends BasePresenter {
    private JSONObject collageForIdentify = new JSONObject();
    private JSONArray list;
    private JSONArray collageForAdd = new JSONArray();
    private int treshold = 0;

    public MainPresenter(MainView mainView) {
        this.mainView = mainView;
          System.out.print(getClass().getName() + "  MainPresenter(MainView mainView) --> ");
    }

    public JSONObject getCollageForIdentify() {
          System.out.print(getClass().getName() + "  getCollageForIdentify() --> ");
        return collageForIdentify;
    }

    public JSONArray getList() {
        return this.list;
    }

    public void setList(JSONArray list) {
        this.list = list;
    }

    public boolean fileChecker(String fileUri) {
          System.out.print(getClass().getName() + "  fileChecker(String fileUri : " + fileUri + ") --> ");
        return settingModel.fileChecker(fileUri);
    }

    public boolean fileDirChecker(String fileUri) {
          System.out.print(getClass().getName() + "  fileDirChecker(String fileUri : " + fileUri + ") --> ");
        return settingModel.fileDirChecker(fileUri);
    }

    public void toggleMenu(boolean show) {
          System.out.print(getClass().getName() + "  toggleMenu(boolean show : " + show + ") --> ");
        if (show) {
            mainView.closeFooterMenu();
        } else {
            mainView.showFooterMenu();
        }
    }

    /**
     * preprocessing image
     *
     * @param data
     * @return
     */
    public byte[] preprocessing(byte[] data, MainActivity mainActivity) {
          System.out.print(getClass().getName() + "  preprocessing(byte[] data, MainActivity mainActivity) --> ");
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        return doPreprocessing(bitmap, mainActivity);
    }

    public byte[] doPreprocessing(Bitmap bitmap, MainActivity mainActivity) {
        // crop to 200 x 200 px
        Bitmap crop = crop(bitmap, mainActivity.borderFocusLeft.getWidth(),
                mainActivity.borderFocusLeft.getTop(), mainActivity.setHeightCaptureRange,
                mainActivity.setWidthCaptureRange);
        // resize to 40 x 40 px
        Bitmap resize = resize(crop, mainActivity.setResizeHeightCapture, mainActivity.setResizeWidthCapture);
        // grayscale
        int grayscale[] = grayscale(resize);
        // binary
        Bitmap binary = binary(resize, grayscale);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        binary.compress(Bitmap.CompressFormat.JPEG, 100, output);
          System.out.print(getClass().getName() + "  preprocessing(byte[] data, MainActivity mainActivity) :: " +"return output.toByteArray();");
        return output.toByteArray();
    }

    public Bitmap crop(Bitmap bitmap, int width, int top, int heightCaptureRange, int widthCaptureRange) {
          System.out.print(getClass().getName() + "  crop(Bitmap bitmap, int width:" + width + ", int top:" + top + ", int heightCaptureRange:" + heightCaptureRange + ", int widthCaptureRange:" + widthCaptureRange + ") --> ");
        return Bitmap.createBitmap(bitmap, width, top, heightCaptureRange, widthCaptureRange);
    }

    public Bitmap resize(Bitmap crop, int resizeHeightCapture, int resizeWidthCapture) {
          System.out.print(getClass().getName() + "  resize(Bitmap crop, int resizeHeightCapture:" + resizeHeightCapture + ", int " + "resizeWidthCapture:" + resizeWidthCapture + ") --> ");
        return Bitmap.createScaledBitmap(crop, resizeHeightCapture, resizeWidthCapture, true);
    }

    public int[] grayscale(Bitmap resize) {
          System.out.print(getClass().getName() + "  grayscale(Bitmap resize) --> ");
        int grayscale[] = new int[resize.getWidth() * resize.getHeight()];
        int index = 0;
        // grayscaling
        for (int x = 0; x < resize.getWidth(); x++) {
            for (int y = 0; y < resize.getHeight(); y++) {
                int color = resize.getPixel(y, x);
//                  System.out.print(getClass().getName() + "  " + color);
                int red = (color & 0x000000FF);
//                  System.out.print(getClass().getName() + "  " + (color & 0x000000FF));
                int green = (color & 0x0000FF00) >> 8;
//                  System.out.print(getClass().getName() + "  " + ((color & 0x0000FF00) >> 8));
                int blue = (color & 0x00FF0000) >> 16;
//                  System.out.print(getClass().getName() + "  " + ((color & 0x00FF0000) >> 16));
                // formula to get grayscale using luminosity
                treshold += grayscale[index++] = Math.round(0.299f * red + 0.587f * green + 0.114f * blue);
//                  System.out.print(getClass().getName() + "  " + Math.round(0.299f * red + 0.587f * green + 0.114f * blue));
//                  System.out.print(getClass().getName() + "  ---------------------------------------------");
            }
        }
        return grayscale;
    }

    /**
     * binary image
     *
     * @param data
     * @return
     */
    public Bitmap binary(Bitmap data, int grayscale[]) {
        // binary
          System.out.print(getClass().getName() + "  binary(Bitmap data, int grayscale[]) --> ");
        int index = 0;
        treshold = treshold / grayscale.length;
        treshold = settingModel.getAutoBinaryTreshold() ? treshold : settingModel.getManualBinaryTreshold();
        list = new JSONArray();
        for (int x = 0; x < data.getWidth(); x++) {
            for (int y = 0; y < data.getHeight(); y++) {
                int binary = 0;
                binary = (grayscale[index++] > treshold) ? 255 : 0;
                // Rinaldi Munir (2004) bahwa 0 (255) adalah putih dan 1 adalah hitam.
                list.add(binary == 255 ? 0 : 1);
                binary = binary + (binary << 8) + (binary << 16);
                // set pixel color
                data.setPixel(y, x, binary);
            }
        }
        collageBinary();
        // return
        return data;
    }

    public void collageBinary() {
        // collageForIdentify binary image
        if (mainView.isIdentify()) {
              System.out.print(getClass().getName() + "  collageForIdentify.put(\"pixel\", list) --> ");
            collageForIdentify.put("pixel", list);
            // System.out.println("BINARY : " + collageForIdentify.get("pixel"));
        } else if (mainView.isAddData()) {
              System.out.print(getClass().getName() + "  collageForAdd.add(list) --> ");
            collageForAdd.add(list);
        }
    }

    /**
     * convert dp to pixel
     *
     * @param dp
     * @return
     */
    public int dpToPixel(int dp) {
          System.out.print(getClass().getName() + "  dpToPixel(int dp:" + dp + ") --> ");
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * convert pixel to dp
     *
     * @param pixel
     * @return
     */
    public int pixelToDp(int pixel) {
          System.out.print(getClass().getName() + "  pixelToDp(int pixel:" + pixel + ") --> ");
        return (int) (pixel / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * set support resolution to capture image
     *
     * @param sizeList
     * @param height
     * @return
     */
    public int getPictureSizeIndexForHeight(List<Camera.Size> sizeList, int height) {
          System.out.print(getClass().getName() + "  getPictureSizeIndexForHeight(List<Camera.Size> sizeList, int " +"height:" + height + ") --> ");
        int chosenHeight = -1;
        for (int i = 0; i < sizeList.size(); i++) {
            if (sizeList.get(i).height < height) {
                chosenHeight = i - 1;
                if (chosenHeight == -1) {
                    chosenHeight = 0;
                }
                break;
            }
        }
        return chosenHeight;
    }

    public void capture() {
          System.out.print(getClass().getName() + "  capture() --> ");
        if (mainView.isIdentify()) {
            mainView.doIdentify();
        } else {
            mainView.doPreview();
        }
    }

    public String getSyncLink() {
          System.out.print(getClass().getName() + "  getSyncLink() --> ");
        return settingModel.getServerAddress() + "/" + settingModel.getAppRootDir() + "/" + bpnnModel.getBpnnDir() +
                bpnnModel.getBpnnFilename();
    }

    public JSONObject getJsonFile(Object id) {
        return pcaModel.getJsonFile(id);
    }

    public void syncWeight() {
          System.out.print(getClass().getName() + "  syncWeight() --> ");
        FileOutputStream fos = null;
        try {
            HttpsURLConnection conn=null;
            // Open SSL
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        public void checkServerTrusted(X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }}, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

            URL url = new URL(getSyncLink());
              System.out.print(getClass().getName() + "  Url : " + url.toString()+"\n");
            conn = (HttpsURLConnection) url.openConnection();
              System.out.print(getClass().getName() + "  open Connection");
            conn.setConnectTimeout(10000);

            DataInputStream dis = new DataInputStream(conn.getInputStream());
            byte[] buffer = new byte[1024];
            int length;
            File mediaStorageDir = new File(settingModel.getAppBasePath() + bpnnModel.getBpnnDir());
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                      System.out.print(getClass().getName() + "  Gagal membuat direktori file");
                }
            }

            File weight = new File(bpnnModel.getBpnnFileUri());
            fos = new FileOutputStream(weight);
            while ((length = dis.read(buffer, 0, buffer.length)) != -1) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
            fos.close();

            mainView.successSyncWeight();
        } catch (IOException ioe) {
            mainView.failedSyncWeight();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public void identify() {
          System.out.print(getClass().getName() + "  identify() --> ");

        PcaPresenter pcaPresenter = new PcaPresenter(mainView);
        BpnnIdentifyPresenter bpnnIdentifyPresenter = new BpnnIdentifyPresenter(mainView);

        Matrix pixel = readJSONToMatrix(getCollageForIdentify(), "pixel");
        System.out.println(getClass().getName() + pixel.getArray().length + " x " + pixel.getArray()[0].length);

        Matrix test = pcaPresenter.test(pixel.getArray()[0]);
        String result = bpnnIdentifyPresenter.identifikasiFKP(test);
        if ((!result.equals("") | !result.equals("ERR")) & fileChecker(pcaModel.getJsonFileUri(result))) {
            mainView.recognized(result);
        } else if (result.equals("ERR")) {
            mainView.errorIdentification();
        } else {
            mainView.unrecognized();
        }

    }

    public void setSettingView(MainActivity mainActivity) {
          System.out.print(getClass().getName() + "  setSettingView(MainActivity mainActivity) --> ");

        mainActivity.server.setText(settingModel.getServerAddress());

        mainActivity.flash.setChecked(settingModel.getFlash());
        mainActivity.setHeightCR.setText(String.valueOf(settingModel.getHeightCaptureRange()));
        mainActivity.setWidthCR.setText(String.valueOf(settingModel.getWidthCaptureRange()));
        mainActivity.setHeightRC.setText(String.valueOf(settingModel.getResizeHeightCapture()));
        mainActivity.setWidthRC.setText(String.valueOf(settingModel.getResizeWidthCapture()));
        mainActivity.setResolution.setText(String.valueOf(settingModel.getResolution()));
        mainActivity.setCaptureEvery.setText(String.valueOf(settingModel.getCaptureEvery()));
        mainActivity.setSamples.setText(String.valueOf(settingModel.getCaptureSample()));
        mainActivity.setSampleTest.setText(String.valueOf(settingModel.getCaptureSampleTest()));
        mainActivity.setDelay.setText(String.valueOf(settingModel.getDelayCapture()));

        mainActivity.binConvert.setChecked(settingModel.getBinaryConvert());
        mainActivity.autoTreshold.setChecked(settingModel.getAutoBinaryTreshold());
        mainActivity.defaultTreshold.setText(String.valueOf(settingModel.getManualBinaryTreshold()));

        if (settingModel.getAutoBinaryTreshold()) {
            System.out.println("treshold : false");
            mainActivity.defaultTreshold.setEnabled(false);
        } else {
            System.out.println("treshold : true");
            mainActivity.defaultTreshold.setEnabled(true);
        }

        mainActivity.maxEpoch.setText(String.valueOf(settingModel.getMaxEpoch()));
        mainActivity.targetError.setText(String.valueOf(settingModel.getMaxError()));
        mainActivity.alpha.setText(String.valueOf(settingModel.getAlpha()));
        mainActivity.neuronHidden.setText(String.valueOf(settingModel.getHiddenNeuron()));
        mainActivity.reduksiPCA.setText(String.valueOf(settingModel.getPcaReduction()));

        if (settingModel.getIdentifyMode() == R.id.liveIdentify) {
            mainActivity.liveIdentify.setChecked(true);
        } else {
            mainActivity.manualIdentify.setChecked(true);
        }
    }

    public void saveSetting(MainActivity mainActivity) {
          System.out.print(getClass().getName() + "  saveSetting(MainActivity mainActivity) --> ");

        settingModel.setServerAddress(mainActivity.server.getText().toString());

        settingModel.setFlash(mainActivity.flash.isChecked());
        settingModel.setHeightCaptureRange(mainActivity.setHeightCR.getText().toString());
        settingModel.setWidthCaptureRange(mainActivity.setWidthCR.getText().toString());
        settingModel.setResizeHeightCapture(mainActivity.setHeightRC.getText().toString());
        settingModel.setResizeWidthCapture(mainActivity.setWidthRC.getText().toString());
        settingModel.setResolution(mainActivity.setResolution.getText().toString());// resolution
        settingModel.setCaptureEvery(mainActivity.setCaptureEvery.getText().toString()); // every 3 seconds
        settingModel.setCaptureSample(mainActivity.setSamples.getText().toString()); // sample
        settingModel.setCaptureSampleTest(mainActivity.setSampleTest.getText().toString()); // sample
        settingModel.setDelayCapture(mainActivity.setDelay.getText().toString()); // delay

        settingModel.setBinaryConvert(mainActivity.binConvert.isChecked());
        settingModel.setAutoBinaryTreshold(mainActivity.autoTreshold.isChecked());
        settingModel.setManualBinaryTreshold(mainActivity.defaultTreshold.getText().toString());

        settingModel.setMaxEpoch(mainActivity.maxEpoch.getText().toString());
        settingModel.setMaxError(mainActivity.targetError.getText().toString());
        settingModel.setAlpha(mainActivity.alpha.getText().toString());
        settingModel.setHiddenNeuron(mainActivity.neuronHidden.getText().toString());
        settingModel.setPcaReduction(mainActivity.reduksiPCA.getText().toString());
        settingModel.setIdentifyMode(mainActivity.identifyMode.getCheckedRadioButtonId());

        settingModel.saveSetting();
    }


    /**
     * @param JSONFile
     * @param id
     * @param name
     */
    public void writeJSONFile(File JSONFile, Object id, Object name) {
          System.out.print(getClass().getName() + "  writeJSONFile(File JSONFile, Object id:" + id.toString() + ", " +"Object name:" + name.toString() + ") --> ");

        JSONObject personalData = new JSONObject();
        personalData.put("id", id);
        personalData.put("name", name);
        personalData.put("binary", getCollageForAdd());
        setCollageForAdd(new JSONArray());
        // write file JSON
        settingModel.writeJSON(personalData, JSONFile.getPath());
    }

    public JSONArray getCollageForAdd() {
          System.out.print(getClass().getName() + "  getCollageForAdd() --> ");

        return this.collageForAdd;
    }

    public void setCollageForIdentify(JSONObject collageForIdentify) {
          System.out.print(getClass().getName() + "  setCollageForIdentify(JSONObject collageForIdentify) --> ");

        this.collageForIdentify = collageForIdentify;
    }

    public void setCollageForAdd(JSONArray collageForAdd) {
          System.out.print(getClass().getName() + "  setCollageForAdd(JSONArray collageForAdd) --> ");

        this.collageForAdd = collageForAdd;
    }

    /**
     * set camera rotation parameter
     *
     * @param activity
     * @param cameraId
     * @param param
     */
    public void setRotationParameter(MainActivity activity, int cameraId, Camera.Parameters param) {
          System.out.print(getClass().getName() + "  setRotationParameter(MainActivity activity, int cameraId, Camera.Parameters param) --> ");

        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        rotation = (rotation + 45) / 90 * 90;
//        Log.i("IFKP", "rotation = " + rotation);
        int toRotate = (info.orientation + rotation) % 360;
//        Log.i("IFKP", "toRotate = " + toRotate);
        activity.imageRotation = toRotate;
        param.setRotation(toRotate);
    }
}
