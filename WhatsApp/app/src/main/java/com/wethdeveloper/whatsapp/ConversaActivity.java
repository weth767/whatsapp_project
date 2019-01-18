package com.wethdeveloper.whatsapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.wethdeveloper.whatsapp.adaptador.MensagemAdapter;
import com.wethdeveloper.whatsapp.modelo.Contato;
import com.wethdeveloper.whatsapp.modelo.Conversa;
import com.wethdeveloper.whatsapp.modelo.Mensagem;
import com.wethdeveloper.whatsapp.uteis.CriptografiaBase64;
import com.wethdeveloper.whatsapp.uteis.FirebaseDB;
import com.wethdeveloper.whatsapp.uteis.Preferencias;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText campoMensagem;
    private ImageButton botaoEnviar;
    private ListView listaMensagens;
    /*variaveis do destinatario*/
    private String nomeContato;
    private String emailContato;
    private String codigoContato;
    /*variavel do remetente*/
    private String codigo;

    private DatabaseReference referencia;

    private List<Mensagem> mensagens;
    private ArrayAdapter <Mensagem> adaptador;
    private ValueEventListener listenerMensagem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_conversa );
        /*recupera os dados recebidos pela contatosfragment*/
        Bundle bundle = getIntent().getExtras();
        /*verifica se recebeu com sucesso os dados*/
        if(bundle != null){
            /*senão estiver null, está ok*/
            nomeContato = bundle.getString( "nome" );
            emailContato = bundle.getString( "email" );
            codigoContato = CriptografiaBase64.criptografarBase64(emailContato);
        }
        /*pega o id dos componentes para poder manipula-los*/
        toolbar = findViewById( R.id.toolbar_conversa );
        campoMensagem = findViewById( R.id.campoMensagem );
        botaoEnviar = findViewById( R.id.botaoEnviar );
        listaMensagens = findViewById( R.id.listaConversas);
        /*configurando a toolbar*/
        /*primeiro passa o titulo, que é o nome do usuário*/
        toolbar.setTitle( nomeContato );
        /*depois passa o icone de navegação para voltar para a tela principal*/
        toolbar.setNavigationIcon( R.drawable.ic_action_arrow_left );
        /*passa o suporte para a activity receber a toolbar */
        setSupportActionBar( toolbar );
        /*configurando o listview*/
        mensagens = new ArrayList();
        adaptador = new MensagemAdapter( getApplicationContext(), mensagens );
        listaMensagens.setAdapter( adaptador );
        /*utiliza a referencia do firebase para pegar todas as mensagens trocadas entre os usuários*/
        /*instancia as preferencias para pegar o email do usuário*/
        Preferencias preferencias = new Preferencias( getApplicationContext() );
        HashMap<String,String> dados = preferencias.recuperarInformacoesBasicas();
        /*recupera o codigo do usuário*/
        codigo = CriptografiaBase64.criptografarBase64( dados.get( "email" ) );
        referencia = FirebaseDB.getReferencia().child( "mensagem" ).child( codigo ).child( codigoContato );
        /*cria o listener para recuperar os dados*/
        listenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*limpa o list para não haver repetições de dados*/
                mensagens.clear();
                /*um for para iterar sobre todas as mensagens, pegando todos os filhos do nó a partir da referencia*/
                for (DataSnapshot dado: dataSnapshot.getChildren()){
                    /*recebe o dado com mensagem*/
                    Mensagem mensagem = dado.getValue(Mensagem.class);
                    /*e passa para a lista*/
                    mensagens.add( mensagem );
                    /*quando tiver uma mensagem nova, vai para o ponto mais baixo possivel do listview*/
                }
                scrollMyListViewToBottom();
                /*depois atualiza o adaptador para atualizar os dados na tela*/
                adaptador.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        /*adiciona o evento ao listener da referencia*/
        referencia.addValueEventListener( listenerMensagem );
        /*listener de click do botão de enviar*/
        botaoEnviar.setOnClickListener( new View.OnClickListener() {
            @Override
            /*quando há click*/
            public void onClick(View v) {
                /*verifica se o campo não está vazio*/
                if(!(campoMensagem.getText().toString().isEmpty())){
                    /*pega o data já formatada em hora e minutos*/
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "HH:mm" );
                    String data = simpleDateFormat.format( new Date(  ) );
                    /*passa os dados para a mensagem*/
                    Mensagem mensagem = new Mensagem( codigo, codigoContato, campoMensagem.getText().toString() ,data);
                    /*chama o método para salvar a mensagem*/
                    boolean verificaMensagemRemetente = salvarMensagem(mensagem);
                    scrollMyListViewToBottom();
                    /*se der erro no método, mostra mensagem de erro*/
                    if(verificaMensagemRemetente == false){
                        Toast.makeText( ConversaActivity.this, "Erro ao enviar a mensagem, tente novamente", Toast.LENGTH_SHORT ).show();
                    }
                    /*senão der erro*/
                    else{
                        /*e chama o método para salvar a mensagem para o destinatario*/
                        boolean verificaMensagemDestinatario = salvarMensagem( mensagem.getCodigoDestino(), mensagem.getCodigo(), mensagem );
                        scrollMyListViewToBottom();
                        /*verifica se o destinatario recebeu a mensagem, senão mostra mensagem de erro*/
                        if(verificaMensagemDestinatario == false){
                            Toast.makeText( ConversaActivity.this, "Erro ao enviar a mensagem ao destinatário, tente novamente", Toast.LENGTH_SHORT ).show();
                        }
                        /*se deu certo, apaga o campo*/
                        else{
                            /*salva agora a conversa para o usuário e para o contato*/
                            /*primeiro salva a conversa para o remetentente*/
                            Conversa conversa = new Conversa( codigoContato, nomeContato, campoMensagem.getText().toString() );
                            boolean verificaConversaRemetente = salvarConversa( codigo, codigoContato, conversa);
                            if(verificaConversaRemetente){
                                /*depois salva a conversa para o destinatario*/
                                /*cria a conversa*/
                                final Conversa c = new Conversa( codigo,"", campoMensagem.getText().toString() );
                                /*pega a referencia do firebase para pegar o nome(apelido do contato para o destinatario*/
                                DatabaseReference novaReferencia = FirebaseDB.getReferencia().child( "contato" )
                                        .child( codigoContato ).child( codigo );
                                /*adiciona um listener para recuperar os dados*/
                                novaReferencia.addValueEventListener( new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        /*seta o nome como o apelido dele para o contato*/
                                        c.setNome(dataSnapshot.getValue( Contato.class ).getApelido());
                                        /*salva a conversa e verifica se deu certo*/
                                        scrollMyListViewToBottom();
                                        boolean verificaConversaDestinatario = salvarConversa( codigoContato, codigo, c );
                                        if(verificaConversaDestinatario == false){
                                            /*se deu erro, mostra mensagem de erro*/
                                            Toast.makeText( ConversaActivity.this, "Erro em salvar a conversa ao destinatário", Toast.LENGTH_SHORT ).show();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                } );
                            /*se deu erro em salvar a conversa para o usuário*/
                            }else{
                                /*mostra mensagem de erro*/
                                Toast.makeText( ConversaActivity.this, "Erro em salvar a conversa", Toast.LENGTH_SHORT ).show();
                            }
                            /*e seta o campo como vazio*/
                            campoMensagem.setText( "" );
                        }
                    }
                }
            }
        } );
    }
    /*método que faz o listview ir para o final sempre que há mensagem*/
    private void scrollMyListViewToBottom() {
        listaMensagens.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listaMensagens.setSelection(adaptador.getCount() - 1);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        referencia.removeEventListener( listenerMensagem );
    }

    /*método para salvar mensagem*/
    private boolean salvarMensagem(Mensagem mensagem) {
        try{
            /*pega a referencia para o nó mensagem*/
            referencia = FirebaseDB.getReferencia().child( "mensagem" );
            /*insere os nós - Obs: método push() cria um id único, no qual o proprio firebase é responsável por gerenciar
            * os incrementos*/
            referencia.child( mensagem.getCodigo() ).child( mensagem.getCodigoDestino() ).push().setValue( mensagem );
            /*retorna true se deu certo*/
            return true;
        }catch (Exception e){
            /*deu algum tipo de erro, retorna false e o erro*/
            e.printStackTrace();
            return false;
        }
    }

    private boolean salvarMensagem(String codigo, String codigoDestino, Mensagem mensagem){
        try{
            /*pega a referencia para o nó mensagem*/
            referencia = FirebaseDB.getReferencia().child( "mensagem" );
            /*insere os nós - Obs: método push() cria um id único, no qual o proprio firebase é responsável por gerenciar
             * os incrementos*/
            referencia.child( codigo ).child( codigoDestino).push().setValue( mensagem );
            /*retorna true se deu certo*/
            return true;
        }catch (Exception e){
            /*deu algum tipo de erro, retorna false e o erro*/
            e.printStackTrace();
            return false;
        }
    }

    private boolean salvarConversa(String codigo, String codigoDestino, Conversa conversa){
        try {
            referencia = FirebaseDB.getReferencia().child( "conversa" );
            referencia.child( codigo ).child( codigoDestino ).setValue( conversa );
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
