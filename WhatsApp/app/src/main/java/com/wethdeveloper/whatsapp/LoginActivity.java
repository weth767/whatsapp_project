package com.wethdeveloper.whatsapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.wethdeveloper.whatsapp.modelo.Usuario;
import com.wethdeveloper.whatsapp.uteis.CriptografiaBase64;
import com.wethdeveloper.whatsapp.uteis.FirebaseDB;
import com.wethdeveloper.whatsapp.uteis.Preferencias;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail;
    private EditText campoSenha;
    private Button botaoConectar;
    private TextView semConta;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*método para verificar se o usuário já está logado*/
        verificaUsuarioLogado();
        /*pegando os ids dos componentes para manipula-los*/
        campoEmail = findViewById(R.id.campoEmail);
        campoSenha = findViewById(R.id.campoSenha);
        botaoConectar = findViewById(R.id.botaoConectar);
        semConta = findViewById(R.id.textViewSemConta);
        /*listener de click do botão e do textview*/
        botaoConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*desabilita o botão de conectar*/
                botaoConectar.setEnabled(false);
                botaoConectar.setClickable(false);
                /*instancia o usuário e passa os dados para a classe*/
                usuario = new Usuario();
                if(campoEmail.getText().toString().isEmpty() || campoSenha.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Os campos deve ser preenchidos para conectar-se no app", Toast.LENGTH_SHORT).show();
                }else{
                    usuario.setEmail(campoEmail.getText().toString());
                    usuario.setSenha(campoSenha.getText().toString());
                    /*valida o login*/
                    validaLogin();
                }
                /*e habilita os botões*/
                botaoConectar.setClickable(true);
                botaoConectar.setEnabled(true);
            }
        });

        semConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*instancia a intent, indicando de onde saiu e para onde vai*/
                Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
                /*chama o método start activity para iniciar a outra activity*/
                startActivity(intent);
                /*finaliza-la essa activity*/
                finish();
            }
        });

    }
    /*método para verificar se o usuário já está logado no app*/
    private void verificaUsuarioLogado() {
        /*pega a instancia do autenficador do firebase*/
        FirebaseAuth autenticacao = FirebaseDB.getAutenticacao();
        /*se o usuário atual for diferente de null*/
        if(autenticacao.getCurrentUser() != null){
            /*e chama a tela principal*/
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            /*e finaliza essa activity*/
            finish();
        }
    }

    /*método para validar o login do usuário*/
    private void validaLogin(){
        /*pega a instancia do autenticador do firebase*/
        FirebaseAuth autenticacao = FirebaseDB.getAutenticacao();
        /*chama o método para conectar a firebase*/
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()).
                /*assim que a ação for completada*/
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    /*quando completar*/
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        /*se deu certo*/
                        if(task.isSuccessful()){
                            /*salva alguns dados basicos*/
                            Preferencias preferencias = new Preferencias(getApplicationContext());
                            HashMap<String,String> dados = preferencias.retornaDadosUsuarioPreferencia();
                            preferencias.salvarInformacoesBasicas(usuario.getEmail(),usuario.getNome(),usuario.getStatus());
                            /*mostra mensagem*/
                            Toast.makeText(LoginActivity.this, "Usuário conectado com sucesso", Toast.LENGTH_SHORT).show();
                            /*e chama a tela principal*/
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            /*e finaliza essa activity*/
                            finish();
                        }
                        /*se deu erro*/
                        else{
                            /*Mostra mensagem de erro de acordo com a exceções jogada pela task*/
                            String erro = "";
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthInvalidUserException e){
                                erro = "Erro, E-mail incorreto, não cadastrado ou desabilitado. Por favor tente novamente";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                erro = "Erro, Senha incorreta, por favor tente novamente";
                            }catch (Exception e){
                                erro = "Erro ao realizar a conexão, por favor tente novamente";
                            }
                            Toast.makeText(LoginActivity.this, erro, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
