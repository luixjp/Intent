package br.edu.ifsp.scl.sdm.intent;

import static android.content.Intent.ACTION_CHOOSER;
import static android.content.Intent.ACTION_PICK;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.EXTRA_INTENT;
import static android.content.Intent.EXTRA_TITLE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;

import br.edu.ifsp.scl.sdm.intent.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;
    private ActivityResultLauncher<String> requisicaoPermissoesActivityResultLauncher;
    private ActivityResultLauncher<Intent> selecionarImagemActivityResultLauncher;
    private ActivityResultLauncher<Intent> escolherAplicativoActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        activityMainBinding.mainTb.appTb.setTitle("Tratando Intents");
        activityMainBinding.mainTb.appTb.setSubtitle("Principais tipos");
        setSupportActionBar(activityMainBinding.mainTb.appTb);

        requisicaoPermissoesActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean concedida) {
                        if( !concedida ) {
                            requisitarPermissaoLigacao();

                        }
                        else {
                            discarTelefone();
                        }
                    }
        });

        selecionarImagemActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult resultado) {
                    visualizarImagem(resultado);
                }
        });

        escolherAplicativoActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult resultado) {
                    visualizarImagem(resultado);
                }
        });
    }

    private void visualizarImagem(ActivityResult resultado) {
        if (resultado.getResultCode() == RESULT_OK) {
            Uri referenciaImagemUri = resultado.getData().getData();
            Intent visualizarImagemIntent = new Intent(ACTION_VIEW, referenciaImagemUri);
            startActivity(visualizarImagemIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewMi:
                String url = activityMainBinding.parameterEt.getText().toString();

                if (!url.matches("http[s]?")) {
                    url = "http://" + url;
                }

                Intent siteIntent = new Intent(ACTION_VIEW, Uri.parse(url));
                startActivity(siteIntent);
                return true;
            case R.id.callMi:

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        //requisitar permissao
                        requisitarPermissaoLigacao();
                    } else{
                        //executar a discagem
                        discarTelefone();
                    }
                } else {
                    //executar discagem
                    discarTelefone();
                }
                return true;
            case R.id.dialMi:
                Intent ligacaoIntent = new Intent(Intent.ACTION_DIAL);
                ligacaoIntent.setData(Uri.parse("tel: " + activityMainBinding.parameterEt.getText()));
                startActivity(ligacaoIntent);
                return true;
            case R.id.pickMi:
                selecionarImagemActivityResultLauncher.launch( prepararPegarImagemIntent());
                return true;
            case R.id.chooserMi:
                Intent escolherAplicativoIntent = new Intent(ACTION_CHOOSER);
                escolherAplicativoIntent.putExtra(EXTRA_TITLE, "Escolha um aplicativo para imagens");
                escolherAplicativoIntent.putExtra(EXTRA_INTENT, prepararPegarImagemIntent());
                escolherAplicativoActivityResultLauncher.launch(escolherAplicativoIntent);
                return true;
            case  R.id.exitMi:
                finish();
                return true;
            case R.id.actionMi:
                Intent actionIntent = new Intent("OPEN_ACTION_ACTIVITY");
                actionIntent.putExtra(Intent.EXTRA_TEXT, activityMainBinding.parameterEt.getText().toString());
                startActivity(actionIntent);
                return true;
            default:
                return false;
        }
    }

    private Intent prepararPegarImagemIntent() {
        Intent pegarImagemIntent = new Intent(ACTION_PICK);
        String diretorio = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
        pegarImagemIntent.setDataAndType(Uri.parse(diretorio), "image/*");
        return pegarImagemIntent;
    }


    private void discarTelefone() {
        Intent discarIntent = new Intent();
        discarIntent.setAction(Intent.ACTION_CALL);
        discarIntent.setData(Uri.parse("tel: " + activityMainBinding.parameterEt.getText()));
        startActivity(discarIntent);
    }

    private void requisitarPermissaoLigacao() {
        requisicaoPermissoesActivityResultLauncher.launch((Manifest.permission.CALL_PHONE));
    }
}