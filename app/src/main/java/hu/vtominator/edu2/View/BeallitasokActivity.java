package hu.vtominator.edu2.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import de.hdodenhof.circleimageview.CircleImageView;
import hu.vtominator.edu2.Controller.BottomNavigationViewHelper;
import hu.vtominator.edu2.Model.Constants;
import hu.vtominator.edu2.Model.SharedPrefManager;
import hu.vtominator.edu2.R;

public class BeallitasokActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext = BeallitasokActivity.this;
    private static final int ACTIVITY_NUM = 3;

    private static boolean VENDEG_FELHASZNALO = false;
    private static boolean ADMIN_FELHASZNALO = false;

    private GoogleSignInClient mGoogleSignInClient;

    private CircleImageView jelenlegiProfilKep;
    private String facebook_kep;

    private TextView tvNev, tvEmail;
    private SwitchCompat kErtesites;
    private RelativeLayout Ertesites, Erdeklodesek, Letrehozas, Modositas, Kijelentkezes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_beallitasok);


        if (SharedPrefManager.getInstance(mContext).getUsername() == null) {

        } else {
            if (SharedPrefManager.getInstance(mContext).getUsername().contains("admin")) {
                ADMIN_FELHASZNALO = true;
            } else {
                ADMIN_FELHASZNALO = false;
            }

            if (SharedPrefManager.getInstance(mContext).getUsername().contains("Vendég")) {
                VENDEG_FELHASZNALO = true;
            } else {
                VENDEG_FELHASZNALO = false;
            }
        }


        jelenlegiProfilKep = findViewById(R.id.profilKep);
        tvNev = findViewById(R.id.tvNev);
        tvEmail = findViewById(R.id.tvEmail);

        kErtesites = findViewById(R.id.kErtesites);
        if (MainActivity.ertesitesGomb) {
            kErtesites.setChecked(true);
        } else {
            kErtesites.setChecked(false);
        }
        kErtesites.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    addNotification();
                } else {
                    deleteNotification();
                }
            }
        });


        Ertesites = findViewById(R.id.Ertesites);
        Erdeklodesek = findViewById(R.id.Erdeklodesek);
        Letrehozas = findViewById(R.id.Letrehozas);
        Modositas = findViewById(R.id.Modositas);
        Kijelentkezes = findViewById(R.id.Kijelentkezes);

        Ertesites.setOnClickListener(this);
        Erdeklodesek.setOnClickListener(this);
        Letrehozas.setOnClickListener(this);
        Modositas.setOnClickListener(this);
        Kijelentkezes.setOnClickListener(this);


        if (ADMIN_FELHASZNALO) {
            Ertesites.setVisibility(View.VISIBLE);
            Erdeklodesek.setVisibility(View.VISIBLE);
            Letrehozas.setVisibility(View.VISIBLE);
            Modositas.setVisibility(View.VISIBLE);
            Kijelentkezes.setVisibility(View.VISIBLE);
        } else if (VENDEG_FELHASZNALO) {
            Ertesites.setVisibility(View.GONE);
            Erdeklodesek.setVisibility(View.GONE);
            Letrehozas.setVisibility(View.GONE);
            Modositas.setVisibility(View.GONE);
            Kijelentkezes.setVisibility(View.VISIBLE);
        } else {
            Ertesites.setVisibility(View.VISIBLE);
            Erdeklodesek.setVisibility(View.VISIBLE);
            Letrehozas.setVisibility(View.GONE);
            Modositas.setVisibility(View.GONE);
            Kijelentkezes.setVisibility(View.VISIBLE);
        }


        normalBejelentkezes();
        facebookBejelentkezes();
        googleBejelentkezes();
        alsoNavigaciosMenusor();

    }

    private void normalBejelentkezes() {
        String felhasznalonev = SharedPrefManager.getInstance(mContext).getUsername();
        String email = SharedPrefManager.getInstance(mContext).getEmail();
        tvNev.setText(felhasznalonev);
        tvEmail.setText(email);
    }

    private void facebookBejelentkezes() {
        AccessTokenTracker tokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    tvNev.setText("");
                    tvEmail.setText("");
                    jelenlegiProfilKep.setImageResource(0);
                } else {
                    facebookProfilBetoltese(currentAccessToken);
                }
            }
        };
    }

    private void facebookProfilBetoltese(AccessToken newAccessToken) {

        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");


                    facebook_kep = "https://graph.facebook.com/" + id + "/picture?type=normal";

                    SharedPrefManager.getInstance(mContext).facebook_login(
                            object.getString("id"),
                            object.getString("first_name"),
                            object.getString("last_name"),
                            object.getString("email")
                    );


                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();

                    tvNev.setText(last_name + " " + first_name);
                    tvEmail.setText(email);
                    Glide.with(getApplicationContext()).load(facebook_kep).into(jelenlegiProfilKep);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name, last_name, email, id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void googleBejelentkezes() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);
        if (account != null) {

            SharedPrefManager.getInstance(mContext).google_login(
                    account.getId(),
                    account.getDisplayName(),
                    account.getEmail()
            );

            tvNev.setText(account.getDisplayName());
            tvEmail.setText(account.getEmail());
            Glide.with(this).load(account.getPhotoUrl()).into(jelenlegiProfilKep);
        }
    }

    private void felhasznaloKijelentkezes() {
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
            SharedPrefManager.getInstance(this).logout();
            Intent intent = new Intent(mContext, BelepesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivity(intent);
        } else {
            googleKijelentkezes();
            SharedPrefManager.getInstance(this).logout();
            finish();
            startActivity(new Intent(this, BelepesActivity.class));
        }
    }

    private void googleKijelentkezes() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(mContext, BelepesActivity.class));
                    }
                });
    }

    private void alsoNavigaciosMenusor() {
        BottomNavigationViewEx alsoMenusor = findViewById(R.id.alsoMenusor);

        BottomNavigationViewHelper.setupBottomNavigationView(alsoMenusor);

        BottomNavigationViewHelper.enableNavigation(mContext, alsoMenusor);

        Menu menu = alsoMenusor.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    private void addNotification() {

        final String user_id = SharedPrefManager.getInstance(mContext).getUserId();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SETNOTIFICATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    private void deleteNotification() {
        final String user_id = SharedPrefManager.getInstance(mContext).getUserId();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DELETENOTIFICATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.Erdeklodesek):
                startActivity(new Intent(mContext, ErdeklodesekActivity.class));
                finish();
                break;
            case (R.id.Letrehozas):
                startActivity(new Intent(mContext, EsemenyHozzaadasActivity.class));
                finish();
                break;
            case (R.id.Modositas):
                startActivity(new Intent(mContext, ModositasActivity.class));
                finish();
                break;
            case (R.id.Kijelentkezes):
                felhasznaloKijelentkezes();
                break;
        }
    }

}
