package com.wethdeveloper.whatsapp.uteis;

import android.app.Activity;
import android.content.Intent;


public class Email {
    /*método para verificar se o formato do email está correto*/
    public static boolean validaFormatoEmail(final String email) {
        /*verifica pela classe de padrões do android*/
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            /*retorna true se estiver ok*/
            return true;
        }
        /*retorna false senão*/
        return false;
    }
}
