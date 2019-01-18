package com.wethdeveloper.whatsapp.uteis;

import java.util.Random;

public class Token {
    private static Random aleatorios[] = null;
    /*método para gerar o token aleatorio*/
    public static String geraToken(){
        /*verifica se o array está instanciado*/
        if(aleatorios == null){
            /*senão estiver instancia o array com 6 posições*/
            aleatorios = new Random[6];
        }
        /*verifica se as posições dos arrays estão instanciadas*/
        for (int i = 0; i < 6; i++){
            /*verifica se está null a posição*/
            if(aleatorios[i] == null){
                /*e se estiver instancia a classe*/
                aleatorios[i] = new Random();
            }
        }
        /*gera o token agora*/
        String token = "";
        for(int i = 0; i < 6; i++){
            token += String.valueOf(aleatorios[i].nextInt(10));
        }
        return token;
    }

}
