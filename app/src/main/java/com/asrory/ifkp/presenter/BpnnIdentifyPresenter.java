package com.asrory.ifkp.presenter;

import Jama.Matrix;
import com.asrory.ifkp.view.MainView;
import org.json.simple.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

/**
 * Created by asrory on 23/06/17.
 */

public class BpnnIdentifyPresenter extends BasePresenter {
    double v[][],// inisialisasi bobot, nilai nilai bobot dan bias awal input ke hidden layer
            w[][],// inisialisasi bobot, nilai nilai bobot dan bias awal hidden layer ke output layer
            z_net[], // meghitung bobot hidden layer
            z[], // menghitung fungsi aktivasi hidden layer
            y_net[], // menghitung bobot output layer
            y[];  // fungsi aktivasi output layer

    public BpnnIdentifyPresenter(MainView mainView) {
        this.mainView = mainView;
        System.out.print(getClass().getName() + "  BpnnIdentifyPresenter(MainView mainView) --> ");
    }

    public boolean isReady() {
        System.out.print(getClass().getName() + "  isReady() --> ");
        if (settingModel.fileChecker(pcaModel.getPcaFileUri())) {
            System.out.println("  return true ; ");
            return true;
        } else {
            System.out.println("  return false ; ");
            return false;
        }
    }

    public double[] forwardPropagation(double input[]) {
        System.out.print(getClass().getName() + "  forwardPropagation(double input[]:" + Arrays.toString(input) + ") --> ");
        z_net = new double[v.length];// total sinyal masukan pada hidden unit j
        z = new double[v.length];// keluaran pada hidden unit j
        for (int j = 0; j < v.length; j++) {
            // doPreprocessing unit masukan menerima sinyal dan meneruskannya ke unit tersembunyi
            z_net[j] = v[j][v[0].length - 1];
            for (int i = 0; i < input.length; i++) {
                z_net[j] += (input[i] * v[j][i]);
            }
            // hitung keluaran semua hidden unit j pada hidden layer
            z[j] = 1 / (1 + Math.exp(-z_net[j]));// activation function
        }

        y_net = new double[w.length];// total sinyal masukan pada output unit k
        y = new double[y_net.length];// keluaran output unit j
        for (int k = 0; k < w.length; k++) {
            // jumlahkan semua sinyal yang masuk ke output unit k
            y_net[k] = w[k][w[0].length - 1];
            for (int j = 0; j < w[0].length - 1; j++) {
                y_net[k] += (z[j] * w[k][j]);
            }
            // hitung keluaran pada semua unit pada output layer
            y[k] = 1 / (1 + Math.exp(-y_net[k]));// activation function
        }

        System.out.println("  return y ; ");
        return y;
    }

