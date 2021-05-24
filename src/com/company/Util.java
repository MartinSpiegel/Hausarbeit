package com.company;

import org.ejml.simple.SimpleMatrix;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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

    /**
     * Liest eine Datei ein und schreibt jede Zeile in einen List-Eintrag
     *
     * @param path Pfad der Datei
     * @return Liste der Zeilen
     */
    private static List<String> getLinesFromFile(String path) {

        List<String> lines = new ArrayList<>();

        try {
            //jede Zeile der eingelesenen Datei wird in eine Liste geschrieben
            lines = Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    public static SimpleMatrix calcD(SimpleMatrix simpleMatrixA){

        return simpleMatrixA.diag();
    }
}
