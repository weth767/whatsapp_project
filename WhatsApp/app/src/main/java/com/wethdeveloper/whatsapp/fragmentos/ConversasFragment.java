package com.wethdeveloper.whatsapp.fragmentos;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.wethdeveloper.whatsapp.ConversaActivity;
import com.wethdeveloper.whatsapp.R;
import com.wethdeveloper.whatsapp.adaptador.ConversaAdapter;
import com.wethdeveloper.whatsapp.modelo.Conversa;
import com.wethdeveloper.whatsapp.uteis.CriptografiaBase64;
import com.wethdeveloper.whatsapp.uteis.FirebaseDB;
import com.wethdeveloper.whatsapp.uteis.Preferencias;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {
    private ListView listaConversas;
    private ArrayAdapter adaptador;
    private List<Conversa> conversas;
    private DatabaseReference referencia;
    private ValueEventListener listenerConversas;

    public ConversasFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        /*carrega o listener a query setada na referencia do firebase, somente quando a activity inicia
         * afim de economizar recursos*/
        referencia.addValueEventListener( listenerConversas );
    }

    @Override
    public void onStop() {
        super.onStop();
        /*quando a activity for para estado stop, o listener será removido com a finalidade de econimizar recursos*/
        referencia.removeEventListener( listenerConversas );
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*pega a view que foi convertida do xml pelo método inflate*/
        View v =  inflater.inflate(R.layout.fragment_conversas, container, false);
        /*pega o id do listview para manipula-lo*/
        listaConversas = v.findViewById( R.id.listaConversas );
        /*instancia o arraylist*/
        conversas = new ArrayList<Conversa>();
        /*instancia o adaptador*/
        adaptador = new ConversaAdapter( getActivity(), conversas );
        /*seta o adapter no listview*/
        listaConversas.setAdapter( adaptador );
        /*recupera o id do usuário para começar a busca pelas conversas*/
        Preferencias p = new Preferencias( getActivity() );
        String email = p.recuperarInformacoesBasicas().get( "email" );
        String id = CriptografiaBase64.criptografarBase64( email );
        referencia = FirebaseDB.getReferencia().child( "conversa" ).child( id );
        /*cria o listener para recuperar os dados*/
        listenerConversas = new ValueEventListener() {
            @Override
            /*recupera os dados*/
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*limpa a lista para não duplicar dados*/
                conversas.clear();
                for(DataSnapshot dado: dataSnapshot.getChildren()){
                    Conversa c = dado.getValue(Conversa.class);
                    /*vai adicionando as conversas na lista de conversas*/
                    conversas.add( c );
                }
                /*notifica o adapter que houve atualização para ele atualizar os dados na listview*/
                adaptador.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        /*evento de click na lista de mensagens*/
        listaConversas.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*pega a conversa*/
                Conversa conversa = conversas.get( position );
                /*cria a intent para iniciar a conversa*/
                Intent intent = new Intent( getActivity() , ConversaActivity.class );
                /*passa os dados*/
                intent.putExtra( "nome", conversa.getNome() );
                intent.putExtra( "email", CriptografiaBase64.descriptografarBase64( conversa.getCodigo() ) );
                /*e inicia a intent*/
                startActivity( intent );
            }
        } );
        /*retorna a view no final*/
        return v;
    }

}