    private void print(double data[][], int colShowLength) {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                if (j < (colShowLength - 2)) {
                    System.out.print(formatDecimal(3, 4, data[i][j]) + " ");
                } else if (j == (colShowLength - 2)) {
                    System.out.println("..... ");
                } else if (j == (data[0].length - 1)) {
                    System.out.print(formatDecimal(3, 4, data[i][j]));
                }
            }
            System.out.println();
        }
    }

    private static double formatDecimal(int front, int back, double value) {
        String style = "";
        for (int i = 0; i < front; i++) {
            style += "#";
        }
        style += ".";
        for (int i = 0; i < back; i++) {
            style += "#";
        }
        NumberFormat formatter = new DecimalFormat(style);
        String formated = formatter.format(value);
        String replace = formated.replace(',', '.');
        System.out.println("  return formatDecimal;");

        return Double.parseDouble(replace);
    }

    public String identifikasiFKP(Matrix inputFromPca) {
        System.out.print(getClass().getName() + "  identifikasiFKP(Matrix inputFromPca) --> ");
        String ID = "";
        if (inputFromPca != null) {
            JSONObject weight = bpnnModel.getBpnnFile();
            // // // System.out.println("-------------------normalisasi bpnn----------------------");
            double[] input = normalisasi(inputFromPca.getArray())[0];
            print(new double[][]{input}, 7);

//              System.out.print(getClass().getName() + "  NORMALISASI INPUT ON BPNN TEST OK\n"
//                    + Arrays.toString(input));

            v = readJSONToMatrix(weight, "V").getArray();
            // // // System.out.println("--------------------------- V --------------------------------");
//              System.out.print(getClass().getName() + "  LOADING... BOBOT V ON BPNN TEST OK : "
//                    + v.length + " x " + v[0].length);
//            print(v, 7);
            // // // System.out.println("--------------------------- W --------------------------------");
            w = readJSONToMatrix(weight, "W").getArray();
//              System.out.print(getClass().getName() + "  LOADING... BOBOT W ON BPNN TEST OK : "
//                    + w.length + " x " + w[0].length);
//            print(w, 7);
//              System.out.print(getClass().getName() + "  FORWARD PROPAGATION ON BPNN TEST OK");
            /*
             * Forward Propagation
             */
            double output[] = forwardPropagation(input);
            // output
            // // // System.out.println("--------------------------- OUTPUT --------------------------------");

//              System.out.print(getClass().getName() + "  output Bin ID :" + Arrays.toString(output));

            /*
              Linear Activation
             */
            System.out.print(getClass().getName() + "  [ Linear Activation ]  ");
            double tmpOutput = output[0];
            for (int j = 0; j < output.length; j++) {
                // max as target
                if (tmpOutput <= output[j]) {
                    tmpOutput = output[j];
                    ID = "" + (j + 1) + "";
                }
            }

              System.out.print(getClass().getName() + "\n  OUTPUT ID : " + ID);
              System.out.print(getClass().getName() + "  --  FINISH IDENTIFIKASI --\n");
            System.out.println(" return ID: " + ID + " -->");
            return ID;
        } else {
            System.out.println(" return ERR -->");

            return "ERR";
        }
    }


    private double[] quickSort(double data[], int lengthData, int high) {
        //  lo adalah index bawah, hi adalah index atas
        //  dari bagian array yang akan di urutkan
        int i = lengthData, j = high;
        double h;
        double pivot = data[lengthData];
        //  pembagian
        do {
            while (data[i] < pivot) {
                i++;
            }
            while (data[j] > pivot) {
                j--;
            }
            if (i <= j) {
                h = data[i];
                data[i] = data[j];
                data[j] = h;//tukar
                i++;
                j--;
            }
        } while (i <= j);
        //  pengurutan
        if (lengthData < j) {
            quickSort(data, lengthData, j);
        }
        if (i < high) {
            quickSort(data, i, high);
        }
        System.out.println("  return  ; ");
        return data;
    }

    /**
     * Jika a adalah data minimum dan b adalah data maksimum,
     * transformasi linier yang dipakai untuk mentransformasikan data ke
     * interval [0.1 , 0.9]
     * <p>
     * normalisasi = ((0.8 * ( x − a )) / (b − a)) + 0.1
     *
     * @param data double data[][]
     * @return
     */
    public double[][] normalisasi(double data[][]) {
        System.out.print(getClass().getName() + "  normalisasi(double data[][]) --> ");
        for (int i = 0; i < data.length; i++) {
            double tmp[] = Arrays.copyOf(data[i], data[i].length);
            tmp = quickSort(tmp, 0, data[i].length - 1);
            System.out.print(getClass().getName() + "  quickSort() --> ");
            double min = tmp[0], max = tmp[tmp.length - 1];
            for (int j = 0; j < data[0].length; j++) {
                data[i][j] = ((0.8 * (data[i][j] - (min))) / (max - (min))) + 0.1;
            }
        }
        System.out.println(" return normalisasi -->");

        return data;
    }

    public void train() {
        System.out.print(getClass().getName() + "  train() --> ");
        String sourceFileUri = pcaModel.getPcaFileUri();
        System.out.print(getClass().getName() + "  PCA File URI : " + sourceFileUri);
        int serverResponseCode = 0;
        String fileName = sourceFileUri;
        HttpsURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        try {
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

            String $_GET = "maxEpoch=" + settingModel.getMaxEpoch() +
                    "&targetError=" + settingModel.getMaxError() +
                    "&alpha=" + settingModel.getAlpha() +
                    "&neuronHidden=" + settingModel.getHiddenNeuron();
            // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(settingModel.getServerAddress() + "/ifkp/upload.php?" + $_GET);
            // Open a HTTP  connection to  the URL
            System.out.print(getClass().getName() + "  [ Url : " + url.toString() + " ]  ");

            conn = (HttpsURLConnection) url.openConnection();

            System.out.print(getClass().getName() + "  [ open SSL Connection ]  ");

            conn.setConnectTimeout(10000);
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);
            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                    + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            if (serverResponseCode == 200) {
                System.out.println(getClass().getName()
                        + " :: uploading file PCA and train BPNN selesai dengan kode respon server : "
                        + "" + serverResponseCode);
                mainView.successBpnnTrain();

            }
            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (IOException e) {
            System.out.print(getClass().getName() + "  uploading file PCA and train BPNN gagal "
                    + Arrays.toString(e.getStackTrace()));
            mainView.failedBpnnTrain();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

}
