package com.asrory.ifkp.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by asrory on 23/06/17.
 */

public class BpnnModel extends BaseModel {
    private String bpnnDir = "bpnn/";
    private String bpnnFilename = "weight.bpnn";

    public String getBpnnDir() {
        System.out.println("  getBpnnDir() --> ");
        System.out.println(" return ;");
        return bpnnDir;
    }

    public String getBpnnFilename() {
        System.out.println("  getBpnnFilename() --> ");
        System.out.println(" return ;");
        return bpnnFilename;
    }

    public String getBpnnFileUri() {
        System.out.println("  getBpnnFileUri() --> ");
        System.out.println(" return ;");
        return appBasePath + bpnnDir + bpnnFilename;
    }

    public JSONObject getBpnnFile() {
        System.out.println("  getBpnnFile() --> ");
        JSONObject weight = loadJsonFile(getBpnnFileUri());
        JSONArray data = (JSONArray) weight.get("weight");
        System.out.println(" return ;");
        return (JSONObject) data.get(0);
    }

}
