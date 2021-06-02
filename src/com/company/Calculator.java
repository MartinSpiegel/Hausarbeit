package com.company;

import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;
import sun.jvm.hotspot.opto.Block;

import java.util.Map;


public class Calculator {

    public static Array2DRowRealMatrix calc(Procedure procedure) {

        Map<MatrixType, RealMatrix> matrixMap;
        try {
            matrixMap = Util.readMatrix("data/example.txt");
        }catch (Exception e){
            System.out.println("Format der Datei ist fehlerhaft!");
            return null;
        }

        Array2DRowRealMatrix array2DRowRealMatrixA = (Array2DRowRealMatrix) matrixMap.get(MatrixType.A);
        BlockRealMatrix blockRealMatrixXZero = (BlockRealMatrix) matrixMap.get(MatrixType.X_ZERO);
        BlockRealMatrix blockRealMatrixB = (BlockRealMatrix) matrixMap.get(MatrixType.B);

        if (!array2DRowRealMatrixA.isSquare()){
            System.out.println("Matrix A ist nicht qudratisch");
            return null;
        }
        if (blockRealMatrixXZero.getColumnDimension() != 1){
            System.out.println("Startvektor ist fehlerhaft");
            return null;
        }
        if (blockRealMatrixB.getColumnDimension() != 1){
            System.out.println("Lösungsvektor b ist fehlerhaft");
            return null;
        }

        switch (procedure) {
            case JACOBI:
                calcJacobi(array2DRowRealMatrixA, blockRealMatrixXZero, blockRealMatrixB);
                break;
            case GAUSS_SEIDEL:
                calcGaussSeidel(array2DRowRealMatrixA, blockRealMatrixXZero, blockRealMatrixB);
                break;
            case SOR:
                //calcSor(array2DRowRealMatrixA, blockRealMatrixXZero, blockRealMatrixB);
                calcSorWithComponentView(array2DRowRealMatrixA, blockRealMatrixXZero, blockRealMatrixB);
                break;
            default:
                return null;
        }

        return null;
    }

    private static void calcJacobi(Array2DRowRealMatrix array2DRowRealMatrixA, BlockRealMatrix blockRealMatrixXZero,
                                   BlockRealMatrix blockRealMatrixB) {

        Array2DRowRealMatrix array2DRowRealMatrixD = Util.getDiagonalMatrix(array2DRowRealMatrixA);

        BlockRealMatrix blockRealMatrixXkMinusOne;
        BlockRealMatrix blockRealMatrixXk = null;
        int counter = 1;
        Array2DRowRealMatrix array2DRowRealMatrixLowerMatrix = Util.getLowerMatrix(array2DRowRealMatrixA);
        Array2DRowRealMatrix array2DRowRealMatrixUpperMatrix = Util.getUpperMatrix(array2DRowRealMatrixA);
        Array2DRowRealMatrix array2DRowRealMatrixLowerUpperMatrix = array2DRowRealMatrixLowerMatrix.add(array2DRowRealMatrixUpperMatrix);
        BlockRealMatrix blockRealMatrixDInverse = (BlockRealMatrix) MatrixUtils.inverse(array2DRowRealMatrixD);
        BlockRealMatrix blockRealMatrixDInverseMultiplyB = blockRealMatrixDInverse.multiply(blockRealMatrixB);

        do {
            blockRealMatrixXkMinusOne = counter == 1 ? blockRealMatrixXZero : blockRealMatrixXk;
            blockRealMatrixXk = blockRealMatrixDInverse
                    .multiply(array2DRowRealMatrixLowerUpperMatrix)
                    .multiply(blockRealMatrixXkMinusOne);

            blockRealMatrixXk = blockRealMatrixXk.add(blockRealMatrixDInverseMultiplyB);

            counter++;

        } while (Util.getMaxNorm(blockRealMatrixXkMinusOne.subtract(blockRealMatrixXk)) >= 0.001);

        System.out.println("Ergebnis");
        System.out.println("-----------------------");
        System.out.println("Schleifendurchläufe: " + --counter);

        System.out.println(Util.formatMatrix(blockRealMatrixXk));
    }

    private static void calcGaussSeidel(Array2DRowRealMatrix array2DRowRealMatrixA, BlockRealMatrix blockRealMatrixXZero,
                                        BlockRealMatrix blockRealMatrixB) {

        Array2DRowRealMatrix array2DRowRealMatrixD = Util.getDiagonalMatrix(array2DRowRealMatrixA);

        BlockRealMatrix blockRealMatrixXkMinusOne;
        BlockRealMatrix blockRealMatrixXk = null;
        int counter = 1;
        Array2DRowRealMatrix array2DRowRealMatrixLowerMatrix = Util.getLowerMatrix(array2DRowRealMatrixA);
        Array2DRowRealMatrix array2DRowRealMatrixUpperMatrix = Util.getUpperMatrix(array2DRowRealMatrixA);

        BlockRealMatrix blockRealMatrixDMinusLInverse = (BlockRealMatrix) MatrixUtils.inverse(array2DRowRealMatrixD.subtract(array2DRowRealMatrixLowerMatrix));
        BlockRealMatrix blockRealMatrixDMinusLInverseMultiplyB = blockRealMatrixDMinusLInverse.multiply(blockRealMatrixB);

        do {
            blockRealMatrixXkMinusOne = counter == 1 ? blockRealMatrixXZero : blockRealMatrixXk;
            blockRealMatrixXk = blockRealMatrixDMinusLInverse
                    .multiply(array2DRowRealMatrixUpperMatrix)
                    .multiply(blockRealMatrixXkMinusOne);

            blockRealMatrixXk = blockRealMatrixXk.add(blockRealMatrixDMinusLInverseMultiplyB);

            counter++;

        } while (Util.getMaxNorm(blockRealMatrixXkMinusOne.subtract(blockRealMatrixXk)) >= 0.001);

        System.out.println("Ergebnis");
        System.out.println("-----------------------");
        System.out.println("Schleifendurchläufe: " + --counter);

        System.out.println(Util.formatMatrix(blockRealMatrixXk));
    }

