package com.company;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.SpecializedOps_DDRM;
import org.ejml.simple.SimpleMatrix;

public class Calculator {

    public static SimpleMatrix calc(Procedure procedure, SimpleMatrix simpleMatrixA, SimpleMatrix simpleMatrixXZero,
                            SimpleMatrix simpleMatrixB){

        switch (procedure){
            case JACOBI:
                calcJacobi(simpleMatrixA, simpleMatrixXZero, simpleMatrixB, simpleMatrixA.diag());
                break;
            case GAUSS_SEIDEL:

                break;
            case SOR:

                break;
            default:
                return null;
        }

        return null;
    }

    private static void calcJacobi(SimpleMatrix simpleMatrixA, SimpleMatrix simpleMatrixXZero,
                                   SimpleMatrix simpleMatrixB, SimpleMatrix simpleMatrixC){
        DMatrixRMaj simpleMatrixU = SpecializedOps_DDRM.copyTriangle(simpleMatrixA.getDDRM(), null, false);
        DMatrixRMaj simpleMatrixL = SpecializedOps_DDRM.copyTriangle(simpleMatrixA.getDDRM(), null, true);
    }
}
