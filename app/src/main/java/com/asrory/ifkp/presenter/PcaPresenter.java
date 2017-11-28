package com.asrory.ifkp.presenter;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import com.asrory.ifkp.model.PcaModel;
import com.asrory.ifkp.view.MainView;
import org.json.simple.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

/**
 * Created by asrory on 23/06/17.
 */

public class PcaPresenter extends BasePresenter {
    public PcaPresenter(MainView mainView) {
//        super(mainView);
        this.mainView = mainView;
        this.pcaModel = new PcaModel();
          System.out.print(getClass().getName() + "  PcaPresenter(MainView mainView) --> ");

    }

    /**
     * Mean of images <br> <code>formula : mean atau (Ψ) = ∑ Γ / M</code>
     *
     * @param biner
     * @return
     */
    public double[] meanOfImages(Matrix biner) {
          System.out.print(getClass().getName() + "  meanOfImages(Matrix biner) --> ");
        double matrix[] = new double[biner.getArray()[0].length];
        for (int i = 0; i < biner.getArray()[0].length; i++) {
            matrix[i] = 0;
            for (int j = 0; j < biner.getArray().length; j++) {
                matrix[i] += biner.get(j, i);
            }
            matrix[i] = matrix[i] / pcaModel.getTotalImage();
        }
        System.out.println(" return meanOfImages -->");
        return matrix;
    }

    /**
     * Normalize matrix <br> <code>formula : nomalisasi atau (Φ) = Γ − Ψ</code>
     *
     * @param original
     * @param mean
     * @return
     */
    public Matrix normalisasi(Matrix original, double[] mean) {
          System.out.print(getClass().getName() + "  normalisasi(Matrix original, double[] mean) --> ");
        Matrix normalisasi = new Matrix(original.getArray().length, original.getArray()[0].length);
        for (int i = 0; i < original.getArray().length; i++) {
            for (int j = 0; j < original.getArray()[0].length; j++) {
                normalisasi.set(i, j, original.get(i, j) - mean[j]);// devide
            }
        }
        System.out.println(" return normalisasi -->");

        return normalisasi;
    }

    /**
     * Covarianc matrix <br>
     * <code>formula : covarianc = Φ x Φ<sup>T</sup></code>
     *
     * @param normalisasi
     * @return
     */
    public Matrix covarian(Matrix normalisasi) {
          System.out.print(getClass().getName() + "  covarian(Matrix normalisasi) --> ");
        System.out.println(" return covarian -->");
        return normalisasi.times(normalisasi.transpose());
    }

    /**
     * EigenvalueDecomposition
     *
     * @param covarian
     * @return
     */
    public EigenvalueDecomposition eigen(Matrix covarian) {
          System.out.print(getClass().getName() + "  ");
        System.out.println(" return eigen -->");

        return covarian.eig();
    }

    /**
     * EigenValue
     *
     * @param eigen
     * @return
     */
    public Matrix eigenValue(EigenvalueDecomposition eigen) {
          System.out.print(getClass().getName() + "  eigenValue(EigenvalueDecomposition eigen) --> ");
        System.out.println(" return eigenValue -->");

        return eigen.getD();
    }

    /**
     * EigenVector
     *
     * @param eigen
     * @return
     */
    public Matrix eigenVector(EigenvalueDecomposition eigen) {
          System.out.print(getClass().getName() + "  eigenVector(EigenvalueDecomposition eigen) --> ");
        System.out.println(" return eigenVector -->");

        return eigen.getV();
    }

    /**
     * EigenFace <br> <code>formula : EigenFace = EigenVector x Φ</code>
     *
     * @param normalisasi
     * @param eigenVector
     * @return
     */
    public Matrix eigenFace(Matrix normalisasi, Matrix eigenVector) {
          System.out.print(getClass().getName() + "  eigenFace(Matrix normalisasi, Matrix eigenVector) --> ");
        System.out.println(" return eigenFaces -->");

        return eigenVector.times(normalisasi);
    }

    /**
     * Future of images <br>
     * <code>formula : Future = Φ x EigenFace<sup>T</sup></code>
     *
     * @param eigenFace
     * @param normalisasi
     * @return
     */
    public Matrix feature(Matrix eigenFace, Matrix normalisasi) {
          System.out.print(getClass().getName() + "  feature(Matrix eigenFace, Matrix normalisasi) --> ");
        System.out.println(" return feature -->");

        return normalisasi.times(eigenFace.transpose());
    }

    /**
     * FutureExtraction Process
     *
     * @return
     */
    public boolean isReady() {
          System.out.print(getClass().getName() + "  isReady() --> ");
        if (settingModel.fileDirChecker(settingModel.getJsonImagesDirUri())) {
            System.out.println(" return true -->");

            return true;
        } else {
            System.out.println(" return false -->");

            return false;
        }
    }