    private static void calcSor(Array2DRowRealMatrix array2DRowRealMatrixA, BlockRealMatrix blockRealMatrixXZero,
                                        BlockRealMatrix blockRealMatrixB) {

        Array2DRowRealMatrix array2DRowRealMatrixD = Util.getDiagonalMatrix(array2DRowRealMatrixA);
        Array2DRowRealMatrix array2DRowRealMatrixEinheitsmatrixA =  Util.createEinheitsmatrix(array2DRowRealMatrixA.getRowDimension());
        Array2DRowRealMatrix array2DRowRealMatrixLowerMatrix = Util.getLowerMatrix(array2DRowRealMatrixA);
        double overRelaxationParameter = Util.calcOverRelaxationParameter((BlockRealMatrix) MatrixUtils.inverse(array2DRowRealMatrixD), array2DRowRealMatrixA);

        BlockRealMatrix blockRealMatrixXkMinusOne;
        BlockRealMatrix blockRealMatrixXk = null;
        int counter = 1;

        BlockRealMatrix blockRealMatrixDDivideOmegaMinusLInverse = (BlockRealMatrix) MatrixUtils.inverse(
                Util.divideMatrixByNumber(array2DRowRealMatrixD, overRelaxationParameter)
                .subtract(array2DRowRealMatrixLowerMatrix)
        );
        BlockRealMatrix blockRealMatrixDDivideOmegaMinusLInverseMultiplyA = blockRealMatrixDDivideOmegaMinusLInverse.multiply(array2DRowRealMatrixA);
        BlockRealMatrix blockRealMatrixDDivideOmegaMinusLInverseMultiplyB = blockRealMatrixDDivideOmegaMinusLInverse.multiply(blockRealMatrixB);
        Array2DRowRealMatrix blockRealMatrixEinheitsmatrixMinusBlockRealMatrixDDivideOmegaMinusLInverseMultiplyA =
                (Array2DRowRealMatrix) array2DRowRealMatrixEinheitsmatrixA.subtract(blockRealMatrixDDivideOmegaMinusLInverseMultiplyA);

        do {
            blockRealMatrixXkMinusOne = counter == 1 ? blockRealMatrixXZero : blockRealMatrixXk;
            blockRealMatrixXk = (BlockRealMatrix) blockRealMatrixEinheitsmatrixMinusBlockRealMatrixDDivideOmegaMinusLInverseMultiplyA
                    .multiply(blockRealMatrixXkMinusOne);

            blockRealMatrixXk = blockRealMatrixXk.add(blockRealMatrixDDivideOmegaMinusLInverseMultiplyB);

            counter++;

        } while (Util.getMaxNorm(blockRealMatrixXkMinusOne.subtract(blockRealMatrixXk)) >= 0.001);

        System.out.println("Ergebnis");
        System.out.println("-----------------------");
        System.out.println("Schleifendurchläufe: " + --counter);

        System.out.println(Util.formatMatrix(blockRealMatrixXk));
    }

    private static void calcSorWithComponentView(Array2DRowRealMatrix array2DRowRealMatrixA, BlockRealMatrix blockRealMatrixXZero,
                                                 BlockRealMatrix blockRealMatrixB){

        double overRelaxationParameter = Util.calcOverRelaxationParameter((BlockRealMatrix) MatrixUtils.inverse(Util.getDiagonalMatrix(array2DRowRealMatrixA)), array2DRowRealMatrixA);
        int counter = 1;
        BlockRealMatrix blockRealMatrixXkMinusOne;
        BlockRealMatrix blockRealMatrixXk = new BlockRealMatrix(blockRealMatrixXZero.getData());

        do {
            blockRealMatrixXkMinusOne = counter == 1 ? new BlockRealMatrix(blockRealMatrixXZero.getData()) : new BlockRealMatrix(blockRealMatrixXk.getData());
            for (int i = 0; i < array2DRowRealMatrixA.getRowDimension(); i++){
                double si = array2DRowRealMatrixA.getRowMatrix(i).multiply(blockRealMatrixXk).getRow(0)[0];
                double loesungsComponent = Util.calcLoesungsComponent(blockRealMatrixXk.getColumn(0)[i], overRelaxationParameter,
                        array2DRowRealMatrixA.getRow(i)[i], si, blockRealMatrixB.getRow(i)[0]);
                blockRealMatrixXk.setEntry(i, 0, loesungsComponent);
            }

            counter++;

        } while (Util.getMaxNorm(blockRealMatrixXkMinusOne.subtract(blockRealMatrixXk)) >= 0.000001);

        System.out.println("Ergebnis");
        System.out.println("-----------------------");
        System.out.println("Schleifendurchläufe: " + --counter);

        System.out.println(Util.formatMatrix(blockRealMatrixXk));
    }
}
