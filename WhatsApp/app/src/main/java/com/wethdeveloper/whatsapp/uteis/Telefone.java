package com.wethdeveloper.whatsapp.uteis;

import android.telephony.SmsManager;

public class Telefone {
    /*método para enviar sms para um telefone*/
    public static boolean enviaSms(String telefone, String mensagem){
        try {
            /*pega a instancia padrão do gerenciador de sms*/
            SmsManager sms = SmsManager.getDefault();
            /*e envia a mensagem*/
            sms.sendTextMessage(telefone,null,mensagem,null,null);
            /*retorna true se deu certo*/
            return true;
        }catch (Exception e){
            e.printStackTrace();
            /*e caso de erro, retorna false*/
            return false;
        }
    }
}
