package com.asrory.ifkp.model;

import android.os.Environment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by asrory on 25/06/17.
 */

public class BaseModel {
    protected String basePath = Environment.getExternalStorageDirectory().toString();
    protected String appBasePath = basePath + "/ifkp/";
    protected String appRootDir = "ifkp";
    protected String imagesDir = "images/";
    protected String jsonImagesDir = "json/";
    protected String syncLink = "";

    public String getBasePath() {
        System.out.println("   getBasePath() --> ");
        System.out.println(" return ;");
        return basePath;
    }

    public String getAppBasePath() {
        System.out.println("   getAppBasePath() --> ");
        System.out.println(" return ;");
        return appBasePath;
    }

    public String getAppRootDir() {
        System.out.println("   getAppRootDir() --> ");
        System.out.println(" return ;");
        return appRootDir;
    }

    public String getImagesDir() {
        System.out.println("   getImagesDir() --> ");
        System.out.println(" return ;");
        return imagesDir;
    }

    public String getJsonImagesDir() {
        System.out.println("   getJsonImagesDir() --> ");
        System.out.println(" return ;");
        return jsonImagesDir;
    }

    public String getJsonImagesDirUri() {
        System.out.println("   getJsonImagesDirUri() --> ");
        System.out.println(" return ;");
        return appBasePath + jsonImagesDir;
    }

    public String getJsonFileUri(Object filename) {
        System.out.println("   getJsonFileUri(Object filename: " + filename.toString() + ") --> ");
        System.out.println(" return ;");
        return appBasePath + jsonImagesDir + filename + ".json";
    }

    public JSONObject getJsonFile(Object id) {
        System.out.println("   getJsonFile(Object id: " + id.toString() + ") --> ");
        System.out.println(" return ;");
        return loadJsonFile(getJsonFileUri(id));
    }

    public void setBasePath(String basePath) {
        System.out.println("   setBasePath(String basePath) --> ");
        this.basePath = basePath;
    }

    public void setAppBasePath(String appBasePath) {
        System.out.println("   setAppBasePath(String appBasePath) --> ");
        this.appBasePath = appBasePath;
    }

    public void setAppRootDir(String appRootDir) {
        System.out.println("   setAppRootDir(String appRootDir) --> ");
        this.appRootDir = appRootDir;
    }

    public void setImagesDir(String imagesDir) {
        System.out.println("   setImagesDir(String imagesDir) --> ");
        this.imagesDir = imagesDir;
    }

    public void setJsonImagesDir(String jsonImagesDir) {
        System.out.println("   setJsonImagesDir(String jsonImagesDir) --> ");
        this.jsonImagesDir = jsonImagesDir;
    }


    public boolean fileChecker(String pathFile) {
        System.out.println("   fileChecker(String pathFile: " + pathFile + ") ");
        String explodePath[] = pathFile.split("/");
        System.out.println("   ON fileChecker : " + Arrays.toString(explodePath));
        File dir = null;
        if (!explodePath[explodePath.length - 2].equalsIgnoreCase(appRootDir)) {
            dir = new File(appBasePath, explodePath[explodePath.length - 2]);
        } else {
            dir = new File(appBasePath);
        }
        System.out.println("   ON fileChecker : " + dir.getPath());
        String[] listFile = dir.list();
        if (listFile == null) {
            return false;
        }
        for (String fileName : listFile) {
            if (fileName.equalsIgnoreCase(explodePath[explodePath.length - 1])) {
                System.out.println(" return ;");
                return true;
            }
        }
        System.out.println(" return ;");
        return false;
    }

    public boolean fileDirChecker(String pathFile) {
        System.out.println("   fileDirChecker(String pathFile : " + pathFile + ") ");
//        String explodePath[] = pathFile.split("/");
        System.out.println("   ON FILE DIR CHECKER : " + pathFile);
        File dir = new File(pathFile);
        System.out.println("   ON FILE DIR CHECKER : " + dir.getPath());
        if (dir.list() != null) {
            System.out.println(" return  ON FILE DIR CHECKER : " + "true");
            return true;
        }
        System.out.println(" return  ON FILE DIR CHECKER : " + "false");
        return false;
    }

    public void writeJSON(JSONObject jsonObject, String path) {
        System.out.println("   writeJSON(JSONObject jsonObject, String path : " + path + ")() --> ");
        writeFile(jsonObject.toString(), path);
    }

    public void writeJSON(JSONArray jSONArray, String path) {
        System.out.println("   writeJSON(JSONArray jSONArray, String path : " + path + ")() --> ");
        writeFile(jSONArray.toString(), path);
    }


    /**
     * @param pathFile
     * @return
     */
    public JSONObject loadJsonFile(String pathFile) {
        System.out.println("   loadJsonFile(String pathFile : " + pathFile + ") --> ");
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) parser.parse(new FileReader(pathFile));
        } catch (IOException | ParseException ex) {
            System.out.println("return   ERROR : " + ex);
            return null;
        }
        System.out.println(" return jsonObject; ");

        return jsonObject;
    }

    public void writeFile(String string, String path) {
        System.out.println("   writeFile(String string, String path : " + path + ") --> ");
        File rootDir = new File(this.basePath, this.appRootDir);
        if (rootDir.mkdirs()) {
        }

        String explodePath[] = path.split("/");
        if (!(explodePath[explodePath.length - 2].equalsIgnoreCase(this.appRootDir))) {
            File mediaStorageDir = null;
            mediaStorageDir = new File(this.appBasePath, explodePath[explodePath.length - 2]);
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    System.out.println("   IFKP : Gagal membuat direktori file");
                }
            }
        }

        FileWriter file;
        try {
            file = new FileWriter(path);
            file.write(string);
            file.flush();
        } catch (IOException e) {
            System.out.println("   () --> ");
        }
    }
}
