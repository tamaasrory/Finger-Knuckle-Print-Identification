package com.asrory.ifkp.model;

import Jama.Matrix;
import com.asrory.ifkp.R;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by asrory on 23/06/17.
 */

public class PcaModel extends BaseModel {
    private File files[];
    private double[][] userID;
    private int totalImage = 0;// total images in directory
    private String pcaDir = "pca/";
    private String pcafilename = "feature.pca";

    public double[][] getUserID() {
        System.out.println("  getUserID() --> ");
        System.out.println(" return UserID; ");

        return userID;
    }

    public int getTotalImage() {
        System.out.println("  getTotalImage() --> ");
        System.out.println(" return TotalImage; ");

        return totalImage;
    }

    public String getPcaDir() {
        return pcaDir;
    }

    public String getPcafilename() {
        return pcafilename;
    }

    public String getPcaFileUri() {
        System.out.println("  getPcaFileUri() --> ");
        System.out.println(" return PcaFileUri; ");

        return appBasePath + pcaDir + pcafilename;
    }

    public JSONObject getPcaFile() {
        System.out.println("  getPcaFile() --> ");
        System.out.println(" return PcaFile; ");

        return loadJsonFile(getPcaFileUri());
    }

    public void setPcaDir(String pcaDir) {
        this.pcaDir = pcaDir;
    }

    public void setPcafilename(String pcafilename) {
        this.pcafilename = pcafilename;
    }

    /**
     * LoadData <br> loading data from avalibe Storage
     *
     * @return
     */
    public Matrix loadAllImages(File mediaStorageDir) {
        System.out.println("  loadAllImages(File mediaStorage) --> ");
        files = mediaStorageDir.listFiles();// save file list
        ArrayList tmpCitraRow = new ArrayList(),
                tmpCitraCol = new ArrayList(),
                tmpTerget = new ArrayList();
        Matrix biner = null;
        if (files != null) {
            String path[] = files[0].getPath().split("/");
            String spath = "";
            for (int i = 0; i < path.length - 1; i++) {
                spath +=  path[i]+"/";
            }
            int tmpFileName[] = new int[files.length];
            for (int i = 0; i < tmpFileName.length; i++) {
                String tmpPath[] = files[i].getPath().split("/");
                tmpFileName[i] = Integer.parseInt(tmpPath[tmpPath.length - 1].split(".json")[0]);
            }
//            Arrays.sort(tmpFileName);
            System.out.println(Arrays.toString(tmpFileName));
            for (int i = 0; i < files.length; i++) {
                if (files[i].getPath().endsWith(".json")) {// filter JSON only
                    System.out.println(spath + "" + tmpFileName[i] + ".json");
                    JSONObject jsonObject = loadJsonFile(spath + "" + tmpFileName[i] + ".json");
                    JSONArray citra = (JSONArray) jsonObject.get("binary");
                    for (int j = 0; j < citra.size(); j++) {
                        totalImage++;
                        JSONArray pixel = (JSONArray) citra.get(j);
                        tmpCitraCol = new ArrayList();
                        tmpTerget.add(jsonObject.get("id").toString());
                        for (int k = 0; k < pixel.size(); k++) {
                            tmpCitraCol.add(pixel.get(k).toString());
                        }
                        tmpCitraRow.add(tmpCitraCol);
                    }
                }
            }

            int maxUserID = Integer.parseInt(tmpTerget.get(0).toString());
            for (int i = 0; i < tmpTerget.size(); i++) {
                int tmpUserID = Integer.parseInt(tmpTerget.get(i).toString());
                if (maxUserID < tmpUserID) {
                    maxUserID = tmpUserID;
                }
            }

            biner = new Matrix(tmpCitraRow.size(), tmpCitraCol.size());
            userID = new double[tmpCitraRow.size()][maxUserID];
            //mencari id terbesar
            for (int i = 0; i < tmpCitraRow.size(); i++) {
                ArrayList tmp = (ArrayList) tmpCitraRow.get(i);
                String tmpID[] = encodeUserId(Integer.parseInt(tmpTerget.get(i).toString()), maxUserID);
                for (int j = 0; j < tmp.size(); j++) {
                    biner.set(i, j, Double.parseDouble(tmp.get(j).toString()));
                }

                /**
                 * Linear
                 */
                for (int k = 0; k < tmpID.length; k++) {
                    userID[i][k] = Double.parseDouble(tmpID[k]);
                    System.out.println("  userID[" + i + "][" + k + "] = " + tmpID[k]);
                }
            }
        }
        System.out.println(" return loadAllImages; ");

        return biner;
    }

    public String[] encodeUserId(int currentId, int maxId) {
        System.out.println("  encodeUserId(int currentId, int maxId) --> ");
        String tmpBinId[] = new String[maxId];
        for (int i = 0; i < maxId; i++) {
            tmpBinId[i] = (i == (currentId - 1)) ? "1" : "0";
        }
        System.out.println("  MAXS = " + maxId + " ... CURRENT FILE ID : " + currentId + " " +".... gen:" +Arrays.toString(tmpBinId));
        System.out.println(" return encodeUserId; ");

        return tmpBinId;
    }

}
