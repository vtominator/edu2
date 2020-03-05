package hu.vtominator.edu2.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import hu.vtominator.edu2.R;
import hu.vtominator.edu2.Model.Constants;
import hu.vtominator.edu2.Controller.FcmVolley;
import hu.vtominator.edu2.Controller.MyFirebaseMessagingService;
import hu.vtominator.edu2.Model.SharedPrefManager;

public class BelepesActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext = BelepesActivity.this;
    private static final String TAG = "BelepesActivity";

    private EditText etFelhasznalonev, etJelszo;
    private Button bBelepes;

    private LoginButton bFacebook;
    private Button facebook_belepesgomb;
    private CallbackManager callbackManager;

    private SignInButton bGoogle;
    private Button google_belepesgomb;
    private GoogleSignInClient mGoogleSignInClient;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_belepes);

        etFelhasznalonev = findViewById(R.id.etFelhasznalonev);
        etJelszo = findViewById(R.id.etJelszo);

        bBelepes = findViewById(R.id.bBelepes);

        bFacebook = findViewById(R.id.bFacebook);
        facebook_belepesgomb = findViewById(R.id.facebook_belepesgomb);
        facebook_belepesgomb.setOnClickListener(this);
        callbackManager = CallbackManager.Factory.create();
        bFacebook.setReadPermissions(Arrays.asList("email", "public_profile"));
        bFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebookBelepes();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        google_belepesgomb = findViewById(R.id.google_belepesgomb);
        google_belepesgomb.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        bBelepes.setOnClickListener(this);

        regisztraciosSzoveg();
        vendegBelepesSzoveg();

        normalFiokkalBelepve();
        googleFiokkalBelepve();


    }


    private void regisztraciosSzoveg() {
        SpannableString regisztracios_szoveg = new SpannableString(getString(R.string.regisztracios_szoveg).toUpperCase());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(mContext, RegisztracioActivity.class));
                finish();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setFakeBoldText(true);
                ds.setColor(Color.BLACK);
            }
        };
        regisztracios_szoveg.setSpan(clickableSpan, 76, 91, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView textView = findViewById(R.id.tvRegisztraciosSzoveg);
        textView.setText(regisztracios_szoveg);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);

    }
    private void vendegBelepesSzoveg() {
        SpannableString vendeg_szoveg = new SpannableString(getString(R.string.vendeg_szoveg).toUpperCase());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                vendegBelepes();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setFakeBoldText(true);
                ds.setColor(Color.BLACK);
            }
        };
        vendeg_szoveg.setSpan(clickableSpan, 0, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView textView = findViewById(R.id.tvVendegBelepes);
        textView.setText(vendeg_szoveg);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
    }

    private void facebookBelepes() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    private void googleBelepes() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            startActivity(new Intent(mContext, MainActivity.class));
            finish();
        } catch (ApiException e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void normalFiokkalBelepve() {
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(mContext, MainActivity.class));
        }
    }
    private void googleFiokkalBelepve() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            startActivity(new Intent(mContext, MainActivity.class));
            finish();
        }
    }

    private void normalBelepes() {
        final String username = etFelhasznalonev.getText().toString().trim();
        final String password = etJelszo.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_LOGINUSER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                SharedPrefManager.getInstance(mContext).login(
                                        obj.getString("user_id"),
                                        obj.getString("username"),
                                        obj.getString("email"),
                                        obj.getString("password")
                                );

                                sendToken(obj.getString("email"));
                                startActivity(new Intent(mContext, MainActivity.class));
                                finish();

                            } else {
                                Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void vendegBelepes() {
        SharedPrefManager.getInstance(mContext).login(null, "Vendég", "", "");
        startActivity(new Intent(mContext, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.facebook_belepesgomb:
                bFacebook.performClick();
                break;
            case R.id.google_belepesgomb:
                googleBelepes();
                break;
            case R.id.bBelepes:
                normalBelepes();
                break;
        }
    }

    public void sendToken(String email) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering Device...");
        progressDialog.show();

        final String Token;
        final String Email;

        if (MyFirebaseMessagingService.getToken(getApplicationContext()) == null){
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        } else {
             Token = MyFirebaseMessagingService.getToken(getApplicationContext());
             Email= email;
        }



        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_REGISTER_DEVICE, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, error.getMessage(),
                                Toast.LENGTH_LONG).show();
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
}
