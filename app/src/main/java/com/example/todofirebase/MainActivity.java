package com.example.todofirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.todofirebase.adapter.TarefaAdapter;
import com.example.todofirebase.modelo.Tarefa;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private EditText editTextNomeTarefa;
    private List<Tarefa> tarefas = new ArrayList<Tarefa>();
    private ArrayAdapter<Tarefa> arrayAdapterTarefa;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextNomeTarefa = findViewById(R.id.edit_text_nome);
        listView = findViewById(R.id.list_view);

        conectarBanco();
        eventoBanco();
    }

    private void conectarBanco()
    {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void eventoBanco() {
        databaseReference.child("tarefa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tarefas.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    Tarefa tarefa = snapshot.getValue(Tarefa.class);
                    tarefas.add(tarefa);
                }

                arrayAdapterTarefa = new TarefaAdapter(MainActivity.this, (ArrayList<Tarefa>) tarefas);
                listView.setAdapter(arrayAdapterTarefa);

                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                        apagarDado(tarefas.get(i));
                        return false;
                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        concluirTarefa(tarefas.get(i));
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Erro ao se comunicar com o banco!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void salvarDado(View v) {
        String valorRecebido = editTextNomeTarefa.getText().toString();
        if(!valorRecebido.equals("")) {
            Tarefa tarefa = new Tarefa(UUID.randomUUID().toString(), valorRecebido);

            databaseReference.child("tarefa").child(tarefa.getUuid()).setValue(tarefa);
            editTextNomeTarefa.setText("");
        }
        else
        {
            Toast.makeText(this, "Infome um nome para tarefa!", Toast.LENGTH_LONG).show();
        }

    }

    public void lerDado(){

    }

    public void apagarDado(final Tarefa tarefa) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Você deseja remover esta tarefa?");
        builder.setIcon(R.drawable.interrogacao);
        // Ao clicar em SIM
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.child("tarefa").child(tarefa.getUuid()).removeValue();
            }
        });

        // Ao clicar em Não
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void concluirTarefa(final Tarefa tarefa)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        if(!tarefa.isStatus()) {
            builder.setMessage("Você quer concluir a tarefa?");
        }
        else
        {
            builder.setMessage("Você quer colocar a terefa atual como pendente?");
        }
        builder.setIcon(R.drawable.interrogacao);
        // Ao clicar em SIM
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.child("tarefa").child(tarefa.getUuid()).child("status").setValue(!tarefa.isStatus());
            }
        });

        // Ao clicar em Não
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();


    }


}
