package com.br.thiago.ceep.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.br.thiago.ceep.R;
import com.br.thiago.ceep.dao.NotaDAO;
import com.br.thiago.ceep.model.Nota;
import com.br.thiago.ceep.ui.recyclerview.adapter.ListaNotasAdapter;

import static com.br.thiago.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOTA;
import static com.br.thiago.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INSERE_NOTA;
import static com.br.thiago.ceep.ui.activity.NotaActivityConstantes.CODIGO_RESULTADO_NOTA_CRIADA;

public class ListaNotasActivity extends AppCompatActivity {

    private ListaNotasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_notas);
        configuraRecyclerView();
        configuraBotaoInsereNota();
    }

    private void configuraBotaoInsereNota() {
        TextView botaoInsereNota = findViewById(R.id.lista_notas_insere_nota);
        botaoInsereNota.setOnClickListener(v -> {
            vaiParaFormularioNotaActivity();
        });
    }

    private void vaiParaFormularioNotaActivity() {
        Intent intent = new Intent(ListaNotasActivity.this, FormularioNotaActivity.class);
        startActivityForResult(intent, CODIGO_REQUISICAO_INSERE_NOTA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (ehResultadoComNota(requestCode, resultCode, data)) {
            Nota notaRecebida = (Nota) data.getSerializableExtra(CHAVE_NOTA);
            new NotaDAO().insere(notaRecebida);
            adapter.adiciona(notaRecebida);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean ehResultadoComNota(int requestCode, int resultCode, @Nullable Intent data) {
        return requestCode == CODIGO_REQUISICAO_INSERE_NOTA &&
                resultCode == CODIGO_RESULTADO_NOTA_CRIADA &&
                data.hasExtra(CHAVE_NOTA);
    }

    private void configuraRecyclerView() {
        RecyclerView listaNotas = findViewById(R.id.lista_notas_recyclerview);
        configuraAdapter(listaNotas);
    }

    private void configuraAdapter(RecyclerView listaNotas) {
        adapter = new ListaNotasAdapter(new NotaDAO().todos(), this);
        listaNotas.setAdapter(adapter);
    }

}
