package com.wethdeveloper.whatsapp.modelo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;
import com.wethdeveloper.whatsapp.uteis.CriptografiaBase64;
import com.wethdeveloper.whatsapp.uteis.FirebaseDB;
import com.wethdeveloper.whatsapp.uteis.Preferencias;

import java.util.HashMap;

/*classe do usuário do app*/
public class Usuario {
    /*atributos da classe*/
    private String codigo;
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String status;

    /*construtor vazio*/
    public Usuario(){
    }

    /*e construtor com atributos*/
    public Usuario(String nome, String email, String senha, String telefone){
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.status = "";
    }
    /*construtor com todos os atributos*/
    public Usuario(String nome, String email, String senha, String telefone,String status){
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.status = status;
    }

    /*métodos gets e sets*/
    @Exclude
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /*método para adicionar o usuário no banco*/
    public void salvarDadosDB(){
        DatabaseReference referencia = FirebaseDB.getReferencia();
        referencia.child("usuario").child(getCodigo()).setValue(this);
    }

    public void salvarDadosDB(String codigo){
        DatabaseReference referencia = FirebaseDB.getReferencia();
        referencia.child("usuario").child(codigo).setValue(this);
    }
}
