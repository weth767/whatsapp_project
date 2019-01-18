package com.wethdeveloper.whatsapp.uteis;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class FirebaseDB {
    /*referencia da raiz do banco*/
    private static DatabaseReference referencia;
    private static FirebaseAuth autenticacao;
    /*método para retornar a referencia*/
    public static DatabaseReference getReferencia(){
        /*verifica se a instancia está nula*/
        if(referencia == null)
            /*se estiver instancia*/
            referencia = FirebaseDatabase.getInstance().getReference();
        /*e por fim retorna*/
        return referencia;
    }
    /*método para retornar a instancia da autenticação*/
    public static FirebaseAuth getAutenticacao(){
        /*verifica se esta null*/
        if(autenticacao == null){
            /*se estiver, instancia*/
            autenticacao = FirebaseAuth.getInstance();
            /*e por fim retorna*/
        }
        return autenticacao;
    }
}
