package com.wethdeveloper.whatsapp.uteis;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Preferencias {
    private SharedPreferences preferencias;
    private static final String NOME_ARQUIVO = "dadosusuariowpp.preferencias";
    private SharedPreferences.Editor editor;

    public Preferencias(Context contexto){
        preferencias = contexto.getSharedPreferences(NOME_ARQUIVO,Context.MODE_PRIVATE);
        editor = preferencias.edit();
    }
    /*Método para salvar os dados no arquivo de preferencias temporario*/
    public void salvarDadosUsuarioPreferencia(String nome, String email, String senha, String telefone, String status, String token){
        /*seta os dados a serem salvos*/
        editor.putString("nome",nome);
        editor.putString("email",email);
        editor.putString("senha",senha);
        editor.putString("token",token);
        editor.putString("telefone",telefone);
        editor.putString("status",status);
        /*envia os dados para o arquivo efetivamente*/
        editor.commit();
    }
    /*método para retornar as preferencias do usuário*/
    public HashMap<String,String> retornaDadosUsuarioPreferencia(){
        /*cria o map para receber os dados*/
        HashMap<String,String> dadosUsuario = new HashMap();
        /*pega os dados do editor e os coloca no map*/
        dadosUsuario.put("nome",preferencias.getString("nome",null));
        dadosUsuario.put("email",preferencias.getString("email",null));
        dadosUsuario.put("senha",preferencias.getString("senha",null));
        dadosUsuario.put("token",preferencias.getString("token",null));
        dadosUsuario.put("telefone",preferencias.getString("telefone",null));
        dadosUsuario.put("status",preferencias.getString("status",null));
        /*retorna o map*/
        return dadosUsuario;
    }
    /*método para salvar os dados básicos do usuário*/
    public void salvarInformacoesBasicas(String email, String nome, String status){
        /*salva o nome e o email*/
        editor.putString("email",email);
        editor.putString("nome",nome);
        editor.putString("status",status);
        editor.commit();
    }
    /*método para recuperar esse dados básicos*/
    public HashMap<String,String> recuperarInformacoesBasicas(){
        /*retorna os dados basicos*/
        HashMap<String,String> dadosBasicos = new HashMap<String,String>();
        dadosBasicos.put("email",preferencias.getString("email",null));
        dadosBasicos.put("nome",preferencias.getString("nome",null));
        dadosBasicos.put("status",preferencias.getString("status",null));
        return dadosBasicos;
    }
}
