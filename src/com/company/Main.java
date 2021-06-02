package com.company;

import java.util.Scanner;

public class Main {

    //Einsprungsmethode
    public static void main(String[] args) {

        while (true) {

            System.out.println("[1] Start");
            System.out.println("[0] Beenden");
            System.out.print("Eingabe: ");

            Scanner scanner = new Scanner(System.in);
            int input = Util.readInput(scanner);

            switch (input) {
                case 0:
                    exit();
                    break;
                case 1:
                    start();
                    break;
                default:
                    System.out.println("Versuchen Sie es erneut");
                    System.out.println();
                    break;
            }
        }
    }

    /**
     * das Programm wird beendet
     */
    private static void exit() {

        System.exit(0);
    }

    /**
     * Steuert das gesamte Programm
     */
    private static void start() {

        System.out.println("[1] Jacobi-Verfahren");
        System.out.println("[2] Gauß-Seidel-Verfahren");
        System.out.println("[3] Successive Overrelaxation (SOR)");
        System.out.print("Eingabe: ");

        Scanner scanner = new Scanner(System.in);
        int input = Util.readInput(scanner);

        switch (input) {
            case 1:
                runJacobi();
                break;
            case 2:
                runGaussSeidel();
                break;
            case 3:
                runSor();
                break;
            default:
                System.out.println("Ungültige Eingabe, versuchen Sie es erneut");
                System.out.println();
                break;
        }


    }

    private static void runJacobi() {

        System.out.println("Jacobi-Verfahren");
        System.out.println("-----------------------");

        Calculator.calc(Procedure.JACOBI);
    }

    private static void runGaussSeidel() {

        System.out.println("Gauß-Seidel-Verfahren");
        System.out.println("-----------------------");

        Calculator.calc(Procedure.GAUSS_SEIDEL);
    }

    private static void runSor() {

        System.out.println("Successive Overrelaxation (SOR)");
        System.out.println("-----------------------");

        Calculator.calc(Procedure.SOR);
    }
}
