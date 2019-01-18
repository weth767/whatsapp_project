package com.wethdeveloper.whatsapp.modelo;

import com.google.firebase.database.Exclude;

public class Mensagem {
    private String codigo;
    private String mensagem;
    private String codigoDestino;
    private String hora;

    public Mensagem(String codigo, String codigoDestino, String mensagem, String horaEnvio){
        this.codigo = codigo;
        this.codigoDestino = codigoDestino;
        this.mensagem = mensagem;
        this.hora = horaEnvio;
    }

    public Mensagem(){
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    @Exclude
    public String getCodigoDestino() {
        return codigoDestino;
    }

    public void setCodigoDestino(String codigoDestino) {
        this.codigoDestino = codigoDestino;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String horaEnvio) {
        this.hora = horaEnvio;
    }
}
