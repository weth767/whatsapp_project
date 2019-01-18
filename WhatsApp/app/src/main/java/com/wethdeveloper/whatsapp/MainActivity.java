package com.wethdeveloper.whatsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.wethdeveloper.whatsapp.adaptador.TabAdapter;
import com.wethdeveloper.whatsapp.modelo.Contato;
import com.wethdeveloper.whatsapp.modelo.Usuario;
import com.wethdeveloper.whatsapp.uteis.CriptografiaBase64;
import com.wethdeveloper.whatsapp.uteis.FirebaseDB;
import com.wethdeveloper.whatsapp.uteis.Preferencias;
import com.wethdeveloper.whatsapp.uteis.SlidingTabLayout;
import com.wethdeveloper.whatsapp.uteis.Strings;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference referencia;

    private Toolbar toolbar;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;

    private Preferencias preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*pega os ids dos componentes para manipulá-los*/
        toolbar = findViewById(R.id.toolbar_main);
        slidingTabLayout = findViewById(R.id.slidingtablayout);
        viewPager = findViewById(R.id.viewPager);
        /*set o titulo da toolbar*/
        toolbar.setTitle("WhatsApp");
        /*e a passa para a main activity*/
        setSupportActionBar(toolbar);
        /*configurando a abas*/
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(getApplicationContext(),R.color.brancoMaisEscuro));


        preferencias = new Preferencias(getApplicationContext());
        /*instancia o tab adaptar para criar as abas*/
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        /*passa o adaptador criado para a view pager*/
        viewPager.setAdapter(tabAdapter);
        /*e por fim passa o view pager para a classe de abas*/
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    /*chama o menu toolbar para a página inicial*/
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    /*método verificar quando um item da toolbar for selecionado, indicar qual item foi
    * e tomar uma ação adequada*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*pega o id da opção selecionada na toolbar*/
        switch (item.getItemId()){
            /*caso for o id igual a um desses abaixo, chama o método de mesmo nome*/
            case R.id.configuracoes:
                configuracoes();
                return true;
            case R.id.adicionarContato:
                adicionarContato();
                return true;
            case R.id.pesquisar:
                pesquisar();
                return true;
            case R.id.desconectar:
                desconectar();
                return true;
            case R.id.status:
                adicionarStatus();
                return true;
            default:
                /*e por default, retorna o item selecionado*/
                return super.onOptionsItemSelected(item);
        }
    }

    private void adicionarStatus() {
        /*instancia o construtor do dialog*/
        AlertDialog.Builder construtor = new AlertDialog.Builder(MainActivity.this);
        /*usa essa propriedade para definir que só pode ser fechado no botão de cancelar*/
        construtor.setCancelable(false);
        /*Titulo do dialog*/
        construtor.setTitle("Status");
        /*cria o campo que será usado no dialog*/
        final EditText campoStatus = new EditText(MainActivity.this);
        /*e seta suas propriedades*/
        campoStatus.setHint("Digite o status");
        campoStatus.setHintTextColor(Color.GRAY);
        /*passa o campo para o construtor*/
        construtor.setView(campoStatus);
        /*seta o botão adicionar*/
        construtor.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
            @Override
            /*e no evento on click*/
            public void onClick(DialogInterface dialog, int which) {
                /*verifica se o campo está vazio*/
                if(campoStatus.getText().toString().isEmpty()){
                    /*se estiver, mostra mensagem de erro*/
                    Toast.makeText(MainActivity.this, "Erro, Campo de status não pode estar vazio", Toast.LENGTH_SHORT).show();
                }
                /*senão estiver*/
                else {
                    /*pega o id do usuário*/
                    final String id = CriptografiaBase64.criptografarBase64(preferencias.recuperarInformacoesBasicas().get("email"));
                    /*pega a nova referencia para os dados do usuário*/
                    referencia = FirebaseDB.getReferencia().child("usuario").child(id);
                    /*adiciona o listener para ela, para recuperar os dados*/
                    referencia.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            /*verifica se tem dados para serem recuperados*/
                           if(dataSnapshot.getValue() != null){
                               /*se for diferente de null, quer dizer que podem ser recuperados*/
                               Usuario u = dataSnapshot.getValue(Usuario.class);
                               /*seta o novo status*/
                               String status = campoStatus.getText().toString();
                               status = status.substring( 0,1 ).toUpperCase();
                               u.setStatus(status + campoStatus.getText().toString().substring( 0,1 ));
                               /*salva os dados no firebase*/
                               u.salvarDadosDB(id);
                               /*mostra mensagem de sucesso*/
                               Toast.makeText(MainActivity.this, "Status atualizado com sucesso", Toast.LENGTH_SHORT).show();
                           }
                        }
                        @Override
                        /*quando der errado, esse método entra em ação, mostrando uma mensagem de erro*/
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(MainActivity.this, "Erro: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        construtor.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        /*cria o dialog*/
        AlertDialog dialog = construtor.create();
        /*mostra ele*/
        dialog.show();
    }

    /*Método para adicionar o contato*/
    private void adicionarContato() {
        /*configura o alertdialog*/
        AlertDialog.Builder construtor = new AlertDialog.Builder(MainActivity.this);
        construtor.setTitle("Adicionar Contato");
        construtor.setCancelable(false);
        /*cria o campo texto para receber o email*/
        final EditText campoEmail = new EditText(MainActivity.this);
        campoEmail.setHint("Digite o e-mail do contato");
        campoEmail.setHintTextColor(Color.GRAY);
        /*cria o campo para receber o apelido*/
        final EditText campoApelido = new EditText(MainActivity.this);
        campoApelido.setHint("Digite um apelido(nome simples) para o contato");
        campoApelido.setHintTextColor(Color.GRAY);
        /*configura o layout liner para setar os campos*/
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        /*passa os campos para o layout*/
        layout.addView(campoEmail);
        layout.addView(campoApelido);
        /*depois passa o layout para o dialog*/
        construtor.setView(layout);
        /*cria o botão de adicionar o contato*/
        construtor.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
            @Override
            /*evento de onclick para o botão*/
            public void onClick(DialogInterface dialog, int which) {
                /*verifica se o campo não está vazio*/
                if(campoEmail.getText().toString().isEmpty() || campoApelido.getText().toString().isEmpty()){
                    /*se estiver, mostra mensagem*/
                    Toast.makeText(MainActivity.this, "Erro, campo de e-mail e/ou campo apelido vazio", Toast.LENGTH_SHORT).show();
                }
                /*senão estiver*/
                else{
                    /*pega o id do contato*/
                    final String id = CriptografiaBase64.criptografarBase64(preferencias.recuperarInformacoesBasicas().get("email"));
                    /*pega o id do usuário e do contato*/
                    final String idContato = CriptografiaBase64.criptografarBase64(campoEmail.getText().toString());
                    /*cria o nó do usuário no nó de contato*/
                    referencia = FirebaseDB.getReferencia().child("usuario").child(idContato);
                    /*adiciona o listener*/
                    referencia.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        /*evento de mudança no dado do firebase*/
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            /*se o usuário existe*/
                            if(dataSnapshot.getValue() != null){
                                /*recuperar os dados do contato*/
                                Usuario contato = dataSnapshot.getValue(Usuario.class);
                                /*passa para a nova classe*/
                                Contato c = new Contato(contato.getNome(),campoApelido.getText().toString(),contato.getTelefone(),contato.getEmail(),contato.getStatus());
                                /*recupera os dados do usuário*/
                                HashMap<String,String> dados = preferencias.recuperarInformacoesBasicas();
                                /*cria a estrutura no banco*/
                                if(id.equals(idContato)){
                                    /*mostra mensagem de erro caso o contato e o usuário forem iguais*/
                                    Toast.makeText(MainActivity.this, "Usuário não pode adicionar a si mesmo como contato", Toast.LENGTH_SHORT).show();
                                }else{
                                    /*pega a referencia nova*/
                                    referencia = FirebaseDB.getReferencia().child("contato").child(id).child(idContato);
                                    /*e passa o contato*/
                                    referencia.setValue(c);
                                    /*mostra mensagem para avisar que deu certo */
                                    Toast.makeText(MainActivity.this, "Contato adicionado com sucesso", Toast.LENGTH_SHORT).show();
                                }
                             /*senão existe*/
                            }else{
                                /*mostra mensagem de erro*/
                                Toast.makeText(MainActivity.this, "E-mail não cadastrado. Usuário não existente", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        /*seta o botão para cancelar*/
        construtor.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        /*cria o dialog*/
        AlertDialog dialog = construtor.create();
        dialog.show();
    }

    private void pesquisar() {

    }
    /*método para desconectar o usuário do app*/
    private void desconectar() {
        /*pega a instancia do autentificador do firebase*/
        autenticacao = FirebaseDB.getAutenticacao();
        /*desloga o usuário do firebase*/
        autenticacao.signOut();
        /*manda o usuário para a tela de login*/
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        /*e finaliza essa activity*/
        finish();
    }

    private void configuracoes() {

    }
}
