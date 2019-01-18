package com.wethdeveloper.whatsapp.adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wethdeveloper.whatsapp.R;
import com.wethdeveloper.whatsapp.modelo.Conversa;
import java.util.List;

public class ConversaAdapter extends ArrayAdapter<Conversa> {

    private List<Conversa> conversas;
    private Context contexto;

    public ConversaAdapter(Context contexto, List<Conversa> conversas) {
        super( contexto, 0, conversas );
        this.contexto = contexto;
        this.conversas = conversas;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*inicializa a view como null*/
        View view = null;
        /*se a lista não estiver vazia*/
        if(conversas != null){
            /*inicializa o objeto para a montagem da view, a partir do serviço do sistema*/
            LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
            /*utiliza o método inflate para converter o xml em view*/
            view = inflater.inflate( R.layout.lista_conversa, parent, false);
            /*recebe os textviews do layout da lista contato*/
            TextView labelNome = view.findViewById(R.id.labelNomeConversa);
            TextView labelUltimaConversa = view.findViewById(R.id.labelUltimaConversa);
            /*e passa para ele o apelido e o email do contado de acordo com a posição no listview*/
            labelNome.setText(conversas.get(position).getNome());
            labelUltimaConversa.setText(conversas.get(position).getMensagem());
        }
        /*retorna a view no final*/
        return view;
    }
}
