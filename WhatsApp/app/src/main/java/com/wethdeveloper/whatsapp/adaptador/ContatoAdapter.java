package com.wethdeveloper.whatsapp.adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wethdeveloper.whatsapp.R;
import com.wethdeveloper.whatsapp.modelo.Contato;
import java.util.List;

public class ContatoAdapter extends ArrayAdapter<Contato> {

    private List<Contato> contatos;
    private Context context;

    public ContatoAdapter(Context context, List<Contato> contatos) {
        super(context, 0, contatos);
        this.contatos = contatos;
        this.context = context;
    }

    @Override
    /*método sobrescrito para modificar a visualização dos dados em cada item da listview*/
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        /*verifica se a lista está vazio*/
        if(contatos != null){
            /*inicializa o objeto para a montagem da view, a partir do serviço do sistema*/
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            /*utiliza o método inflate para converter o xml em view*/
            view = inflater.inflate(R.layout.lista_contato, parent, false);
            /*recebe os textviews do layout da lista contato*/
            TextView labelNome = view.findViewById(R.id.labelNome);
            TextView labelEmail = view.findViewById(R.id.labelStatus);
            /*e passa para ele o apelido e o email do contado de acordo com a posição no listview*/
            labelNome.setText(contatos.get(position).getApelido());
            labelEmail.setText(contatos.get(position).getStatus());
        }
        /*retorna a view no final*/
        return view;
    }
}
