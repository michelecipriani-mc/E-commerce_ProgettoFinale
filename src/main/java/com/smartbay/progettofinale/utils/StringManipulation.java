package com.smartbay.progettofinale.utils;
// Classe utility per operazioni sulle stringhe
public class StringManipulation {
    // Metodo statico che restituisce l'estensione di un file a partire dal nome
    public static String getFileExtension(String nameFile){
        // Trova la posizione del primo punto, l'estensione del file è tutto ciò che viene dopo l'ultimo punto
        int dotIndex = nameFile.indexOf('.');
        // Estrae la sottostringa a partire dal carattere successivo al punto fino alla fine
        String extension = nameFile.substring(dotIndex + 1);
        // Restituisce l'estensione del file
        return extension;
    }
}