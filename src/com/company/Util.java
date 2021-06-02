package com.company;

import org.apache.commons.math3.linear.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Util {

    /**
     * Es wird versucht, eine Zahl auf der Kommandozeile einzulesen, sollte dabei eine Exception geworfen werden,
     * wird eine Fehlermeldung ausgegeben
     *
     * @param scanner darüber wird auf die Kommandozeile zugegriffen
     * @return den eingegebenen Wert auf der Kommandozeile
     */
    public static int readInput(Scanner scanner) {

        try {
            //liest eine Ziffer auf der Kommandozeile ein
            return scanner.nextInt();
        } catch (InputMismatchException e) {

            System.out.println("Ihre Eingabe hatte das falsche Format, es darf nur eine Ziffer angegeben werden");
        }

        //im Falle einer Exception wird -1 zurückgegeben
        return -1;
    }

    public static Map<MatrixType, RealMatrix> readMatrix(String path) {

        List<String> lines = new ArrayList<>();
        Map<MatrixType, RealMatrix> matrixMap = new HashMap<>();

        try {
            //jede Zeile der eingelesenen Datei wird in eine Liste geschrieben
            lines = Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        lines.removeIf(line -> line.startsWith("//"));

        double[] xZero = Arrays.stream(lines.get(0).substring(1).split(","))
                .mapToDouble(Double::parseDouble)
                .toArray();

        BlockRealMatrix blockRealMatrixXZero = new BlockRealMatrix(xZero.length, 1);
        blockRealMatrixXZero.setColumn(0, xZero);
        matrixMap.put(MatrixType.X_ZERO, blockRealMatrixXZero);

        double[] B = Arrays.stream(lines.get(1).substring(1).split(","))
                .mapToDouble(Double::parseDouble)
                .toArray();

        BlockRealMatrix blockRealMatrixB = new BlockRealMatrix(B.length, 1);
        blockRealMatrixB.setColumn(0, B);
        matrixMap.put(MatrixType.B, blockRealMatrixB);

        Array2DRowRealMatrix array2DRowRealMatrixAAndB = new Array2DRowRealMatrix(lines.size() - 2, xZero.length);
        for (int i = 2; i < lines.size(); i++) {
            double[] row = Arrays.stream(lines.get(i).split(","))
                    .mapToDouble(Double::parseDouble)
                    .toArray();
            array2DRowRealMatrixAAndB.setRow(i - 2, row);
        }

        matrixMap.put(MatrixType.A, array2DRowRealMatrixAAndB);

        return matrixMap;
    }

    public static Array2DRowRealMatrix getLowerMatrix(Array2DRowRealMatrix array2DRowRealMatrixA) {

        double[][] data = array2DRowRealMatrixA.getData();

        int helper = 0;
        // i -> Zeile
        // j -> Spalte
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                if (j + helper >= data.length) {
                    break;
                }
                data[i][j + helper] = 0;
            }
            helper++;
        }

        helper = 1;

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                if (j + helper >= data.length) {
                    break;
                }
                data[j + helper][i] = data[j + helper][i] * -1;
            }
            helper++;
        }


        return new Array2DRowRealMatrix(data);
    }

    public static Array2DRowRealMatrix getUpperMatrix(Array2DRowRealMatrix array2DRowRealMatrixA) {

        double[][] data = array2DRowRealMatrixA.getData();

        int helper = 0;
        // i -> Zeile
        // j -> Spalte
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                if (j + helper >= data.length) {
                    break;
                }
                data[j + helper][i] = 0;
            }
            helper++;
        }

        helper = 1;

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                if (j + helper >= data.length) {
                    break;
                }
                data[i][j + helper] = data[i][j + helper] * -1;
            }
            helper++;
        }

        return new Array2DRowRealMatrix(data);
    }

    public static Array2DRowRealMatrix getDiagonalMatrix(Array2DRowRealMatrix array2DRowRealMatrixA) {

        double[][] data = array2DRowRealMatrixA.getData();

        int helper = 1;
        // i -> Zeile
        // j -> Spalte
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                if (j + helper >= data.length) {
                    break;
                }
                data[j + helper][i] = 0;
            }
            helper++;
        }

        helper = 1;

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                if (j + helper >= data.length) {
                    break;
                }
                data[i][j + helper] = 0;
            }
            helper++;
        }

        return new Array2DRowRealMatrix(data);
    }

    public static double getMaxNorm(BlockRealMatrix blockRealMatrix) {

        return getMaxAbsoluteValue(blockRealMatrix.getColumn(0));
    }

    public static String formatMatrix(BlockRealMatrix blockRealMatrix) {

        StringBuilder stringBuilder = new StringBuilder();

        int counter = 1;
        for (int i = 0; i < blockRealMatrix.getData().length; i++) {
            stringBuilder.append("x" + counter++ + "=" + blockRealMatrix.getData()[i][0]);
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    public static Array2DRowRealMatrix createEinheitsmatrix(int dimension) {

        Array2DRowRealMatrix array2DRowRealMatrixEinheitsmatrix = new Array2DRowRealMatrix(dimension, dimension);

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                array2DRowRealMatrixEinheitsmatrix.setEntry(i, j, i == j ? 1 : 0);
            }
        }

        return array2DRowRealMatrixEinheitsmatrix;
    }

    public static double calcOverRelaxationParameter(BlockRealMatrix blockRealMatrixVerfahrensmatrix,
                                                     Array2DRowRealMatrix array2DRowRealMatrixA) {

        double spectralRadius = calcSpectralRadius(blockRealMatrixVerfahrensmatrix, array2DRowRealMatrixA);
        return 2/(1+Math.sqrt(1-Math.pow(spectralRadius, 2)));
    }

    public static Array2DRowRealMatrix divideMatrixByNumber(Array2DRowRealMatrix array2DRowRealMatrix, double number){

        Array2DRowRealMatrix array2DRowRealMatrixDivided = new Array2DRowRealMatrix(array2DRowRealMatrix.getData());

        for (int i = 0; i < array2DRowRealMatrixDivided.getRowDimension(); i++) {
            for (int j = 0; j < array2DRowRealMatrixDivided.getColumnDimension(); j++) {
                array2DRowRealMatrixDivided.setEntry(i, j, array2DRowRealMatrixDivided.getEntry(i, j)/number);
            }
        }

        return array2DRowRealMatrixDivided;
    }

    public static double calcLoesungsComponent(double previousXi, double overRelaxationParameter, double squareMatrixAjj,
                                               double helperValueSi, double loesungsVectorBi){

        return previousXi - (overRelaxationParameter/squareMatrixAjj)*(helperValueSi - loesungsVectorBi);
    }

    private static double calcSpectralRadius(BlockRealMatrix blockRealMatrixVerfahrensmatrix, Array2DRowRealMatrix array2DRowRealMatrixA) {

        RealMatrix realMatrixCMultiplyA = blockRealMatrixVerfahrensmatrix.multiply(array2DRowRealMatrixA);
        RealMatrix realMatrixIteration = createEinheitsmatrix(array2DRowRealMatrixA.getRowDimension()).subtract(realMatrixCMultiplyA);

        EigenDecomposition eigenDecomposition = new EigenDecomposition(realMatrixIteration);
        return getMaxAbsoluteValue(eigenDecomposition.getRealEigenvalues());
    }

    private static double getMaxAbsoluteValue(double[] values) {

        List<Double> valueList = Arrays.stream(values).boxed().collect(Collectors.toList());
        double maxValue = Collections.max(valueList);

        List<Double> negateValueList = new ArrayList<>();
        valueList.forEach(value -> negateValueList.add(value * -1));
        double maxValueNegative = Collections.max(negateValueList);

        return Collections.max(Arrays.asList(maxValue, maxValueNegative));
    }
}
