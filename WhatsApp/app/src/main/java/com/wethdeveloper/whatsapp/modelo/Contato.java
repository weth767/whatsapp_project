package com.wethdeveloper.whatsapp.modelo;
/*classe para guardar alguns dados do contato*/
public class Contato {
    private String codigo;
    private String nome;
    private String telefone;
    private String email;
    private String apelido;
    private String status;

    /*construtores vazio e com dados*/
    public Contato(String nome, String apelido, String telefone, String email,String status) {
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.apelido = apelido;
        this.status = status;
    }

    public Contato() {
    }
    /*gets e sets*/
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
