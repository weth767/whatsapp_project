package com.wethdeveloper.whatsapp.uteis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissoes {
    public static boolean validaPermissoes(Activity activity, String[] permissoes, int requestCode){
        /*verifica a versão do sdk*/
        if(Build.VERSION.SDK_INT >= 23){
            List<String> listaPermissoes = new ArrayList();
            for (String permissao : permissoes){
                if(ContextCompat.checkSelfPermission(activity.getApplicationContext(), permissao) == PackageManager.PERMISSION_DENIED){
                    listaPermissoes.add(permissao);
                }
            }
            /*se já tem a permissão, retorna true*/
            if(listaPermissoes.isEmpty()){
                return true;
            }
            /*se ainda não tem*/
            /*converte a lista em array*/
            String[] novasPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);
            /*solicita a permissão*/
            ActivityCompat.requestPermissions(activity,novasPermissoes,requestCode);
        }
        /*caso for versão 23 abaixo, já retorna true*/
        else{
            return true;
        }
        /*em todo caso, retorna false senão acontecer nada*/
        return false;
    }
    /*método para mostrar um alerta na tela sobre as permissões*/
    public static void alertaValidacaoPermissao(Activity activity){
        /*construtor do dialog*/
        AlertDialog.Builder construtor = new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        /*passa o titulo da mensagem*/
        construtor.setTitle("Mensagem importante");
        /*passa a mensagem*/
        construtor.setMessage("Deve-se aceitar as permissões para utilizar o app");
        /*seta o botão de aceitar*/
        construtor.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*finaliza a activity*/
                System.exit(1);
            }
        });
        /*passa o construtor para o dialog*/
        AlertDialog dialog = construtor.create();
        /*e depois mostrar a dialog*/
        dialog.show();
    }
}
