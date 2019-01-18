package com.wethdeveloper.whatsapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.database.DatabaseReference;
import com.wethdeveloper.whatsapp.uteis.Email;
import com.wethdeveloper.whatsapp.uteis.FirebaseDB;
import com.wethdeveloper.whatsapp.uteis.Permissoes;
import com.wethdeveloper.whatsapp.uteis.Preferencias;
import com.wethdeveloper.whatsapp.uteis.Telefone;
import com.wethdeveloper.whatsapp.uteis.Token;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoEmail;
    private EditText campoNome;
    private EditText campoSenha;
    private EditText campoTelefone;
    private EditText campoArea;
    private EditText campoPais;
    private Button botaoCadastrar;

    private String email;
    private String senha;
    private String nome;
    private String token;
    private String telefone;

    /*Array de permissões necessárias*/
    private String[] permissoes = new String[]{
            Manifest.permission.SEND_SMS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        /*pega os ids dos campos e botões para poder acessar seus métodos e dados*/
        campoNome = findViewById(R.id.campoNome);
        campoEmail = findViewById(R.id.campoEmail);
        campoSenha = findViewById(R.id.campoSenha);
        campoTelefone = findViewById(R.id.campoTelefone);
        campoArea = findViewById(R.id.campoArea);
        campoPais = findViewById(R.id.campoPais);
        botaoCadastrar = findViewById(R.id.botaoCadastrar);

        /*configurando as mascaras dos campos de telefone*/
        SimpleMaskFormatter mascaraPais = new SimpleMaskFormatter("+NN");
        SimpleMaskFormatter mascaraArea = new SimpleMaskFormatter("NN");
        SimpleMaskFormatter mascaraTelefone = new SimpleMaskFormatter("NNNNN-NNNN");
        MaskTextWatcher visualizadorMascaraPais = new MaskTextWatcher(campoPais,mascaraPais);
        MaskTextWatcher visualizadorMascaraArea = new MaskTextWatcher(campoArea,mascaraArea);
        MaskTextWatcher visualizadorMascaraTelefone = new MaskTextWatcher(campoTelefone,mascaraTelefone);
        /*seta os listener para executar as mascaras*/
        campoPais.addTextChangedListener(visualizadorMascaraPais);
        campoArea.addTextChangedListener(visualizadorMascaraArea);
        campoTelefone.addTextChangedListener(visualizadorMascaraTelefone);
        /*listener do botão cadastrar, para quando for clicado, executar as ações abaixo*/
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*verifica se o email está no formato correto*/
                if(Email.validaFormatoEmail(campoEmail.getText().toString())){
                    /*verifica se o campo nome está preenchido*/
                    if(campoNome.getText().toString().isEmpty()){
                        /*mostra mensagem de erro alertando sobre o campo não preenchido*/
                        Toast.makeText(CadastroActivity.this, "Campo nome deve ser preenchido", Toast.LENGTH_SHORT).show();
                    }
                    /*se estiver preenchido*/
                    else{
                        /*verifica a senha, se a senha tem pelo menos 8 caracteres*/
                        if(campoSenha.getText().toString().length() >= 8){
                            /*se tiver correto, verifica os campos do telefone*/
                            if(campoArea.getText().toString().isEmpty() || campoPais.getText().toString().isEmpty() ||
                                    campoTelefone.getText().toString().isEmpty()){
                                /*se houver alguma vazio, mostra mensagem de erro*/
                                Toast.makeText(CadastroActivity.this, "O telefone deve ser preenchido inteiramente", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                /*guarda os dados nas variaveis*/
                                nome = campoNome.getText().toString();
                                email = campoEmail.getText().toString();
                                senha = campoSenha.getText().toString();
                                token = Token.geraToken();
                                telefone = campoPais.getText().toString().replace("+","")
                                    +campoArea.getText().toString().replace("(","").replace(")","")
                                    +campoTelefone.getText().toString().replace("-","");
                                /*instancia as preferencias para guardar esses dados temporariamente*/
                                Preferencias preferencias = new Preferencias(getApplicationContext());
                                /*salva os dados temporarios*/
                                preferencias.salvarDadosUsuarioPreferencia(nome,email,senha,telefone,"",token);
                                /*pede permissão para enviar a mensagem*/
                                boolean verifica = false;
                                if(Permissoes.validaPermissoes(CadastroActivity.this, permissoes, 1)) {
                                    /*envia o token via sms para o usuário validar seu cadastro*/
                                    verifica = Telefone.enviaSms("5556", "Código de acesso ao Whatsapp: " + token);
                                    /*verifica se a mensagem foi enviada*/
                                    if(verifica){
                                        preferencias.salvarInformacoesBasicas(email,nome,"");
                                        /*cria uma nova intent indicando de onde sai e pra onde vai*/
                                        Intent intent = new Intent(CadastroActivity.this, ValidadorActivity.class);
                                        /*manda o usuário para a proxima activity*/
                                        startActivity(intent);
                                        /*e finaliza essa activity*/
                                        finish();
                                    }
                                    /*se deu erro no envio da mensagem, mostra mensagem de erro*/
                                    else{
                                        /*Mostra mensagem de erro*/
                                        Toast.makeText(CadastroActivity.this, "Erro ao enviar mensagem, tente novamente", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }
                        /*senão, mostra mensagem de erro, alertando sobre o tamanho da senha*/
                        else{
                            Toast.makeText(CadastroActivity.this, "Senha deve conter ao menos 8 caracteres", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                /*senão mostra mensagem de erro, alertando sobre o email invalido*/
                else{
                    Toast.makeText(CadastroActivity.this, "E-mail inválido", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    /*método sobrescrito para verificar se as permissões requisitadas foram negadas*/
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        /*utiliza o método da propria classe*/
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        /*verifica o vetor de acessos*/
        for (int result: grantResults){
            /*se teve algum negado, mostra mensagem de erro*/
            if(result == PackageManager.PERMISSION_DENIED){
                /*mensagem de erro da classe de permissões*/
                Permissoes.alertaValidacaoPermissao(CadastroActivity.this);
            }
        }
    }
}
