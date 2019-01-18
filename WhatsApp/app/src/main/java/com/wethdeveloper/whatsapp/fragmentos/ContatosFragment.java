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
import com.wethdeveloper.whatsapp.adaptador.ContatoAdapter;
import com.wethdeveloper.whatsapp.modelo.Contato;
import com.wethdeveloper.whatsapp.modelo.Usuario;
import com.wethdeveloper.whatsapp.uteis.CriptografiaBase64;
import com.wethdeveloper.whatsapp.uteis.FirebaseDB;
import com.wethdeveloper.whatsapp.uteis.Preferencias;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {


    private ListView listaContatos;
    private ArrayAdapter adaptador;
    private List<Contato> contatos;
    private DatabaseReference referencia;
    private ValueEventListener listenerContatos;

    public ContatosFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        /*carrega o listener a query setada na referencia do firebase, somente quando a activity inicia
         * afim de economizar recursos*/
        referencia.addValueEventListener( listenerContatos );
    }

    @Override
    public void onStop() {
        super.onStop();
        /*quando a activity for para estado stop, o listener será removido com a finalidade de econimizar recursos*/
        referencia.removeEventListener( listenerContatos );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*transforma o xml em um fragment de fato, retornando uma view*/
        View v = inflater.inflate( R.layout.fragment_contatos, container, false );
        /*pega o id do listview*/
        listaContatos = v.findViewById( R.id.listaContatos );
        /*cria o arraylist para armazenar os contatos*/
        contatos = new ArrayList<Contato>();
        /*instancia o adaptador para que configurará o listview*/
        //adaptador = new ArrayAdapter(getActivity(),R.layout.lista_contato, contatos);
        adaptador = new ContatoAdapter( getActivity(), contatos );
        /*seta o adaptar com as configs do listview*/
        listaContatos.setAdapter( adaptador );
        /*recupera os contatos do firebase*/
        /*instancia as preferencias*/
        Preferencias p = new Preferencias( getActivity() );
        /*pega as informações basicas do usuário*/
        HashMap<String, String> dados = p.recuperarInformacoesBasicas();
        /*guarda o id*/
        final String id = CriptografiaBase64.criptografarBase64( dados.get( "email" ) );
        /*pega a referencia para este contato*/
        referencia = FirebaseDB.getReferencia().child( "contato" ).child( id );
        /*um listener para quando mude os dados, apresente eles para o usuário*/
        listenerContatos = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*limpa a lista para manté-la somente com os dados necessários*/
                contatos.clear();
                /*agora itera sobre todos os contatos desse usuário*/
                for (DataSnapshot dado : dataSnapshot.getChildren()) {
                    /*aqui tenho todos os contatos desse usuário*/
                    /*recebe os dados de cada usuário por vez em um map*/
                    HashMap<String, String> dados = (HashMap<String, String>) dado.getValue();
                    /*cria o contato*/
                    final Contato c = new Contato();
                    c.setCodigo( CriptografiaBase64.criptografarBase64( dados.get( "email" ) ) );
                    c.setNome( dados.get( "nome" ) );
                    c.setApelido( dados.get( "apelido" ) );
                    c.setEmail( dados.get( "email" ) );
                    c.setTelefone( dados.get( "telefone" ) );
                    /*agora pega uma nova referencia, só que para o nó de usuários e pega o status atualizado desse usuario*/
                    DatabaseReference novaReferencia = FirebaseDB.getReferencia().child( "usuario" ).child( c.getCodigo() );
                    /*adicionei um listener para pegar os dados do usuário do id utilizado acima*/
                    novaReferencia.addListenerForSingleValueEvent( new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnap) {
                            /*peguei os dados*/
                            Usuario u = dataSnap.getValue( Usuario.class );
                            /*atualizei o status*/
                            c.setStatus( u.getStatus() );
                            /*verifica se o contato já está na lista, se estiver não adiciona denovo*/
                            boolean jaTem = false;
                            for (int i = 0; i < contatos.size(); i++) {
                                if (contatos.get( i ).getEmail().equals( c.getEmail() )) {
                                    jaTem = true;
                                }
                            }
                            /*adicionei na lista de contatos*/
                            if (jaTem == false) {
                                contatos.add( c );
                            }
                            /*notifiquei o adaptar para ele poder tratar os dados*/
                            adaptador.notifyDataSetChanged();
                            /*agora, salvo novamente o contato, entratanto agora atualizado*/
                            String codigo = c.getCodigo();
                            c.setCodigo( null );
                            referencia.child( codigo ).setValue( c );
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    } );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        /*listener para os itens do listview*/
        listaContatos.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            /*quando há um click em um dos itens*/
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*cria a intent passando a activity origem para destino*/
                Intent intent = new Intent(getActivity(), ConversaActivity.class);
                Contato c = contatos.get( position );
                intent.putExtra( "nome",  c.getApelido());
                intent.putExtra( "email",  c.getEmail());
                /*start a nova activity*/
                startActivity(intent);
            }
        } );
        /*retorna a view*/
        return v;
    }
}
