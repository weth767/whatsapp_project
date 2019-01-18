package com.wethdeveloper.whatsapp.modelo;

public class Conversa {
    private String codigo;
    private String nome;
    private String mensagem;

    public Conversa(String codigo, String nome, String mensagem) {
        this.codigo = codigo;
        this.nome = nome;
        this.mensagem = mensagem;
    }

    public Conversa() {
    }

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

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
