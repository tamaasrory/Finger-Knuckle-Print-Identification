package com.asrory.ifkp.presenter;

import Jama.Matrix;
import com.asrory.ifkp.model.BpnnModel;
import com.asrory.ifkp.model.PcaModel;
import com.asrory.ifkp.model.SettingModel;
import com.asrory.ifkp.view.MainView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by asrory on 24/06/17.
 */

public class BasePresenter {
    protected MainView mainView;
    protected SettingModel settingModel = new SettingModel().loadSetting(getClass().getSimpleName());
    protected BpnnModel bpnnModel = new BpnnModel();
    protected PcaModel pcaModel = new PcaModel();

    public MainView getMainView() {
        System.out.println("  return  ; ");
        return mainView;
    }

    public SettingModel getSettingModel() {
        System.out.println("  getSettingModel() --> ");
        System.out.println("  return  ; ");
        return settingModel;
    }

    public BpnnModel getBpnnModel() {
        System.out.println("  getBpnnModel() --> ");
        System.out.println("  return  ; ");
        return bpnnModel;
    }

    public PcaModel getPcaModel() {
        System.out.println("  getPcaModel() --> ");
        System.out.println("  return  ; ");
        return pcaModel;
    }

    /**
     * @param jsonObject
     * @param key
     * @return
     */
    public Matrix readJSONToMatrix(JSONObject jsonObject, String key) {
        System.out.println("  readJSONToMatrix(JSONObject jsonObject, String key: "+key+") --> ");
        ArrayList tmpCitraRow = new ArrayList();
        ArrayList tmpCitraCol = new ArrayList();
        double biner[][] = null;
        JSONArray citra = (JSONArray) jsonObject.get(key);
        try {
            for (int row = 0; row < citra.size(); row++) {
                JSONArray pixel = (JSONArray) citra.get(row);
                tmpCitraCol = new ArrayList();
                for (int col = 0; col < pixel.size(); col++) {
                    tmpCitraCol.add(pixel.get(col).toString());
                }
                tmpCitraRow.add(tmpCitraCol);
            }
            biner = new double[tmpCitraRow.size()][tmpCitraCol.size()];
            for (int row = 0; row < tmpCitraRow.size(); row++) {
                ArrayList tmp = (ArrayList) tmpCitraRow.get(row);
                for (int col = 0; col < tmp.size(); col++) {
                    biner[row][col] = Double.parseDouble(tmp.get(col).toString());
                }
            }
        } catch (Exception ex) {
            for (int col = 0; col < citra.size(); col++) {
                tmpCitraCol.add(citra.get(col).toString());
            }
            biner = new double[1][tmpCitraCol.size()];
            for (int col = 0; col < biner[0].length; col++) {
                biner[0][col] = Double.parseDouble(tmpCitraCol.get(col).toString());
            }
        }

        System.out.println("  return  ; ");
        return new Matrix(biner);
    }

    /**
     * @param jsonObject
     * @param key
     * @return
     */
    public String[][] readJSONToString(JSONObject jsonObject, String key) {
        System.out.println("  readJSONToString(JSONObject jsonObject, String key) --> ");
        ArrayList tmpCitraRow = new ArrayList();
        ArrayList tmpCitraCol = new ArrayList();
        String biner[][] = null;
        JSONArray citra = (JSONArray) jsonObject.get(key);
        try {
            for (int row = 0; row < citra.size(); row++) {
                JSONArray pixel = (JSONArray) citra.get(row);
                tmpCitraCol = new ArrayList();
                for (int col = 0; col < pixel.size(); col++) {
                    tmpCitraCol.add(pixel.get(col).toString());
                }
                tmpCitraRow.add(tmpCitraCol);
            }
            biner = new String[tmpCitraRow.size()][tmpCitraCol.size()];
            for (int row = 0; row < tmpCitraRow.size(); row++) {
                ArrayList tmp = (ArrayList) tmpCitraRow.get(row);
                for (int col = 0; col < tmp.size(); col++) {
                    biner[row][col] = tmp.get(col).toString();
                }
            }
        } catch (Exception ex) {
            for (int col = 0; col < citra.size(); col++) {
                tmpCitraCol.add(citra.get(col).toString());
            }
            biner = new String[1][tmpCitraCol.size()];
            for (int col = 0; col < biner[0].length; col++) {
                biner[0][col] = tmpCitraCol.get(col).toString();
            }
        }

        System.out.println("  return  ; ");
        return biner;
    }

    /**
     * @param jsonObject
     * @param data
     * @param key
     * @return
     */
    public JSONObject matrixToJSON(JSONObject jsonObject, Matrix data, String key) {
        System.out.println("  matrixToJSON(JSONObject jsonObject, Matrix data, String key: "+key+") --> ");
        JSONArray arrayRow = new JSONArray(), arrayCol;
        for (int i = 0; i < data.getArray().length; i++) {
            arrayCol = new JSONArray();
            for (int j = 0; j < data.getArray()[0].length; j++) {
                arrayCol.add(data.getArray()[i][j]);
            }
            arrayRow.add(arrayCol);
        }
        jsonObject.put(key, arrayRow);
        System.out.println("  return  ; ");
        return jsonObject;
    }

    /**
     * @param jsonObject
     * @param data
     * @param key
     * @return
     */
    public JSONObject matrixToJSON(JSONObject jsonObject, String data[][], String key) {
        System.out.println("  matrixToJSON(JSONObject jsonObject, String data[][], String key: "+key+") --> ");
        JSONArray arrayRow = new JSONArray(), arrayCol;
        for (int i = 0; i < data.length; i++) {
            arrayCol = new JSONArray();
            for (int j = 0; j < data[0].length; j++) {
                arrayCol.add(data[i][j]);
            }
            arrayRow.add(arrayCol);
        }
        jsonObject.put(key, arrayRow);
        System.out.println("  return  ; ");
        return jsonObject;
    }

    public JSONObject matrixToJSON(JSONObject jsonObject, double data[][], String key) {
        System.out.println("  matrixToJSON(JSONObject jsonObject, double data[][], String key: "+key+") --> ");
        JSONArray arrayRow = new JSONArray(), arrayCol;
        for (int i = 0; i < data.length; i++) {
            arrayCol = new JSONArray();
            for (int j = 0; j < data[0].length; j++) {
                arrayCol.add(data[i][j]);
            }
            arrayRow.add(arrayCol);
        }
        jsonObject.put(key, arrayRow);
        System.out.println("  return  ; ");
        return jsonObject;
    }

    /**
     * @param jsonObject
     * @param data
     * @param key
     * @return
     */
    public JSONObject matrixToJSON(JSONObject jsonObject, double data[], String key) {
        System.out.println("  matrixToJSON(JSONObject jsonObject, double data[], String key: "+key+") --> ");
        JSONArray arrayCol = new JSONArray();
        for (int col = 0; col < data.length; col++) {
            arrayCol.add(data[col]);
        }
        jsonObject.put(key, arrayCol);
        System.out.println("  return  ; ");
        return jsonObject;
    }

}
