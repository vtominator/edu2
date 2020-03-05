package hu.vtominator.edu2.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import hu.vtominator.edu2.R;
import hu.vtominator.edu2.Controller.BottomNavigationViewHelper;
import hu.vtominator.edu2.Model.Constants;
import hu.vtominator.edu2.Model.SharedPrefManager;


public class KapcsolodjKiActivity extends AppCompatActivity {
    private Context mContext = KapcsolodjKiActivity.this;
    private static final int ACTIVITY_NUM = 1;

    private TextView imgSeenSzulok, imgSeenFiatalok, imgSeenSzakemberek;
    private Button tSzulok, tFiatalok, tSzakemberek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_kapcsolodj_ki);

        imgSeenSzulok = findViewById(R.id.imgSeenSzulok);
        imgSeenFiatalok = findViewById(R.id.imgSeenFiatalok);
        imgSeenSzakemberek = findViewById(R.id.imgSeenSzakemberek);

        tSzulok = findViewById(R.id.tSzulok);
        tFiatalok = findViewById(R.id.tFiatalok);
        tSzakemberek = findViewById(R.id.tSzakemberek);

        kozepsoCsempeGombok();
        ujdonsagEllenorzo();
        alsoNavigaciosMenusor();

    }

    private void kozepsoCsempeGombok() {
        tSzulok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPrefManager.getInstance(mContext).getUsername().equals("Vendég")) {
                    addSeenCsempe(v);
                }
                EsemenyekListazasa.KATEGORIA_NEVE = getString(R.string.szulok);
                EsemenyekListazasa.KATEGORIA_SZINE = getResources().getColor(R.color.black);
                startActivity(new Intent(mContext, EsemenyekListazasa.class));
                finish();
            }
        });
        tFiatalok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPrefManager.getInstance(mContext).getUsername().equals("Vendég")) {
                    addSeenCsempe(v);
                }
                EsemenyekListazasa.KATEGORIA_NEVE = getString(R.string.fiatalok);
                EsemenyekListazasa.KATEGORIA_SZINE = getResources().getColor(R.color.black);
                startActivity(new Intent(mContext, EsemenyekListazasa.class));
                finish();
            }
        });
        tSzakemberek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPrefManager.getInstance(mContext).getUsername().equals("Vendég")) {
                    addSeenCsempe(v);
                }
                EsemenyekListazasa.KATEGORIA_NEVE = getString(R.string.szakemberek);
                EsemenyekListazasa.KATEGORIA_SZINE = getResources().getColor(R.color.black);
                startActivity(new Intent(mContext, EsemenyekListazasa.class));
                finish();
            }
        });
    }

    private void addSeenCsempe(View csempe) {

        final String user_id = SharedPrefManager.getInstance(mContext).getUserId();
        final String csempe_neve = getResources().getResourceEntryName(csempe.getId());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SETSEEN_CSEMPE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (!jsonObject.getBoolean("error")) {
                        csempeLatott(csempe_neve);
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
                params.put("csempe_neve", csempe_neve);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }
    private void getSeenCsempe(View csempe) {


        final String currentUserId = SharedPrefManager.getInstance(mContext).getUserId();
        final String currentCsempeNeve = getResources().getResourceEntryName(csempe.getId());
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.URL_GETSEEN_CSEMPE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);

                    if (!object.getBoolean("error")) {
                        JSONArray favorites = object.getJSONArray("result");

                        for (int i = 0; i < favorites.length(); i++) {
                            JSONObject favObj = favorites.getJSONObject(i);

                            String user_id = favObj.getString("user_id");
                            String csempe_neve = favObj.getString("csempe_neve");

                            if (currentUserId == null) {
                                if (account == null) {
                                    //csempeNemLatott(csempe_neve);
                                }
                            } else if (currentUserId.equals(user_id)) {

                                if (currentCsempeNeve.equals(csempe_neve)) {
                                    csempeLatott(csempe_neve);
                                    return;
                                }
                            }
                            // csempeNemLatott(csempe_neve);
                        }
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
        });
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }
    private void csempeLatott(String csempe){
        switch (csempe){
            case "tSzulok":
                imgSeenSzulok.setVisibility(View.GONE);
                break;
            case "tFiatalok":
                imgSeenFiatalok.setVisibility(View.GONE);
                break;
            case "tSzakemberek":
                imgSeenSzakemberek.setVisibility(View.GONE);
                break;

        }
    }

    private void ujdonsagEllenorzo() {
        getSeenCsempe(tSzulok);
        getSeenCsempe(tFiatalok);
        getSeenCsempe(tSzakemberek);
    }

    private void alsoNavigaciosMenusor() {
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.alsoMenusor);

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);

        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public void onBackPressed() {}
}
