package com.example.pmtarea;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FormPersonaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imgFotoForm;
    private Button btnSelectFoto;
    private TextInputEditText etNombresForm;
    private TextInputEditText etApellidosForm;
    private TextInputEditText etDireccionForm;
    private TextInputEditText etTelefonoForm;
    private Button btnGuardarForm;

    private Uri imageUri = null;

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_persona);

        imgFotoForm = findViewById(R.id.imgFotoForm);
        btnSelectFoto = findViewById(R.id.btnSelectFoto);
        etNombresForm = findViewById(R.id.etNombresForm);
        etApellidosForm = findViewById(R.id.etApellidosForm);
        etDireccionForm = findViewById(R.id.etDireccionForm);
        etTelefonoForm = findViewById(R.id.etTelefonoForm);
        btnGuardarForm = findViewById(R.id.btnGuardarForm);

        // Listener para seleccionar foto
        btnSelectFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Listener para guardar persona y enviar a API
        btnGuardarForm.setOnClickListener(v -> {
            String nombres = etNombresForm.getText().toString().trim();
            String apellidos = etApellidosForm.getText().toString().trim();
            String direccion = etDireccionForm.getText().toString().trim();
            String telefono = etTelefonoForm.getText().toString().trim();
            String fotoBase64 = imageUri != null ? convertirImageUriABase64(imageUri) : "";

            if (nombres.isEmpty() || apellidos.isEmpty()) {
                Toast.makeText(this, "Nombres y Apellidos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            enviarDatosApi(nombres, apellidos, direccion, telefono, fotoBase64);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgFotoForm.setImageURI(imageUri);
        }
    }

    private String convertirImageUriABase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            return Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void enviarDatosApi(String nombres, String apellidos, String direccion, String telefono, String fotoBase64) {
        String url = "http://192.168.212.202/crudautenticacion/crear_persona.php"; // Cambia al endpoint correcto

        // Crear JSON manualmente
        String json = "{"
                + "\"nombres\":\"" + nombres + "\","
                + "\"apellidos\":\"" + apellidos + "\","
                + "\"direccion\":\"" + direccion + "\","
                + "\"telefono\":\"" + telefono + "\","
                + "\"foto\":\"" + fotoBase64 + "\""
                + "}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // Petición asíncrona
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(FormPersonaActivity.this, "Error al enviar datos: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String respStr = response.body().string();
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(FormPersonaActivity.this, "Persona creada con éxito", Toast.LENGTH_SHORT).show();
                        finish(); // Cierra el formulario y vuelve a lista
                    } else {
                        Toast.makeText(FormPersonaActivity.this, "Error en respuesta: " + respStr, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