    private void print(double data[][], int colShowLength) {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                if (j < (colShowLength - 2)) {
                    System.out.print(formatDecimal(6, 6, data[i][j]) + " ");
                } else if (j == (colShowLength - 2)) {
                    System.out.println("..... ");
                } else if (j == (data[0].length - 1)) {
                    System.out.print(formatDecimal(6, 6, data[i][j]));
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
        return Double.parseDouble(replace);
    }

    int showOnly = 90;

    public void extract() {
          System.out.print(getClass().getName() + "  extract() --> ");
        JSONObject output = new JSONObject();
        File file = new File(settingModel.getJsonImagesDirUri());

        Matrix data = pcaModel.loadAllImages(file);
        // // System.out.println("---------------------------LOAD DATA---------------------------");
// print(data.getArray(), showOnly);

        // // System.out.println("---------------------------MEAN---------------------------");
        double mean[] = meanOfImages(data);
// print(new double[][]{mean}, showOnly);

        output = matrixToJSON(output, mean, "mean");
        // // System.out.println("--------------------------NORMALISASI----------------------------");
        Matrix normalisasi = normalisasi(data, mean);
// print(normalisasi.getArray(), showOnly);

        // // System.out.println("--------------------------COVARIAN----------------------------");
        Matrix covarian = covarian(normalisasi);
// print(covarian.getArray(), showOnly);

        EigenvalueDecomposition eigen = eigen(covarian);

        Matrix eigenValue = eigenValue(eigen),
                eigenVector = eigenVector(eigen),
                eigenFace = eigenFace(normalisasi, eigenVector);
        // // System.out.println("------------------------EIGEN VALUE------------------------------");
// print(eigenValue.getArray(), showOnly);
        // // System.out.println("--------------------------EIGEN VECTOR----------------------------");
// print(eigenVector.getArray(), showOnly);
        // // System.out.println("--------------------------EIGEN FACES----------------------------");
// print(eigenFace.getArray(), showOnly);

        output = matrixToJSON(output, eigenFace, "eigenFaces");
        // // System.out.println("-------------------------FUTURE / PROJECT IMAGE-----------------------------");
        Matrix feature = feature(eigenFace, normalisasi);
// print(feature.getArray(), showOnly);
        // reduksi
        int reduksiPCA = settingModel.getPcaReduction();
        // // System.out.println("-------------------------reduksi FUTURE / PROJECT IMAGE-----------------------------");
        feature = futureReduction(feature, reduksiPCA, 'R');
// print(feature.getArray(), showOnly);
        // end
        output = matrixToJSON(output, feature, "feature");
        output = matrixToJSON(output, pcaModel.getUserID(), "target");
// print(pcaModel.getUserID(), showOnly);
        settingModel.writeJSON(output, pcaModel.getPcaFileUri());
        /*
         * on success
         */
        mainView.successPcaExtraction();
    }

    /**
     * @param future
     * @param reductionTo
     * @param from
     * @return
     */
    public Matrix futureReduction(Matrix future, int reductionTo, char from) {
          System.out.print(getClass().getName() + "  futureReduction(Matrix feature, int reductionTo:" + reductionTo + ", char " +"from:" + from + ") --> ");
        Matrix matrix = new Matrix(future.getArray().length, reductionTo);
        for (int i = 0; i < matrix.getArray().length; i++) {
            int col = (from == 'R') ? (future.getArray()[0].length) - reductionTo : 0;
            for (int j = 0; j < matrix.getArray()[0].length; j++) {
                matrix.set(i, j, future.get(i, col++));
            }
        }
        System.out.println(" return futureReduction -->");

        return matrix;
    }

    /**
     * Pengujian
     *
     * @param input
     * @return
     */
    public Matrix test(double input[]) {
          System.out.print(getClass().getName() + "  test(double input[]) --> ");
        JSONObject data = pcaModel.getPcaFile();
        if (data != null) {
            double mean[] = readJSONToMatrix(data, "mean").getArray()[0];// 1 x 1600
            if (mean.length == input.length) {
                // System.out.println("MEAN = " + mean.length);
                // System.out.println("INPUT = " + input.length);
                // // System.out.println("--------------------input---------------------------");
        // print(new double[][]{input}, showOnly);
                // // System.out.println("--------------------mean---------------------------");
        // print(new double[][]{mean}, showOnly);
                Matrix normalisasi = normalisasi(new Matrix(new double[][]{input}), mean);// 1 x 1600
                // // System.out.println("--------------------normalisasi---------------------------");
        // print(normalisasi.getArray(), showOnly);
                // System.out.println("NORMAL = " + normalisasi.getRowDimension() + " x " + normalisasi.getColumnDimension());
                Matrix eigenFaces = readJSONToMatrix(data, "eigenFaces");// eigVec x normal | 18 x 1600 |
                // // System.out.println("--------------------eigenFaces---------------------------");
        // print(eigenFaces.getArray(), showOnly);
                Matrix feature = feature(eigenFaces, normalisasi);// normal x eigF' | [1 x 1600] x [1600 x 18] = [1 x 18]
                // // System.out.println("--------------------feature---------------------------");
        // print(feature.getArray(), showOnly);
                int reduksiPCA = settingModel.getPcaReduction();
                feature = futureReduction(feature, reduksiPCA, 'R');
                // // System.out.println("--------------------feature reduksi---------------------------");
                System.out.println(" return feature -->");
        // print(feature.getArray(), showOnly);
                return feature;
            } else {
                System.out.println(" return null -->");
                return null;
            }
        }
        System.out.println(" return null -->");
        return null;
    }

}
