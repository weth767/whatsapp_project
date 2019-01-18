package com.wethdeveloper.whatsapp.uteis;

public class Strings {
    /*método para inverter o email*/
    public static String inverteString(String texto){
        String textoInvertido = "";
        for (int i = texto.length() - 1; i >= 0; i--){
            textoInvertido += texto.charAt(i);
        }
        /*retorna o texto ao contrario*/
        return textoInvertido;
    }
}
