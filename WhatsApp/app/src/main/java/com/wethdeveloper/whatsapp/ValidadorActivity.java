package com.wethdeveloper.whatsapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.wethdeveloper.whatsapp.modelo.Usuario;
import com.wethdeveloper.whatsapp.uteis.CriptografiaBase64;
import com.wethdeveloper.whatsapp.uteis.FirebaseDB;
import com.wethdeveloper.whatsapp.uteis.Preferencias;
import com.wethdeveloper.whatsapp.uteis.Strings;

import java.util.HashMap;

public class ValidadorActivity extends AppCompatActivity {

    private EditText campoCodigoValidacao;
    private Button botaoValidar;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validador);
        /*pega os ids dos campos e botões para manipular os dados neles*/
        campoCodigoValidacao = findViewById(R.id.campoCodigoValidacao);
        botaoValidar = findViewById(R.id.botaoValidar);
        /*mascara do campo para limitar a 6 digitos*/
        SimpleMaskFormatter mascaraCampoCodigo = new SimpleMaskFormatter("NNNNNN");
        MaskTextWatcher visualizadorMascaraCC = new MaskTextWatcher(campoCodigoValidacao,mascaraCampoCodigo);
        campoCodigoValidacao.addTextChangedListener(visualizadorMascaraCC);
        /*seta o listener do click do botão*/
        botaoValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*quando clica no botão, recupera os dados salvos temporariamente*/
                Preferencias preferencias = new Preferencias(getApplicationContext());
                /*salva os dados no map*/
                HashMap<String,String> dados = preferencias.retornaDadosUsuarioPreferencia();
                /*verifica se o campo do codigo de validação está vazio*/
                if(campoCodigoValidacao.getText().toString().isEmpty()){
                    /*se estiver, mostra mensagem de erro*/
                    Toast.makeText(ValidadorActivity.this, "Preencha o campo com o código", Toast.LENGTH_SHORT).show();
                }
                /*senão*/
                else{
                    /*verifica se o token é igual */
                    if(dados.get("token").equals(campoCodigoValidacao.getText().toString())){
                        /*se for igual, cadastra o usuário no usuário no firebase*/
                        Usuario u = new Usuario(dados.get("nome"),dados.get("email"),dados.get("senha"),dados.get("telefone"));
                        cadastrarUsuario(u);
                    }
                    /*senão for*/
                    else{
                        /*Mostra mensagem*/
                        Toast.makeText(ValidadorActivity.this, "Código diferente do gerado. Tente novamente", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    /*método para cadastrar o usuário no firebase*/
    private void cadastrarUsuario(final Usuario u){
        /*pega a instancia da autentificação do firebase*/
        autenticacao = FirebaseDB.getAutenticacao();
        /*cria o usuário com o email e a senha fornecedida pelo usuário*/
        autenticacao.createUserWithEmailAndPassword(u.getEmail(),u.getSenha()).
                /*e assim que ele executar a funcionalidade*/
                addOnCompleteListener(ValidadorActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        /*impede que o fique clicando no botão*/
                        botaoValidar.setClickable(false);
                        botaoValidar.setEnabled(false);
                        /*verifica se deu certo*/
                        if(task.isSuccessful()){
                            /*se deu certo, passa o email ao contrario como chave e sem os caracteres especiais*/
                            String codigo = CriptografiaBase64.criptografarBase64(u.getEmail());
                            u.setCodigo(codigo);
                            /*salva os dados do usuário no banco*/
                            u.salvarDadosDB();
                            /*mostra mensagem*/
                            Toast.makeText(ValidadorActivity.this, "Usuário cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                            /*e inicia a nova tela*/
                            startActivity(new Intent(ValidadorActivity.this,MainActivity.class));
                            /*finaliza a activity atual*/
                            finish();
                        }
                        /*se deu erro, mostra mensagem de erro*/
                        else{
                            Toast.makeText(ValidadorActivity.this, "Erro ao cadastrar o usuário. Tente novamente", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        /*libera os botões novamente*/
        botaoValidar.setClickable(true);
        botaoValidar.setEnabled(true);
    }
}
