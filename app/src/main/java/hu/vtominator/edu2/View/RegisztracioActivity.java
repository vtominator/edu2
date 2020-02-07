package hu.vtominator.edu2.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import hu.vtominator.edu2.R;
import hu.vtominator.edu2.Model.Constants;
import hu.vtominator.edu2.Controller.FcmVolley;
import hu.vtominator.edu2.Controller.MyFirebaseMessagingService;

public class RegisztracioActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext = RegisztracioActivity.this;

    private static final String TAG = "RegisztracioActivity";
    private boolean helyesJelszo = false, helyesJelszoMegegyszer = false, helyesEmail = false;

    private EditText etFelhasznalonev, etJelszo, etJelszoMegegyszer, etEmail;
    private Button bRegisztracio;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_regisztracio);

        etFelhasznalonev = findViewById(R.id.etFelhasznalonev);
        etJelszo = findViewById(R.id.etJelszo);
        etJelszoMegegyszer = findViewById(R.id.etJelszoMegegyszer);
        etEmail = findViewById(R.id.etEmail);
        bRegisztracio = findViewById(R.id.bRegisztracio);

        bRegisztracio.setOnClickListener(this);
    }


    private void felhasznaloLetrehozasa() {
        final String felhasznalonev = etFelhasznalonev.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String jelszo = etJelszo.getText().toString().trim();
        final String jelszo_megegyszer = etJelszoMegegyszer.getText().toString().trim();


        Random random = new Random();
        final int email_activation_hash = random.nextInt(999) + 1;
        final int defaultZero = 0;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CREATEUSER,
                new Response.Listener<String>() {
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
                                sendToken(email);
                                startActivity(new Intent(mContext, BelepesActivity.class));
                                finish();
                            } else {
                                if (!etFelhasznalonev.getText().toString().trim().equals("") && jsonObject.getString("code").equals("0"))
                                    etFelhasznalonev.setError(jsonObject.getString("message"));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", felhasznalonev);
                params.put("email", email);
                params.put("password", jelszo);
                params.put("password_again", jelszo_megegyszer);
                params.put("hash", Integer.toString(email_activation_hash));
                params.put("active", Integer.toString(defaultZero));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
    public void sendToken(String email) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering Device...");
        progressDialog.show();


        final String Token = MyFirebaseMessagingService.getToken(getApplicationContext());
        final String Email = email;

        if (Token == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", Email);
                params.put("token", Token);
                return params;
            }
        };
        FcmVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bRegisztracio:
                if (etFelhasznalonev.getText().toString().trim().equals(""))
                    etFelhasznalonev.setError("A mező kitöltése kötelező");
                if (etEmail.getText().toString().trim().equals(""))
                    etEmail.setError("A mező kitöltése kötelező");
                if (etJelszo.getText().toString().trim().equals(""))
                    etJelszo.setError("A mező kitöltése kötelező");
                if (etJelszoMegegyszer.getText().toString().trim().equals(""))
                    etJelszoMegegyszer.setError("A mező kitöltése kötelező");


                if (!etJelszo.getText().toString().trim().matches(Constants.PASSWORD_PATTERN)) {
                    etJelszo.setError("A jelszónak legalább 6 karakter hosszúnak kell lennie, tartalmaznia kell kis- és nagybetűket, valamint legalább egy számot!");
                } else helyesJelszo = true;

                if (!etJelszo.getText().toString().trim().matches(etJelszoMegegyszer.getText().toString().trim())) {
                    etJelszoMegegyszer.setError("A két jelszó nem egyezik!");
                } else helyesJelszoMegegyszer = true;

                if (!etEmail.getText().toString().trim().matches(Constants.EMAIL_PATTERN)) {
                    etEmail.setError("Helytelen e-mail cím!");
                } else helyesEmail = true;

                if (helyesJelszo && helyesJelszoMegegyszer && helyesEmail){
                    felhasznaloLetrehozasa();
                }
                break;
        }
    }
}
