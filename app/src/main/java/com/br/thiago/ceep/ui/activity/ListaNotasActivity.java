package com.br.thiago.ceep.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.br.thiago.ceep.R;
import com.br.thiago.ceep.dao.NotaDAO;
import com.br.thiago.ceep.model.Nota;
import com.br.thiago.ceep.ui.recyclerview.adapter.ListaNotasAdapter;
import com.br.thiago.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;
import com.br.thiago.ceep.ui.recyclerview.helper.callback.NotaItemTouchHelperCallback;

import static com.br.thiago.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOTA;
import static com.br.thiago.ceep.ui.activity.NotaActivityConstantes.CHAVE_POSICAO;
import static com.br.thiago.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUESICAO_ALTERA_NOTA;
import static com.br.thiago.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INSERE_NOTA;
import static com.br.thiago.ceep.ui.activity.NotaActivityConstantes.POSICAO_INVALIDA;

public class ListaNotasActivity extends AppCompatActivity {

    public static final String TITULO_APPBAR = "Notes";
    private ListaNotasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_notas);
        setTitle(TITULO_APPBAR);
        configuraRecyclerView();
        configuraBotaoInsereNota();
    }

    private void configuraBotaoInsereNota() {
        TextView botaoInsereNota = findViewById(R.id.lista_notas_insere_nota);
        botaoInsereNota.setOnClickListener(v -> {
            vaiParaFormularioNotaActivityInsere();
        });
    }

    private void vaiParaFormularioNotaActivityInsere() {
        Intent intent = new Intent(ListaNotasActivity.this, FormularioNotaActivity.class);
        startActivityForResult(intent, CODIGO_REQUISICAO_INSERE_NOTA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ehResultadoInsereNota(requestCode, resultCode, data)) {
            Nota notaRecebida = (Nota) data.getSerializableExtra(CHAVE_NOTA);
            new NotaDAO().insere(notaRecebida);
            adapter.adiciona(notaRecebida);
        }
        if (ehResultadoAlteraNota(requestCode, resultCode, data)) {
            Nota notaRecebida = (Nota) data.getSerializableExtra(CHAVE_NOTA);
            int posicaoRecebida = data.getIntExtra(CHAVE_POSICAO, POSICAO_INVALIDA);
            if (ehPosicaoValida(posicaoRecebida)) {
                altera(notaRecebida, posicaoRecebida);
            } else {
                Toast.makeText(ListaNotasActivity.this,
                        "There was a problem changing the note",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void altera(Nota nota, int posicao) {
        new NotaDAO().altera(posicao, nota);
        adapter.altera(posicao, nota);
    }

    private boolean ehPosicaoValida(int posicao) {
        return posicao > POSICAO_INVALIDA;
    }

    private boolean ehResultadoAlteraNota(int requestCode, int resultCode, @Nullable Intent data) {
        return requestCode == CODIGO_REQUESICAO_ALTERA_NOTA &&
                resultCode == Activity.RESULT_OK &&
                data.hasExtra(CHAVE_NOTA);
    }

    private boolean ehResultadoInsereNota(int requestCode, int resultCode, @Nullable Intent data) {
        return requestCode == CODIGO_REQUISICAO_INSERE_NOTA &&
                resultCode == Activity.RESULT_OK &&
                data.hasExtra(CHAVE_NOTA);
    }

    private void configuraRecyclerView() {
        RecyclerView listaNotas = findViewById(R.id.lista_notas_recyclerview);
        configuraAdapter(listaNotas);
        configuraItemTouchHelper(listaNotas);
    }

    private void configuraItemTouchHelper(RecyclerView listaNotas) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new NotaItemTouchHelperCallback(adapter));
        itemTouchHelper.attachToRecyclerView(listaNotas);
    }

    private void configuraAdapter(RecyclerView listaNotas) {
        adapter = new ListaNotasAdapter(new NotaDAO().todos(), this);
        listaNotas.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Nota nota, int posicao) {
                vaiParaFormularioNotaActivityAltera(nota, posicao);
            }
        });
    }

    private void vaiParaFormularioNotaActivityAltera(Nota nota, int posicao) {
        Intent abreFormularioComNota = new Intent(ListaNotasActivity.this,
                FormularioNotaActivity.class);
        abreFormularioComNota.putExtra(CHAVE_NOTA, nota);
        abreFormularioComNota.putExtra(CHAVE_POSICAO, posicao);
        startActivityForResult(abreFormularioComNota, CODIGO_REQUESICAO_ALTERA_NOTA);
    }

}
