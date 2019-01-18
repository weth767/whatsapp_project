package com.wethdeveloper.whatsapp.adaptador;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wethdeveloper.whatsapp.R;
import com.wethdeveloper.whatsapp.modelo.Mensagem;
import com.wethdeveloper.whatsapp.uteis.CriptografiaBase64;
import com.wethdeveloper.whatsapp.uteis.Preferencias;

import java.util.List;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class MensagemAdapter extends ArrayAdapter<Mensagem> {

    private Context contexto;
    private List<Mensagem> mensagens;

    public MensagemAdapter(Context c,List<Mensagem> mensagens) {
        super( c, 0, mensagens );
        this.contexto = c;
        this.mensagens = mensagens;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*inicia a view como null*/
        View v = null;
        /*verifica se há mensagens*/
        if(mensagens != null){
            /*se houver, tem o que fazer*/
            Preferencias preferencias = new Preferencias(contexto);
            String id = CriptografiaBase64.criptografarBase64(preferencias.recuperarInformacoesBasicas().get( "email" ));
            /*inicializa o objeto para a montagem da view, a partir do serviço do sistema*/
            LayoutInflater inflater = (LayoutInflater) contexto.getSystemService( contexto.LAYOUT_INFLATER_SERVICE );
            /*se o id for igual ao que está logado, quer dizer que ele mandou a mensagem*/
            if(id.equals( mensagens.get( position ).getCodigo() )){
                /*então a mensagem deve ter o fundo um amarelo esverdeado*/
                /*utiliza o método inflate para converter o xml em view*/
                v = inflater.inflate( R.layout.item_mensagem_direita, parent, false);
            }
            /*senão for igual, coloca o fundo branco*/
            else{
                /*utiliza o método inflate para converter o xml em view*/
                v = inflater.inflate( R.layout.item_mensagem_esquerda, parent, false);
            }
            /*recebe os textviews do layout da lista de mensagens*/
            TextView textMensagem = v.findViewById( R.id.textMensagem );
            /*cria duas spannable string para criar tamanho diferentes para as strings*/
            SpannableString span1 = new SpannableString(mensagens.get( position ).getMensagem());
            span1.setSpan(new AbsoluteSizeSpan(42), 0, mensagens.get( position ).getMensagem().length(), SPAN_INCLUSIVE_INCLUSIVE);

            SpannableString span2 = new SpannableString(mensagens.get( position ).getHora());
            span2.setSpan(new AbsoluteSizeSpan(25), 0, mensagens.get( position ).getHora().length(), SPAN_INCLUSIVE_INCLUSIVE);
            /*concatena elas em um charsequence*/
            CharSequence finalText = TextUtils.concat(span1, "\n", span2);
            /*e seta na textview*/
            textMensagem.setText(finalText);
        }
        /*retorna a view no final*/
        return v;
    }

}
