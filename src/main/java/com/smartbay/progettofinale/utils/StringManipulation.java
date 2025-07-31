package com.smartbay.progettofinale.utils;

/**
 * Estrae l'estensione del file dal nome dato.
 * 
 * @param nameFile il nome completo del file (es. "immagine.jpg")
 * @return la stringa che segue il primo punto (es. "jpg")
 * 
 *         Nota: il metodo cerca il primo punto nel nome del file e restituisce
 *         tutto ciò che c'è dopo.
 *         Attenzione: se il nome non contiene punti o il punto è all'inizio, il
 *         metodo può lanciare eccezioni o restituire valori imprevisti.
 */

public class StringManipulation {

    public static String getFileExtension(String nameFile) {
        int dotIndex = nameFile.indexOf('.');
        String extension = nameFile.substring(dotIndex + 1);
        return extension;
    }
}