package com.example.pmtarea;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvPersonas;
    private FloatingActionButton fabAdd;

    private PersonaAdapter adapter;
    private List<Persona> listaPersonas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ajuste de paddings para barras de sistema
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
                    return insets;
                }
        );

        // Inicializar RecyclerView con adapter
        rvPersonas = findViewById(R.id.rvPersonas);
        rvPersonas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PersonaAdapter(listaPersonas);
        rvPersonas.setAdapter(adapter);

        // Botón para abrir el formulario
        fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, FormPersonaActivity.class);
            startActivity(i);
        });

        // Cargar datos de la API
        cargarPersonasDesdeAPI();
    }

    private void cargarPersonasDesdeAPI() {
        OkHttpClient client = new OkHttpClient();

        String url = "http://192.168.33.202/crud-php/";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-API-KEY", "GRUPO6")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Persona>>() {
                    }.getType();

                    List<Persona> personas = gson.fromJson(json, listType);

                    runOnUiThread(() -> {
                        listaPersonas.clear();
                        listaPersonas.addAll(personas);
                        adapter.notifyDataSetChanged();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}
