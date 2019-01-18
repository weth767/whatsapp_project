package com.wethdeveloper.whatsapp.uteis;

import android.util.Base64;

public class CriptografiaBase64 {

    public static String criptografarBase64(String texto){
        return Base64.encodeToString(texto.getBytes(), Base64.NO_WRAP).replaceAll("(\\n|)\\r","");
    }

    public static String descriptografarBase64(String textoCriptografado){
        return new String(Base64.decode(textoCriptografado, Base64.NO_WRAP));
    }
}
